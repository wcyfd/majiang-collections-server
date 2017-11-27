package com.randioo.majiang_collections_server.module.fight.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.mahjong_public_server.protocol.Gm.GmRoundRequest;
import com.randioo.majiang_collections_server.module.fight.service.FightService;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.template.IActionSupport;

@Controller
@PTAnnotation(GmRoundRequest.class)
public class FightGmRoundAction implements IActionSupport {

    @Autowired
    private FightService fightService;

    @Override
    public void execute(Object data, IoSession session) {
        GmRoundRequest request = (GmRoundRequest) data;
        String roomId = request.getRoomId();
        int remainRound = request.getRemainRound();
        fightService.gmRound(roomId, remainRound);
    }

}
