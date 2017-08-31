package com.randioo.majiang_collections_server.module.fight.component.calcuator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.entity.po.CardSort;
import com.randioo.majiang_collections_server.module.fight.component.BaidaMajiangRule;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.CardList;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Gang;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Peng;
import com.randioo.majiang_collections_server.module.fight.component.parameter.HuaParameter;

/**
 * @Description:计算花的数量
 * @author zsy
 * @date 2017年7月27日 上午10:12:11
 */
@Component
public class HuaNumberCalculator {

    public int cal(HuaParameter parameter) {

        List<CardList> showCard = parameter.getShowCard();
        List<Integer> huaCards = parameter.getHuaCards();
        List<Integer> handCard = parameter.getHandCard();
        int baida = parameter.getBaida();

        CardSort cardSort = new CardSort(4);
        cardSort.fillCardSort(handCard);
        int number = 0;

        Set<Integer> set = cardSort.get(2);
        // 判断手牌里的东南西北刻字
        for (Integer card : set) {
            if (BaidaMajiangRule.Feng_CARDS.contains(card / 100))
                number += 1;
        }
        for (CardList item : showCard) {
            // 判断风向碰
            if (item instanceof Peng) {
                Peng peng = (Peng) item;
                if (BaidaMajiangRule.Feng_CARDS.contains(peng.card / 100))
                    number += 1;
            }
            if (item instanceof Gang) {
                Gang gang = (Gang) item;
                if (BaidaMajiangRule.Feng_CARDS.contains(gang.card / 100)) { // 判断风向杠
                    if (gang.dark)
                        number += 3;
                    else
                        number += 2;
                } else {
                    if (gang.dark)
                        number += 2;
                    else
                        number += 1;
                }
            }

        }

        number += huaCards.size();
        number += Collections.frequency(handCard, baida);
        System.out.println("hua number:" + number);
        return number;
    }

    public static void main(String[] args) {
        List<CardList> showCardList = new ArrayList<CardList>();

        Peng peng = new Peng();
        // peng.card = 501;
        Gang gang = new Gang();
        gang.card = 401;
        gang.dark = true;

        Gang gang1 = new Gang();
        gang1.card = 401;
        gang1.dark = false;

        showCardList.add(peng);
        // showCardList.add(gang);
        // showCardList.add(gang1);

        CardSort handCard = new CardSort(4);
        handCard.fillCardSort(Arrays.asList(401, 401, 101));

        HuaParameter parameter = new HuaParameter();
        // parameter.showCard = showCardList;
        // parameter.huaCards = Arrays.asList();
        // parameter.handCard = handCard;
        HuaNumberCalculator calculator = new HuaNumberCalculator();
        System.out.println(calculator.cal(parameter));

    }

}
