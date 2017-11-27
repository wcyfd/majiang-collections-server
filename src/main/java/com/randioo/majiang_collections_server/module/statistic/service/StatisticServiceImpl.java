/**
 * 
 */
package com.randioo.majiang_collections_server.module.statistic.service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.mahjong_public_server.protocol.Entity.GameRoleData;
import com.randioo.mahjong_public_server.protocol.Entity.HuType;
import com.randioo.mahjong_public_server.protocol.Entity.OverMethod;
import com.randioo.mahjong_public_server.protocol.Entity.RoleRoundOverInfoData;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightRoundOver;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.majiang_collections_server.GlobleConstant;
import com.randioo.majiang_collections_server.dao.LoginDao;
import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.bo.LoginBO;
import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.majiang_collections_server.entity.po.RoleGameInfo;
import com.randioo.majiang_collections_server.module.ServiceConstant;
import com.randioo.majiang_collections_server.module.fight.FightConstant;
import com.randioo.majiang_collections_server.module.fight.component.score.round.GameOverResult;
import com.randioo.majiang_collections_server.module.fight.component.score.round.RoundOverResult;
import com.randioo.majiang_collections_server.module.fight.service.FightService;
import com.randioo.majiang_collections_server.module.login.LoginConstant;
import com.randioo.majiang_collections_server.module.login.service.LoginService;
import com.randioo.majiang_collections_server.module.match.service.MatchService;
import com.randioo.randioo_platform_sdk.GameConstant;
import com.randioo.randioo_platform_sdk.RandiooPlatformSdk;
import com.randioo.randioo_platform_sdk.entity.RoundOverEntity;
import com.randioo.randioo_server_base.config.GlobleMap;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.Observer;
import com.randioo.randioo_server_base.utils.TimeUtils;

/**
 * @Description:
 * @author zsy
 * @date 2017年9月26日 下午12:34:42
 */
@Service("statisticService")
public class StatisticServiceImpl extends ObserveBaseService implements StatisticService {

    @Autowired
    private RandiooPlatformSdk randiooPlatformSdk;

    @Autowired
    private MatchService matchService;

    @Autowired
    private FightService fightService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private LoginDao loginDao;

    @Override
    public void initService() {

        fightService.addObserver(this);
        loginService.addObserver(this);
    }

    @Override
    public void update(Observer observer, String msg, Object... args) {

        if (FightConstant.ROUND_OVER.equals(msg)) {
            SC sc = (SC) args[0];
            Game game = (Game) args[1];
            boolean checkHu = (Boolean) args[2];
            if (!checkHu) {
                return;
            }

            game.roundEndTime = TimeUtils.getNowTime();
            if (game.getFinishRoundCount() == 1) {
                game.firstRoundStartTime = game.roundStartTime;
            }

            SCFightRoundOver scFightRoundOver = sc.getSCFightRoundOver();
            RoundOverEntity roundOverEntity = new RoundOverEntity();
            roundOverEntity.startTime = game.roundStartTime;
            roundOverEntity.appId = GlobleMap.String(GlobleConstant.ARGS_PLATFORM_PACKAGE_NAME);
            roundOverEntity.endTime = TimeUtils.getNowTime();
            roundOverEntity.roomId = matchService.getLockString(game.getLockKey());
            List<RoleRoundOverInfoData> list = scFightRoundOver.getRoleRoundOverInfoDataList();
            for (int i = 0; i < list.size(); i++) {
                RoleRoundOverInfoData data = list.get(i);
                OverMethod overMethod = data.getOverMethod();

                GameRoleData gameRoleData = data.getGameRoleData();
                String gameRoleId = gameRoleData.getGameRoleId();
                RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);

                if (roleGameInfo.roleId == 0) {
                    continue;
                }
                String account = gameRoleData.getAccount();
                String name = gameRoleData.getName();

                roundOverEntity.account = account;
                roundOverEntity.name = name;
                roundOverEntity.win = isWin(overMethod);
                roundOverEntity.point = data.getRoundScore();
                randiooPlatformSdk.logRoundOver(roundOverEntity);

                String appId = GlobleMap.String(GlobleConstant.ARGS_PLATFORM_PACKAGE_NAME);
                TreeMap<Integer, Integer> paramsMap = new TreeMap<>();
                if (ServiceConstant.COM_RANDIOO_RDMJ_ZHONG_HUA.equals(appId)) {
                    hongZhongRoundOver(roleGameInfo, paramsMap);
                }
                if (ServiceConstant.COM_RANDIOO_RDMJ_BAI_DA.equals(appId)) {
                    baidaRoundOver(roleGameInfo, paramsMap);
                }
                if (ServiceConstant.COM_RANDIOO_RDMJ_QIAO_MA.equals(appId)) {
                    qiaomaRoundOver(roleGameInfo, paramsMap);
                }
                String roomId = game.getGameConfig().getRoomId();
                randiooPlatformSdk.roundOverInfo(account, roomId, data.getRoundScore(), isWin(overMethod),
                        game.getFinishRoundCount(), game.roundStartTime, game.roundEndTime, paramsMap, roomId
                                + game.firstRoundStartTime);
            }
            return;
        }

        if (FightConstant.FIGHT_GAME_START.equals(msg)) {
            Game game = (Game) args[0];
            List<String> accountList = new ArrayList<>();
            for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
                if (roleGameInfo.roleId == 0) {
                    continue;
                }
                Role role = loginService.getRoleById(roleGameInfo.roleId);
                accountList.add(role.getAccount());
            }
            String roomId = game.getGameConfig().getRoomId();

            randiooPlatformSdk.gameStartInfo(accountList, roomId, 1, game.roundStartTime, null,
                    game.getFinishRoundCount() + 1, roomId
                            + (game.getFinishRoundCount() == 0 ? game.roundStartTime : game.firstRoundStartTime));
            return;
        }
        if (FightConstant.FIGHT_GAME_OVER.equals(msg)) {
            // fightGameOverSC, game
            Game game = (Game) args[1];
            if (game == null) {
                return;
            }
            for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
                GameOverResult gameOverResult = game.getStatisticResultMap().get(roleGameInfo.gameRoleId);

                Role role = loginService.getRoleById(roleGameInfo.roleId);
                if (role == null) {
                    continue;
                }
                // 胡的次数大于0 ,就认为他赢
                TreeMap<Integer, Integer> paramsMap = new TreeMap<>();

                String appId = GlobleMap.String(GlobleConstant.ARGS_PLATFORM_PACKAGE_NAME);
                if (ServiceConstant.COM_RANDIOO_RDMJ_ZHONG_HUA.equals(appId)) {
                    hongZhongGameOver(roleGameInfo, gameOverResult, paramsMap);
                }
                if (ServiceConstant.COM_RANDIOO_RDMJ_BAI_DA.equals(appId)) {
                    baidaGameOver(roleGameInfo, gameOverResult, paramsMap);
                }
                if (ServiceConstant.COM_RANDIOO_RDMJ_QIAO_MA.equals(appId)) {
                    qiaomaGameOver(roleGameInfo, gameOverResult, paramsMap);
                }

                String roomId = game.getGameConfig().getRoomId();
                randiooPlatformSdk.gameOverInfo(role.getAccount(), roomId, gameOverResult.score,
                        game.getFinishRoundCount(), (long) game.firstRoundStartTime, (long) game.roundEndTime,
                        paramsMap, roomId + game.firstRoundStartTime);
            }
        }

        if (LoginConstant.LOGIN_GET_ROLE_DATA.equals(msg)) {
            Role role = (Role) args[0];

            LoginBO loginBO = new LoginBO();
            loginBO.setAccount(role.getAccount());
            loginBO.setOnline(1);
            loginDao.insert(loginBO);
            return;
        }

        if (LoginConstant.LOGIN_OFFLINE.equals(msg)) {
            Role role = (Role) args[0];

            LoginBO loginBO = new LoginBO();
            loginBO.setAccount(role.getAccount());
            loginBO.setOnline(0);
            loginDao.insert(loginBO);
            return;
        }

    }

    private int hongZhongRoundOver(RoleGameInfo roleGameInfo, TreeMap<Integer, Integer> paramsMap) {

        RoundOverResult roundOverResult = roleGameInfo.roundOverResult;
        // 轮数属性(从1开始)
        paramsMap.put(GameConstant.ZHONG_HUA_GANG_KAI, roundOverResult.gangKai ? 1 : 0);
        paramsMap.put(GameConstant.ZHONG_HUA_MING_GANG, roundOverResult.mingGangCountPlus);
        paramsMap.put(GameConstant.ZHONG_HUA_AN_GANG, roundOverResult.darkGangCountPlus);
        paramsMap.put(GameConstant.ZHONG_HUA_BU_GANG, roundOverResult.addGangCountPlus);
        paramsMap.put(GameConstant.ZHONG_HUA_MING_GANG_SCORE, roundOverResult.mingGangScorePlus);
        paramsMap.put(GameConstant.ZHONG_HUA_AN_GANG_SCORE, roundOverResult.darkGangScorePlus);
        paramsMap.put(GameConstant.ZHONG_HUA_BU_GANG_SCORE, roundOverResult.addGangScorePlus);

        paramsMap.put(GameConstant.ZHONG_HUA_ZHA_MA, roundOverResult.zhaMaScore);
        paramsMap.put(GameConstant.ZHONG_HUA_ZI_MO, roundOverResult.moScore);
        paramsMap.put(GameConstant.ZHONG_HUA_QIANG_GANG, roundOverResult.qiangGangScore);
        paramsMap.put(GameConstant.ZHONG_HUA_ZHUA_HU, roundOverResult.zhuaHuScore);
        return roundOverResult.score;
    }

    private int baidaRoundOver(RoleGameInfo roleGameInfo, TreeMap<Integer, Integer> paramsMap) {
        RoundOverResult roundOverResult = roleGameInfo.roundOverResult;
        paramsMap.put(GameConstant.BAI_DA_HUA, roundOverResult.flowerCount);
        paramsMap.put(GameConstant.BAI_DA_ZI_MO, roundOverResult.moScore);
        paramsMap.put(GameConstant.BAI_DA_QIANG_GANG, roundOverResult.qiangGangScore);
        paramsMap.put(GameConstant.BAI_DA_DA_DAI_CHE, roundOverResult.huTypeList.contains(HuType.DA_DIAO_CHE) ? 1 : 0);
        paramsMap.put(GameConstant.BAI_DA_MEN_QING, roundOverResult.huTypeList.contains(HuType.MEN_QING) ? 1 : 0);
        paramsMap.put(GameConstant.BAI_DA_GANG_KAI, roundOverResult.gangKai ? 1 : 0);
        paramsMap.put(GameConstant.BAI_DA_SI_BAI_DA, roundOverResult.huTypeList.contains(HuType.SI_BAI_DA) ? 1 : 0);
        paramsMap.put(GameConstant.BAI_DA_WU_BAI_DA, roundOverResult.huTypeList.contains(HuType.WU_BAI_DA) ? 1 : 0);
        paramsMap.put(GameConstant.BAI_DA_PAO_BAI_DA, roundOverResult.huTypeList.contains(HuType.PAO_DAI_DA) ? 1 : 0);
        paramsMap.put(GameConstant.BAI_DA_FEI_CANG_YING, roundOverResult.cangYingScore);
        return roundOverResult.score;
    }

    private int qiaomaRoundOver(RoleGameInfo roleGameInfo, TreeMap<Integer, Integer> paramsMap) {
        RoundOverResult roundOverResult = roleGameInfo.roundOverResult;
        // 轮数属性(从1开始)
        paramsMap.put(GameConstant.QIAO_MA_ZI_MO, roundOverResult.moScore);
        paramsMap.put(GameConstant.QIAO_MA_QIANG_GANG, roundOverResult.qiangGangScore);
        paramsMap.put(GameConstant.QIAO_MA_ZHUA_HU, roundOverResult.zhuaHuScore);
        paramsMap.put(GameConstant.QIAO_MA_HUA, roundOverResult.flowerCount);
        paramsMap.put(GameConstant.QIAO_MA_QING_PENG, roundOverResult.huTypeList.contains(HuType.QING_PENG) ? 1 : 0);
        paramsMap.put(GameConstant.QIAO_MA_HUN_PENG, roundOverResult.huTypeList.contains(HuType.HUN_PENG) ? 1 : 0);
        paramsMap.put(GameConstant.QIAO_MA_PENG_PENG_HU,
                roundOverResult.huTypeList.contains(HuType.PENG_PENG_HU) ? 1 : 0);
        paramsMap.put(GameConstant.QIAO_MA_QING_YI_SE, roundOverResult.huTypeList.contains(HuType.QING_YI_SE) ? 1 : 0);
        paramsMap.put(GameConstant.QIAO_MA_HUN_YI_SE, roundOverResult.huTypeList.contains(HuType.HUN_YI_SE) ? 1 : 0);
        paramsMap.put(GameConstant.QIAO_MA_WU_HUA_GUO, roundOverResult.huTypeList.contains(HuType.WU_HUA_GUO) ? 1 : 0);
        paramsMap.put(GameConstant.QIAO_MA_DA_DIAO_CHE, roundOverResult.huTypeList.contains(HuType.DA_DIAO_CHE) ? 1 : 0);
        paramsMap.put(GameConstant.QIAO_MA_GANG_KAI, roundOverResult.gangKai ? 1 : 0);
        paramsMap.put(GameConstant.QIAO_MA_MEN_QING, roundOverResult.huTypeList.contains(HuType.MEN_QING) ? 1 : 0);
        paramsMap.put(GameConstant.QIAO_MA_HAI_DI_LAO, roundOverResult.huTypeList.contains(HuType.HAI_DI_LAO) ? 1 : 0);
        paramsMap.put(GameConstant.QIAO_MA_QIANG_GANG, roundOverResult.qiangGangScore);
        return roundOverResult.score;
    }

    private void hongZhongGameOver(RoleGameInfo roleGameInfo,
            GameOverResult gameOverResult,
            TreeMap<Integer, Integer> paramsMap) {
        paramsMap.put(GameConstant.ZHONG_HUA_ZHUA_HU, gameOverResult.zhuaHuCount);
        paramsMap.put(GameConstant.ZHONG_HUA_ZI_MO, gameOverResult.moHuCount);
        paramsMap.put(GameConstant.ZHONG_HUA_CHU_CHONG, gameOverResult.dianChong);
        paramsMap.put(GameConstant.ZHONG_HUA_MING_GANG, gameOverResult.mingGangCount);
        paramsMap.put(GameConstant.ZHONG_HUA_AN_GANG, gameOverResult.darkGangCount);
    }

    private void baidaGameOver(RoleGameInfo roleGameInfo,
            GameOverResult gameOverResult,
            TreeMap<Integer, Integer> paramsMap) {
        paramsMap.put(GameConstant.BAI_DA_ZHUA_HU, gameOverResult.zhuaHuCount);
        paramsMap.put(GameConstant.BAI_DA_ZI_MO, gameOverResult.moHuCount);
        paramsMap.put(GameConstant.BAI_DA_CHU_CHONG, gameOverResult.dianChong);
        paramsMap.put(GameConstant.BAI_DA_MING_GANG, gameOverResult.mingGangCount);
        paramsMap.put(GameConstant.BAI_DA_AN_GANG, gameOverResult.darkGangCount);
    }

    private void qiaomaGameOver(RoleGameInfo roleGameInfo,
            GameOverResult gameOverResult,
            TreeMap<Integer, Integer> paramsMap) {
        paramsMap.put(GameConstant.QIAO_MA_ZHUA_HU, gameOverResult.zhuaHuCount);
        paramsMap.put(GameConstant.QIAO_MA_ZI_MO, gameOverResult.moHuCount);
        paramsMap.put(GameConstant.QIAO_MA_CHU_CHONG, gameOverResult.dianChong);
        paramsMap.put(GameConstant.QIAO_MA_MING_GANG, gameOverResult.mingGangCount);
        paramsMap.put(GameConstant.QIAO_MA_AN_GANG, gameOverResult.darkGangCount);
    }

    /**
     * 是否胜利
     * 
     * @param overMethod
     * @return
     */
    private boolean isWin(OverMethod overMethod) {
        boolean isWin = false;
        switch (overMethod) {
        case CHU_CHONG:
        case GANG_CHONG:
        case LOSS:
            isWin = false;
            break;
        case MO_HU:
        case QIANG_GANG:
        case ZHUA_HU:
            isWin = true;
            break;
        default:
            break;

        }
        return isWin;
    }

}
