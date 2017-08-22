package com.randioo.majiang_collections_server.module.fight.component;

import org.springframework.stereotype.Component;

@Component
public class Processor {
    /**
     * 执行下一个动作
     * 
     * @param game
     * @author wcy 2017年8月22日
     */
    public void nextProcess(RuleableGame game) {
        MajiangRule majiangRule = game.getRule();
        majiangRule.executeNextProcess(game);
    }
}
