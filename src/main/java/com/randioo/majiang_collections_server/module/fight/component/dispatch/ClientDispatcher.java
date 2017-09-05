package com.randioo.majiang_collections_server.module.fight.component.dispatch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.mahjong_public_server.protocol.Entity.ClientCard;
import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.po.RoleGameInfo;
import com.randioo.majiang_collections_server.util.Lists;
import com.randioo.randioo_server_base.utils.RandomUtils;

/**
 * 客户端分牌
 * 
 * @author wcy 2017年8月14日
 *
 */
@Component
public class ClientDispatcher implements Dispatcher {

    @Autowired
    private RandomDispatcher randomDispatcher;

    @Override
    public List<CardPart> dispatch(Game game, List<Integer> originCards, int partCount, int everyPartCount) {

        List<CardPart> cardParts = new ArrayList<>();
        List<Integer> removeList = new ArrayList<>();
        // 指定牌加入
        for (int i = 0; i < partCount; i++) {
            CardPart cardPart = new CardPart();
            cardParts.add(cardPart);
            String gameRoleId = game.getRoleIdList().get(i);
            RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
            roleGameInfo.cards.clear();

            if (i < game.getClientCards().size()) {
                List<ClientCard> clientCards = game.getClientCards();
                for (int j = 0; j < clientCards.get(i).getCardsList().size(); j++) {
                    int value = game.getClientCards().get(i).getCardsList().get(j);
                    cardPart.add(value);
                    removeList.add(value);
                }
            }
        }

        Lists.removeElementByList(originCards, removeList);
        removeList.clear();

        // 指定剩余卡牌
        Lists.removeElementByList(originCards, game.clientRemainCards);
        for (int i = game.clientRemainCards.size() - 1; i >= 0; i--) {
            originCards.add(0, game.clientRemainCards.get(i));
        }

        // 剩余牌补全
        for (int i = 0; i < partCount; i++) {
            CardPart cardPart = cardParts.get(i);
            for (int j = cardPart.size(); j < everyPartCount; j++) {
                int index = RandomUtils.getRandomNum(originCards.size());
                int value = originCards.get(index);
                cardPart.add(value);
                removeList.add(value);
            }
        }

        Lists.removeElementByList(originCards, removeList);
        return cardParts;
    }

}
