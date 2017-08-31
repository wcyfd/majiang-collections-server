/**
 * 
 */
package com.randioo.majiang_collections_server.module.fight.component.parameter;

import java.util.List;

import com.randioo.majiang_collections_server.module.fight.component.cardlist.CardList;
import com.randioo.randioo_server_base.template.Ref;

/**
 * @Description:
 * @author zsy
 * @date 2017年7月27日 上午11:59:32
 */
public class HuTypeParameter {
    private List<Integer> handCard;
    private List<CardList> showCard;
    private boolean isGangKai;
    private Integer baida;
    private boolean isPaoBaiDa;
    private Ref<Integer> fanNum;
    private int lastCard; // 胡牌时最后一张牌

    public int getLastCard() {
        return lastCard;
    }

    public void setLastCard(int lastCard) {
        this.lastCard = lastCard;
    }

    public Ref<Integer> getFanNum() {
        return fanNum;
    }

    public void setFanNum(Ref<Integer> fanNum) {
        this.fanNum = fanNum;
    }

    public boolean isPaoBaiDa() {
        return isPaoBaiDa;
    }

    public void setPaoBaiDa(boolean isPaoBaiDa) {
        this.isPaoBaiDa = isPaoBaiDa;
    }

    public List<Integer> getHandCard() {
        return handCard;
    }

    public void setHandCard(List<Integer> handCard) {
        this.handCard = handCard;
    }

    public List<CardList> getShowCard() {
        return showCard;
    }

    public void setShowCard(List<CardList> showCard) {
        this.showCard = showCard;
    }

    public boolean isGangKai() {
        return isGangKai;
    }

    public void setGangKai(boolean isGangKai) {
        this.isGangKai = isGangKai;
    }

    public Integer getBaida() {
        return baida;
    }

    public void setBaida(Integer baida) {
        this.baida = baida;
    }

}
