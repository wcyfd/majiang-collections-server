package com.randioo.majiang_collections_server.module.fight.component.cardlist;

import java.util.ArrayList;
import java.util.List;

import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.po.CardSort;

public abstract class Hu extends AbstractCardList {

    public int card;
    public List<Integer> handCards = new ArrayList<>();
    public List<CardList> showCardList = new ArrayList<>();
    public boolean isMine;
    public boolean gangKai;
    public boolean gangChong;
    public int gangChongTargetSeat;
    public boolean isPaoBaiDa;

    public abstract boolean checkTing(Game game, CardSort cardSort, List<Integer> waitCards);
}
