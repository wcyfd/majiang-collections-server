package com.randioo.majiang_collections_server.module.fight.component.cardlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.randioo.majiang_collections_server.entity.po.CardSort;
import com.randioo.majiang_collections_server.util.Lists;

/**
 * 凯恩发明，基于五步胡拓展成百搭胡
 * 
 * @author wcy 2017年9月12日
 *
 */
public class SuperHu {
    public static boolean checkHu(CardSort cardSort, int baidaCard) {
        if ((cardSort.sumCard() - 2) % 3 != 0) {
            return false;
        }

        CardSort copySort = cardSort.clone();
        // 移除所有百搭牌
        int baidaCount = copySort.removeAll(baidaCard);

        // 所有可能作为将牌的麻将牌
        Set<Integer> setVec0 = copySort.get(0);
        Set<Integer> setVec1 = copySort.get(1);

        List<Integer> cards;

        // 0百搭将牌
        for (int jiang : setVec1) {
            cards = copySort.toArray();
            removeJiang2(cards, jiang);
            Collections.sort(cards);
            if (gather3(cards, baidaCount)) {
                return true;
            }

            cards = copySort.toArray();
            removeJiang2(cards, jiang);
            negate(cards);
            if (gather3(cards, baidaCount)) {
                return true;
            }
        }

        // 1百搭将牌
        for (int jiang : setVec0) {

            cards = copySort.toArray();
            removeJiang1(cards, jiang);
            Collections.sort(cards);
            if (gather3(cards, baidaCount - 1)) {
                return true;
            }

            cards = copySort.toArray();
            removeJiang1(cards, jiang);
            negate(cards);
            if (gather3(cards, baidaCount - 1)) {
                return true;
            }
        }

        // 2百搭将牌
        cards = copySort.toArray();
        Collections.sort(cards);
        if (gather3(cards, baidaCount - 2)) {
            return true;
        }

        cards = copySort.toArray();
        negate(cards);
        if (gather3(cards, baidaCount - 2)) {
            return true;
        }

        return false;
    }

    private static boolean gather3(List<Integer> cards, int baidaCount) {
        while (true) {
            // 检查胡牌
            if (cards.size() == 0) {
                return true;
            }

            // 0百搭凑克子顺子
            if (gatherKe(cards, 0)) {
                continue;
            }
            if (gatherShun(cards, 0)) {
                continue;
            }

            if (baidaCount >= 1) {
                // 1百搭凑克子顺子
                if (gatherKe(cards, 1)) {
                    baidaCount -= 1;
                    continue;
                }
                if (gatherShun(cards, 1)) {
                    baidaCount -= 1;
                    continue;
                }

                if (baidaCount >= 2) {
                    // 2百搭凑克子顺子
                    if (gatherKe(cards, 2)) {
                        baidaCount -= 2;
                        continue;
                    }
                    if (gatherShun(cards, 2)) {
                        baidaCount -= 2;
                        continue;
                    }
                }
            }

            break;
        }

        return false;
    }
    
    /**
     * 移除将牌
     */
    private static void removeJiang2(List<Integer> cards, int jiang) {
        cards.remove(cards.indexOf(jiang));
        cards.remove(cards.indexOf(jiang));
    }
    
    /**
     * 移除将牌（另一张百搭）
     */
    private static void removeJiang1(List<Integer> cards, int jiang) {
        cards.remove(cards.indexOf(jiang));
    }

    /**
     * 凑克子，若数组第一张牌无法与其它牌组成克子，则返回false
     */
    private static boolean gatherKe(List<Integer> cards, int baidaCount) {
        // 缺牌数量
        int lackCount = 2;
        if (cards.size() >= 2 && (int) cards.get(0) == (int) cards.get(1)) {
            lackCount--;
        }
        if (cards.size() >= 3 && (int) cards.get(0) == (int) cards.get(2)) {
            lackCount--;
        }

        // 当缺牌数量大于剩余百搭时不可胡
        if (lackCount > baidaCount) {
            return false;
        }

        for (; lackCount < 3; lackCount++) {
            cards.remove(0);
        }

        return true;
    }

    /**
     * 凑顺子，若数组第一张牌无法与其它牌组成顺子，则返回false
     */
    private static boolean gatherShun(List<Integer> cards, int baidaCount) {
        // 数组中可能组成顺子的元素集合
        int firstCard = cards.get(0);
        int secondCard = -1;
        List<Integer> shunCards = new ArrayList<>();
        shunCards.add(firstCard);
        for (int i = 1; i < cards.size(); i++) {
            int curCard = cards.get(i);
            if (curCard - firstCard > 2)
                break;
            if (curCard - firstCard == 2) {
                shunCards.add(curCard);
                break;
            }
            if (secondCard == -1 && curCard - firstCard == 1) {
                shunCards.add(curCard);
                secondCard = curCard;
            }
        }

        // 缺牌数量
        int lackCount = 2;
        if (shunCards.size() >= 2)
            lackCount--;
        if (shunCards.size() >= 3)
            lackCount--;

        // 当缺牌数量大于剩余百搭时不可胡
        if (lackCount > baidaCount)
            return false;

        Lists.removeElementByList(cards, shunCards);

        return true;
    }

    /**
     * 取反
     */
    private static void negate(List<Integer> cards) {
        for (int i = 0; i < cards.size(); i++) {
            cards.set(i, -cards.get(i));
        }
        
        Collections.sort(cards);
        
//        int lenMinusOne = cards.size() - 1;
//        int half = cards.size() / 2;
//        for (int i = 0; i < half; i++) {
//            int temp = cards.get(i);
//            cards.set(i, -cards.get(lenMinusOne - i));
//            cards.set(lenMinusOne - i, -temp);
//        }
//        if (lenMinusOne >= 0 && lenMinusOne % 2 == 0) {
//            cards.set(half, -cards.get(half));
//        }
    }

}
