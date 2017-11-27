package com.randioo.majiang_collections_server.cache.local;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.module.fight.component.MajiangRule;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.CardList;
import com.randioo.randioo_server_base.template.Function;

public class GameCache {
    private static Map<Integer, Game> gameMap = new LinkedHashMap<>();
    private static Map<String, Integer> gameLockMap = new LinkedHashMap<>();
    private static Map<Class<? extends CardList>, Function> parseCardListToProtoFunctionMap = new HashMap<>();
    private static Map<Class<? extends CardList>, Function> addProtoFunctionMap = new HashMap<>();

    private static Map<String, Function> roundOverFunctionMap = new HashMap<>();
    private static Map<String, MajiangRule> ruleMap = new HashMap<>();

    public static Map<Integer, Game> getGameMap() {
        return gameMap;
    }

    public static Map<String, Integer> getGameLockStringMap() {
        return gameLockMap;
    }

    public static Map<Class<? extends CardList>, Function> getParseCardListToProtoFunctionMap() {
        return parseCardListToProtoFunctionMap;
    }

    public static Map<Class<? extends CardList>, Function> getNoticeChooseCardListFunctionMap() {
        return addProtoFunctionMap;
    }

    public static Map<String, Function> getRoundOverFunctionMap() {
        return roundOverFunctionMap;
    }

    public static Map<String, MajiangRule> getRuleMap() {
        return ruleMap;
    }

}
