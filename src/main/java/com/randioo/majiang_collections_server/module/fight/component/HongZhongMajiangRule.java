package com.randioo.majiang_collections_server.module.fight.component;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Gang;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Hu;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.NewHu;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Peng;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.hongzhong.HongzhongGang;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.hongzhong.HongzhongHu;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.hongzhong.HongzhongPeng;
import com.randioo.majiang_collections_server.module.fight.service.FightService;
import com.randioo.randioo_server_base.utils.ReflectUtils;

/**
 * 红中麻将规则
 * 
 * @author wcy 2017年8月21日
 *
 */

@Component
public class HongZhongMajiangRule extends MajiangRule {

    @Autowired
    private FightService fightService;

    @Autowired
    private RoleGameInfoGetter roleGameInfoGetter;

    private final List<Integer> cards = Arrays.asList(
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

            801, 801, 801, 801// 中
            );

    private List<Integer> flowers = Arrays.asList();

    private int hongzhongCard;

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
            if (game.getRemainCards().size() == 0) {
                operations.clear();
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
            list.add(MajiangStateEnum.STATE_CHECK_ZHUANG);
            list.add(MajiangStateEnum.STATE_DISPATCH);
            list.add(MajiangStateEnum.STATE_SC_GAME_START);
            list.add(MajiangStateEnum.STATE_TOUCH_CARD);
            list.add(MajiangStateEnum.STATE_SC_TOUCH_CARD);
            list.add(MajiangStateEnum.STATE_CHECK_MINE_CARDLIST);
            list.addAll(sendCardProcesses);
            list.add(MajiangStateEnum.STATE_NEXT_SEAT);

            break;
        case STATE_TOUCH_CARD: {
            RoleGameInfo currentRoleGameInfo = roleGameInfoGetter.getCurrentRoleGameInfo(game);
            for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
                if (currentRoleGameInfo.gameRoleId.equals(roleGameInfo.gameRoleId)) {
                    continue;
                }
                roleGameInfo.everybodyTouchCard = 0;
            }
        }
            break;
        case STATE_ROLE_SEND_CARD: {// 出牌后
            list.add(MajiangStateEnum.STATE_CHECK_OTHER_CARDLIST);
            game.checkOtherCardListSeats.clear();
            int seat = game.getCurrentRoleIdIndex();
            for (int i = 0; i < game.getRoleIdList().size(); i++) {
                if (seat != i) {
                    game.checkOtherCardListSeats.add(i);
                }
            }
        }
            break;
        case STATE_CHECK_MINE_CARDLIST:
            if (game.getCallCardLists().size() > 0) {
                list.addAll(noticeCardListProcesses);
            }
            break;
        case STATE_CHECK_MINE_CARDLIST_OUTER: {
            if (game.getCallCardLists().size() > 0) {
                list.addAll(noticeCardListProcesses);
            } else {
                RoleGameInfo roleGameInfo = roleGameInfoGetter.getCurrentRoleGameInfo(game);
                int size = roleGameInfo.cards.size();
                int cardListSize = roleGameInfo.showCardLists.size();
                int totalSize = size + cardListSize * 3;
                // 牌数量不够，就要继续摸牌，否则直接出牌
                if (totalSize < 14) {
                    list.addAll(touchCardProcesses);
                } else {
                    list.addAll(sendCardProcesses);
                }
            }
        }
            break;
        case STATE_CHECK_OTHER_CARDLIST:
            if (game.getCallCardLists().size() > 0) {
                list.addAll(noticeCardListProcesses);
            }
            break;
        case STATE_NEXT_SEAT: {
            // 没有花进入的下一个人则视为上家出牌没有人要杠碰吃胡
            // 直接重置
            game.sendCard = 0;
            game.sendCardSeat = 0;

            list.addAll(touchCardProcesses);
            list.addAll(sendCardProcesses);
            list.add(MajiangStateEnum.STATE_NEXT_SEAT);
        }
            break;
        case STATE_ROLE_CHOSEN_CARDLIST: {
            // 获得第一个人的卡组
            List<CallCardList> callCardLists = game.getCallCardLists();
            if (callCardLists.size() > 0) {
                CallCardList callCardList = game.getCallCardLists().get(0);
                CardList cardList = callCardList.cardList;

                if (cardList instanceof Peng) {
                    list.add(MajiangStateEnum.STATE_PENG);
                    // 如果需要跳转,则要填上出牌流程，实质上和下一家的流程差不多
                    list.add(MajiangStateEnum.STATE_JUMP_SEAT);
                    list.addAll(sendCardProcesses);
                } else if (cardList instanceof Gang) {
                    Gang gang = (Gang) cardList;

                    int seat = callCardList.masterSeat;
                    RoleGameInfo roleGameInfo = roleGameInfoGetter.getRoleGameInfoBySeat(game, seat);
                    game.checkOtherCardListSeats.clear();
                    for (int i = 0; i < game.getRoleIdList().size(); i++) {
                        if (seat != i) {
                            game.checkOtherCardListSeats.add(i);
                        }
                    }
                    if (gang.peng != null && !hasHongzhong(game, roleGameInfo, 0) && fightService.checkQiangGang(game)) {
                        // 显示已经杠了
                        list.add(MajiangStateEnum.STATE_GANG);
                        list.addAll(noticeCardListProcesses);
                    } else {
                        list.add(MajiangStateEnum.STATE_GANG);
                        // 如果需要跳转,则要填上出牌流程，实质上和下一家的流程差不多
                        if (game.getCurrentRoleIdIndex() != callCardList.masterSeat) {
                            list.add(MajiangStateEnum.STATE_JUMP_SEAT);
                            list.add(MajiangStateEnum.STATE_GANG_CAL_SCORE);
                            list.addAll(touchCardProcesses);
                            list.addAll(sendCardProcesses);
                        } else {
                            list.add(MajiangStateEnum.STATE_GANG_CAL_SCORE);
                            list.addAll(touchCardProcesses);
                        }

                    }
                } else if (cardList instanceof Hu) {
                    // 如果前面是抢杠,则还原杠变为碰
                    if (game.qiangGangCallCardList != null) {
                        list.add(MajiangStateEnum.STATE_RECOVERY_PENG);
                    }
                    list.add(MajiangStateEnum.STATE_HU);
                }
            } else {
                if (game.qiangGangCallCardList != null) {
                    // 如果需要跳转,则要填上出牌流程，实质上和下一家的流程差不多
                    list.add(MajiangStateEnum.STATE_GANG_CAL_SCORE);
                    list.addAll(touchCardProcesses);
                    game.qiangGangCallCardList = null;
                }
            }
            break;
        }
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

    /** 所有的牌型 */
    private Map<Class<? extends CardList>, CardList> allCardListMap = new HashMap<>();

    private List<Class<? extends CardList>> mineCardListSequence = new ArrayList<>();
    private List<Class<? extends CardList>> otherCardListSequence = new ArrayList<>();

    private List<Class<? extends CardList>> moCardListSequence = new ArrayList<>(2);

    public HongZhongMajiangRule() {
        allCardListMap.put(Gang.class, ReflectUtils.newInstance(HongzhongGang.class));
        allCardListMap.put(Peng.class, ReflectUtils.newInstance(HongzhongPeng.class));
        allCardListMap.put(Hu.class, ReflectUtils.newInstance(HongzhongHu.class));
        allCardListMap.put(Hu.class, ReflectUtils.newInstance(NewHu.class));

        otherCardListSequence.add(Hu.class);
        otherCardListSequence.add(Gang.class);
        otherCardListSequence.add(Peng.class);

        moCardListSequence.add(Gang.class);
        moCardListSequence.add(Peng.class);

        mineCardListSequence.add(Hu.class);
        mineCardListSequence.add(Gang.class);

        hongzhongCard = 801;
    }

    @Override
    public List<Integer> getCards() {
        return cards;
    }

    @Override
    public List<Integer> getFlowers() {
        return flowers;
    }

    @Override
    public int getBaidaCard(RuleableGame game) {
        // 红中为百搭牌
        return hongzhongCard;
    }

    @Override
    public List<Class<? extends CardList>> getOtherCardListSequence(RoleGameInfo roleGameInfo, Game game) {
        boolean zhuaHu = game.getGameConfig().getZhuaHu();
        // 能否抓胡判断
        if (!zhuaHu) {
            return moCardListSequence;
        }

        if (hasHongzhong(game, roleGameInfo, game.sendCard)) {
            return moCardListSequence;
        }
        return otherCardListSequence;

    }

    private boolean hasHongzhong(Game game, RoleGameInfo roleGameInfo, int card) {

        if (hongzhongCard == card || roleGameInfo.cards.contains(hongzhongCard)) {
            return true;
        }

        return false;
    }

    @Override
    public List<Class<? extends CardList>> getMineCardListSequence(RoleGameInfo roleGameInfo, Game game) {
        return mineCardListSequence;
    }

    @Override
    public Map<Class<? extends CardList>, CardList> getCardListMap() {
        return allCardListMap;
    }

    @Override
    public void executeRoundOverProcess(Game game, boolean checkHu) {
        fightService.roundOverHongZhong(game, checkHu);
    }

    @Override
    public void executeGameOverProcess(Game game) {
        fightService.gameOverHongZhong(game);
    }

    @Override
    public boolean isFlower(Integer card) {
        return false;
    }

}
