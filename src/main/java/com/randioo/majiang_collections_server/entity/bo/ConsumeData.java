/**
 * 
 */
package com.randioo.majiang_collections_server.entity.bo;

/**
 * @Description:
 * @author zsy
 * @date 2017年9月27日 上午10:16:01
 */
public class ConsumeData {
    private int id;
    private int roleId;
    private int money;

    public ConsumeData(int roleId, int money) {
        this.roleId = roleId;
        this.money = money;
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

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

}
