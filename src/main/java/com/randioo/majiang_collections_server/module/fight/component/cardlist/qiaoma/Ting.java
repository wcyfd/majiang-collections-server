/**
 * 
 */
package com.randioo.majiang_collections_server.module.fight.component.cardlist.qiaoma;

import java.util.List;

import com.randioo.mahjong_public_server.protocol.Entity.TingData;
import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.po.CardSort;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.AbstractCardList;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.CardList;

/**
 * @Description:
 * @author zsy
 * @date 2017年9月6日 下午6:29:39
 */
public class Ting extends AbstractCardList {
    public List<TingData> tingData;

    @Override
    public void check(Game game, List<CardList> cardLists, CardSort cardSort, int card, List<CardList> showCardList,
            boolean isMine) {
    }

    @Override
    public List<Integer> getCards() {

        return null;
    }

}
