package com.randioo.majiang_collections_server.entity.bo;

import com.randioo.randioo_server_base.db.DataEntity;

/**
 * 登录记录
 * 
 * @author wcy 2017年11月3日
 *
 */
public class LoginBO extends DataEntity {
    private int online;
    private String account;

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

}
