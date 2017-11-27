package com.randioo.majiang_collections_server.module.fight.action;

import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.mahjong_public_server.protocol.Entity.ClientCard;
import com.randioo.mahjong_public_server.protocol.Gm.GmDispatchCardRequest;
import com.randioo.majiang_collections_server.module.fight.service.FightService;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.template.IActionSupport;

@Controller
@PTAnnotation(GmDispatchCardRequest.class)
public class FightGmDispatchCardAction implements IActionSupport {

    @Autowired
    private FightService fightService;

    @Override
    public void execute(Object data, IoSession session) {
        GmDispatchCardRequest request = (GmDispatchCardRequest) data;
        String roomId = request.getRoomId();
        List<ClientCard> list = request.getClientCardsList();
        List<Integer> remainCards = request.getRemainCardsList();
        boolean remainCardBoolean = request.getRemainCardBoolean();
        int count = request.getRemainCardsCount();
        fightService.gmDispatchCard(roomId, list, remainCards, session,remainCardBoolean,count);
    }

}
