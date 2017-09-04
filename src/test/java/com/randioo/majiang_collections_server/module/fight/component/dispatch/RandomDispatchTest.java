package com.randioo.majiang_collections_server.module.fight.component.dispatch;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.randioo.majiang_collections_server.util.CardTools;
import com.randioo.majiang_collections_server.util.Lists;

public class RandomDispatchTest {
    @Test
    public void dispatch() {
        List<Integer> remainCards = new ArrayList<>(CardTools.CARDS.length);
        Lists.fillList(remainCards, CardTools.CARDS);
        RandomDispatcher dispatcher = new RandomDispatcher();
        List<CardPart> list = dispatcher.dispatch(null, remainCards, 4, 13);
        System.out.println(list);
        System.out.println(remainCards);
    }
}
