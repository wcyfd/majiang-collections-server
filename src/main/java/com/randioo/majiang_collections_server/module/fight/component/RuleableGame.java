package com.randioo.majiang_collections_server.module.fight.component;

import java.util.ArrayList;
import java.util.List;

import com.randioo.majiang_collections_server.module.fight.component.MajiangRule.MajiangState;

/**
 * 有规则的游戏
 * 
 * @author wcy 2017年8月21日
 *
 */
public class RuleableGame {

    /** 游戏状态 */
    private List<MajiangState> majiangStateList = new ArrayList<>();
    /** 麻将规则 */
    private MajiangRule rule;

    public List<MajiangState> getMajiangStateList() {
        return majiangStateList;
    }

    public MajiangRule getRule() {
        return rule;
    }

    public void setRule(MajiangRule rule) {
        this.rule = rule;
    }

}
