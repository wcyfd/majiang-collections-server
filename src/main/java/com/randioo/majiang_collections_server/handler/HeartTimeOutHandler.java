package com.randioo.majiang_collections_server.handler;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.module.close.service.CloseService;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Component
public class HeartTimeOutHandler implements KeepAliveRequestTimeoutHandler {

    @Autowired
    private CloseService closeService;

    @Override
    public void keepAliveRequestTimedOut(KeepAliveFilter arg0, IoSession arg1) throws Exception {
        System.out.println(TimeUtils.getDetailTimeStr() + " keepAliveRequestTimedOut");
        // arg1.close(true);
//        Role role = (Role) RoleCache.getRoleBySession(arg1);
        // closeService.asynManipulate(role);
        //
        arg1.close(true);
    }

}
