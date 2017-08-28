package com.randioo.majiang_collections_server.module.fight.component.cardlist;

import java.util.ArrayList;
import java.util.List;

import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.majiang_collections_server.entity.po.CardSort;

public class Chi extends AbstractCardList {
    public int card;
    public int targetCard;

    @Override
    public void check(GameConfigData gameConfigData, List<CardList> cardLists, CardSort cardSort, int card,
            List<CardList> showCardList, boolean isMine) {

    }

    @Override
    public List<Integer> getCards() {
        List<Integer> list = new ArrayList<>(3);
        for (int i = 0; i < 3; i++)
            list.add(card + i);
        return list;
    }

}
