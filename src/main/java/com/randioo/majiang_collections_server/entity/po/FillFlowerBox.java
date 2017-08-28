/**
 * 
 */
package com.randioo.majiang_collections_server.entity.po;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.randioo.mahjong_public_server.protocol.Entity.FillFlowerUnit;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightFillFlower;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightFillFlower.Builder;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.majiang_collections_server.module.fight.component.BaidaMajiangRule;

/**
 * @Description:
 * @author zsy
 * @date 2017年8月23日 上午9:35:57
 */
public class FillFlowerBox {
    private List<Line> box;

    public FillFlowerBox() {
        this.box = new ArrayList<Line>();
    }

    public class Line {
        public List<Integer> cards; // 普通牌
        public List<Integer> flowers; // 花牌

        Line() {
            this.cards = new ArrayList<Integer>();
            this.flowers = new ArrayList<Integer>();
        }

        @Override
        public String toString() {
            return cards + "\t" + flowers + "\r\n";
        }
    }

    public void addLine(List<Integer> cards) {
        if (cards.size() == 0) {
            return;
        }
        Line line = new Line();
        for (Integer card : cards) {
            if (isHua(card)) {
                line.flowers.add(card);
            } else {
                line.cards.add(card);
            }
        }
        this.box.add(line);
    }

    private boolean isHua(int card) {
        return BaidaMajiangRule.HUA_CARDS.contains(card / 100);
    }

    /**
     * 获得所有的普通牌
     * 
     * @return
     */
    public List<Integer> getOrdinaryCards() {
        List<Integer> list = new ArrayList<Integer>();
        for (Line line : box) {
            list.addAll(line.cards);
        }
        return list;
    }

    /**
     * 获得所遇的花牌
     * 
     * @return
     */
    public List<Integer> getFlowerCards() {
        List<Integer> list = new ArrayList<Integer>();
        for (Line line : box) {
            list.addAll(line.flowers);
        }
        return list;
    }

    public List<SC> toProtocol() {
        List<SC> list = new ArrayList<SC>();
        // list第一个元素发给补花的本人，其他三个元素发给其他人
        for (int i = 0; i < 4; i++) {
            Builder scFightFillFlower = SCFightFillFlower.newBuilder();
            for (Line line : box) {
                FillFlowerUnit.Builder unit = FillFlowerUnit.newBuilder()
                        .addAllOrdinaryCards(i == 0 ? line.cards : Arrays.asList(0)).addAllFlowerCards(line.flowers);
                scFightFillFlower.addFillFlowerCards(unit);
            }
            SC sc = SC.newBuilder().setSCFightFillFlower(scFightFillFlower).build();
            list.add(sc);
        }
        return list;
    }

    public List<Line> getBox() {
        return box;
    }

    @Override
    public String toString() {
        return box.toString();
    }

}
