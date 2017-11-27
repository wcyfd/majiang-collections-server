package com.randioo.majiang_collections_server.module.close.service;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.majiang_collections_server.dao.OnlineTimeDao;
import com.randioo.majiang_collections_server.dao.RoleDao;
import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.majiang_collections_server.module.fight.service.FightService;
import com.randioo.majiang_collections_server.module.login.service.LoginService;
import com.randioo.majiang_collections_server.module.match.service.MatchService;
import com.randioo.randioo_server_base.db.GameDB;
import com.randioo.randioo_server_base.service.BaseService;
import com.randioo.randioo_server_base.template.EntityRunnable;
import com.randioo.randioo_server_base.utils.SaveUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("closeService")
public class CloseServiceImpl extends BaseService implements CloseService {

    @Autowired
    private LoginService loginService;

    @Autowired
    private FightService fightService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private GameDB gameDB;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private OnlineTimeDao onlineTimeDao;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public void asynManipulate(Role role) {
        if (role == null)
            return;

        logger.info("保存至数据库 {} {}", role.getAccount(), role.getName());

        role.setOfflineTimeStr(TimeUtils.getDetailTimeStr());

        matchService.serviceCancelMatch(role);
        fightService.disconnect(role);

        if (!gameDB.isUpdatePoolClose()) {
            gameDB.getUpdatePool().submit(new EntityRunnable<Role>(role) {
                @Override
                public void run(Role role) {
                    roleDataCache2DB(role, true);
                }
            });
        }

    }

    @Override
    public void roleDataCache2DB(Role role, boolean mustSave) {
        try {
            if (SaveUtils.needSave(role, mustSave)) {
                roleDao.update(role);
                logger.info("数据库表 << role >> 保存成功 {}", role.getAccount());
            }
        } catch (Exception e) {
            logger.error("数据保存出错 {} {}", role.getAccount(), e);
        }
    }
}
