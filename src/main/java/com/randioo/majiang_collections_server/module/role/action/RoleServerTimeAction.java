package com.randioo.majiang_collections_server.module.role.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.mahjong_public_server.protocol.Role.RoleGetServerTimeRequest;
import com.randioo.majiang_collections_server.module.role.service.RoleService;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.template.IActionSupport;

@Controller
@PTAnnotation(RoleGetServerTimeRequest.class)
public class RoleServerTimeAction implements IActionSupport {

    @Autowired
    private RoleService roleService;

    @Override
    public void execute(Object data, IoSession session) {
        RoleGetServerTimeRequest request = (RoleGetServerTimeRequest) data;
        roleService.getServerTime(session);
    }

}
