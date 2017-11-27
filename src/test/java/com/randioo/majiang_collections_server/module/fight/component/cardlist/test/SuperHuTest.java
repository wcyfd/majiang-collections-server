package com.randioo.majiang_collections_server.module.fight.component.cardlist.test;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.randioo.majiang_collections_server.entity.po.CardSort;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.SuperHu;

public class SuperHuTest {
    @Test
    public void superHuTest() {
        // List<Integer> cards = Lists.newArrayList(103, 104, 104, 104, 105,
        // 106, 106, 106, 107, 107, 108, 109, 801, 801);
        List<Integer> cards = Lists.newArrayList(801, 801, 801, 801,101,104,105,105,107,109,307,308,309,109);
//        List<Integer> cards = Lists.newArrayList(101, 106, 106, 701, 201);
        CardSort cardSort = new CardSort(5);
        cardSort.fillCardSort(cards);
        boolean result = SuperHu.checkHu(cardSort, 801);
        System.out.println(result);
        
//        101,103,104,105,108,201,203,208,303,304,306,309,309
//        801,801,801,101,104,105,107,301,109,204,307,308,309
//        102,103,107,109,201,203,203,203,207,301,303,305,307
//        101,101,106,107,203,203,204,206,207,208,209,302,304
    }
}
