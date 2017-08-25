package com.randioo.majiang_collections_server.module.fight.component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.cache.file.GameRoundConfigCache;
import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.majiang_collections_server.entity.file.GameRoundConfig;
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

    /** 开始流程 */
    private List<MajiangStateEnum> startProcesses = Arrays.asList(//
            MajiangStateEnum.STATE_CHECK_ZHUANG, // 2
            MajiangStateEnum.STATE_DISPATCH, // 3
            MajiangStateEnum.STATE_SC_GAME_START // 4
            );

    /** 摸牌流程 */
    private List<MajiangStateEnum> touchCardProcesses = Arrays.asList(//
            MajiangStateEnum.STATE_TOUCH_CARD,// 摸牌
            MajiangStateEnum.STATE_CHECK_MINE_CARDLIST,// 检查我自己的手牌
            MajiangStateEnum.STATE_CHECK_MINE_CARDLIST_OUTER// 加上别人的牌再检查一次
            // MajiangStateEnum.STATE_SC_SEND_CARD,// 通知出牌
            // MajiangStateEnum.STATE_WAIT_OPERATION// 玩家等待
            );

    /** 出牌流程 */
    private List<MajiangStateEnum> sendCardProcesses = Arrays.asList(//
            MajiangStateEnum.STATE_SC_SEND_CARD,// 通知出牌
            MajiangStateEnum.STATE_WAIT_OPERATION// 玩家等待
            );

    /** 检查别人的卡牌 */
    private List<MajiangStateEnum> checkOtherCardListProcesses = Arrays.asList(//
            MajiangStateEnum.STATE_CHECK_OTHER_CARDLIST//
            );

    /** 自己有胡杠碰吃 */
    private List<MajiangStateEnum> noticeCardListProcesses = Arrays.asList(//
            MajiangStateEnum.STATE_SC_SEND_CARDLIST_2_ROLE,//
            MajiangStateEnum.STATE_WAIT_OPERATION//
            );

    /** 下一个人 */
    private List<MajiangStateEnum> nextOneProcesses = Arrays.asList(//
            MajiangStateEnum.STATE_NEXT_SEAT//
            );

    /** 跳转座位 */
    private List<MajiangStateEnum> jumpSeatProcess = Arrays.asList(//
            MajiangStateEnum.STATE_JUMP_SEAT//
            );

    /** 下家流程 */
    private List<MajiangStateEnum> addFlowersProcess = Arrays.asList(//
            MajiangStateEnum.STATE_ADD_FLOWERS,//
            MajiangStateEnum.STATE_CHECK_MINE_CARDLIST,// 检查我自己的手牌
            MajiangStateEnum.STATE_CHECK_MINE_CARDLIST_OUTER// 加上别人的牌再检查一次
            );

    private List<MajiangStateEnum> overProcess = Arrays.asList(//
            MajiangStateEnum.STATE_ROUND_OVER,//
            MajiangStateEnum.STATE_GAME_OVER//
            );

    @Override
    public List<MajiangStateEnum> afterStateExecute(RuleableGame ruleableGame, MajiangStateEnum currentState,
            int currentSeat) {
        Game game = (Game) ruleableGame;
        RoleGameInfo roleGameInfo = roleGameInfoGetter.getCurrentRoleGameInfo(game);
        List<MajiangStateEnum> list = new ArrayList<>();
        switch (currentState) {
        case STATE_GAME_START:
            list = startProcesses;
            break;
        case STATE_SC_GAME_START:
            list = touchCardProcesses;
            break;
        case STATE_ROLE_SEND_CARD:
            list = checkOtherCardListProcesses;
            break;
        case STATE_CHECK_MINE_CARDLIST:
            if (game.getCallCardLists().size() > 0) {
                list = noticeCardListProcesses;
            }
            break;
        case STATE_CHECK_MINE_CARDLIST_OUTER: {
            if (game.getCallCardLists().size() > 0) {
                list = noticeCardListProcesses;
            } else {
                int size = roleGameInfo.cards.size();
                int cardListSize = roleGameInfo.showCardLists.size();
                int totalSize = size + cardListSize * 3;
                // 牌数量不够，就要继续摸牌，否则直接出牌
                if (totalSize < 14) {
                    list = touchCardProcesses;
                } else {
                    list = sendCardProcesses;
                }
            }
        }
            break;
        case STATE_CHECK_OTHER_CARDLIST:
            if (game.getCallCardLists().size() > 0) {
                list = noticeCardListProcesses;
            } else {
                list = nextOneProcesses;
            }
            break;
        case STATE_NEXT_SEAT: {
            if (fightService.containsFlowers(game, roleGameInfo)) {
                list.addAll(addFlowersProcess);
            }
            list.addAll(touchCardProcesses);
        }

            break;
        case STATE_GANG: {
            list.addAll(jumpSeatProcess);
            list.addAll(touchCardProcesses);
            if (fightService.containsFlowers(game, roleGameInfo)) {
                list.addAll(addFlowersProcess);
            }
        }
            break;
        case STATE_PENG:
        case STATE_CHI: {
            list = jumpSeatProcess;
            if (fightService.containsFlowers(game, roleGameInfo)) {
                list.addAll(addFlowersProcess);
            }
        }
            break;
        case STATE_HU:
            break;

        default:
        }

        return list;

        // if (list != null && list.size() > 0) {
        // this.addProcesses(operations, list);
        // }
    }

    @Override
    public Set<Integer> getFlowers(Game game) {
        // TODO Auto-generated method stub
        return null;
    }

    // /**
    // * 添加流程 例子：<br>
    // * p1,p2,p3->p3,p2,p1<br>
    // *
    // * @param stack
    // * @param list
    // * @author wcy 2017年8月25日
    // */
    // private void addProcesses(Stack<MajiangStateEnum> stack,
    // List<MajiangStateEnum> list) {
    // for (int i = list.size() - 1; i >= 0; i--) {
    // MajiangStateEnum state = list.get(i);
    // stack.push(state);
    // }
    // }

    // @Override
    // public void execute(RuleableGame ruleableGame, int preStateIndex, int
    // seat) {
    //
    // Game game = (Game) ruleableGame;
    // List<Integer> flows = game.getFlows();
    //
    // // 跳转后的状态索引
    // int afterStateIndex = preStateIndex;
    //
    // switch (preStateIndex) {
    // case STATE_INIT_READY:
    // afterStateIndex = STATE_GAME_READY;
    // break;
    // case STATE_TOUCH_CARD:
    // if (game.getRemainCards().size() <= 0) {
    // afterStateIndex = STATE_ROUND_OVER;
    // } else {
    // afterStateIndex = preStateIndex + 1;
    // }
    // break;
    // case STATE_ROUND_OVER:
    // // 消耗燃点币
    // // this.consumeRandiooCoin(game);
    // if (this.isGameOver(game)) {
    // afterStateIndex = STATE_GAME_OVER;
    // } else {
    // afterStateIndex = preStateIndex + 1;
    // }
    // break;
    // case STATE_CHECK_MINE_CARDLIST:
    // if (game.getCallCardLists().size() > 0) {
    // afterStateIndex = STATE_SC_SEND_CARDLIST_2_ROLE;
    // } else {
    // afterStateIndex = preStateIndex + 1;
    // }
    // break;
    // case STATE_GAME_OVER:
    // // 增加活跃度
    // // this.addRandiooActive(game);
    // break;
    // case STATE_GANG:
    // afterStateIndex = STATE_TOUCH_CARD;
    // break;
    // case STATE_PENG:
    // afterStateIndex = STATE_SC_SEND_CARD;
    // break;
    // case STATE_HU:
    // afterStateIndex = STATE_ROUND_OVER;
    // break;
    // case STATE_GUO:
    // if (game.getCurrentRoleIdIndex() == seat) {
    // afterStateIndex = STATE_SC_SEND_CARD;
    // } else {
    // CallCardList preCallCardList =
    // fightService.getPreviousCallCardList(game.getCallCardLists());
    // if (preCallCardList == null) {
    // RoleGameInfo roleGameInfo =
    // roleGameInfoGetter.getRoleGameInfoBySeat(game, seat);
    // RoleGameInfo currentRoleGameInfo =
    // roleGameInfoGetter.getCurrentRoleGameInfo(game);
    //
    // if (currentRoleGameInfo.qiangGang != null) {
    // fightService.addGangSuccess(currentRoleGameInfo,
    // currentRoleGameInfo.qiangGang);
    // Gang gang = currentRoleGameInfo.qiangGang;
    // currentRoleGameInfo.qiangGang = null;
    // fightService.gangProcess2(game, game.getCurrentRoleIdIndex(),
    // roleGameInfo, gang);
    // } else {
    // gameSeat.nextSeat(game);
    // afterStateIndex = STATE_TOUCH_CARD;
    // // this.nextIndex(game);
    // // this.touchCard(game);
    // }
    // } else {
    // if (preCallCardList.call) {
    // gameSeat.jumpSeat(game, preCallCardList.masterSeat);
    // afterStateIndex = STATE_TOUCH_CARD;
    // // this.jumpToIndex(game, preCallCardList.masterSeat);
    // // this.touchCard(game);
    // }
    // }
    // }
    // default:
    // afterStateIndex = preStateIndex + 1;
    //
    // flows.add(afterStateIndex);
    // }

    // game.setStateIndex(afterStateIndex);
    // MajiangStateEnum state = majiangStates[afterStateIndex];
    // System.out.println(state);

    // }

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
    public List<MajiangStateEnum> getOverProcess() {
        return overProcess;
    }

}
