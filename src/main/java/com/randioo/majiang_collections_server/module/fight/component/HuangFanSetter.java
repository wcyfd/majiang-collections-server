package com.randioo.majiang_collections_server.module.fight.component;

import java.util.ArrayList;
import java.util.List;

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
public class HuangFanSetter {

    public void set(Game game) {
        List<Integer> dice = create();
        game.dice = dice;
        // 荒番计数器
        if (game.getGameConfig().getHuangFan()) {
            if (dice.get(0) == dice.get(1)) {
                if (dice.get(0) == 1 || dice.get(0) == 4) {
                    game.setHuangFanCount(game.getHuangFanCount() + 2);
                } else {
                    game.setHuangFanCount(game.getHuangFanCount() + 1);
                }
            } else {
                if (dice.contains(1) && dice.contains(4)) {
                    game.setHuangFanCount(game.getHuangFanCount() + 1);
                }
            }
            // 设置当前局是否位荒番
            game.setHuangFan(game.getHuangFanCount() > 0 ? true : false);
        }
    }

    private List<Integer> create() {
        List<Integer> dice = new ArrayList<>(2);
        // 随机出两个色子值
        dice.add(RandomUtils.getRandomNum(0, 5) + 1);
        dice.add(RandomUtils.getRandomNum(0, 5) + 1);
        return dice;

    }
}
