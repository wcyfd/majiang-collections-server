package com.randioo.majiang_collections_server.module.fight.component;

import java.util.List;

import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.randioo_server_base.utils.RandomUtils;

/**
 * 
 * @Description: 产生百搭牌
 * @author zsy
 * @date 2017年8月28日 下午4:58:32
 */
@Component
public class CreateBaiDaCard {

    public void createBaiDaCard(Game game) {
        List<Integer> cards = BaidaMajiangRule.CARDS;
        int index = RandomUtils.getRandomNum(cards.size());
        int card = cards.get(index);
        // 设置第一次产生的百搭牌
        game.setFristBaidaCard(card);

        if (BaidaMajiangRule.HUA_CARDS.contains(card / 100)) {
            card = BaidaMajiangRule.DONG;
        } else if (BaidaMajiangRule.NUM_CARDS.contains(card / 100)) {
            if (card % 10 == 9) {// 如果是9条、桶、万
                card = card - 9 + 1;
            } else {
                card = card + 1;
            }
        } else {// 如果是东南西北风
            if (card == BaidaMajiangRule.BEI) {
                card = BaidaMajiangRule.DONG;
            } else {
                card += 100;
            }
        }
        game.setBaidaCard(card);
    }

    public static void main(String[] args) {
        CreateBaiDaCard obj = new CreateBaiDaCard();
        // obj.createBaiDaCard(null, 501);
    }

}
