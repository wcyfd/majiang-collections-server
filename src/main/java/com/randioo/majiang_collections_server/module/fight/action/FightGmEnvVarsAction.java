package com.randioo.majiang_collections_server.module.fight.action;

import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.mahjong_public_server.protocol.Entity.EnvVarsData;
import com.randioo.mahjong_public_server.protocol.Gm.GmEnvVarsRequest;
import com.randioo.majiang_collections_server.module.fight.service.FightService;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.template.IActionSupport;

@PTAnnotation(GmEnvVarsRequest.class)
@Controller
public class FightGmEnvVarsAction implements IActionSupport {

    @Autowired
    private FightService fightService;

    @Override
    public void execute(Object data, IoSession session) {
        GmEnvVarsRequest request = (GmEnvVarsRequest) data;
        String roomId = request.getRoomId();
        List<EnvVarsData> list = request.getEnvVarsDataList();
        
        fightService.gmEnvVars(roomId, list, session);
    }

}
