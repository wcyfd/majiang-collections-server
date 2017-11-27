package com.randioo.majiang_collections_server.entity.po;

public class PlayerScoreData {
    public String account;
    public String name;
    public int score;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PlayerScoreData [account=").append(account).append(", name=").append(name).append(", score=")
                .append(score).append("]");
        return builder.toString();
    }

}
