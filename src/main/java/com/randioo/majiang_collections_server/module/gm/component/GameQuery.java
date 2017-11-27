package com.randioo.majiang_collections_server.module.gm.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.cache.local.GameCache;
import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.majiang_collections_server.entity.po.RoleGameInfo;
import com.randioo.randioo_server_base.cache.RoleCache;

@Component
public class GameQuery {
    /**
     * 检查游戏
     * 
     * @param lockString
     * @return
     * @author wcy 2017年11月2日
     */
    public List<String> queryGame(String lockString) {
        List<String> list = new ArrayList<>();
        Integer gameId = GameCache.getGameLockStringMap().get(lockString);
        if (gameId == null) {
            return list;
        }

        Game game = GameCache.getGameMap().get(gameId);
        if (game == null) {
            return list;
        }

        Map<String, RoleGameInfo> roleGameInfoMap = game.getRoleIdMap();

        for (RoleGameInfo roleGameInfo : roleGameInfoMap.values()) {
            int roleId = roleGameInfo.roleId;
            Role role = (Role) RoleCache.getRoleById(roleId);
            String name = role.getName();
            String account = role.getAccount();

            String str = account + "," + name;
            list.add(str);
        }

        return list;
    }
}
