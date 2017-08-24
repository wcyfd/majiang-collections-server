package com.randioo.majiang_collections_server.module.fight.component;

import java.util.ArrayList;
import java.util.List;

/**
 * 有规则的游戏
 * 
 * @author wcy 2017年8月21日
 *
 */
public class RuleableGame {

    /** 游戏状态 */
    private List<Integer> flows = new ArrayList<>();
    /** 麻将规则 */
    private MajiangRule rule;

    public MajiangRule getRule() {
        return rule;
    }

    public void setRule(MajiangRule rule) {
        this.rule = rule;
    }

    public List<Integer> getFlows() {
        return flows;
    }

}
