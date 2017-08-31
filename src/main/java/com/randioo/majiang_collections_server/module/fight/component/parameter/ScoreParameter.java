/**
 * 
 */
package com.randioo.majiang_collections_server.module.fight.component.parameter;

/**
 * @Description:
 * @author zsy
 * @date 2017年7月27日 下午12:01:46
 */
public class ScoreParameter {
    private int baseScore;
    private int huaMultiple;
    private int fanNum;
    private int huaNum;
    private boolean isHuangFan;
    private int flyScore; // 苍蝇数
    private int limit;// 封顶

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getBaseScore() {
        return baseScore;
    }

    public void setBaseScore(int baseScore) {
        this.baseScore = baseScore;
    }

    public int getHuaMultiple() {
        return huaMultiple;
    }

    public void setHuaMultiple(int huaMultiple) {
        this.huaMultiple = huaMultiple;
    }

    public int getFanNum() {
        return fanNum;
    }

    public void setFanNum(int fanNum) {
        this.fanNum = fanNum;
    }

    public int getHuaNum() {
        return huaNum;
    }

    public void setHuaNum(int huaNum) {
        this.huaNum = huaNum;
    }

    public boolean isHuangFan() {
        return isHuangFan;
    }

    public void setHuangFan(boolean isHuangFan) {
        this.isHuangFan = isHuangFan;
    }

    public int getFlyScore() {
        return flyScore;
    }

    public void setFlyScore(int flyScore) {
        this.flyScore = flyScore;
    }

}
