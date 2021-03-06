/**
 * 
 */
package com.randioo.majiang_collections_server.module.fight.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.mahjong_public_server.protocol.Fight.FightTingCheckResultRequest;
import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.majiang_collections_server.module.fight.service.FightService;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

/**
 * @Description:
 * @author zsy
 * @date 2017年9月6日 下午6:03:19
 */
@PTAnnotation(FightTingCheckResultRequest.class)
@Controller
public class FightTingCheckResult implements IActionSupport {
    @Autowired
    private FightService fightService;

    @Override
    public void execute(Object data, IoSession session) {
        FightTingCheckResultRequest request = (FightTingCheckResultRequest) data;
        Role role = (Role) RoleCache.getRoleBySession(session);
        fightService.tingCheckResult(role, request.getTingDataList());
    }

}
