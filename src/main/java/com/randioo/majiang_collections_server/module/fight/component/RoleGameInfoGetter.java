package com.randioo.majiang_collections_server.module.fight.component;

import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.po.RoleGameInfo;

/**
 * 玩家信息获得者
 * 
 * @author AIM
 *
 */
@Component
public class RoleGameInfoGetter {
    /**
     * 获得当前玩家信息F
     * 
     * @param game
     * @return
     */
    public RoleGameInfo getCurrentRoleGameInfo(Game game) {
        int index = game.getCurrentRoleIdIndex();
        RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, index);
        return roleGameInfo;
    }

    /**
     * 根据座位获得玩家信息
     * 
     * @param game
     * @param seat
     * @return
     */
    private RoleGameInfo getRoleGameInfoBySeat(Game game, int seat) {
        String gameRoleId = game.getRoleIdList().get(seat);
        return game.getRoleIdMap().get(gameRoleId);
    }
}
