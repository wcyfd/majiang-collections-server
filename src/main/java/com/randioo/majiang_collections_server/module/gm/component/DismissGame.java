package com.randioo.majiang_collections_server.module.gm.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.cache.local.GameCache;
import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.majiang_collections_server.entity.po.RoleGameInfo;
import com.randioo.majiang_collections_server.module.playback.component.PlaybackManager;
import com.randioo.majiang_collections_server.util.key.Key;
import com.randioo.randioo_server_base.cache.RoleCache;

/**
 * 强制解散房间
 * 
 * @author wcy 2017年11月1日
 *
 */
@Component
public class DismissGame {

    @Autowired
    private PlaybackManager playbackManager;

    public void dismissGameByLockString(String lockString) {
        Integer gameId = GameCache.getGameLockStringMap().get(lockString);
        if (gameId == null) {
            return;
        }
        Game game = GameCache.getGameMap().get(gameId);

        // 移除录像
        playbackManager.remove(game);

        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            Role role = (Role) RoleCache.getRoleById(roleGameInfo.roleId);
            if (role != null) {
                role.setGameId(0);
                role.setGameOverSC(null);
                role.setGameConfigData(null);
            }
        }

        Key key = game.getLockKey();
        if (key != null) {
            GameCache.getGameLockStringMap().remove(lockString);
            key.recycle();
        }

        // 将游戏从缓存池中移除
        GameCache.getGameMap().remove(game.getGameId());
    }
}
