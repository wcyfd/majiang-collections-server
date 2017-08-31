/**
 * 
 */
package com.randioo.majiang_collections_server.module.fight.component.fly;

/**
 * @Description: 百搭麻将飞苍蝇结果
 * @author zsy
 * @date 2017年8月29日 上午11:58:36
 */
public class BaidaFlyResult {
    private int flyScore;
    /** 哪张牌 */
    private int flys;

    public BaidaFlyResult(int flyScore, int fly) {
        this.flys = fly;
        this.flyScore = flyScore;
    }

    public int getFlyScore() {
        return flyScore;
    }

    public void setFlyScore(int flyScore) {
        this.flyScore = flyScore;
    }

    public int getFlys() {
        return flys;
    }

    public void setFlys(int fly) {
        this.flys = fly;
    }

}
