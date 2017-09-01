package com.randioo.majiang_collections_server.module.fight.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.po.CallCardList;
import com.randioo.majiang_collections_server.entity.po.RoleGameInfo;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.CardList;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Chi;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Gang;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Hu;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Peng;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Step5Hu;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.baida.BaidaChi;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.baida.BaidaGang;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.baida.BaidaPeng;
import com.randioo.majiang_collections_server.module.fight.component.score.round.qiaoma.QiaomaHuTypeCalculator;
import com.randioo.majiang_collections_server.module.fight.component.score.round.qiaoma.QiaomaHuTypeResult;
import com.randioo.majiang_collections_server.module.fight.service.FightService;
import com.randioo.randioo_server_base.utils.ReflectUtils;

/**
 * 敲麻规则
 * 
 * @author wcy 2017年8月21日
 *
 */

@Component
public class QiaoMaRule extends MajiangRule {

    @Autowired
    private FightService fightService;

    @Autowired
    private RoleGameInfoGetter roleGameInfoGetter;

    @Autowired
    private CardChecker cardChecker;

    @Autowired
    private SeatIndexCalc seatIndexCalc;

    @Autowired
    private QiaomaHuTypeCalculator typeCalc;

    public static final int TONG = 1;
    public static final int TIAO = 2;
    public static final int WAN = 3;
    public static final int ZHONG = 8;
    public static final int DONG = 401;
    public static final int BEI = 701;
    public static final int SPRING = 1101;

    public final static List<Integer> NUM_CARDS = Arrays.asList(1, 2, 3);
    public final static List<Integer> HUA_CARDS = Arrays.asList(8, 9, 10, 11);
    public final static List<Integer> Feng_CARDS = Arrays.asList(4, 5, 6, 7);
    // 用于判断跑百搭
    public final static List<Integer> TEST_BAI_DAI = Arrays.asList(101, 102, 103, 104, 105, 106, 107, 108, 109, 201,
            202, 203, 204, 205, 206, 207, 208, 209, 301, 302, 303, 304, 305, 306, 307, 308, 309, 401, 501, 601, 701);
    // 用于产生百搭牌
    public final static List<Integer> CARDS = Arrays.asList(101, 102, 103, 104, 105, 106, 107, 108, 109, 201, 202, 203,
            204, 205, 206, 207, 208, 209, 301, 302, 303, 304, 305, 306, 307, 308, 309, 401, 501, 601, 701, 801, 901,
            1001, 1101, 1102, 1103, 1104, 1105, 1106, 1107, 1108);
    private List<Integer> fengCards = Arrays.asList(401, 501, 601, 701);
    private List<Integer> flowerCards = Arrays.asList(801, 901, 1001, 1101, 1102, 1103, 1104, 1105, 1106, 1107, 1108);
    private final List<Integer> cards = Arrays.asList( //
            //
            101, 102, 103, 104, 105, 106, 107, 108, 109, // 条
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
            401, 401, 401, 401, // 东
            501, 501, 501, 501, // 南
            601, 601, 601, 601, // 西
            701, 701, 701, 701, // 北
            801, 801, 801, 801, // 中
            901, 901, 901, 901, // 发
            1001, 1001, 1001, 1001, // 白
            1101, // 春
            1102, // 夏
            1103, // 秋
            1104, // 冬
            1105, // 梅
            1106, // 兰
            1107, // 竹
            1108// 菊
    // B9,// 财神
    // BA,// 猫
    // BB,// 老鼠
    // BC,// 聚宝盆
    // C1,// 白搭
    // C1,// 白搭
    // C1,// 白搭
    // C1,// 白搭
    );

    /** 摸牌流程 */
    private List<MajiangStateEnum> touchCardProcesses = Arrays.asList(//
            MajiangStateEnum.STATE_TOUCH_CARD, // 摸牌
            MajiangStateEnum.STATE_SC_TOUCH_CARD, // 摸牌通知
            MajiangStateEnum.STATE_CHECK_MINE_CARDLIST // 检查我自己的手牌
    );

    /** 出牌流程 */
    private List<MajiangStateEnum> sendCardProcesses = Arrays.asList(//
            MajiangStateEnum.STATE_SC_SEND_CARD, // 通知出牌
            MajiangStateEnum.STATE_WAIT_OPERATION// 玩家等待
    );
    /** 下家流程 */
    private List<MajiangStateEnum> addFlowersProcess = Arrays.asList(//
            MajiangStateEnum.STATE_ADD_FLOWERS, //
            MajiangStateEnum.STATE_CHECK_MINE_CARDLIST, // 检查我自己的手牌
            MajiangStateEnum.STATE_CHECK_MINE_CARDLIST_OUTER// 加上别人的牌再检查一次
    );

    /** 自己有胡杠碰吃 */
    private List<MajiangStateEnum> noticeCardListProcesses = Arrays.asList(//
            MajiangStateEnum.STATE_SC_SEND_CARDLIST_2_ROLE, //
            MajiangStateEnum.STATE_WAIT_OPERATION//
    );

    @Override
    public List<MajiangStateEnum> beforeStateExecute(RuleableGame ruleableGame, MajiangStateEnum majiangStateEnum,
            int currentSeat) {
        Game game = (Game) ruleableGame;
        Stack<MajiangStateEnum> operations = ruleableGame.getOperations();
        List<MajiangStateEnum> list = new ArrayList<>();
        switch (majiangStateEnum) {
        case STATE_TOUCH_CARD:
            if (game.getRemainCards().size() == 0) {// 流局
                operations.clear();
                if (game.getGameConfig().getHuangFan()) { // 带荒番
                    game.setHuangFanCount(game.getHuangFanCount() + 1);
                }
                list.add(MajiangStateEnum.STATE_ROUND_OVER);
            }

            break;
        case STATE_ROUND_OVER:
            operations.clear();
            list.add(MajiangStateEnum.STATE_ROUND_OVER);
            break;

        default:
            break;
        }
        return list;
    }

    @Override
    public List<MajiangStateEnum> afterStateExecute(RuleableGame ruleableGame, MajiangStateEnum currentState,
            int currentSeat) {
        Game game = (Game) ruleableGame;
        List<MajiangStateEnum> list = new ArrayList<>();

        switch (currentState) {
        case STATE_INIT_READY:
            list.add(MajiangStateEnum.STATE_ROLE_GAME_READY);
            break;
        case STATE_ROLE_GAME_READY:
            if (fightService.checkAllReady(game)) {
                list.add(MajiangStateEnum.STATE_GAME_START);
            } else {
                list.add(MajiangStateEnum.STATE_WAIT_OPERATION);
            }
            break;
        case STATE_GAME_START:
            list.add(MajiangStateEnum.STATE_QIAOMA_INIT);
            list.add(MajiangStateEnum.STATE_CHECK_ZHUANG);
            list.add(MajiangStateEnum.STATE_DISPATCH);
            list.add(MajiangStateEnum.STATE_SC_GAME_START);
            list.addAll(addFlowersProcess);
            // list.add(MajiangStateEnum.STATE_TOUCH_CARD);
            // list.add(MajiangStateEnum.STATE_SC_TOUCH_CARD);
            // list.add(MajiangStateEnum.STATE_CHECK_MINE_CARDLIST);
            // list.addAll(sendCardProcesses);
            // list.add(MajiangStateEnum.STATE_NEXT_SEAT);

            break;
        case STATE_ROLE_SEND_CARD: { // 出牌后
            {
                RoleGameInfo roleGameInfo = roleGameInfoGetter.getCurrentRoleGameInfo(game);
                if (roleGameInfo.tingCards.size() > 0) {
                    list.add(MajiangStateEnum.STATE_TING);
                }
            }
            list.add(MajiangStateEnum.STATE_CHECK_OTHER_CARDLIST);
            // 添加需要检测的人
            game.checkOtherCardListSeats.clear();
            int next = seatIndexCalc.getNext(game);
            // 当前指针还在出牌人上
            int seat = game.getCurrentRoleIdIndex();
            for (int i = 0; i < game.getRoleIdList().size(); i++) {
                RoleGameInfo roleGameInfo = roleGameInfoGetter.getRoleGameInfoBySeat(game, i);
                boolean isContainsFlowers = containsFlowers(roleGameInfo);
                // 如果我是下家并且有花，则跳过我
                if (seat == i || (next == i && isContainsFlowers)) {
                    continue;
                }
                game.checkOtherCardListSeats.add(i);
            }
        }
            break;
        case STATE_SC_TOUCH_CARD: {// 通知摸牌后
            boolean isFlower = game.touchCardIsFlower;
            if (isFlower) {// 先把通知发出去后再加流程
                list.addAll(touchCardProcesses);
            }
        }
            break;
        case STATE_TOUCH_CARD: {// 摸牌后
            RoleGameInfo currentRoleGameInfo = roleGameInfoGetter.getCurrentRoleGameInfo(game);
            int card = currentRoleGameInfo.newCard;

            boolean isFlower = this.isFlower(game, card);
            game.touchCardIsFlower = isFlower;
            // 给发牌通知赋值
            for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
                if (currentRoleGameInfo.gameRoleId.equals(roleGameInfo.gameRoleId) || isFlower) {
                    roleGameInfo.everybodyTouchCard = card;
                } else {
                    roleGameInfo.everybodyTouchCard = 0;
                }
            }
        }
            break;
        case STATE_CHECK_MINE_CARDLIST:// 检查自己
            if (game.getCallCardLists().size() > 0) {
                list.addAll(noticeCardListProcesses);
            }
            break;
        case STATE_CHECK_MINE_CARDLIST_OUTER: {// 加上别人的牌检查自己
            if (game.getCallCardLists().size() > 0) {
                list.addAll(noticeCardListProcesses);
            } else {
                // 进入后 以下就是正常流程了
                game.isAddFlowerState = false;
                RoleGameInfo roleGameInfo = roleGameInfoGetter.getCurrentRoleGameInfo(game);
                int size = roleGameInfo.cards.size();
                int cardListSize = roleGameInfo.showCardLists.size();
                int totalSize = size + cardListSize * 3;
                // 牌数量不够，就要继续摸牌，否则直接出牌
                if (totalSize < 14) {
                    list.addAll(touchCardProcesses);
                    list.addAll(sendCardProcesses);
                    list.add(MajiangStateEnum.STATE_NEXT_SEAT);
                } else {
                    list.addAll(sendCardProcesses);
                    list.add(MajiangStateEnum.STATE_NEXT_SEAT);
                }
            }
        }
            break;
        case STATE_CHECK_OTHER_CARDLIST:
            if (game.getCallCardLists().size() > 0) {
                list.addAll(noticeCardListProcesses);
            }

            break;
        case STATE_NEXT_SEAT: {// 移到下一个
            RoleGameInfo roleGameInfo = roleGameInfoGetter.getCurrentRoleGameInfo(game);
            if (this.containsFlowers(roleGameInfo)) {
                // 有花并且顺到下一家
                list.addAll(addFlowersProcess);
            } else {
                // 没有花进入的下一个人则视为上家出牌没有人要杠碰吃胡
                // 直接重置
                game.sendCard = 0;
                game.sendCardSeat = 0;

                list.addAll(touchCardProcesses);
                list.addAll(sendCardProcesses);
                list.add(MajiangStateEnum.STATE_NEXT_SEAT);
            }
        }
            break;
        case STATE_ROLE_CHOSEN_CARDLIST: {// 选择后
            // 如有栈中有CHECK_MINE_CARDLIST_OUTER，说明在补花流程中
            boolean isAddFlower = game.getOperations().contains(MajiangStateEnum.STATE_CHECK_MINE_CARDLIST_OUTER);
            // 获得第一个人的卡组
            List<CallCardList> callCardLists = game.getCallCardLists();
            if (callCardLists.size() > 0) {
                CallCardList callCardList = game.getCallCardLists().get(0);
                CardList cardList = callCardList.cardList;
                if (cardList instanceof Peng) {
                    list.add(MajiangStateEnum.STATE_PENG);
                    list.add(MajiangStateEnum.STATE_JUMP_SEAT);
                    list.add(MajiangStateEnum.STATE_FLOWER_SCORE_CHANGE);
                    list.addAll(sendCardProcesses);
                } else if (cardList instanceof Gang) {
                    Gang gang = (Gang) cardList;
                    list.add(MajiangStateEnum.STATE_GANG);

                    int seat = callCardList.masterSeat;
                    game.checkOtherCardListSeats.clear();
                    for (int i = 0; i < game.getRoleIdList().size(); i++) {
                        if (seat != i) {
                            game.checkOtherCardListSeats.add(i);
                        }
                    }

                    if (gang.peng != null && fightService.checkQiangGang(game)) {
                        // 显示已经杠了
                        list.add(MajiangStateEnum.STATE_GANG);
                        list.addAll(noticeCardListProcesses);
                    } else {
                        list.add(MajiangStateEnum.STATE_GANG);
                        // 如果需要跳转,则要填上出牌流程，实质上和下一家的流程差不多
                        if (game.getCurrentRoleIdIndex() != callCardList.masterSeat) {
                            list.add(MajiangStateEnum.STATE_JUMP_SEAT);
                            list.add(MajiangStateEnum.STATE_FLOWER_SCORE_CHANGE);
                            list.add(MajiangStateEnum.STATE_GANG_CAL_SCORE);
                            list.addAll(touchCardProcesses);
                            if (!isAddFlower) {
                                list.addAll(sendCardProcesses);
                            }
                        } else {
                            list.add(MajiangStateEnum.STATE_FLOWER_SCORE_CHANGE);
                            list.add(MajiangStateEnum.STATE_GANG_CAL_SCORE);
                            list.addAll(touchCardProcesses);
                        }
                    }
                } else if (cardList instanceof Chi) {
                    list.add(MajiangStateEnum.STATE_CHI);
                    list.add(MajiangStateEnum.STATE_JUMP_SEAT);
                    list.addAll(sendCardProcesses);
                } else if (cardList instanceof Hu) {
                    // 如果前面是抢杠,则还原杠变为碰
                    if (game.qiangGangCallCardList != null) {
                        list.add(MajiangStateEnum.STATE_RECOVERY_PENG);
                    }
                    list.add(MajiangStateEnum.STATE_HU);
                }

            } else {
                // 补花流程中,栈中无CHECK_MINE_CARDLIST_OUTER，有操作，但是选择了过
                if (game.isAddFlowerState && !isAddFlower) {
                    game.isAddFlowerState = false;
                    list.addAll(touchCardProcesses);
                    list.addAll(sendCardProcesses);
                    list.add(MajiangStateEnum.STATE_NEXT_SEAT);
                }
                if (game.qiangGangCallCardList != null) {
                    // 如果需要跳转,则要填上出牌流程，实质上和下一家的流程差不多
                    list.add(MajiangStateEnum.STATE_GANG_CAL_SCORE);
                    list.addAll(touchCardProcesses);
                    game.qiangGangCallCardList = null;
                }
            }
        }
            break;
        case STATE_HU:
            list.add(MajiangStateEnum.STATE_ROUND_OVER);
            break;
        case STATE_ROUND_OVER:
            if (fightService.isGameOver(game)) {
                list.add(MajiangStateEnum.STATE_GAME_OVER);
            } else {
                list.add(MajiangStateEnum.STATE_INIT_READY);
            }
            break;

        default:
        }
        return list;
    }

    public boolean containsFlowers(RoleGameInfo roleGameInfo) {
        for (int flower : flowerCards) {
            if (roleGameInfo.cards.contains(flower)) {
                return true;
            }
        }
        return false;
    }

    /** 所有的牌型 */
    public Map<Class<? extends CardList>, CardList> allCardListMap = new HashMap<>();

    /** 别人打牌 不是下一家 花足够时，可以抓胡、杠、碰 */
    private List<Class<? extends CardList>> otherFlowerMoreCardListSequence = new ArrayList<>();
    /** 别人打牌 不是下一家 花不够时，只能杠、碰 */
    private List<Class<? extends CardList>> otherFlowerLessCardListSequence = new ArrayList<>();
    /** 别人打牌 下一家 花够时 可以抓胡、杠、碰、吃 */
    private List<Class<? extends CardList>> otherNextAndFlowerMoreCardListSequence = new ArrayList<>();
    /** 别人打牌 下一家 花够时 可以杠、碰、吃 */
    private List<Class<? extends CardList>> otherNextAndFlowerLessCardListSequence = new ArrayList<>();
    /** 进入听状态 别人打的牌都不用检测 */
    private List<Class<? extends CardList>> otherTingCardListSequence = new ArrayList<>();
    /** 自己摸牌 花够时 可以 胡、杠 */
    private List<Class<? extends CardList>> mineFlowerMoreCardListSequence = new ArrayList<>();

    /** 自己摸牌 花不够时 可以 杠 */
    private List<Class<? extends CardList>> mineFlowerLessCardListSequence = new ArrayList<>();

    public QiaoMaRule() {
        allCardListMap.put(Gang.class, ReflectUtils.newInstance(BaidaGang.class));
        allCardListMap.put(Peng.class, ReflectUtils.newInstance(BaidaPeng.class));
        allCardListMap.put(Chi.class, ReflectUtils.newInstance(BaidaChi.class));
        allCardListMap.put(Hu.class, ReflectUtils.newInstance(Step5Hu.class));

        otherFlowerMoreCardListSequence.add(Hu.class);
        otherFlowerMoreCardListSequence.add(Gang.class);
        otherFlowerMoreCardListSequence.add(Peng.class);

        otherFlowerLessCardListSequence.add(Gang.class);
        otherFlowerLessCardListSequence.add(Peng.class);

        otherNextAndFlowerMoreCardListSequence.add(Hu.class);
        otherNextAndFlowerMoreCardListSequence.add(Gang.class);
        otherNextAndFlowerMoreCardListSequence.add(Peng.class);
        otherNextAndFlowerMoreCardListSequence.add(Chi.class);

        otherNextAndFlowerLessCardListSequence.add(Gang.class);
        otherNextAndFlowerLessCardListSequence.add(Peng.class);
        otherNextAndFlowerLessCardListSequence.add(Chi.class);

        mineFlowerMoreCardListSequence.add(Hu.class);
        mineFlowerMoreCardListSequence.add(Gang.class);

        mineFlowerLessCardListSequence.add(Gang.class);
    }

    @Override
    public List<Integer> getCards() {
        return cards;
    }

    @Override
    public List<Integer> getFlowers() {
        return flowerCards;
    }

    @Override
    public int getBaidaCard(RuleableGame game) {
        return 0;
    }

    @Override
    public void executeRoundOverProcess(Game game, boolean checkHu) {
        fightService.roundOverQiaoMa(game, checkHu);
    }

    @Override
    public void executeGameOverProcess(Game game) {
        // 红中和百搭差不多
        fightService.gameOverHongZhong(game);
    }

    @Override
    public List<Class<? extends CardList>> getOtherCardListSequence(RoleGameInfo roleGameInfo, Game game) {
        // 如果是听状态
        if (roleGameInfo.isTing) {
            return otherTingCardListSequence;
        }
        // 有牌型可以抓胡和自摸，不受花数的限制
        QiaomaHuTypeResult typeRes = typeCalc.calc(roleGameInfo, game, game.sendCard, false);
        if (typeRes.typeList.size() > 0) {
            return otherNextAndFlowerMoreCardListSequence;
        }
        // 普通胡
        int nextSeat = seatIndexCalc.getNext(game);
        RoleGameInfo nextRoleInfo = roleGameInfoGetter.getRoleGameInfoBySeat(game, nextSeat);
        // 目前有几个花
        int flowerCount = roleGameInfo.flowerCount + getDarkFlowerCount(roleGameInfo.cards);
        // 获得规则
        int needCount = game.getGameConfig().getZhuaFlowerCount();
        if (nextRoleInfo == roleGameInfo) { // 下一家
            if (flowerCount >= needCount) {// 花够了
                return otherNextAndFlowerMoreCardListSequence;
            } else {
                return otherNextAndFlowerLessCardListSequence;
            }
        } else {// 非下一家
            if (flowerCount >= needCount) {
                return otherFlowerMoreCardListSequence;
            } else {
                return otherFlowerLessCardListSequence;
            }
        }
    }

    @Override
    public List<Class<? extends CardList>> getMineCardListSequence(RoleGameInfo roleGameInfo, Game game) {
        // 有牌型或听时
        QiaomaHuTypeResult typeRes = typeCalc.calc(roleGameInfo, game, roleGameInfo.newCard, false);
        if (typeRes.typeList.size() > 0 || roleGameInfo.isTing) {
            return mineFlowerMoreCardListSequence;
        }

        // 目前有几个花
        int flowerCount = roleGameInfo.flowerCount + getDarkFlowerCount(roleGameInfo.cards);
        int needCount = game.getGameConfig().getMoFlowerCount();
        if (flowerCount >= needCount) {
            return mineFlowerMoreCardListSequence;
        } else {
            return mineFlowerLessCardListSequence;
        }
    }

    @Override
    public Map<Class<? extends CardList>, CardList> getCardListMap() {
        return allCardListMap;
    }

    public boolean isFlower(Game game, int newCard) {
        RoleGameInfo roleGameInfo = roleGameInfoGetter.getCurrentRoleGameInfo(game);
        boolean isFlower = cardChecker.isHua(newCard);
        if (isFlower) {
            // 加入花牌集合
            roleGameInfo.sendFlowrCards.add(roleGameInfo.newCard);
            roleGameInfo.flowerCount++;
            roleGameInfo.isGang = true;
        }
        return isFlower;
    }

    /**
     * 获取手牌里超过3个的风向牌的个数
     * 
     * @param roleGameInfo
     * @return
     */
    public int getDarkFlowerCount(List<Integer> cards) {
        int count = 0;
        for (Integer fengCard : fengCards) {
            if (Collections.frequency(cards, fengCard) >= 3) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean isFlower(Integer card) {
        return flowerCards.contains(card);
    }

}
