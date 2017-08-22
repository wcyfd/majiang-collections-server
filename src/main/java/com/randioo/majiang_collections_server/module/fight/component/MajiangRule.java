package com.randioo.majiang_collections_server.module.fight.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.CardList;
import com.randioo.majiang_collections_server.util.DefaultObservePattern;

/**
 * 麻将规则
 * 
 * @author wcy 2017年8月21日
 *
 */
public abstract class MajiangRule extends DefaultObservePattern {

    /**
     * 麻将状态
     * 
     * @author wcy 2017年8月21日
     *
     */
    public enum MajiangState {
        /** 通知游戏准备 */
        STATE_NOTICE_READY,
        /** 游戏准备 */
        STATE_GAME_READY,
        /** 游戏开始 */
        STATE_GAME_START,
        /** 游戏初始化 */
        STATE_GAME_INIT,
        /** 检查庄家 */
        STATE_CHECK_ZHUANG,
        /** 发牌 */
        STATE_DISPATCH,
        /** 通知游戏开始 */
        STATE_NOTICE_GAME_START,
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
        STATE_HU;
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
    public int getBaidaCard() {
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

    /**
     * 执行下一条流程
     * 
     * @param ruleableGame
     * @author wcy 2017年8月21日
     */
    public void executeNextProcess(RuleableGame game) {
        this.execute(game);

        MajiangState state = this.getCurrentState(game);

        this.notifyObservers(state.toString(), game);
    }

    /**
     * 
     * @param majiangState
     * @return
     * @author wcy 2017年8月21日
     */
    protected abstract void execute(RuleableGame ruleableGame);

    protected abstract MajiangState getCurrentState(RuleableGame ruleableGame);

}
