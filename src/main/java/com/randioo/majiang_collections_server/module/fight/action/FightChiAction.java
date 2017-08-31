/**
 * 
 */
package com.randioo.majiang_collections_server.module.fight.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.mahjong_public_server.protocol.Fight.FightChiRequest;
import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.majiang_collections_server.module.fight.service.FightService;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

/**
 * @Description:
 * @author zsy
 * @date 2017年8月10日 上午10:27:30
 */
@Controller
@PTAnnotation(FightChiRequest.class)
public class FightChiAction implements IActionSupport {
    @Autowired
    private FightService fightService;

    @Override
    public void execute(Object data, IoSession session) {
        FightChiRequest request = (FightChiRequest) data;
        Role role = (Role) RoleCache.getRoleBySession(session);
        fightService.chi(role, request.getTempGameCount(), request.getCallCardListId());
    }

}
