/**
 * 
 */
package com.randioo.majiang_collections_server.module.fight.component.score.round.qiaoma;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.randioo.mahjong_public_server.protocol.Entity.HuType;
import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.po.CardSort;
import com.randioo.majiang_collections_server.entity.po.RoleGameInfo;
import com.randioo.majiang_collections_server.module.fight.component.calcuator.HuTypeCalculator;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.CardList;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Chi;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Gang;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Peng;

/**
 * @Description: 敲麻胡牌类型
 * @author zsy
 * @date 2017年8月31日 上午9:40:46
 */
@Component
public class QiaomaHuTypeCalculator {

    public QiaomaHuTypeResult calc(RoleGameInfo roleGameInfo, Game game, int lastCard, boolean isGangKai) {
        // 手牌
        List<Integer> cards = roleGameInfo.cards;
        cards.add(lastCard);
        CardSort handCard = new CardSort(4);
        handCard.fillCardSort(cards);
        List<Integer> remainCard = game.getRemainCards();
        // 别人能看见的牌
        List<CardList> showCard = roleGameInfo.showCardLists;

        List<QiaomaTypeEnum> typeEnumList = new ArrayList<QiaomaTypeEnum>();
        QiaomaHuTypeResult res = new QiaomaHuTypeResult();

        // 获得所有的牌=手牌+亮出的牌
        List<Integer> allCards = handCard.toArray();
        for (CardList item : showCard)
            allCards.addAll(item.getCards());

        if (isGangKai) {
            typeEnumList.add(QiaomaTypeEnum.GANG_KAI);
            res.typeList.add(HuType.GANG_KAI);
        }

        if (remainCard.size() == 0) {
            typeEnumList.add(QiaomaTypeEnum.HAI_DI_LAO);
            res.typeList.add(HuType.HAI_DI_LAO);
        }

        if (handCard.toArray().size() == 2) {
            typeEnumList.add(QiaomaTypeEnum.DA_DIAO_CHE);
            res.typeList.add(HuType.DA_DIAO_CHE);
        }

        if (isQingYiSe(allCards)) {
            typeEnumList.add(QiaomaTypeEnum.QING_YI_SE);
            res.typeList.add(HuType.QING_YI_SE);
        }

        if (isHunYiSe(allCards)) {
            typeEnumList.add(QiaomaTypeEnum.HUN_YI_SE);
            res.typeList.add(HuType.HUN_YI_SE);
        }

        if (isWuHuaGuo(handCard, showCard)) {
            typeEnumList.add(QiaomaTypeEnum.WU_HUA_GUO);
            res.typeList.add(HuType.WU_HUA_GUO);
            res.fanCount += 10;
        }

        if (isMenQing(showCard)) {
            typeEnumList.add(QiaomaTypeEnum.MEN_QING);
            res.typeList.add(HuType.MEN_QING);
        }

        if (isPengPengHu(handCard, showCard)) {
            typeEnumList.add(QiaomaTypeEnum.PENG_PENG_HU);
            res.typeList.add(HuType.PENG_PENG_HU);
        }

        if (isHunPeng(allCards)) {
            typeEnumList.add(QiaomaTypeEnum.HUN_PENG);
            res.typeList.add(HuType.HUN_PENG);
        }

        if (isQingPeng(handCard, showCard, allCards)) {
            typeEnumList.add(QiaomaTypeEnum.QING_PENG);
            res.typeList.add(HuType.QING_PENG);
        }

        for (QiaomaTypeEnum item : typeEnumList)
            res.fanCount += item.fan;

        return res;
    }

    // 清一色：全部牌型为一色牌组成
    public boolean isQingYiSe(List<Integer> allCards) {
        Integer frist = allCards.get(0) / 100;

        for (Integer i : allCards) {
            if (i / 100 != frist)
                return false;
        }

        return true;
    }

    // 混一色：全由序数牌加风向组成的牌
    public boolean isHunYiSe(List<Integer> allCards) {
        List<Integer> target = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        for (Integer i : allCards) {
            if (!target.contains(i / 100))
                return false;
        }
        return true;
    }

    // 混碰：全部牌型为一色牌和风向组成
    public boolean isHunPeng(List<Integer> allCards) {
        List<Integer> copyCards = new ArrayList<Integer>(allCards);
        List<Integer> target = Arrays.asList(4, 5, 6, 7);

        Iterator<Integer> it = copyCards.iterator();
        while (it.hasNext()) { // 删除所有风牌
            Integer i = it.next();
            if (target.contains(i / 100)) {
                it.remove();
            }
        }
        if (copyCards.size() == allCards.size())
            return false;
        return isQingYiSe(copyCards);
    }

    // 清碰：全部牌型为一色牌且为碰碰胡组成
    public boolean isQingPeng(CardSort handCard, List<CardList> showCard, List<Integer> allCards) {
        return isPengPengHu(handCard, showCard) && isQingYiSe(allCards);
    }

    // 无花果：没有花牌获得胜利。
    public boolean isWuHuaGuo(CardSort handCard, List<CardList> showCard) {
        List<Integer> target = Arrays.asList(4, 5, 6, 7);
        Set<Integer> set = handCard.get(2);
        // 判断手牌里的东南西北刻字
        for (Integer card : set) {
            if (target.contains(card / 100))
                return false;
        }
        for (CardList item : showCard) {
            if (item instanceof Gang) // 如果杠
                return false;
            if (item instanceof Peng) { // 如果碰东南西北
                Peng peng = (Peng) item;
                if (target.contains(peng.card / 100))
                    return false;
            }
        }
        return true;
    }

    // 门清：没有吃过，没有碰，没有明杠 补杠 过，最后胡牌。
    public boolean isMenQing(List<CardList> showCard) {
        for (CardList item : showCard) {
            if (item instanceof Peng || item instanceof Chi)
                return false;
            if (item instanceof Gang) {
                Gang gang = (Gang) item;
                if (!gang.dark) // 如果是明或补杠
                    return false;
            }
        }
        return true;
    }

    // 碰碰胡：全由碰所产生的胡牌
    public boolean isPengPengHu(CardSort handCard, List<CardList> showCard) {
        for (CardList item : showCard) {
            if (!(item instanceof Peng))
                return false;
        }
        int line1 = handCard.get(0).size();
        int line2 = handCard.get(1).size();
        int line3 = handCard.get(2).size();

        if (!(line1 == line2 && line2 == line3 + 1))
            return false;

        return true;
    }

    public static void main(String[] args) {
        HuTypeCalculator cal = new HuTypeCalculator();

        List<Integer> list = Arrays.asList(101, 102, 103, 102, 102, 108, 108, 108);
        CardSort handSort = new CardSort(4);
        handSort.fillCardSort(list);

        List<CardList> showCard = new ArrayList<CardList>();
        Gang gang = new Gang();
        gang.dark = true;
        Peng peng = new Peng();
        peng.card = 601;
        // showCard.add(gang);
        showCard.add(new Chi());
        showCard.add(peng);

    }

    public enum QiaomaTypeEnum {
        // 清碰：全部牌型为一色牌且为碰碰胡组成
        QING_PENG(3),
        // 混碰：全部牌型为一色牌和风向组成
        HUN_PENG(2),
        // 清一色：全部牌型为一色牌组成
        QING_YI_SE(2),
        // 碰碰胡：全由碰所产生的胡牌
        PENG_PENG_HU(1),
        // 混一色：全由序数牌加风向组成的牌
        HUN_YI_SE(1),
        // 杠开：当摸到花或者杠时从背后摸一张牌，由该牌胡牌
        GANG_KAI(1),
        // 门清：该局游戏没有进行过吃，碰，明杠操作
        MEN_QING(1),
        // 无花果：没有花牌获得胜利。
        WU_HUA_GUO(0),
        // 大吊车：当前牌只剩下最后一个时，胡牌。
        DA_DIAO_CHE(1),
        // 海底捞：摸最后一张牌时胡牌
        HAI_DI_LAO(1);

        public int fan;

        QiaomaTypeEnum(int fan) {
            this.fan = fan;
        }
    }
}
