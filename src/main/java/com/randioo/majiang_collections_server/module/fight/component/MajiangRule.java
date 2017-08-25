package com.randioo.majiang_collections_server.module.fight.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.randioo.majiang_collections_server.entity.bo.Game;
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
        /** 通知游戏准备 */
        STATE_INIT_READY,
        /** 游戏准备 */
        STATE_GAME_READY,
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
        /** 消费燃点币 */
        STATE_CONSUME_MONEY,
        /** 增加燃点活动点数 */
        STATE_ADD_RANDIOO_ACTIVE,
        /** 杠 */
        STATE_GANG,
        /** 碰 */
        STATE_PENG,
        /** 吃 */
        STATE_CHI,
        /** 胡 */
        STATE_HU,
        /** 过 */
        STATE_GUO,
        /** 玩家选择杠碰胡吃 */
        STATE_ROLE_CHOSEN_CARDLIST,
        /** 下一个人 */
        STATE_NEXT_SEAT,
        /** 跳转到一个人 */
        STATE_JUMP_SEAT,
        /** 等待操作 */
        STATE_WAIT_OPERATION,
        /** 玩家出牌 */
        STATE_ROLE_SEND_CARD,
        /** 补花 */
        STATE_ADD_FLOWERS
    }

    /** 所有的牌型 */
    protected Map<Class<? extends CardList>, CardList> allCardListMap = new HashMap<>();

    protected List<Class<? extends CardList>> mineCardListSequence = new ArrayList<>();
    protected List<Class<? extends CardList>> otherCardListSequence = new ArrayList<>();

    protected List<Class<? extends CardList>> gangCardListSequence = new ArrayList<>();

    /**
     * 获得所有牌
     * 
     * @return
     * @author wcy 2017年8月21日
     */
    public abstract int[] getCards();

    /**
     * 能否抓胡
     * 
     * @return
     * @author wcy 2017年8月21日
     */
    public boolean canZhuaHu(Game game) {
        return true;
    }

    /**
     * 能否白搭抓胡
     * 
     * @return
     * @author wcy 2017年8月21日
     */
    public boolean canBaiDaZhuaHu(Game game) {
        return true;
    }

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
    public List<Class<? extends CardList>> getOtherCardListSequence() {
        return otherCardListSequence;
    }

    /**
     * 
     * @return
     * @author wcy 2017年8月21日
     */
    public List<Class<? extends CardList>> getMineCardListSequence() {
        return mineCardListSequence;
    }

    // /**
    // * 执行下一条流程
    // *
    // * @param ruleableGame
    // * @author wcy 2017年8月21日
    // */
    // public void execute(RuleableGame game, int currentSeat) {
    // // List<Integer> flows = game.getFlows();
    // // // 流程数量为0直接跳出
    // // if (flows.size() == 0) {
    // // return;
    // // }
    // // int flowId = flows.remove(0);
    // // this.execute(game, flowId, currentSeat);
    // //
    // // MajiangStateEnum state = this.getCurrentState(flowId);
    // //
    // // this.notifyObservers(state.toString(), game);
    //
    // }

    public abstract Set<Integer> getFlowers(Game game);

    /**
     * 
     * @param majiangState
     * @return
     * @author wcy 2017年8月21日
     */
    public abstract List<MajiangStateEnum> afterStateExecute(RuleableGame ruleableGame,
            MajiangStateEnum majiangStateEnum, int currentSeat);

    /**
     * 游戏结束流程
     * 
     * @return
     * @author wcy 2017年8月25日
     */
    public abstract List<MajiangStateEnum> getOverProcess();
}
