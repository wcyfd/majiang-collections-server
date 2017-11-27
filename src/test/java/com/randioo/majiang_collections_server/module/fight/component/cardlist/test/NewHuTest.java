package com.randioo.majiang_collections_server.module.fight.component.cardlist.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.google.common.collect.Lists;
import com.randioo.majiang_collections_server.entity.po.CardSort;
import com.randioo.majiang_collections_server.module.fight.component.HongZhongMajiangRule;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.NewHu;
import com.randioo.randioo_server_base.template.Ref;

public class NewHuTest {
    // @Test
    public void removeBeforeStartIndexElementTest() {
        NewHu hu = new NewHu();
        List<Integer> cards = Lists.newArrayList(101, 102, 103, 105, 205);
        List<Integer> singleLists = new ArrayList<>();
        Method method = ReflectionUtils.findMethod(NewHu.class, "removeBeforeStartIndexElement", List.class,
                List.class, int.class);
        ReflectionUtils.makeAccessible(method);
        ReflectionUtils.invokeMethod(method, hu, singleLists, cards, 0);
        System.out.println(singleLists);
    }

     @Test
    public void checkHuTest() {
        NewHu hu = new NewHu();
        // List<Integer> cards = Lists.newArrayList(103, 104, 104, 104, 105,
        // 106, 106, 106, 107, 107, 108, 109, 801, 801);
        // List<Integer> cards =
        // Lists.newArrayList(401,401,401,401,501,501,501,501,601,601,601,601,701,701);
//        List<Integer> cards = Lists.newArrayList(101, 102, 103, 203, 204, 204, 205, 205, 206, 306, 306, 308, 309, 307);
        List<Integer> cards = Lists.newArrayList(103, 104, 105, 108, 108, 108, 204, 204, 204, 302, 302, 303, 304, 305);
//        List<Integer> cards = Lists.newArrayList(103,104,105,105,105,106,106,106,106,107,108);
        // List<Integer> cards =
        // Lists.newArrayList(303,303,303,303,304,304,304,304,305,305,305,305,306,306);
        // List<Integer> cards = Lists.newArrayList(102,102);
        CardSort cardSort = new CardSort(5);
        cardSort.fillCardSort(cards);
        Method method = ReflectionUtils.findMethod(NewHu.class, "checkHu", CardSort.class, int.class);
        ReflectionUtils.makeAccessible(method);
        Field findAllField = ReflectionUtils.findField(NewHu.class, "findAll");
        Field printField = ReflectionUtils.findField(NewHu.class, "print");
        ReflectionUtils.makeAccessible(findAllField);
        ReflectionUtils.makeAccessible(printField);
        ReflectionUtils.setField(findAllField, hu, false);
        ReflectionUtils.setField(printField, hu, true);
        boolean result = (boolean) ReflectionUtils.invokeMethod(method, hu, cardSort, 801);
        System.out.println(result);
    }

//     @Test
    public void checkTingTest() {
        NewHu hu = new NewHu();
        // List<Integer> cards = Lists.newArrayList(103, 104, 104, 104, 105,
        // 106, 106, 106, 107, 107, 108, 109, 801, 801);

        Method method = ReflectionUtils.findMethod(NewHu.class, "checkHu", CardSort.class, int.class);
        ReflectionUtils.makeAccessible(method);
        Field findAllField = ReflectionUtils.findField(NewHu.class, "findAll");
        Field printField = ReflectionUtils.findField(NewHu.class, "print");
        ReflectionUtils.makeAccessible(findAllField);
        ReflectionUtils.makeAccessible(printField);
        ReflectionUtils.setField(findAllField, hu, false);
        ReflectionUtils.setField(printField, hu, false);

        HongZhongMajiangRule rule = new HongZhongMajiangRule();
        List<Integer> cards = rule.getCards();
        Set<Integer> set = new HashSet<>();
        set.addAll(cards);

        for (int replaceCard : set) {
            for (int j = 0; j < 14; j++) {
                List<Integer> currentValues = Arrays.asList(301, 301, 301, 302, 303, 304, 305, 306, 307, 308, 309, 309,
                        309, replaceCard);
                List<Integer> c = Lists.newArrayList(currentValues);
                CardSort cardSort = new CardSort(5);
                cardSort.fillCardSort(c);
                boolean result = (boolean) ReflectionUtils.invokeMethod(method, hu, cardSort, 801);
                System.out.println(result);
            }
        }
        System.out.println("end");
    }

//    @Test
    public void checkMotherFucker() {
        NewHu hu = new NewHu();
        Field findAllField = ReflectionUtils.findField(NewHu.class, "findAll");
        Field printField = ReflectionUtils.findField(NewHu.class, "print");
        ReflectionUtils.makeAccessible(findAllField);
        ReflectionUtils.makeAccessible(printField);
        ReflectionUtils.setField(findAllField, hu, true);
        ReflectionUtils.setField(printField, hu, true);

     
    }
}
