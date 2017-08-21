package com.randioo.majiang_collections_server.module.fight.component;

import com.randioo.majiang_collections_server.module.fight.component.MajiangRule.MajiangState;

/**
 * 有规则的游戏
 * 
 * @author wcy 2017年8月21日
 *
 */
public class RuleableGame {
    /** 游戏状态 */
    private MajiangState majiangState;
    /** 麻将规则 */
    private MajiangRule majiangRule;

    public void setMajiangState(MajiangState majiangState) {
        this.majiangState = majiangState;
    }

    public MajiangState getMajiangState() {
        return majiangState;
    }

    public MajiangRule getMajiangRule() {
        return majiangRule;
    }

    public void setMajiangRule(MajiangRule majiangRule) {
        this.majiangRule = majiangRule;
    }

}
