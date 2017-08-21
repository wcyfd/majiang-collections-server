package com.randioo.majiang_collections_server.entity.po;

import com.randioo.randioo_server_base.scheduler.DefaultTimeEvent;

public abstract class SendCardTimeEvent extends DefaultTimeEvent {

	private int gameId;
	private int sendCardCount;

	public int getSendCardCount() {
		return sendCardCount;
	}

	public void setSendCardCount(int sendCardCount) {
		this.sendCardCount = sendCardCount;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
}
