/**
 * 
 */
package com.randioo.majiang_collections_server.module.fight.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.mahjong_public_server.protocol.Fight.FightPreTingRequest;
import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.majiang_collections_server.module.fight.service.FightService;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

/**
 * @Description:
 * @author zsy
 * @date 2017年9月6日 下午9:08:02
 */
@Controller
@PTAnnotation(FightPreTingRequest.class)
public class FightPreTing implements IActionSupport {
    @Autowired
    FightService fightService;

    @Override
    public void execute(Object data, IoSession session) {
        FightPreTingRequest request = (FightPreTingRequest) data;
        Role role = (Role) RoleCache.getRoleBySession(session);
        fightService.preTing(role, request.getTempGameCount(), request.getCallCardListId());
    }

}
