/**
 * 
 */
package com.randioo.majiang_collections_server.entity.po;

import java.util.ArrayList;
import java.util.List;

import com.randioo.majiang_collections_server.module.fight.component.BaidaMajiangRule;

/**
 * @Description:
 * @author zsy
 * @date 2017年8月23日 上午9:35:57
 */
public class FillFlowerBox {
    /** 补的所有牌 */
    private List<Integer> cards = new ArrayList<>();
    /** 每一次补的牌数 */
    private List<Integer> everyAddCardCountList = new ArrayList<>();
    private List<Integer> flowerCards = new ArrayList<>();
    private List<Integer> nomalCards = new ArrayList<>();

    public List<Integer> getFlowerCards() {
        return flowerCards;
    }

    public List<Integer> getNomalCards() {
        return nomalCards;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public List<Integer> getEveryAddCardCountList() {
        return everyAddCardCountList;
    }

    public void addCards(List<Integer> newList) {
        cards.addAll(newList);
        everyAddCardCountList.add(newList.size());

        for (Integer card : newList) {
            if (isHua(card)) {
                flowerCards.add(card);
            } else {
                nomalCards.add(card);
            }
        }
    }

    public List<Integer> getHideCards() {
        List<Integer> hideCards = new ArrayList<>();
        for (Integer card : cards) {
            if (isHua(card)) {
                hideCards.add(0);
            } else {
                hideCards.add(card);
            }
        }
        return hideCards;
    }

    private boolean isHua(int card) {
        return BaidaMajiangRule.HUA_CARDS.contains(card / 100);
    }

}
