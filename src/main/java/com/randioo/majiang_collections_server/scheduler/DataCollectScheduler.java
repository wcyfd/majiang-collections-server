package com.randioo.majiang_collections_server.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.GlobleConstant;
import com.randioo.majiang_collections_server.cache.local.GameCache;
import com.randioo.majiang_collections_server.util.JedisUtils;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.config.GlobleMap;

@Component
public class DataCollectScheduler {
    @Scheduled(cron = "0/5 * * * * ?")
    public void collect() {
        try {
            String project = GlobleMap.String(GlobleConstant.ARGS_PROJECT_NAME);
            JedisUtils.set(project + "_session_count", String.valueOf(SessionCache.getAllSession().size()));
            JedisUtils.set(project + "_role_count", String.valueOf(RoleCache.getRoleAccountMap().size()));
            JedisUtils.set(project + "_game_count", String.valueOf(GameCache.getGameMap().size()));
        } catch (Exception e) {
        }
    }
}
