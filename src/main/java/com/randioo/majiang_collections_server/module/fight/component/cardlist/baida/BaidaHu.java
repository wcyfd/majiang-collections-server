package com.randioo.majiang_collections_server.module.fight.component.cardlist.baida;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.randioo.mahjong_public_server.protocol.Entity.OverMethod;
import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.po.CardSort;
import com.randioo.majiang_collections_server.module.fight.component.BaidaMajiangRule;
import com.randioo.majiang_collections_server.module.fight.component.MajiangRule;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.CardList;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Hu;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.SuperHu;
import com.randioo.majiang_collections_server.util.Lists;
import com.randioo.majiang_collections_server.util.Sets;

/**
 * 陈超想了一种新的算法，替换了朱一峰的胡，在我写下这句注释时<br>
 * 此算法的在我大脑中的内存开始清空<br>
 * 如果算法出错，找陈超，戴海涛，葛志宇，千万别找朱一峰
 * 
 * @author wcy 2017年9月1日
 *
 */
@Component
public class BaidaHu extends Hu {

    /** 寻找所有的胡 */
    private boolean findAll;
    /** 调试打印 */
    private boolean print;

    @Override
    public void check(Game game, List<CardList> cardLists, CardSort cardSort, int card, List<CardList> showCardList,
            boolean isMine) {
        MajiangRule rule = game.getRule();
        int baidaCard = rule.getBaidaCard(game);
        // 是不是跑百搭
        boolean isPaoBaida = true;
        // boolean hasHu = this.checkHu(cardSort, baidaCard);
        // if (!hasHu) {
        // return;
        // }
        boolean hasHu = SuperHu.checkHu(cardSort, baidaCard);
        if (!hasHu) {
            return;
        }
        // 检测跑百搭
        CardSort copyCardSort = cardSort.clone();
        copyCardSort.remove(card);
        for (int i : BaidaMajiangRule.TEST_BAI_DAI) {
            copyCardSort.addCard(i);
            // 使用新的胡牌算法
            // if (!checkHu(copyCardSort, baidaCard)) {
            if (!SuperHu.checkHu(copyCardSort, baidaCard)) {
                // 有一个不能胡 就不是跑百搭
                isPaoBaida = false;
                break;
            }
            copyCardSort.remove(i);
        }
        BaidaHu hu = new BaidaHu();
        List<Integer> list = cardSort.toArray();
        hu.card = card;
        hu.isMine = isMine;
        hu.isPaoBaiDa = isPaoBaida;
        Lists.removeElementByList(list, Arrays.asList(card));
        Collections.sort(list);
        hu.handCards.addAll(list);
        hu.showCardList.addAll(showCardList);
        if (isMine) {
            hu.setTargetSeat(-1);
            hu.overMethod = OverMethod.MO_HU;
        } else {
            hu.setTargetSeat(game.sendCardSeat);
            hu.overMethod = OverMethod.ZHUA_HU;
        }

        cardLists.add(hu);
    }

    private void print(List<Integer> pengList, List<Integer> chiList, List<Integer> singleList) {
        pf("碰的列表={0} ", pengList);
        pf("吃的列表={0} ", chiList);
        pfln("剩余的列表={0} ", singleList);
    }

    /**
     * 杠后能听牌的变化
     * 
     * @param cards 去除杠的手牌
     * @return
     */
    public List<Integer> checkTingCards(List<Integer> cards) {
        List<Integer> res = new ArrayList<>();
        List<Integer> testCards = BaidaMajiangRule.TEST_BAI_DAI;
        for (Integer card : testCards) {
            CardSort cardSort = new CardSort(5);
            cardSort.fillCardSort(cards);
            cardSort.addCard(card);
            if (checkHu(cardSort, 0)) {
                res.add(card);
            }
        }
        return res;
    }

    private boolean checkHu(CardSort cardSort, int baidaCard) {

        CardSort cloneCardSort = cardSort.clone();
        // 1.移除所有红中
        int baidaCount = cloneCardSort.removeAll(baidaCard);
        List<Integer> cards = cloneCardSort.toArray();

        pfln("移除百搭后的剩余牌{0}", cards);

        // 说明都是百搭牌，可以胡
        if (cards.size() == 0) {
            pfln("全是百搭牌,可以胡");
            return true;
        }
        Collections.sort(cards);

        // 2.先找碰，再找顺
        List<Integer> pengList = new ArrayList<>();
        List<Integer> chiList = new ArrayList<>();
        List<Integer> singleList = new ArrayList<>();
        // 设置当前索引
        int sum = cloneCardSort.sumCard();
        for (int startIndex = 0; startIndex < sum; startIndex++) {
            pfln("");
            pfln("////////////////////////////////////////////////");
            List<Integer> cloneCards = cloneCardSort.toArray();
            Collections.sort(cloneCards);

            pfln("牌组={0}", cloneCards);

            pfln("从索引=================[ {0} ]=================开始,值为{1}", startIndex, cloneCards.get(startIndex));
            // 移除索引前所有的牌,不包括索引
            this.removeBeforeStartIndexElement(singleList, cloneCards, startIndex);
            print(pengList, chiList, singleList);

            // 3.取三
            // 拿出当前索引牌,如果没有剩余牌则跳出循环
            while (cloneCards.size() != 0) {
                this.remove3(pengList, chiList, singleList, cloneCards);
            }

            pfln("取三完毕");
            pfln("");
            print(pengList, chiList, singleList);

            if (singleList.size() == 0) {
                pfln("取三完毕后没有留下来的剩余牌就直接胡了");
                return true;
            }

            // // //////////////////////////////
            if (check2(singleList, baidaCount)) {
                return true;
            }

            pengList.clear();
            chiList.clear();
            singleList.clear();
            pfln("");
        }

        return false;
    }

    public void pf(String format, Object... values) {
        if (print) {
            String value = MessageFormat.format(format, values);
            value = this.replaceCardNum2Str(value);
            System.out.print(value);
        }
    }

    public void pfln(String format, Object... values) {
        if (print) {
            String value = MessageFormat.format(format, values);
            value = this.replaceCardNum2Str(value);
            System.out.println(value);
        }
    }

    public String replaceCardNum2Str(String value) {

        value = value.replaceAll("101", "一条");
        value = value.replaceAll("102", "二条");
        value = value.replaceAll("103", "三条");
        value = value.replaceAll("104", "四条");
        value = value.replaceAll("105", "五条");
        value = value.replaceAll("106", "六条");
        value = value.replaceAll("107", "七条");
        value = value.replaceAll("108", "八条");
        value = value.replaceAll("109", "九条");

        value = value.replaceAll("201", "一筒");
        value = value.replaceAll("202", "二筒");
        value = value.replaceAll("203", "三筒");
        value = value.replaceAll("204", "四筒");
        value = value.replaceAll("205", "五筒");
        value = value.replaceAll("206", "六筒");
        value = value.replaceAll("207", "七筒");
        value = value.replaceAll("208", "八筒");
        value = value.replaceAll("209", "九筒");

        value = value.replaceAll("301", "一万");
        value = value.replaceAll("302", "二万");
        value = value.replaceAll("303", "三万");
        value = value.replaceAll("304", "四万");
        value = value.replaceAll("305", "五万");
        value = value.replaceAll("306", "六万");
        value = value.replaceAll("307", "七万");
        value = value.replaceAll("308", "八万");
        value = value.replaceAll("309", "九万");

        value = value.replaceAll("801", "红中");

        return value;
    }

    /**
     * 检查将牌
     * 
     * @param cards
     * @param baidaCount
     * @return
     * @author wcy 2017年9月1日
     */
    private boolean check2(List<Integer> cards, int baidaCount) {
        CardSort singleCardSort = new CardSort(5);
        singleCardSort.fillCardSort(cards);
        Set<Integer> set1 = singleCardSort.get(1);
        // 如果有将牌
        if (set1.size() == 1) {
            pfln("只有一个对子={0}", set1);
            int need = 0;
            CardSort cloneCardSort = singleCardSort.clone();

            // 移除将牌
            List<Integer> list = new ArrayList<>(set1);
            int card = list.get(0);
            cloneCardSort.remove(card, card);

            List<Integer> remainCards = cloneCardSort.toArray();

            pfln("移除将牌{0},剩余牌{1}", card, remainCards);

            need = singleCardNeedBaidaCount(remainCards, need);

            // 如果百搭数量足够就胡
            if (need <= baidaCount) {
                pfln("取三后的牌{0}加{1}张百搭牌,可胡", cards, baidaCount);
                if (!findAll) {
                    return true;
                }
            } else {
                pfln("取三后的牌{0}加现有{1}张百搭牌,不可胡,需要{2}张百搭牌", cards, baidaCount, need);
            }

        } else if (set1.size() == 0) {
            CardSort cloneCardSort = singleCardSort.clone();
            Set<Integer> set0 = cloneCardSort.get(0);
            pfln("一个对子都没有,剩下的单张是{0}", set0);
            for (int card : set0) {
                int need = 0;
                List<Integer> remainCards = cloneCardSort.toArray();
                Lists.removeElementByList(remainCards, Arrays.asList(card));
                pfln("{0}需要配一张百搭变成对子", card);
                need++;
                need = singleCardNeedBaidaCount(remainCards, need);
                // 如果百搭数量足够就胡
                if (need <= baidaCount) {
                    pfln("取三后的牌{0}加{1}张百搭牌,可胡", cards, baidaCount);
                    if (!findAll) {
                        return true;
                    }
                } else {
                    pfln("取三后的牌{0}加现有{1}张百搭牌,不可胡,需要{2}张百搭牌", cards, baidaCount, need);
                }

            }

        } else if (set1.size() > 1) {
            {
                pfln("多于一个对子{0}", set1);
                CardSort cloneCardSort = singleCardSort.clone();

                List<Integer> twoList = new ArrayList<>(cloneCardSort.get(1));
                for (int i = 0; i < twoList.size(); i++) {
                    int c = twoList.get(i);
                    cloneCardSort.remove(c, c);
                }

                List<Integer> oneList = new ArrayList<>(cloneCardSort.toArray());

                pfln("将对子的列表和单张的列表分开,单张列表={0},对子列表={1}", oneList, twoList);
                for (int card : twoList) {
                    List<Integer> twoList2 = new ArrayList<>(twoList);
                    List<Integer> oneList2 = new ArrayList<>(oneList);
                    int need = 0;

                    // 移除将牌
                    pfln("移除将牌{0}", card);
                    Lists.removeElementByList(twoList2, Arrays.asList(card));

                    pfln("走剩下的对子{0}，检查是否能组成3", twoList2);
                    // 走剩下的对子，检查是否能组成3
                    for (int j = twoList2.size() - 1; j >= 0; j--) {
                        int duiziCard = twoList2.get(j);
                        // 拿对子到单张中去找碰，如果有就移除
                        pfln("拿对子{0}到单张中去找碰，如果有就移除", duiziCard);
                        if (oneList2.contains(duiziCard)) {
                            pfln("对子牌{0}碰成功,移除", duiziCard);
                            // 对子牌移除
                            twoList2.remove(j);
                            // 单张牌移除
                            Lists.removeElementByList(oneList2, Arrays.asList(duiziCard));
                            pfln("剩余单张数组{0}", oneList2);
                        }

                    }

                    // 没有组成3的对子,则要加百搭
                    need += twoList2.size();
                    pfln("对子{0}没有组成3的对子,则要加{1}张百搭牌", twoList2, twoList2.size());

                    // 没有对子了，那就检查单张
                    need = singleCardNeedBaidaCount(oneList2, need);

                    // 如果百搭数量足够就胡
                    if (need <= baidaCount) {
                        pfln("取三后的牌{0}加{1}张百搭牌,可胡", cards, baidaCount);
                        if (!findAll) {
                            return true;
                        }
                    } else {
                        pfln("取三后的牌{0}加现有{1}张百搭牌,不可胡,需要{2}张百搭牌", cards, baidaCount, need);
                    }

                }
            }
            // ==================================================================================================
            {
                pfln("多于一个对子{0}", set1);
                CardSort cloneCardSort = singleCardSort.clone();

                List<Integer> twoList = new ArrayList<>(cloneCardSort.get(1));
                for (int i = 0; i < twoList.size(); i++) {
                    int c = twoList.get(i);
                    cloneCardSort.remove(c, c);
                }

                List<Integer> oneList = new ArrayList<>(cloneCardSort.toArray());

                pfln("将对子的列表和单张的列表分开,单张列表={0},对子列表={1}", oneList, twoList);
                for (int card : twoList) {
                    List<Integer> twoList2 = new ArrayList<>(twoList);
                    List<Integer> oneList2 = new ArrayList<>(oneList);

                    // 移除将牌
                    pfln("移除将牌{0}", card);
                    Lists.removeElementByList(twoList2, Arrays.asList(card));

                    for (int duizi : twoList2) {
                        oneList2.add(duizi);
                        oneList2.add(duizi);
                    }
                    pfln("剩余牌都当作单牌{0}", oneList2);
                    Collections.sort(oneList2);

                    for (int i = 0; i < oneList2.size(); i++) {
                        Set<Integer> indexSet = new HashSet<>();
                        int count1 = getChiCountAndRecordUseIndex(oneList2, i, oneList2.size(), indexSet);
                        int count2 = getChiCountAndRecordUseIndex(oneList2, 0, oneList2.size(), indexSet);
                        int needCount = count1 + count2;
                        if (needCount <= baidaCount) {
                            if (!findAll) {
                                System.out.println("可胡");
                                return true;
                            }
                        } else {
                            pfln("不可胡，继续");
                        }
                    }

                    // 如果百搭数量足够就胡

                    pfln("不可胡");

                }
            }
        }
        return false;
    }

    /**
     * 单张牌需要百搭的数量
     * 
     * @param oneList
     * @param need
     * @return
     * @author wcy 2017年9月1日
     */
    private int singleCardNeedBaidaCount(List<Integer> oneList, int need) {
        while (oneList.size() != 0) {
            int c1 = oneList.get(0);
            int c2 = c1 + 1;
            int c3 = c1 + 2;
            if (oneList.contains(c2) && oneList.contains(c3)) {
                pfln("牌{0}组成吃成功找到了{1}和{2}", c1, c2, c3);
                Lists.removeElementByList(oneList, Arrays.asList(c1, c2, c3));
            } else {
                if (oneList.contains(c2)) {
                    Lists.removeElementByList(oneList, Arrays.asList(c1, c2));
                    need++;
                    pfln("牌{0}组成吃除了现有的{1}还需要一张百搭", c1, c2);
                } else if (oneList.contains(c3)) {
                    Lists.removeElementByList(oneList, Arrays.asList(c1, c3));
                    pfln("牌{0}组成吃除了现有的{1}还需要一张百搭", c1, c3);
                    need++;
                } else {
                    pfln("牌{0}组成吃需要两张百搭", c1);
                    oneList.remove(0);
                    need += 2;
                }

            }
        }

        return need;
    }

    private void remove3(List<Integer> pengList, List<Integer> chiList, List<Integer> singleList, List<Integer> cards) {
        int baseValue = cards.remove(0);
        pfln("当前要用于取三的牌={0}", baseValue);
        // 移除该牌的碰
        boolean removeSuccess = this.removePeng(baseValue, cards);

        // 移除成功,加入到3的列表
        if (removeSuccess) {
            pengList.add(baseValue);
            pengList.add(baseValue);
            pengList.add(baseValue);
            print(pengList, chiList, singleList);
            return;
        }

        removeSuccess = this.removeChi(baseValue, cards);

        // 移除成功,加入到3的列表
        if (removeSuccess) {
            chiList.add(baseValue);
            chiList.add(baseValue + 1);
            chiList.add(baseValue + 2);
            print(pengList, chiList, singleList);
            return;
        }

        // 碰和吃都没有则加入到单牌列表
        singleList.add(baseValue);
        print(pengList, chiList, singleList);
    }

    /**
     * 移除cards 中 targetIndex索引之前的牌
     * 
     * @param singleList 单牌的存储列表
     * @param cards 牌列表
     * @param targetIndex
     * @author wcy 2017年9月1日
     */
    private void removeBeforeStartIndexElement(List<Integer> singleList, List<Integer> cards, int targetIndex) {
        // System.out.println(MessageFormat.format(
        // "NewHu.removeBeforeStartIndexElement(singleList={0} cards={1}
        // targetIndex={2})",
        // singleList, cards,
        // targetIndex));
        if (targetIndex == 0) {
            return;
        }

        for (int i = targetIndex - 1; i >= 0; i--) {
            singleList.add(0, cards.remove(i));
        }
    }

    /**
     * 移除碰
     * 
     * @param baseValue
     * @param indexSet
     * @param cardSort
     * @author wcy 2017年9月1日
     */
    private boolean removePeng(Integer baseValue, List<Integer> cards) {
        // System.out.println(MessageFormat.format("NewHu.removePeng(baseValue={0}
        // cards={1})",
        // baseValue, cards));

        int size = Lists.containsCount(cards, baseValue);
        if (size >= 2) {
            List<Integer> removeList = Arrays.asList(baseValue, baseValue);
            Lists.removeElementByList(cards, removeList);
            return true;
        }

        return false;

    }

    /**
     * 移除吃
     * 
     * @param baseValue
     * @param needRemoveList
     * @param cardSort
     * @author wcy 2017年9月1日
     */
    private boolean removeChi(int baseValue, List<Integer> cards) {
        // System.out.println(MessageFormat.format("NewHu.removeChi(baseValue={0}
        // cards={1})",
        // baseValue, cards));
        int two = baseValue + 1;
        int three = baseValue + 2;
        if (cards.contains(two) && cards.contains(three)) {
            List<Integer> removeList = Arrays.asList(two, three);
            Lists.removeElementByList(cards, removeList);
            return true;
        }
        return false;
    }

    @Override
    public List<Integer> getCards() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return "cardList:hu=>gangkai=" + gangKai + ",isMine=" + isMine + ",card=" + card + "," + super.toString();
    }

    @Override
    public boolean checkTing(Game game, CardSort cardSort, List<Integer> waitCards) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * 找到没有使用过的指定对象索引
     * 
     * @param cards
     * @param indexSet 使用过的对象索引
     * @param startIndex
     * @param card
     * @return
     * @author wcy 2017年7月21日
     */
    private int findUnuseCardIndex(List<Integer> cards, Set<Integer> indexSet, int startIndex, int endIndex, int card) {
        int c2Index = Lists.indexOf(cards, startIndex, endIndex, card);
        if (c2Index == -1)
            return -1;
        while (indexSet.contains(c2Index)) {
            c2Index++;
            if (c2Index > endIndex)
                return -1;
            c2Index = Lists.indexOf(cards, c2Index, endIndex, card);
        }

        return c2Index;
    }

    public int getChiCountAndRecordUseIndex(List<Integer> cards, int startIndex, int endIndex, Set<Integer> indexSet) {
        int count = 0;
        for (int i = startIndex; i < endIndex; i++) {
            // 如果该索引已经使用过了,则直接继续
            if (indexSet.contains(i))
                continue;
            int c1 = cards.get(i);

            int c2Index = findUnuseCardIndex(cards, indexSet, i, endIndex, c1 + 1);
            int c3Index = findUnuseCardIndex(cards, indexSet, i, endIndex, c1 + 2);

            // 检查有没有这个吃
            if (c2Index >= 0 && c3Index >= 0) {
                // 加入index
                Sets.add(indexSet, i, c2Index, c3Index);
                pfln("成功找到吃{0},{1},{2}", cards.get(i), cards.get(c2Index), cards.get(c3Index));
                continue;
            } else if (c2Index == -1 && c3Index != -1) { // 如果有红中,则使用红中
                Sets.add(indexSet, i, c3Index);
                count++;
                pfln("缺少一张组成吃{0},{1}需要一张百搭牌", cards.get(i), cards.get(c3Index));
                continue;
            } else if (c2Index != -1 && c3Index == -1) {
                count++;
                Sets.add(indexSet, i, c2Index);
                pfln("缺少一张组成吃{0},{1}需要一张百搭牌", cards.get(i), cards.get(c2Index));
                continue;
            } else if (c2Index == -1 && c3Index == -1) {
                count += 2;
                Sets.add(indexSet, i);
                pfln("缺少两张组成吃{0}需要一张百搭牌", cards.get(i));
                continue;
            }

        }

        return count;
    }
}
