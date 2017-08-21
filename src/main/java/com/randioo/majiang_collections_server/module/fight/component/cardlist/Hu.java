package com.randioo.majiang_collections_server.module.fight.component.cardlist;

import java.util.ArrayList;
import java.util.List;

import com.randioo.majiang_collections_server.entity.po.CardSort;
import com.randioo.majiang_collections_server.protocol.Entity.GameConfigData;

public abstract class Hu extends AbstractCardList {

	public int card;
	public List<Integer> handCards = new ArrayList<>();
	public List<CardList> showCardList = new ArrayList<>();
	public boolean isMine;
	public boolean gangKai;
	public boolean gangChong;
	public int gangChongTargetSeat;

	public abstract void checkTing(CardSort cardSort, List<Integer> waitCards,GameConfigData gameConfigData);
}
