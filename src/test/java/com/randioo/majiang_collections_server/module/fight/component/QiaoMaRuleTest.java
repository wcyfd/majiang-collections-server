package com.randioo.majiang_collections_server.module.fight.component;

import java.util.Stack;

import org.junit.Test;

import com.randioo.majiang_collections_server.module.fight.component.MajiangRule.MajiangStateEnum;

public class QiaoMaRuleTest {

    @Test
    public void test() {
        Stack<MajiangStateEnum> stack = new Stack<>();
        stack.add(MajiangStateEnum.STATE_NEXT_SEAT);
        stack.add(MajiangStateEnum.STATE_WAIT_OPERATION);
        stack.add(MajiangStateEnum.STATE_SC_SEND_CARD);

        stack.remove(MajiangStateEnum.STATE_SC_SEND_CARD);
        stack.push(MajiangStateEnum.STATE_AUTO_SEND_CARD);

        System.out.println(stack);
    }
}
