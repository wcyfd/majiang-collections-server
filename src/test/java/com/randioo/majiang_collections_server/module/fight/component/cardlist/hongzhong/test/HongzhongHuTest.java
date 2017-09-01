package com.randioo.majiang_collections_server.module.fight.component.cardlist.hongzhong.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.google.common.collect.Lists;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.majiang_collections_server.entity.po.CardSort;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.NewHu;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.hongzhong.HongzhongHu;
import com.randioo.randioo_server_base.utils.ReflectUtils;

public class HongzhongHuTest {

    public void checkHu() {
        HongzhongHu hu = new HongzhongHu();

        CardSort cardSort = new CardSort(4);
        // cardSort.fillCardSort(Arrays.asList(801, 801, 203, 203, 206, 206,
        // 207, 207, 208, 308, 308));
        // cardSort.fillCardSort(Arrays.asList(102, 102, 107, 108, 109, 204,
        // 204, 206, 207, 208, 307, 307, 308, 308));
        cardSort.fillCardSort(Arrays.asList(801, 801, 801, 201, 302));
        // cardSort.fillCardSort(Arrays.asList(101, 102, 103, 104, 105, 201,
        // 302, 101, 102, 201, 302, 801, 801, 302));

        // List<Integer> cards = Arrays.asList(101, 102, 103, 104, 105, 201,
        // 302, 101, 102, 201, 302, 801, 801, 302);
        // List<Integer> cards = Arrays.asList(101, 102, 201, 302, 101, 102,
        // 201, 302, 801, 801, 302);
        // List<Integer> cards = Arrays.asList(101, 102, 103, 104, 105, 201,
        // 302, 101, 102, 201, 302, 801, 801, 302);

        // List<Integer> cards = Arrays.asList(101, 102, 103, 801, 801, 201,
        // 302, 101, 102, 201, 302, 801, 801, 302);

        // List<Integer> cards = Arrays.asList(101, 102, 104, 104, 104, 107,
        // 107, 108, 108, 201, 203, 801, 801, 801);
        // List<Integer> cards = Arrays.asList(103, 104, 105, 205, 205, 206,
        // 801, 206);
        // List<Integer> cards = Arrays.asList(108, 109, 302, 302, 801, 801,
        // 801, 207);
        // List<Integer> cards = Arrays.asList(108, 109, 201, 202, 203, 203,
        // 203, 801);
        // List<Integer> cards = Arrays.asList(102, 103, 104, 305, 306, 307,
        // 307, 304);
        //
        // cardSort.fillCardSort(cards);

        Method method = ReflectionUtils.findMethod(HongzhongHu.class, "checkHu", GameConfigData.class, CardSort.class);
        Field field = ReflectionUtils.findField(HongzhongHu.class, "ifHuDirectHu");

        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.makeAccessible(method);

        ReflectionUtils.setField(field, hu, true);
        long start = System.currentTimeMillis();
        boolean b = ReflectUtils.invokeMethodWithResult(hu, method, null, cardSort);

        System.out.println(b);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

}
