package com.randioo.majiang_collections_server.module.fight.component;

import com.randioo.randioo_server_base.scheduler.DefaultTimeEvent;

public abstract class VoteTimeEvent extends DefaultTimeEvent {
    /** 游戏id */
    private int gameId;
    /** 投票id */
    private int voteId;

    public int getVoteId() {
        return voteId;
    }

    public void setVoteId(int voteId) {
        this.voteId = voteId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("VoteTimeEvent [gameId=").append(gameId).append(", voteId=").append(voteId).append("]");
        return builder.toString();
    }
}
