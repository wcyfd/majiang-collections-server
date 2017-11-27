/**
 * 
 */
package com.randioo.majiang_collections_server.module.fight.component.score.round.qiaoma;

import com.randioo.mahjong_public_server.protocol.Entity.HuType;
import com.randioo.mahjong_public_server.protocol.Entity.PlayMode;
import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.po.CardSort;
import com.randioo.majiang_collections_server.entity.po.RoleGameInfo;
import com.randioo.majiang_collections_server.module.fight.component.QiaoMaRule;
import com.randioo.majiang_collections_server.module.fight.component.calcuator.HuTypeCalculator;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.CardList;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Chi;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Gang;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Peng;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Description: 敲麻胡牌类型
 * @author zsy
 * @date 2017年8月31日 上午9:40:46
 */
@Component
public class QiaomaHuTypeCalculator {

    public QiaomaHuTypeResult calc(RoleGameInfo roleGameInfo, Game game, int lastCard, boolean isGangKai) {
        // 手牌
        List<Integer> cards = new ArrayList<>(roleGameInfo.cards);
        if (lastCard != 0) {
            cards.add(lastCard);
        }
        CardSort handCard = new CardSort(5);
        handCard.fillCardSort(cards);
        List<Integer> remainCard = game.getRemainCards();
        // 别人能看见的牌
        List<CardList> showCard = roleGameInfo.showCardLists;

        List<QiaomaTypeEnum> typeEnumList = new ArrayList<>();
        QiaomaHuTypeResult res = new QiaomaHuTypeResult();

        // 获得所有的牌=手牌+亮出的牌
        List<Integer> allCards = handCard.toArray();
        for (CardList item : showCard) {
            allCards.addAll(item.getCards());
        }

        // 是不是勒子玩法
        boolean isLezi = game.getGameConfig().getPlayMode() == (PlayMode.LE_ZI);
        // List<HuType> leZiTypeList = game.getGameConfig().getLeZiTypeList();
        // boolean isLezi = leZiTypeList.size() == 0 ? false : true;

        if (isLezi) {
            leziCalc2(res, handCard, showCard, allCards, roleGameInfo, allCards, game);
            // leziCalc2(res, handCard, leZiTypeList, showCard, allCards,
            // roleGameInfo, allCards, game);
            if (res.leziCount == 0) {
                normal(roleGameInfo, game, isGangKai, cards, handCard, remainCard, showCard, typeEnumList, res,
                        allCards);
            }
        } else {
            normal(roleGameInfo, game, isGangKai, cards, handCard, remainCard, showCard, typeEnumList, res, allCards);
        }

        return res;
    }

    private void normal(RoleGameInfo roleGameInfo, Game game, boolean isGangKai, List<Integer> cards,
            CardSort handCard, List<Integer> remainCard, List<CardList> showCard, List<QiaomaTypeEnum> typeEnumList,
            QiaomaHuTypeResult res, List<Integer> allCards) {
        if (isGangKai) {
            typeEnumList.add(QiaomaTypeEnum.GANG_KAI);
            res.typeList.add(HuType.GANG_KAI);
        }

        // 剩余牌为0，且是自己
        if (remainCard.size() == 0
                && game.getCurrentRoleIdIndex() == game.getRoleIdList().indexOf(roleGameInfo.gameRoleId)) {
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

        if (isWuHuaGuo(roleGameInfo, cards, (QiaoMaRule) game.getRule())) {
            typeEnumList.add(QiaomaTypeEnum.WU_HUA_GUO);
            res.typeList.add(HuType.WU_HUA_GUO);
        }

        if (isMenQing(showCard)) {
            typeEnumList.add(QiaomaTypeEnum.MEN_QING);
            res.typeList.add(HuType.MEN_QING);
        }

        if (isPengPengHu(handCard, showCard)) {
            typeEnumList.add(QiaomaTypeEnum.PENG_PENG_HU);
            res.typeList.add(HuType.PENG_PENG_HU);
        }
        if (isHunYiSe(allCards)) {
            typeEnumList.add(QiaomaTypeEnum.HUN_YI_SE);
            res.typeList.add(HuType.HUN_YI_SE);
        }
        // 必须在混一色和碰碰胡后面
        if (isHunPeng(allCards, handCard, showCard)) {
            typeEnumList.remove(QiaomaTypeEnum.PENG_PENG_HU);
            typeEnumList.remove(QiaomaTypeEnum.HUN_YI_SE);
            typeEnumList.add(QiaomaTypeEnum.HUN_PENG);

            res.typeList.remove(HuType.PENG_PENG_HU);
            res.typeList.remove(HuType.HUN_YI_SE);
            res.typeList.add(HuType.HUN_PENG);
        }
        // 必须在清一色和碰碰胡后面
        if (isQingPeng(handCard, showCard, allCards)) {
            typeEnumList.remove(QiaomaTypeEnum.PENG_PENG_HU);
            typeEnumList.remove(QiaomaTypeEnum.QING_YI_SE);
            typeEnumList.add(QiaomaTypeEnum.QING_PENG);

            res.typeList.remove(HuType.PENG_PENG_HU);
            res.typeList.remove(HuType.QING_YI_SE);
            res.typeList.add(HuType.QING_PENG);
        }
        // 必须在最后
        if (isLajiHu(typeEnumList)) {
            typeEnumList.add(QiaomaTypeEnum.LA_JI_HU);
            res.typeList.add(HuType.LA_JI_HU);
        }

        // 计算番数
        for (QiaomaTypeEnum item : typeEnumList)
            res.fanCount += item.fan;
    }

    public void testLezi() {
        ArrayList<HuType> configList = new ArrayList<>();
        // configList.add(HuType.QING_YI_SE);
        configList.add(HuType.QING_PENG);
        // configList.add(HuType.WU_HUA_GUO);

        QiaomaHuTypeResult res = new QiaomaHuTypeResult();
        res.fanCount = 10;
        // res.typeList.add(HuType.QING_PENG);
        res.typeList.add(HuType.WU_HUA_GUO);
        res.typeList.add(HuType.MEN_QING);
        res.typeList.add(HuType.QING_YI_SE);

        leziCalc(res, configList);
        System.out.println(res.fanCount);
        System.out.println(res.leziCount);
    }

    // private void leziCalc2(QiaomaHuTypeResult res, CardSort handCard,
    // List<HuType> leZiTypeList,
    // List<CardList> showCard, List<Integer> allCards, RoleGameInfo
    // roleGameInfo, List<Integer> cards,
    // Game game) {
    // if (leZiTypeList.contains(HuType.LEZI_WU_HUA_GUO)) {
    // if (isWuHuaGuo(roleGameInfo, cards, (QiaoMaRule) game.getRule())) {
    // if (isMenQing(showCard)) {// 如果有门清无花果就算门清无花果的勒子
    // res.leziCount += 1;
    // res.typeList.add(HuType.LEZI_MEN_QING_WU_HUA_GUO);
    // } else {// 无花果的勒子
    // res.leziCount += 0.5f;
    // res.typeList.add(HuType.LEZI_WU_HUA_GUO);
    // }
    // }
    // }
    // if (leZiTypeList.contains(HuType.LEZI_QING_YI_SE)) {
    // if (isQingYiSe(allCards)) {
    // if (isPengPengHu(handCard, showCard)) {// 有清碰，算清碰
    // res.leziCount += 2;
    // res.typeList.add(HuType.LEZI_QING_PENG);
    // } else {
    // res.leziCount += 1;
    // res.typeList.add(HuType.LEZI_QING_YI_SE);
    // }
    // }
    // }
    // }

    private void leziCalc2(QiaomaHuTypeResult res, CardSort handCard, List<CardList> showCard, List<Integer> allCards,
            RoleGameInfo roleGameInfo, List<Integer> cards, Game game) {

        if (isWuHuaGuo(roleGameInfo, cards, (QiaoMaRule) game.getRule())) {
            if (isMenQing(showCard)) {// 如果有门清无花果就算门清无花果的勒子
                res.leziCount += 1;
                res.typeList.add(HuType.LEZI_MEN_QING_WU_HUA_GUO);
            } else {// 无花果的勒子
                res.leziCount += 0.5f;
                res.typeList.add(HuType.LEZI_WU_HUA_GUO);
            }
        }

        if (isQingYiSe(allCards)) {
            if (isPengPengHu(handCard, showCard)) {// 有清碰，算清碰
                res.leziCount += 2;
                res.typeList.add(HuType.LEZI_QING_PENG);
            } else {
                res.leziCount += 1;
                res.typeList.add(HuType.LEZI_QING_YI_SE);
            }
        }

    }

    private void leziCalc(QiaomaHuTypeResult res, List<HuType> configLeziList) {
        if (configLeziList.contains(HuType.QING_PENG) && res.typeList.contains(HuType.QING_PENG)) {
            res.leziCount += QiaomaTypeEnum.QING_PENG.lezi;
            res.fanCount -= QiaomaTypeEnum.QING_PENG.fan;
        } else {
            if (configLeziList.contains(HuType.QING_YI_SE) && res.typeList.contains(HuType.QING_YI_SE)) {
                res.leziCount += QiaomaTypeEnum.QING_YI_SE.lezi;
                res.fanCount -= QiaomaTypeEnum.QING_YI_SE.fan;
            }
        }
        if (configLeziList.contains(HuType.WU_HUA_GUO) && res.typeList.contains(HuType.WU_HUA_GUO)) {
            res.leziCount += QiaomaTypeEnum.WU_HUA_GUO.lezi;
            res.flowerCount -= 10;

            if (res.typeList.contains(HuType.MEN_QING)) {
                res.leziCount += QiaomaTypeEnum.WU_HUA_GUO.lezi;
            }
        }
    }

    // 垃圾胡
    public boolean isLajiHu(List<QiaomaTypeEnum> list) {
        for (QiaomaTypeEnum item : list) {
            if (lajihuTestList.contains(item)) {
                return false;
            }
        }
        return true;
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
        List<Integer> feng = Arrays.asList(4, 5, 6, 7);
        ArrayList<Integer> sfengCards = new ArrayList<>();
        ArrayList<Integer> scards = new ArrayList<>();
        // 把风向牌和序数牌分开赋值
        for (int i = 0; i < allCards.size(); i++) {
            int card = allCards.get(i) / 100;
            if (feng.contains(card)) {// 风向牌
                sfengCards.add(card);
            } else {
                scards.add(card);
            }
        }
        Set<Integer> set = new HashSet<>(scards);
        return set.size() == 1 && sfengCards.size() > 0;
    }

    // 混碰：混一色和碰碰胡组成
    public boolean isHunPeng(List<Integer> allCards, CardSort handCard, List<CardList> showCard) {
        return isHunYiSe(allCards) && isPengPengHu(handCard, showCard);
    }

    // 清碰：全部牌型为一色牌且为碰碰胡组成
    public boolean isQingPeng(CardSort handCard, List<CardList> showCard, List<Integer> allCards) {
        return isPengPengHu(handCard, showCard) && isQingYiSe(allCards);
    }

    // 无花果：没有花牌获得胜利。
    public boolean isWuHuaGuo(RoleGameInfo roleGameInfo, List<Integer> cards, QiaoMaRule rule) {
        int flowerCount = roleGameInfo.flowerCount;
        int darkFlowerCount = rule.getDarkFlowerCount(cards);
        int totalCount = flowerCount + darkFlowerCount;
        return totalCount == 0;
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
            // if (!(item instanceof Peng)) {
            // 杠和碰都算碰
            if (!(item instanceof Peng) && !(item instanceof Gang)) {
                return false;
            }
        }
        int pengCount = 4 - showCard.size();
        Set<Integer> set = handCard.get(2);

        if (set.size() != pengCount) {
            return false;
        }

        CardSort cloneHandCard = handCard.clone();
        for (int i : set) {
            cloneHandCard.remove(i, i, i);
        }
        return cloneHandCard.get(1).size() == 1;
    }

    public static void main(String[] args) {
        HuTypeCalculator cal = new HuTypeCalculator();

        List<Integer> list = Arrays.asList(101, 102, 103, 102, 102, 108, 108, 108);
        CardSort handSort = new CardSort(5);
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

    /** 用于判断是不是垃圾胡的list */
    public List<QiaomaTypeEnum> lajihuTestList = new ArrayList<>(Arrays.asList(QiaomaTypeEnum.QING_PENG,
            QiaomaTypeEnum.HUN_PENG, QiaomaTypeEnum.QING_YI_SE, QiaomaTypeEnum.PENG_PENG_HU, QiaomaTypeEnum.HUN_YI_SE,
            QiaomaTypeEnum.WU_HUA_GUO));

    public enum QiaomaTypeEnum {
        // 清碰：全部牌型为一色牌且为碰碰胡组成
        QING_PENG(3, 2),
        // 混碰：全部牌型为一色牌和风向组成
        HUN_PENG(2),
        // 清一色：全部牌型为一色牌组成
        QING_YI_SE(2, 1),
        // 碰碰胡：全由碰所产生的胡牌
        PENG_PENG_HU(1),
        // 混一色：全由序数牌加风向组成的牌
        HUN_YI_SE(1),
        // 杠开：当摸到花或者杠时从背后摸一张牌，由该牌胡牌
        GANG_KAI(1),
        // 门清：该局游戏没有进行过吃，碰，明杠操作
        MEN_QING(1),
        // 无花果：没有花牌获得胜利。
        WU_HUA_GUO(0, 0.5f),
        // 大吊车：当前牌只剩下最后一个时，胡牌。
        DA_DIAO_CHE(1),
        // 海底捞：摸最后一张牌时胡牌
        HAI_DI_LAO(1),
        // 垃圾胡
        LA_JI_HU(0);

        public int fan;
        public float lezi;

        QiaomaTypeEnum(int fan, float lezi) {
            this.fan = fan;
            this.lezi = lezi;
        }

        QiaomaTypeEnum(int fan) {
            this.fan = fan;
            this.lezi = 0;
        }
    }
}
