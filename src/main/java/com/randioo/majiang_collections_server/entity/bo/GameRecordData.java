/**
 * 
 */
package com.randioo.majiang_collections_server.entity.bo;

/**
 * @Description:
 * @author zsy
 * @date 2017年9月27日 上午10:28:34
 */
public class GameRecordData {
    private int id;
    private int roleId;

    public GameRecordData(int roleId) {
        this.roleId = roleId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

}
