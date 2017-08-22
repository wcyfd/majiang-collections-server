package com.randioo.majiang_collections_server.module.fight.component;

/**
 * 有规则的游戏
 * 
 * @author wcy 2017年8月21日
 *
 */
public class RuleableGame {

    /** 游戏状态 */
    private int stateIndex;
    /** 麻将规则 */
    private MajiangRule rule;

    public MajiangRule getRule() {
        return rule;
    }

    public void setRule(MajiangRule rule) {
        this.rule = rule;
    }

    public int getStateIndex() {
        return stateIndex;
    }

    public void setStateIndex(int stateIndex) {
        this.stateIndex = stateIndex;
    }

}
