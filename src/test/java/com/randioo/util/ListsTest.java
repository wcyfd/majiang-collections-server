package com.randioo.util;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.randioo.majiang_collections_server.util.Lists;

public class ListsTest {
    @Test
    public void containsCountTest() {
        List<Integer> arr = Arrays.asList(1, 2,4, 2, 3);
        System.out.println(Lists.containsCount(arr, 2));
    }
}
