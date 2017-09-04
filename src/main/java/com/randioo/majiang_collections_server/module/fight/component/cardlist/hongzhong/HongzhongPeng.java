package com.randioo.majiang_collections_server.module.fight.component.cardlist.hongzhong;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.po.CardSort;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.CardList;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Peng;

/**
 * 三个相同
 * 
 * @author wcy 2017年6月12日
 *
 */
public class HongzhongPeng extends Peng {
    // public int card;

    @Override
    public void check(Game game, List<CardList> cardLists, CardSort cardSort, int card, List<CardList> showCardList,
            boolean isMine) {
        if (game.getRule().getBaidaCard(game) == card)
            return;

        Set<Integer> set = cardSort.getList().get(2);
        if (set.contains(card)) {
            HongzhongPeng hongzhongPeng = new HongzhongPeng();
            hongzhongPeng.card = card;
            cardLists.add(hongzhongPeng);
        }
    }

    @Override
    public List<Integer> getCards() {
        List<Integer> list = new ArrayList<>(3);
        for (int i = 0; i < 3; i++)
            list.add(card);
        return list;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
