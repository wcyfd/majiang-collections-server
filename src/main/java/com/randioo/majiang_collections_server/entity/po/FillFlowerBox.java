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
    private List<List<Integer>> cardList = new ArrayList<>();

    private List<Integer> flowerCards = new ArrayList<>();
    private List<Integer> nomalCards = new ArrayList<>();

    public List<List<Integer>> getCardList() {
        return cardList;
    }

    public List<Integer> getFlowerCards() {
        return flowerCards;
    }

    public List<Integer> getNomalCards() {
        return nomalCards;
    }

    public void addLine(List<Integer> newList) {
        this.cardList.add(newList);
        for (Integer card : newList) {
            if (isHua(card)) {
                flowerCards.add(card);
            } else {
                nomalCards.add(card);
            }
        }
    }

    public List<Integer> getLine(int index) {
        return cardList.get(index);
    }

    public List<Integer> getHideCards(int index) {
        List<Integer> hideCards = new ArrayList<>();
        List<Integer> onLine = cardList.get(index);
        for (Integer card : onLine) {
            if (isHua(card)) {
                hideCards.add(card);
            } else {
                hideCards.add(0);
            }
        }
        return hideCards;
    }

    private boolean isHua(int card) {
        return BaidaMajiangRule.HUA_CARDS.contains(card / 100);
    }

}
