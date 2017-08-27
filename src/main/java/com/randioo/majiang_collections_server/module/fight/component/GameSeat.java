package com.randioo.majiang_collections_server.module.fight.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.entity.bo.Game;

/**
 * 座位
 * 
 * @author AIM
 *
 */
@Component
public class GameSeat {
    @Autowired
    private GameAccumlator gameAccumlator;

    /**
     * 下一个座位
     * 
     * @param game
     */
    public void nextSeat(Game game) {
        int currentSeat = game.getCurrentRoleIdIndex();
        this.jumpSeat(game, currentSeat);
    }

    /**
     * 跳转座位
     * 
     * @param game
     * @param seat
     */
    public void jumpSeat(Game game, int seat) {
        game.setCurrentRoleIdIndex(seat);
        gameAccumlator.accumlate(game);
    }

    /**
     * 查看下一个位置
     * 
     * @param game
     * @return
     * @author wcy 2017年8月25日
     */
    public int seekNextSeat(Game game) {
        int index = game.getCurrentRoleIdIndex();
        return (index + 1) >= game.getRoleIdList().size() ? 0 : index + 1;
    }
}
