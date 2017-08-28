/**
 * 
 */
package com.randioo.majiang_collections_server.module.fight.component;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * @Description: 判断单张牌的类型
 * @author zsy
 * @date 2017年8月12日 下午4:04:35
 */
@Component
public class CardChecker {
    public boolean isHua(int card) {
        return BaidaMajiangRule.HUA_CARDS.contains(card / 100);
    }

    public boolean isFeng(int card) {
        return BaidaMajiangRule.Feng_CARDS.contains(card / 100);
    }

    public boolean isContainFlower(List<Integer> cards) {
        for (Integer card : cards) {
            if (isHua(card)) {
                return true;
            }
        }
        return false;
    }

    public int getFlowerCount(List<Integer> cards) {
        int count = 0;
        for (Integer card : cards) {
            if (isHua(card)) {
                count++;
            }
        }
        return count;
    }

}
