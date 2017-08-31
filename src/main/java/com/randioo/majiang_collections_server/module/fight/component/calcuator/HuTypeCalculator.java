package com.randioo.majiang_collections_server.module.fight.component.calcuator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.randioo.mahjong_public_server.protocol.Entity.HuType;
import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.po.CardSort;
import com.randioo.majiang_collections_server.entity.po.RoleGameInfo;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.CardList;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Chi;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Gang;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Hu;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Peng;
import com.randioo.randioo_server_base.template.Ref;

/**
 * @Description: 胡的牌型判断
 * @author zsy
 * @date 2017年7月27日 上午10:16:33
 */
@Component
public class HuTypeCalculator {

    public List<HuType> getHuTypeList(Game game, RoleGameInfo roleGameInfo, Hu hu, Ref<Integer> fanNum) {
        Integer baida = game.getBaidaCard();

        List<Integer> handCardList = roleGameInfo.cards;
        List<CardList> showCard = roleGameInfo.showCardLists;
        boolean isGangKai = hu.gangKai;
        boolean isPaoBaiDa = hu.isPaoBaiDa;
        int lastCard = hu.card;

        List<HuType> res = new ArrayList<>();
        ArrayList<HuTypeEnum> HuTypeEnumList = new ArrayList<>();

        CardSort handCard = new CardSort(4);
        handCard.fillCardSort(handCardList);

        // 获得所有的牌=手牌+亮出的牌
        List<Integer> allCards = handCard.toArray();
        for (CardList item : showCard)
            allCards.addAll(item.getCards());
        allCards.add(lastCard);

        if (isSiBaiDa(baida, allCards)) {
            res.add(HuType.SI_BAI_DA);
            HuTypeEnumList.add(HuTypeEnum.SI_BAI_DA);
        }
        if (isWuBaiDa(baida, allCards)) {
            res.add(HuType.WU_BAI_DA);
            HuTypeEnumList.add(HuTypeEnum.WU_BAI_DA);
        }
        if (isPaoBaiDa) {
            res.add(HuType.PAO_DAI_DA);
            HuTypeEnumList.add(HuTypeEnum.PAO_DAI_DA);
        }
        if (isGangKai) {
            res.add(HuType.GANG_KAI);
            HuTypeEnumList.add(HuTypeEnum.GANG_KAI);
        }

        if (handCard.toArray().size() == 2) {
            res.add(HuType.DA_DIAO_CHE);
            HuTypeEnumList.add(HuTypeEnum.DA_DIAO_CHE);
        }

        if (isMenQing(showCard)) {
            res.add(HuType.MEN_QING);
            HuTypeEnumList.add(HuTypeEnum.MEN_QING);
        }

        if (res.contains(HuType.SI_BAI_DA)) { // 四百搭 无百搭 跑百搭 不能并存，取番数最高的一个。
            res.remove(HuType.PAO_DAI_DA);
            res.remove(HuType.WU_BAI_DA);
            HuTypeEnumList.remove(HuTypeEnum.PAO_DAI_DA);
            HuTypeEnumList.remove(HuTypeEnum.WU_BAI_DA);

        }

        int count = 0;
        for (HuTypeEnum item : HuTypeEnumList) {
            count += item.fan;
        }
        // 不能超过三番
        count = count > 3 ? 3 : count;
        fanNum.set(count);

        return res;
    }

    // 没有百搭牌
    public boolean isWuBaiDa(Integer baida, List<Integer> allCards) {
        return Collections.frequency(allCards, baida) == 0;
    }

    // 手中四个百搭牌
    public boolean isSiBaiDa(Integer baida, List<Integer> allCards) {
        return Collections.frequency(allCards, baida) == 4;
    }

    // 门清：没有吃过，没有碰，没有明杠 补杠 过，最后胡牌。
    public boolean isMenQing(List<CardList> showCard) {
        return showCard.size() == 0;
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
        // peng.card = 601;
        // showCard.add(gang);
        showCard.add(new Chi());
        showCard.add(peng);

        System.out.println(cal.isWuBaiDa(101, list));

    }

}
