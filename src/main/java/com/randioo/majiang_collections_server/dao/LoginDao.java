package com.randioo.majiang_collections_server.dao;

import com.randioo.majiang_collections_server.entity.bo.LoginBO;
import com.randioo.randioo_server_base.annotation.MyBatisGameDaoAnnotation;
import com.randioo.randioo_server_base.db.BaseDao;

@MyBatisGameDaoAnnotation
public interface LoginDao extends BaseDao<LoginBO> {
}
