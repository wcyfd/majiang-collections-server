/**
 * 
 */
package com.randioo.majiang_collections_server.module.fight.component.calcuator;

import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.module.fight.component.parameter.ScoreParameter;

/**
 * @Description: 计算一局游戏结束时的分数
 * @author zsy
 * @date 2017年7月27日 上午10:23:51
 */
@Component
public class ScoreCalculator {

    public int cal(ScoreParameter parameter) {

        int baseScore = parameter.getBaseScore();
        int fanNum = parameter.getFanNum();
        int huaMultiple = parameter.getHuaMultiple();
        int huaNum = parameter.getHuaNum();
        boolean isHuangFan = parameter.isHuangFan();
        int flyScore = parameter.getFlyScore();
        int limit = parameter.getLimit();

        int score = (baseScore + huaMultiple * huaNum) * (int) Math.pow(2, fanNum) * (isHuangFan ? 2 : 1);
        score += flyScore;
        score = score > limit ? limit : score;
        return score;
    }
}
