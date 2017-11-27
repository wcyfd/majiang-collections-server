package com.randioo.majiang_collections_server.module.fight.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.springframework.stereotype.Component;

import com.randioo.mahjong_public_server.protocol.Entity.GameState;
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
        while (operations.size() != 0 && operations.peek() != MajiangStateEnum.STATE_WAIT_OPERATION) {
            MajiangRule majiangRule = game.getRule();

            // 获取栈顶但是不要取出来
            MajiangStateEnum initOperation = operations.peek();
            {
                // 状态操作前执行
                List<MajiangStateEnum> newList = majiangRule.beforeStateExecute(game, initOperation, currentSeat);
                this.addProcesses(operations, newList);
            }

            MajiangStateEnum topOperation = operations.pop();
            game.logger.info("pop process={}", topOperation);

            // 获得当前事件
            Flow flow = flows.get(topOperation);

            if (flow != null) {
                // 执行流过程
                flow.execute(game, currentSeat);
            } else {
                this.noFlowException(game, topOperation);
            }

            // 游戏结束标识,直接结束
            if (topOperation == MajiangStateEnum.STATE_GAME_OVER) {
                game.getOperations().clear();
                break;
            }

            {
                List<MajiangStateEnum> list = majiangRule.afterStateExecute(game, topOperation, currentSeat);
                this.addProcesses(operations, list);
                game.logger.info("add process={}", list);
                game.logger.info("remain process {}", operations);
            }

            if (game.getOperations().size() == 0) {
                game.logger.info("栈为空,下次循环会报错");
            }

        }

    }

    public void pop(Game game) {
        if (game.getGameState() == GameState.GAME_START_END) {
            return;
        }
        if (game.getOperations().size() != 0) {
            game.getOperations().pop();
        } else {
            game.logger.info("栈为空，不可弹出");
        }
    }

    public void push(Game game, MajiangStateEnum... states) {
        if (game.getGameState() == GameState.GAME_START_END) {
            return;
        }
        for (MajiangStateEnum item : states) {
            game.getOperations().push(item);
        }
    }

    public void push(Game game, List<MajiangStateEnum> states) {
        if (game.getGameState() == GameState.GAME_START_END) {
            return;
        }
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

    private void noFlowException(Game game, MajiangStateEnum majiangStateEnum) {
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
        game.logger.info(sb.toString());
    }

}
