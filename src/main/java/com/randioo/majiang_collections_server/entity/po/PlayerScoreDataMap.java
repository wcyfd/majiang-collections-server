package com.randioo.majiang_collections_server.entity.po;

import java.util.HashMap;
import java.util.Map;

public class PlayerScoreDataMap {
    private Map<Integer, PlayerScoreData> map = new HashMap<>();

    public Map<Integer, PlayerScoreData> getMap() {
        return map;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PlayerScoreDataMap [map=").append(map).append("]");
        return builder.toString();
    }

}
