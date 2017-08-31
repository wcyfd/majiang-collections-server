package com.randioo.majiang_collections_server.module.fight.component.cardlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.po.CardSort;
import com.randioo.majiang_collections_server.module.fight.component.MajiangRule;

public class Chi extends AbstractCardList {
    /** 吃的第一张牌 */
    public int card;
    /** 吃进去的那张牌 */
    public int targetCard;

    @Override
    public void check(Game game, List<CardList> cardLists, CardSort cardSort, int card, List<CardList> showCardList,
            boolean isMine) {
        MajiangRule rule = game.getRule();
        int baida = rule.getBaidaCard(game);
        // 如果是我的牌
        if (isMine)
            return;

        // 别人出的百搭牌可以吃
        if (card == baida)
            return;

        Set<Integer> set = cardSort.get(0);
        int add1 = card + 1;
        int add2 = card + 2;
        int del1 = card - 1;
        int del2 = card - 2;
        if (set.contains(del1) && set.contains(del2)) {
            if (!containsBaida(baida, del1, del2))
                addChi(cardLists, card, del2);
        }
        if (set.contains(add1) && set.contains(del1)) {
            if (!containsBaida(baida, add1, del1))
                addChi(cardLists, card, del1);
        }
        if (set.contains(add1) && set.contains(add2)) {
            if (!containsBaida(baida, add1, add2))
                addChi(cardLists, card, card);
        }
    }

    /**
     * 除了新摸得牌，如果有百搭牌就不能杠碰吃
     * 
     * @param baida
     * @param card1
     * @param card2
     * @return
     */
    private boolean containsBaida(int baida, int... cards) {
        boolean flag = false;
        for (int card : cards) {
            if (card == baida)
                flag = true;
        }
        return flag;
    }

    private void addChi(List<CardList> cardLists, int card, int first) {
        Chi chi = new Chi();
        chi.card = first;
        chi.targetCard = card;
        cardLists.add(chi);
    }

    @Override
    public List<Integer> getCards() {
        List<Integer> list = new ArrayList<>(3);
        for (int i = 0; i < 3; i++)
            list.add(card + i);
        return list;
    }

    @Override
    public String toString() {
        return "cardList=>chi:card=" + card + " " + super.toString();
    }

}
