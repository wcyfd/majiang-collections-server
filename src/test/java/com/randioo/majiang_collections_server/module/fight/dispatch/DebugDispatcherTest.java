package com.randioo.majiang_collections_server.module.fight.dispatch;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.po.RoleGameInfo;
import com.randioo.majiang_collections_server.module.fight.component.HongZhongMajiangRule;
import com.randioo.majiang_collections_server.module.fight.component.dispatch.CardPart;
import com.randioo.majiang_collections_server.module.fight.component.dispatch.DebugDispatcher;

public class DebugDispatcherTest {
    @Test
    public void test() {
        Game game = new Game();

        RoleGameInfo role1 = new RoleGameInfo();
        role1.gameRoleId = "1";
        RoleGameInfo role2 = new RoleGameInfo();
        role2.gameRoleId = "2";
        RoleGameInfo role3 = new RoleGameInfo();
        role3.gameRoleId = "3";
        RoleGameInfo role4 = new RoleGameInfo();
        role4.gameRoleId = "4";

        game.getRoleIdList().add(role1.gameRoleId);
        game.getRoleIdList().add(role2.gameRoleId);
        game.getRoleIdList().add(role3.gameRoleId);
        game.getRoleIdList().add(role4.gameRoleId);

        game.getRoleIdMap().put(role1.gameRoleId, role1);
        game.getRoleIdMap().put(role2.gameRoleId, role2);
        game.getRoleIdMap().put(role3.gameRoleId, role3);
        game.getRoleIdMap().put(role4.gameRoleId, role4);

        DebugDispatcher dispatcher = new DebugDispatcher();
        HongZhongMajiangRule rule = new HongZhongMajiangRule();
        List<Integer> cards = new ArrayList<>(rule.getCards());

        List<CardPart> list = dispatcher.dispatch(game, cards, 4, 13);
        System.out.println(list);
    }
}
