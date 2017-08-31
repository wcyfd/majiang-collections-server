/**
 * 
 */
package com.randioo.majiang_collections_server.module.fight.component.parameter;

import java.util.List;

import com.randioo.majiang_collections_server.module.fight.component.cardlist.CardList;

/**
 * @Description:
 * @author zsy
 * @date 2017年7月27日 下午12:00:54
 */
public class HuaParameter {
    // 出过的花牌
    private List<Integer> huaCards;
    private List<CardList> showCard;
    private List<Integer> handCard;
    private int baida;

    public List<Integer> getHuaCards() {
        return huaCards;
    }

    public void setHuaCards(List<Integer> huaCards) {
        this.huaCards = huaCards;
    }

    public List<CardList> getShowCard() {
        return showCard;
    }

    public void setShowCard(List<CardList> showCard) {
        this.showCard = showCard;
    }

    public List<Integer> getHandCard() {
        return handCard;
    }

    public void setHandCard(List<Integer> handCard) {
        this.handCard = handCard;
    }

    public int getBaida() {
        return baida;
    }

    public void setBaida(int baida) {
        this.baida = baida;
    }

}
