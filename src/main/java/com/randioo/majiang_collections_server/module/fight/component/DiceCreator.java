package com.randioo.majiang_collections_server.module.fight.component;

import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.randioo_server_base.utils.RandomUtils;

/**
 * 
 * @Description: 生成两个骰子
 * @author zsy
 * @date 2017年8月29日 下午2:38:36
 */
@Component
public class DiceCreator {

    public int[] create(Game game) {
        int dice[] = new int[2];
        // 随机出两个色子值
        dice[0] = RandomUtils.getRandomNum(0, 5) + 1;
        dice[1] = RandomUtils.getRandomNum(0, 5) + 1;

        return dice;

    }
}
