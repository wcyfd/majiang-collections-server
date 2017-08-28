package com.randioo.majiang_collections_server.module.fight.component.cardlist;

import java.util.List;

import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.majiang_collections_server.entity.po.CardSort;

public interface CardList {
    public void check(GameConfigData gameConfigData, List<CardList> cardLists, CardSort cardSort, int card,
            List<CardList> showCardList, boolean isMine);

    public List<Integer> getCards();

    public int getTargetSeat();

    public void setTargetSeat(int targetSeat);
}
