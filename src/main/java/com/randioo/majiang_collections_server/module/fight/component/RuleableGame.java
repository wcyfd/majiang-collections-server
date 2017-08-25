package com.randioo.majiang_collections_server.module.fight.component;

import java.util.Stack;

import com.randioo.majiang_collections_server.module.fight.component.MajiangRule.MajiangStateEnum;

/**
 * 有规则的游戏
 * 
 * @author wcy 2017年8月21日
 *
 */
public class RuleableGame {

    /** 游戏状态 */
    private Stack<MajiangStateEnum> operations = new Stack<>();
    /** 麻将规则 */
    private MajiangRule rule;

    public MajiangRule getRule() {
        return rule;
    }

    public void setRule(MajiangRule rule) {
        this.rule = rule;
    }

    /**
     * 
     * @return
     * @author wcy 2017年8月25日
     */
    public Stack<MajiangStateEnum> getOperations() {
        return operations;
    }

}
