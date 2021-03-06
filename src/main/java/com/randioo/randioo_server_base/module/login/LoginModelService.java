package com.randioo.randioo_server_base.module.login;

import org.apache.mina.core.session.IoSession;

import com.randioo.randioo_server_base.entity.RoleInterface;
import com.randioo.randioo_server_base.service.BaseServiceInterface;
import com.randioo.randioo_server_base.template.Ref;

public interface LoginModelService extends BaseServiceInterface {
    void setLoginHandler(LoginHandler handler);

    RoleInterface getRoleData(LoginInfo loginInfo, Ref<Integer> errorCode, IoSession ioSession);

    RoleInterface getRoleInterfaceById(int roleId);

    RoleInterface getRoleInterfaceByAccount(String account);

    void offline(IoSession session);
}
