package com.randioo.majiang_collections_server.scheduler;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.majiang_collections_server.module.close.service.CloseService;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.db.GameDB;
import com.randioo.randioo_server_base.entity.RoleInterface;
import com.randioo.randioo_server_base.template.EntityRunnable;

@Component
public class SaveAllRoleScheduler {

    private static Logger logger = LoggerFactory.getLogger(SaveAllRoleScheduler.class);
    @Autowired
    private CloseService closeService;
    @Autowired
    private GameDB gameDB;

    // @Scheduled(cron = "1/1 * * * * ?")
    @Scheduled(cron = "0 0/10 * * * ?")
    public void saveAllRole() {
        try {
            logger.info("定时保存");
            saveRole(false);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    private void saveRole(final boolean mustSave) {
        Map<Integer, RoleInterface> roleMap = RoleCache.getRoleMap();
        Iterator<Entry<Integer, RoleInterface>> entrySetIterator = roleMap.entrySet().iterator();
        while (entrySetIterator.hasNext()) {
            Entry<Integer, RoleInterface> entrySet = entrySetIterator.next();
            Role role = (Role) entrySet.getValue();

            if (!gameDB.isUpdatePoolClose()) {
                gameDB.getUpdatePool().submit(new EntityRunnable<Role>(role) {
                    @Override
                    public void run(Role role) {
                        closeService.roleDataCache2DB(role, mustSave);
                    }
                });
            }
        }
    }
}
