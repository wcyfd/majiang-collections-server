package com.randioo.majiang_collections_server.module.gm.component;

import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.GlobleConstant;
import com.randioo.randioo_server_base.config.GlobleMap;

@Component
public class GmLogin {
    public void login(boolean permit) {
        GlobleMap.putParam(GlobleConstant.ARGS_LOGIN, permit);
    }
}
