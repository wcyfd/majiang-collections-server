package com.randioo.majiang_collections_server.module.fight.component;

import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.cache.file.GameRoundConfigCache;
import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.majiang_collections_server.entity.file.GameRoundConfig;
import com.randioo.majiang_collections_server.entity.po.CallCardList;
import com.randioo.majiang_collections_server.entity.po.RoleGameInfo;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Chi;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Gang;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Hu;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Peng;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.ZLPBaiDaHu;
import com.randioo.majiang_collections_server.module.fight.service.FightService;
import com.randioo.majiang_collections_server.module.role.service.RoleService;
import com.randioo.majiang_collections_server.protocol.Entity.GameConfigData;
import com.randioo.majiang_collections_server.protocol.Entity.GameOverMethod;
import com.randioo.randioo_platform_sdk.RandiooPlatformSdk;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.Observer;
import com.randioo.randioo_server_base.utils.ReflectUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

/**
 * 红中麻将规则
 * 
 * @author wcy 2017年8月21日
 *
 */

@Component
public class HongZhongMajiangRule extends MajiangRule {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RandiooPlatformSdk randiooPlatformSdk;

    @Autowired
    private FightService fightService;

    @Autowired
    private RoleGameInfoGetter roleGameInfoGetter;

    @Autowired
    private GameSeat gameSeat;

    private final int[] cards = { 101, 102, 103, 104, 105, 106, 107, 108, 109, // 条
            101, 102, 103, 104, 105, 106, 107, 108, 109, // 条
            101, 102, 103, 104, 105, 106, 107, 108, 109, // 条
            101, 102, 103, 104, 105, 106, 107, 108, 109, // 条

            201, 202, 203, 204, 205, 206, 207, 208, 209, // 筒
            201, 202, 203, 204, 205, 206, 207, 208, 209, // 筒
            201, 202, 203, 204, 205, 206, 207, 208, 209, // 筒
            201, 202, 203, 204, 205, 206, 207, 208, 209, // 筒

            301, 302, 303, 304, 305, 306, 307, 308, 309, // 万
            301, 302, 303, 304, 305, 306, 307, 308, 309, // 万
            301, 302, 303, 304, 305, 306, 307, 308, 309, // 万
            301, 302, 303, 304, 305, 306, 307, 308, 309, // 万
            // 401, 401, 401, 401,// 东
            // 501, 501, 501, 501,// 南
            // 601, 601, 601, 601,// 西
            // 701, 701, 701, 701,// 北
            801, 801, 801, 801,// 中
            // 901, 901, 901, 901,// 发
            // 1001, 1001, 1001, 1001,// 白
            // 1101,// 春
            // 1102,// 夏
            // 1103,// 秋
            // 1104,// 冬
            // 1105,// 梅
            // 1106,// 兰
            // 1107,// 竹
            // 1108,// 菊
            // B9,// 财神
            // BA,// 猫
            // BB,// 老鼠
            // BC,// 聚宝盆
            // C1,// 白搭
            // C1,// 白搭
            // C1,// 白搭
            // C1,// 白搭

            // 11, 12, 13, 14, 15, 16, 17, 18, 19, // 条
            // 11, 12, 13, 14, 15, 16, 17, 18, 19, // 条
            // 11, 12, 13, 14, 15, 16, 17, 18, 19, // 条
            // 11, 12, 13, 14, 15, 16, 17, 18, 19, // 条
            //
            // 21, 22, 23, 24, 25, 26, 27, 28, 29, // 筒
            // 21, 22, 23, 24, 25, 26, 27, 28, 29, // 筒
            // 21, 22, 23, 24, 25, 26, 27, 28, 29, // 筒
            // 21, 22, 23, 24, 25, 26, 27, 28, 29, // 筒
            //
            // 31, 32, 33, 34, 35, 36, 37, 38, 39, // 万
            // 31, 32, 33, 34, 35, 36, 37, 38, 39, // 万
            // 31, 32, 33, 34, 35, 36, 37, 38, 39, // 万
            // 31, 32, 33, 34, 35, 36, 37, 38, 39, // 万
            // // 41, 41, 41, 41,// 东
            // // 51, 51, 51, 51,// 南
            // // 61, 61, 61, 61,// 西
            // // 71, 71, 71, 71,// 北
            // 81, 81, 81, 81,// 中
            // 91, 91, 91, 91,// 发
            // A1, A1, A1, A1,// 白
            // B1,// 梅
            // B2,// 兰
            // B3,// 竹
            // B4,// 菊
            // B5,// 春
            // B6,// 夏
            // B7,// 秋
            // B8,// 冬
            // B9,// 财神
            // BA,// 猫
            // BB,// 老鼠
            // BC,// 聚宝盆
            // C1,// 白搭
            // C1,// 白搭
            // C1,// 白搭
            // C1,// 白搭
    };

    private MajiangState states[] = { //
            // //////////////////
            MajiangState.STATE_GAME_READY, // 0
            MajiangState.STATE_GAME_START, // 1
            MajiangState.STATE_CHECK_ZHUANG, // 2
            MajiangState.STATE_DISPATCH, // 3
            MajiangState.STATE_SC_GAME_START, // 4
            MajiangState.STATE_TOUCH_CARD, // 5
            MajiangState.STATE_CHECK_MINE_CARDLIST, // 6
            MajiangState.STATE_SC_SEND_CARD, // 7
            // ///////////////////////////////////
            MajiangState.STATE_SC_SEND_CARDLIST_2_ROLE, // 8
            MajiangState.STATE_ROLE_CHOSEN_CARDLIST, // 9
            // ///////////////////////////////////
            MajiangState.STATE_ROUND_OVER, // 10
            MajiangState.STATE_INIT_READY, // 11
            // ///////////////////////////////////
            MajiangState.STATE_GAME_OVER, // 12
            // ///////////////////////////////////
            MajiangState.STATE_GANG, // 13
            MajiangState.STATE_PENG, // 14
            MajiangState.STATE_CHI, // 15
            MajiangState.STATE_HU, // 16
            MajiangState.STATE_GUO, // 17

            // ///////////////////////////////////
            MajiangState.STATE_NEXT_SEAT, // 18
            MajiangState.STATE_TOUCH_CARD,// 19
    };

    private static final int STATE_GAME_READY = 0;
    private static final int STATE_TOUCH_CARD = 5;
    private static final int STATE_CHECK_MINE_CARDLIST = 6;
    private static final int STATE_SC_SEND_CARD = 7;
    private static final int STATE_SC_SEND_CARDLIST_2_ROLE = 8;
    private static final int STATE_ROUND_OVER = 10;
    private static final int STATE_INIT_READY = 11;
    private static final int STATE_GAME_OVER = 12;
    private static final int STATE_GANG = 13;
    private static final int STATE_PENG = 14;
    private static final int STATE_CHI = 15;
    private static final int STATE_HU = 16;
    private static final int STATE_GUO = 17;

    @Override
    public int getChiStateIndex() {
        return STATE_CHI;
    }

    @Override
    public int getGangStateIndex() {
        return STATE_GANG;
    }

    @Override
    public int getPengStateIndex() {
        return STATE_PENG;
    }

    @Override
    public int getGuoStateIndex() {
        return STATE_GUO;
    }

    @Override
    public int getHuStateIndex() {
        return STATE_HU;
    }

    @Override
    public void execute(RuleableGame ruleableGame, int currentSeat) {

        Game game = (Game) ruleableGame;
        // 已经完成的状态索引
        int preStateIndex = game.getStateIndex();
        // 跳转后的状态索引
        int afterStateIndex = preStateIndex;

        switch (preStateIndex) {
        case STATE_INIT_READY:
            afterStateIndex = STATE_GAME_READY;
            break;
        case STATE_TOUCH_CARD:
            if (game.getRemainCards().size() <= 0) {
                afterStateIndex = STATE_ROUND_OVER;
            } else {
                afterStateIndex = preStateIndex + 1;
            }
            break;
        case STATE_ROUND_OVER:
            // 消耗燃点币
            // this.consumeRandiooCoin(game);
            if (this.isGameOver(game)) {
                afterStateIndex = STATE_GAME_OVER;
            } else {
                afterStateIndex = preStateIndex + 1;
            }
            break;
        case STATE_CHECK_MINE_CARDLIST:
            if (game.getCallCardLists().size() > 0) {
                afterStateIndex = STATE_SC_SEND_CARDLIST_2_ROLE;
            } else {
                afterStateIndex = preStateIndex + 1;
            }
            break;
        case STATE_GAME_OVER:
            // 增加活跃度
            // this.addRandiooActive(game);
            break;
        case STATE_GANG:
            afterStateIndex = STATE_TOUCH_CARD;
            break;
        case STATE_PENG:
            afterStateIndex = STATE_SC_SEND_CARD;
            break;
        case STATE_HU:
            afterStateIndex = STATE_ROUND_OVER;
            break;
        case STATE_GUO:
            if (game.getCurrentRoleIdIndex() == currentSeat) {
                afterStateIndex = STATE_SC_SEND_CARD;
            } else {
                CallCardList preCallCardList = fightService.getPreviousCallCardList(game.getCallCardLists());
                if (preCallCardList == null) {
                    RoleGameInfo currentRoleGameInfo = roleGameInfoGetter.getCurrentRoleGameInfo(game);
                    if (currentRoleGameInfo.qiangGang != null) {
                        this.addGangSuccess(currentRoleGameInfo, currentRoleGameInfo.qiangGang);
                        Gang gang = currentRoleGameInfo.qiangGang;
                        currentRoleGameInfo.qiangGang = null;
                        this.gangProcess2(game, game.getCurrentRoleIdIndex(), roleGameInfo, gang);
                    } else {
                        gameSeat.nextSeat(game);
                        afterStateIndex = STATE_TOUCH_CARD;
                        // this.nextIndex(game);
                        // this.touchCard(game);
                    }
                } else {
                    if (preCallCardList.call) {
                        gameSeat.jumpSeat(game, preCallCardList.masterSeat);
                        afterStateIndex = STATE_TOUCH_CARD;
                        // this.jumpToIndex(game, preCallCardList.masterSeat);
                        // this.touchCard(game);
                    }
                }
            }
        default:
            afterStateIndex = preStateIndex + 1;

        }

        game.setStateIndex(afterStateIndex);
        MajiangState state = states[afterStateIndex];
        System.out.println(state);

    }

    @Override
    public void update(Observer paramObserver, String paramString, Object... paramArrayOfObject) {
        System.out.println(paramString);
    }

    public HongZhongMajiangRule() {
        allCardListMap.put(Gang.class, ReflectUtils.newInstance(Gang.class));
        allCardListMap.put(Peng.class, ReflectUtils.newInstance(Peng.class));
        allCardListMap.put(Chi.class, ReflectUtils.newInstance(Chi.class));
        allCardListMap.put(Hu.class, ReflectUtils.newInstance(ZLPBaiDaHu.class));

        otherCardListSequence.add(Hu.class);
        otherCardListSequence.add(Gang.class);
        otherCardListSequence.add(Peng.class);

        mineCardListSequence.add(Hu.class);
        mineCardListSequence.add(Gang.class);
    }

    @Override
    public int[] getCards() {
        return cards;
    }

    @Override
    public boolean canZhuaHu(Game game) {
        return false;
    }

    @Override
    public boolean canBaiDaZhuaHu(Game game) {
        return false;
    }

    @Override
    public int getBaidaCard(RuleableGame game) {
        // 红中为百搭牌
        return 801;
    }

    @Override
    protected MajiangState getCurrentState(RuleableGame ruleableGame) {
        int stateIndex = ruleableGame.getStateIndex();
        return states[stateIndex];
    }

    private boolean isGameOver(Game game) {
        GameConfigData gameConfigData = game.getGameConfig();
        GameOverMethod gameOverMethod = gameConfigData.getGameOverMethod();

        // 回合制方式游戏结束
        if (gameOverMethod == GameOverMethod.GAME_OVER_ROUND) {
            int roundCount = gameConfigData.getRoundCount();
            int finshRoundCount = game.getFinishRoundCount();

            return finshRoundCount >= roundCount;
        } else {
            String endTimeStr = gameConfigData.getEndTime();
            String nowTimeStr = TimeUtils.get_HHmmss_DateFormat().format(new Date());
            boolean isPassTime = false;
            try {
                isPassTime = TimeUtils.compareHHmmss(nowTimeStr, endTimeStr) >= 0;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return isPassTime;
        }
    }

    /**
     * 消费燃点币
     * 
     * @param game
     * @author wcy 2017年8月23日
     */
    private void consumeRandiooCoin(Game game) {
        if (game.getFinishRoundCount() != 1) {
            return;
        }

        // 大于一局就扣除燃点币
        GameRoundConfig config = GameRoundConfigCache.getGameRoundByRoundCount(game.getGameConfig().getRoundCount());
        Role role = (Role) RoleCache.getRoleById(game.getMasterRoleId());

        roleService.addRandiooMoney(role, -config.needMoney);
    }

    /**
     * 增加活跃度
     * 
     * @param game
     * @author wcy 2017年8月23日
     */
    private void addRandiooActive(Game game) {

        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            if (roleGameInfo.roleId == 0) {
                continue;
            }

            Role role = (Role) RoleCache.getRoleById(roleGameInfo.roleId);
            if (role == null) {
                continue;
            }

            try {
                randiooPlatformSdk.addActive(role.getAccount());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
