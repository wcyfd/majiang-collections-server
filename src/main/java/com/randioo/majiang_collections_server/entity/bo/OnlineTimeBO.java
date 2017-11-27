package com.randioo.majiang_collections_server.entity.bo;

import com.randioo.randioo_server_base.db.DataEntity;

/**
 * 在线时长
 * 
 * @author wcy 2017年11月3日
 *
 */
public class OnlineTimeBO extends DataEntity {
    private int roleId;
    private long onlineTime;

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public long getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(long onlineTime) {
        this.onlineTime = onlineTime;
    }
}
