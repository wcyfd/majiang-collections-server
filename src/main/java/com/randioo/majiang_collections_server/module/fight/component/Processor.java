package com.randioo.majiang_collections_server.module.fight.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.module.fight.component.MajiangRule.MajiangStateEnum;

/**
 * 流程控制器
 * 
 * @author wcy 2017年8月24日
 *
 */
@Component
public class Processor {

    private Map<MajiangStateEnum, Flow> flows = new HashMap<>();

    public void regist(MajiangStateEnum majiangStateEnum, Flow flow) {
        flows.put(majiangStateEnum, flow);
    }

    /**
     * 执行下一个动作
     * 
     * @param game
     * @author wcy 2017年8月22日
     */
    public void process(Game game) {
        this.process(game, -1);
    }

    /**
     * 执行下一个动作
     * 
     * @param game
     * @param currentSeat 执行者的座位
     * @author wcy 2017年8月23日
     */
    public void process(Game game, int currentSeat) {
        // 继续执行执行出栈
        Stack<MajiangStateEnum> operations = game.getOperations();
        // 栈顶为等待操作状态时不继续流程
        while (operations.peek() != MajiangStateEnum.STATE_WAIT_OPERATION) {
            MajiangRule majiangRule = game.getRule();

            // 获取栈顶但是不要取出来
            MajiangStateEnum initOperation = operations.peek();
            {
                // 状态操作前执行
                List<MajiangStateEnum> newList = majiangRule.beforeStateExecute(game, initOperation, currentSeat);
                this.addProcesses(operations, newList);
            }

            MajiangStateEnum topOperation = operations.pop();
            System.out.println("pop process=" + topOperation);

            // 获得当前事件
            Flow flow = flows.get(topOperation);

            if (flow != null) {
                // 执行流过程
                flow.execute(game, currentSeat);
            } else {
                this.noFlowException(topOperation);
            }

            // 游戏结束标识,直接结束
            if (topOperation == MajiangStateEnum.STATE_GAME_OVER) {
                break;
            }

            {
                List<MajiangStateEnum> list = majiangRule.afterStateExecute(game, topOperation, currentSeat);
                this.addProcesses(operations, list);
                System.out.println("add process=" + list);
                System.out.println("remain process" + operations);
            }

        }

    }

    public void pop(RuleableGame game) {
        game.getOperations().pop();
    }

    public void push(RuleableGame game, MajiangStateEnum... states) {
        for (MajiangStateEnum item : states) {
            game.getOperations().push(item);
        }
    }

    public void push(RuleableGame game, List<MajiangStateEnum> states) {
        for (MajiangStateEnum item : states) {
            game.getOperations().push(item);
        }
    }

    /**
     * 添加流程 例子：<br>
     * p1,p2,p3->p3,p2,p1<br>
     * 
     * @param stack
     * @param list
     * @author wcy 2017年8月25日
     */
    private void addProcesses(Stack<MajiangStateEnum> stack, List<MajiangStateEnum> list) {
        if (list == null || list.size() == 0) {
            return;
        }

        for (int i = list.size() - 1; i >= 0; i--) {
            MajiangStateEnum state = list.get(i);
            stack.push(state);
        }
    }

    private void noFlowException(MajiangStateEnum majiangStateEnum) {
        String equals = "==========================================================";
        String context = " no flow : " + majiangStateEnum + " ";
        int len1 = equals.length();
        int len2 = context.length();

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(equals).append("\n");
        for (int i = 0; i < (len1 - len2) / 2; i++) {
            sb.append("=");
        }
        sb.append(context);
        for (int i = 0; i < (len1 - len2) / 2; i++) {
            sb.append("=");
        }
        sb.append("\n");
        sb.append(equals).append("\n");
        System.out.println(sb);
    }

}
