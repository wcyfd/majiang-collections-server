/**
 * 
 */
package com.randioo.majiang_collections_server.module.fight.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.entity.po.FillFlowerBox;
import com.randioo.majiang_collections_server.entity.po.RoleGameInfo;

/**
 * @Description: 补花
 * @author zsy
 * @date 2017年8月23日 上午9:03:15
 */
@Component
public class FillFlower {
    public FillFlowerBox fill(List<Integer> remainCards, RoleGameInfo roleGameInfo) {
        FillFlowerBox fillFlowerBox = new FillFlowerBox();
        List<Integer> cards = roleGameInfo.cards;
        // 第一次
        List<Integer> flowerCards = removeHua(cards);
        roleGameInfo.sendFlowrCards.addAll(flowerCards);

        fill2(fillFlowerBox, remainCards, flowerCards.size());

        return fillFlowerBox;
    }

    private void fill2(FillFlowerBox fillFlowerBox, List<Integer> remainCards, int needCardCount) {
        // 递归的出口，没有花就结束
        if (needCardCount == 0) {
            return;
        }
        // 新摸那几张牌
        List<Integer> newTouchCards = new ArrayList<Integer>();
        // 摸牌
        for (int i = 0; i < needCardCount; i++) {
            newTouchCards.add(remainCards.remove(0));
        }
        // 加入box
        fillFlowerBox.addCards(newTouchCards);

        // 进入下一轮
        fill2(fillFlowerBox, remainCards, huaCount(newTouchCards));
    }

    /**
     * 牌里有几个花
     * 
     * @param cards
     *            新摸的那几个牌
     * @return
     */
    private int huaCount(List<Integer> cards) {
        int count = 0;
        for (Integer card : cards) {
            if (isHua(card)) {
                count++;
            }
        }
        return count;
    }

    private List<Integer> removeHua(List<Integer> cards) {
        List<Integer> huaCards = new ArrayList<Integer>();
        Iterator<Integer> iterator = cards.iterator();

        while (iterator.hasNext()) {
            Integer card = iterator.next();
            if (isHua(card)) {
                huaCards.add(card);
                iterator.remove();
            }
        }
        return huaCards;
    }

    private boolean isHua(int card) {
        return BaidaMajiangRule.HUA_CARDS.contains(card / 100);
    }

    public void test() {
        List<Integer> cards1 = Arrays.asList(101, 101, 103, 104, 105, 801, 801, 901, 1101);
        List<Integer> remainCards1 = Arrays.asList(106, 1001, 901, 1102, 1103, 1105, 201, 205, 301, 901, 1108, 202, 202,
                308);
        ArrayList<Integer> cards = new ArrayList<Integer>();
        cards.addAll(cards1);
        ArrayList<Integer> remainCards = new ArrayList<Integer>();
        remainCards.addAll(remainCards1);

        // FillFlowerBox box = fill(remainCards, cards);
        // System.out.println(box.getCards());
        // System.out.println(box.getEveryAddCardCountList());
    }
}
