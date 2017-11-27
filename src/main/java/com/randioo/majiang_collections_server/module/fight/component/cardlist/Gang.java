package com.randioo.majiang_collections_server.module.fight.component.cardlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.majiang_collections_server.cache.local.GameCache;
import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.po.CardSort;
import com.randioo.majiang_collections_server.module.fight.component.MajiangRule;

public class Gang extends AbstractCardList {
    public int card;
    public boolean dark;
    public Peng peng = null;

    @Override
    public void check(Game game, List<CardList> cardLists, CardSort cardSort, int card, List<CardList> showCardList,
            boolean isTouch) {
        MajiangRule rule = game.getRule();
        int baida = rule.getBaidaCard(game);
        // 如果是百搭牌 肯定不能杠
        if (card == baida)
            return;

        Set<Integer> set = cardSort.getList().get(3);
        boolean hasPeng = set.size() > 0;

        // 如果是我的牌
        if (isTouch) {
            // 检查暗杠
            if (hasPeng) {
                for (int value : set) {
                    if (value == baida)
                        continue;

                    Gang gang = new Gang();
                    gang.card = value;
                    gang.dark = true;
                    cardLists.add(gang);
                }
            }
            // 拿的牌检查补杠
            // if (GameCache.getBaiDaCardNumSet().contains(baida)) {
            // return;
            // }
            for (CardList cardList : showCardList) {
                if (cardList instanceof Peng) {
                    Peng peng = (Peng) cardList;
                    // 如果碰过的牌是这张牌，则可以补杠
                    // 只有碰了才可以补杠,情况分两种，第一种是本轮自摸到的牌可以用于补杠，第二种是手牌有可以用于补杠的
                    if (peng.card == card || cardSort.getList().get(0).contains(peng.card)) {
                        Gang gang = new Gang();
                        gang.dark = false;
                        gang.card = peng.card;
                        gang.peng = peng;

                        cardLists.add(gang);
                        break;
                    }

                }
            }
        } else {
            // 明杠
            if (hasPeng) {
                for (int value : set) {
                    if (card != value) {
                        continue;
                    }

                    Gang gang = new Gang();
                    gang.card = value;
                    gang.dark = false;
                    cardLists.add(gang);
                }
            }
        }

    }

    @Override
    public List<Integer> getCards() {
        List<Integer> list = new ArrayList<>(4);
        for (int i = 0; i < 4; i++)
            list.add(card);
        return list;
    }

    @Override
    public String toString() {
        return "cardList=>gang:card=" + card + " visible=" + dark + " peng=" + peng + " " + super.toString();
    }
}
