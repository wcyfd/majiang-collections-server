/**
 * 
 */
package com.randioo.majiang_collections_server.module.fight.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.mahjong_public_server.protocol.Fight.FightSendTingCardRequest;
import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.majiang_collections_server.module.fight.service.FightService;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

/**
 * @Description:
 * @author zsy
 * @date 2017年9月8日 下午1:11:33
 */
@Controller
@PTAnnotation(FightSendTingCardRequest.class)
public class FightSendTingCardAction implements IActionSupport {
    @Autowired
    FightService fightService;

    @Override
    public void execute(Object data, IoSession session) {
        FightSendTingCardRequest request = (FightSendTingCardRequest) data;
        Role role = (Role) RoleCache.getRoleBySession(session);
        fightService.sendTingCard(role, request.getCard(), request.getIsTouchCard(), request.getTingCardsList());

    }

}
