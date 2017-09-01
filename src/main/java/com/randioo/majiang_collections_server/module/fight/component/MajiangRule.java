package com.randioo.majiang_collections_server.module.fight.component;

import java.util.List;
import java.util.Map;

import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.po.RoleGameInfo;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.CardList;

/**
 * 麻将规则
 * 
 * @author wcy 2017年8月21日
 *
 */
public abstract class MajiangRule {

    /**
     * 麻将状态
     * 
     * @author wcy 2017年8月21日
     *
     */
    public enum MajiangStateEnum {
        /** 摸花 */
        STATE_TOUCH_FLOWER,
        /** 补花 */
        STATE_ADD_FLOWERS,
        /** 碰杠时花分数变化 */
        STATE_FLOWER_SCORE_CHANGE,
        /** 百搭麻将初始化：产生百搭牌，确定荒番 */
        STATE_BAIDA_INIT,
        /** 敲麻初始化 */
        STATE_QIAOMA_INIT,
        /** 进入听状态 */
        STATE_TING,

        /** 通知游戏准备 */
        STATE_INIT_READY,
        /** 游戏准备 */
        STATE_ROLE_GAME_READY,
        /** 游戏开始 */
        STATE_GAME_START,
        /** 检查庄家 */
        STATE_CHECK_ZHUANG,
        /** 发牌 */
        STATE_DISPATCH,
        /** 通知游戏开始 */
        STATE_SC_GAME_START,
        /** 通知出牌 */
        STATE_SC_SEND_CARD,
        /** 出牌 */
        STATE_GAME_SEND_CARD,
        /** 摸牌 */
        STATE_TOUCH_CARD,
        /** 通知摸到的牌 */
        STATE_SC_TOUCH_CARD,
        /** 检查别人的杠碰吃胡 */
        STATE_CHECK_OTHER_CARDLIST,
        /** 检查自己的杠碰吃胡 */
        STATE_CHECK_MINE_CARDLIST,
        /** 检查自己的杠碰吃胡（外检） */
        STATE_CHECK_MINE_CARDLIST_OUTER,
        /** 通知卡组到玩家 */
        STATE_SC_SEND_CARDLIST_2_ROLE,
        /** 回合结束 */
        STATE_ROUND_OVER,
        /** 游戏结束 */
        STATE_GAME_OVER,

        /** 吃 */
        STATE_CHI,
        /** 碰 */
        STATE_PENG,
        /** 杠 */
        STATE_GANG,
        /** 杠算分 */
        STATE_GANG_CAL_SCORE,
        /** 胡 */
        STATE_HU,

        /** 抢杠重新变为碰 */
        STATE_RECOVERY_PENG,

        /** 下一个人 */
        STATE_NEXT_SEAT,
        /** 跳转到一个人 */
        STATE_JUMP_SEAT,
        /** 等待操作 */
        STATE_WAIT_OPERATION,

        /** 玩家选择了卡组 */
        STATE_ROLE_CHOSEN_CARDLIST,
        /** 玩家出牌 */
        STATE_ROLE_SEND_CARD,
    }

    /**
     * 获得所有牌
     * 
     * @return
     * @author wcy 2017年8月21日
     */
    public abstract List<Integer> getCards();

    public abstract List<Integer> getFlowers();

    /**
     * 获得百搭牌
     * 
     * @return
     * @author wcy 2017年8月22日
     */
    public int getBaidaCard(RuleableGame game) {
        return 0;
    }

    /**
     * 
     * @return
     * @author wcy 2017年8月21日
     */
    public abstract List<Class<? extends CardList>> getOtherCardListSequence(RoleGameInfo roleGameInfo, Game game);

    /**
     * 
     * @return
     * @author wcy 2017年8月21日
     */
    public abstract List<Class<? extends CardList>> getMineCardListSequence(RoleGameInfo roleGameInfo, Game game);

    public abstract Map<Class<? extends CardList>, CardList> getCardListMap();

    /**
     * 执行前的处理
     * 
     * @param ruleGame
     * @param majiangStateEnum
     * @param currentSeat
     * @return
     */
    public abstract List<MajiangStateEnum> beforeStateExecute(RuleableGame ruleableGame,
            MajiangStateEnum majiangStateEnum, int currentSeat);

    /**
     * 
     * @param majiangState
     * @return
     * @author wcy 2017年8月21日
     */
    public abstract List<MajiangStateEnum> afterStateExecute(RuleableGame ruleableGame,
            MajiangStateEnum majiangStateEnum, int currentSeat);

    /**
     * 回合结束
     * 
     * @param game
     * @author wcy 2017年8月28日
     */
    public abstract void executeRoundOverProcess(Game game, boolean checkHu);

    /**
     * 游戏结束
     * 
     * @param game
     * @author wcy 2017年8月28日
     */
    public abstract void executeGameOverProcess(Game game);

    public abstract boolean isFlower(Integer card);

}
