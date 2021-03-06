package com.randioo.randioo_server_base.module.login;

import org.apache.mina.core.session.IoSession;

import com.randioo.randioo_server_base.entity.RoleInterface;
import com.randioo.randioo_server_base.template.Ref;

public interface LoginHandler {

    /**
     * 检查创建的帐号是否合法
     * 
     * @param account
     * @return
     * @author wcy 2016年11月30日
     */
    boolean createRoleCheckAccount(LoginInfo info, Ref<Integer> errorCode);

    /**
     * 创建帐号
     * 
     * @param conn
     * @param createRoleMessage
     * @return
     * @author wcy 2016年11月30日
     */
    RoleInterface createRole(LoginInfo loginInfo);

    RoleInterface getRoleInterfaceFromDBById(int roleId);

    RoleInterface getRoleInterfaceFromDBByAccount(String account);

    void loginRoleModuleDataInit(RoleInterface roleInterface);

    /**
     * 通知异地登录
     * 
     * @param oldFacility
     * @author wcy 2017年7月1日
     */
    void noticeOtherPlaceLogin(IoSession lastSession);

    void closeCallback(IoSession session);

    void synOffline(IoSession session);

}
