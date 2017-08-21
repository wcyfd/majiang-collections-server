package com.randioo.majiang_collections_server.entity.po;

import com.randioo.randioo_server_base.scheduler.DefaultTimeEvent;
import com.randioo.randioo_server_base.scheduler.TimeEvent;

public abstract class AISendCardTimeEvent extends DefaultTimeEvent {

	protected int gameId;

	@Override
	public abstract void update(TimeEvent timeEvent);

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

}
