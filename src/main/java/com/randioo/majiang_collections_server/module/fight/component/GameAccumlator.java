package com.randioo.majiang_collections_server.module.fight.component;

import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.entity.bo.Game;

/**
 * 游戏计数器
 * 
 * @author AIM
 *
 */
@Component
public class GameAccumlator {
    /**
     * 计数
     * 
     * @param game
     */
    public void accumlate(Game game) {
        game.setSendCardCount(game.getSendCardCount() + 1);
    }
}
