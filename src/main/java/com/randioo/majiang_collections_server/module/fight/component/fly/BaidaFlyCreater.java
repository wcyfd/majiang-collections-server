/**
 * 
 */
package com.randioo.majiang_collections_server.module.fight.component.fly;

import java.util.List;

import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.module.fight.component.BaidaMajiangRule;

/**
 * @Description: 百搭麻将苍蝇生成
 * @author zsy
 * @date 2017年8月29日 上午11:59:58
 */
@Component
public class BaidaFlyCreater {
    public BaidaFlyResult fly(Game game) {
        BaidaFlyResult res = new BaidaFlyResult(0, 0);

        List<Integer> remainCards = game.getRemainCards();
        if (remainCards.size() <= 0) {
            return res;
        }
        Integer card = remainCards.remove(0);
        res.setFlys(card);
        boolean isFengOrHua = BaidaMajiangRule.Feng_CARDS.contains(card / 100)
                || BaidaMajiangRule.HUA_CARDS.contains(card / 100);
        if (isFengOrHua) {
            res.setFlyScore(5);
        } else {
            res.setFlyScore(card % 10);
        }
        return res;
    }
}
