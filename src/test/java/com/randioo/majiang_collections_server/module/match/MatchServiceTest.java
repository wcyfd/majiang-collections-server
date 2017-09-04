package com.randioo.majiang_collections_server.module.match;

import java.lang.reflect.Method;

import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.module.match.service.MatchServiceImpl;
import com.randioo.randioo_server_base.config.GlobleMap;

public class MatchServiceTest {
    @Test
    public void copyConfigTest() {
        GlobleMap.putParam("test", false);
        Game game = new Game();
        MatchServiceImpl impl = new MatchServiceImpl();
        Method method = ReflectionUtils.findMethod(MatchServiceImpl.class, "copyGlobleMap", Game.class);
        ReflectionUtils.makeAccessible(method);

        ReflectionUtils.invokeMethod(method, impl, game);
        method.setAccessible(false);
        System.out.println(game.envVars);
    }
}
