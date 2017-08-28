package com.randioo.majiang_collections_server.module.fight.component.dispatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.util.CardTools;
import com.randioo.majiang_collections_server.util.Lists;

@Component
public class RandomDispatcher implements Dispatcher {

    @Override
    public List<CardPart> dispatch(Game game, List<Integer> cards, int partCount, int everyPartCount) {
        // 打乱牌的顺序
        Collections.shuffle(cards);

        List<CardPart> cardParts = new ArrayList<>(partCount);
        for (int i = 0; i < partCount; i++) {
            CardPart cardPart = new CardPart();
            cardParts.add(cardPart);
            for (int j = 0; j < everyPartCount; j++) {
                int card = cards.remove(j);
                cardPart.add(card);
            }
        }
        return cardParts;
    }

    public static void main(String[] args) {
        List<Integer> remainCards = new ArrayList<>(CardTools.CARDS.length);
        Lists.fillList(remainCards, CardTools.CARDS);
        RandomDispatcher dispatcher = new RandomDispatcher();
        List<CardPart> list = dispatcher.dispatch(null, remainCards, 4, 13);
        System.out.println(list);
        System.out.println(remainCards);
    }

}
