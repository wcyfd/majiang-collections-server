package com.randioo.majiang_collections_server.module.fight.component;

import org.springframework.stereotype.Component;

/**
 * 流程控制器
 * 
 * @author wcy 2017年8月24日
 *
 */
@Component
public class Processor {
    /**
     * 执行下一个动作
     * 
     * @param game
     * @author wcy 2017年8月22日
     */
    public void nextProcess(RuleableGame game) {
        this.nextProcess(game, -1);
    }

    /**
     * 执行下一个动作
     * 
     * @param game
     * @param currentSeat 执行者的座位
     * @author wcy 2017年8月23日
     */
    public void nextProcess(RuleableGame game, int currentSeat) {
        MajiangRule majiangRule = game.getRule();
        majiangRule.execute(game, currentSeat);
    }

}
