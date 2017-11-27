/**
 * 
 */
package com.randioo.majiang_collections_server.module.fight.component.fly;

import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.module.fight.component.BaidaMajiangRule;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 百搭麻将苍蝇生成
 * @author zsy
 * @date 2017年8月29日 上午11:59:58
 */
@Component
public class BaidaFlyCreater {
	public BaidaFlyResult fly(Game game) {
		BaidaFlyResult res = new BaidaFlyResult(0, new ArrayList<Integer>());

		List<Integer> remainCards = game.getRemainCards();
		if (remainCards.size() <= 0) {
			return res;
		}
		Integer card = remainCards.get(0);
		res.getFlys().add(card);
		boolean isFengOrHua = BaidaMajiangRule.Feng_CARDS.contains(card / 100)
				|| BaidaMajiangRule.HUA_CARDS.contains(card / 100);
		// 苍蝇分为苍蝇数乘以底花分
		int baseScore = game.getGameConfig().getHuaScore();
		int fly = 0;
		if (isFengOrHua) {
			fly = 5;
		} else {
			fly = card % 10;
			// 飞到1条1万1筒，变为10花
			fly = fly == 1 ? 10 : fly;
		}
		res.setFlyScore(fly * baseScore);
		return res;
	}
}
