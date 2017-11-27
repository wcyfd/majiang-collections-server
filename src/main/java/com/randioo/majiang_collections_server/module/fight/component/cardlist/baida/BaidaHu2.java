/**
 * 
 */
package com.randioo.majiang_collections_server.module.fight.component.cardlist.baida;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.po.CardSort;
import com.randioo.majiang_collections_server.module.fight.component.BaidaMajiangRule;
import com.randioo.majiang_collections_server.module.fight.component.MajiangRule;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.CardList;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Hu;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.ZLPBaiDaHu;
import com.randioo.majiang_collections_server.util.Lists;
import com.randioo.majiang_collections_server.util.Sets;
import com.randioo.randioo_server_base.template.Ref;

/**
 * @Description:
 * @author zsy
 * @date 2017年8月30日 上午9:15:19
 */
public class BaidaHu2 extends Hu {

    private Logger logger = LoggerFactory.getLogger(ZLPBaiDaHu.class.getSimpleName());

    @Override
    public void check(Game game, List<CardList> cardLists, CardSort cardSort, int card, List<CardList> showCardList,
            boolean isMine) {
        MajiangRule rule = game.getRule();
        GameConfigData gameConfigData = game.getGameConfig();
        int baidaCard = rule.getBaidaCard(game);

        // 是不是跑百搭
        boolean isPaoBaida = true;
        boolean hasHu = this.checkHu(gameConfigData, cardSort, baidaCard);
        if (!hasHu) {
            return;
        }
        // 检测跑百搭
        cardSort.remove(card);
        for (int i : BaidaMajiangRule.TEST_BAI_DAI) {
            cardSort.addCard(i);
            // if (!checkHu(gameConfigData, cardSort, baidaCard)) {
            // 使用新的胡牌算法
            if (!checkHu(cardSort, baidaCard)) {
                // 有一个不能胡 就不是跑百搭
                isPaoBaida = false;
                break;
            }
            cardSort.remove(i);
        }

        BaidaHu2 hu = new BaidaHu2();
        List<Integer> list = cardSort.toArray();
        hu.card = card;
        hu.isMine = isMine;
        hu.isPaoBaiDa = isPaoBaida;
        Lists.removeElementByList(list, Arrays.asList(card));
        Collections.sort(list);
        hu.handCards.addAll(list);
        hu.showCardList.addAll(showCardList);

        cardLists.add(hu);

    }

    private boolean checkHu(GameConfigData gameConfigData, CardSort cardSort, int baida) {
        boolean debug = true;
        // 1.克隆牌组
        CardSort cardSort1 = cardSort.clone();

        List<Integer> l = cardSort1.toArray();
        Collections.sort(l);
        System.out.println(l);

        // 2.去除所有的白搭
        int baiDaCount = cardSort1.removeAll(baida);

        // 只剩下百搭牌,肯定可以胡
        if (cardSort1.sumCard() == 0) {
            return true;
        }

        // 3.三个一样的先拿走
        List<Integer> kezi_arr = new ArrayList<>(cardSort1.get(2));
        for (int kezi : kezi_arr)
            cardSort1.remove(kezi, kezi, kezi);

        // 4.以每个数字为基准,分别从头到尾吃一遍
        Set<Integer> indexSet = new HashSet<>();
        {
            List<Integer> cards = cardSort1.toArray();
            Collections.sort(cards);

            for (int startIndex = 0; startIndex < cards.size(); startIndex++) {
                logger.debug(startIndex + "");
                int step4chiCount = kezi_arr.size();
                Ref<Integer> baiDaCountRef = new Ref<>();
                baiDaCountRef.set(baiDaCount);
                step4chiCount += getLoopChiCount(cards, baiDaCountRef, startIndex, indexSet);
                logger.debug("step4chiCount=" + step4chiCount);
                List<Integer> cloneCards = new ArrayList<>(cards);
                Lists.removeAllIndex(cloneCards, new ArrayList<>(indexSet));
                logger.debug("remain=" + cloneCards);
                if (checkOnlyJiangCards(baiDaCountRef, cloneCards)) {
                    // 可以胡
                    logger.debug("hu");
                    if (debug)
                        return true;
                }
            }

        }

        System.out.println("//////////////////////////////");

        // 5.刻子拿回来以每个数字为基准,分别从头到尾吃一遍,但有四个相同的先拿走三个
        {
            for (int kezi : kezi_arr) {
                cardSort1.addCard(kezi);
                cardSort1.addCard(kezi);
                cardSort1.addCard(kezi);
            }

            List<Integer> gangzi_arr = new ArrayList<>(cardSort1.get(3));
            for (int gangzi : gangzi_arr)
                cardSort1.remove(gangzi, gangzi, gangzi);

            List<Integer> cards = cardSort1.toArray();
            Collections.sort(cards);

            for (int startIndex = 0; startIndex < cards.size(); startIndex++) {
                int step5chiCount = kezi_arr.size();
                Ref<Integer> baiDaCountRef = new Ref<>();
                baiDaCountRef.set(baiDaCount);
                step5chiCount += getLoopChiCount(cards, baiDaCountRef, startIndex, indexSet);
                logger.debug("step5chiCount=" + step5chiCount);
                List<Integer> cloneCards = new ArrayList<>(cards);
                Lists.removeAllIndex(cloneCards, new ArrayList<>(indexSet));

                step5chiCount += check3(cloneCards);
                logger.debug("remain=" + cloneCards);
                if (checkOnlyJiangCards(baiDaCountRef, cloneCards)) {
                    // 可以胡
                    logger.debug("hu");
                    if (debug)
                        return true;
                }
            }
        }

        // 6.如果都没有胡,则先选择碰,再选择吃,三个一样的先拿走
        for (int kezi : kezi_arr)
            cardSort1.remove(kezi, kezi, kezi);

        {
            List<Integer> cards = cardSort1.toArray();
            Collections.sort(cards);
            for (int startIndex = 0; startIndex < cards.size(); startIndex++) {
                int step6pengChiCount = kezi_arr.size();
                Ref<Integer> baiDaCountRef = new Ref<>();
                baiDaCountRef.set(baiDaCount);
                step6pengChiCount += getLoopPengChiCount(cards, baiDaCountRef, startIndex, indexSet);
                logger.debug("step6pengChiCount=" + step6pengChiCount);
                List<Integer> cloneCards = new ArrayList<>(cards);
                Lists.removeAllIndex(cloneCards, new ArrayList<>(indexSet));
                logger.debug("remain=" + cloneCards);
                if (checkOnlyJiangCards(baiDaCountRef, cloneCards)) {
                    logger.debug("hu");
                    if (debug)
                        return true;
                }
            }
        }

        // 7.刻子放回去,先选择碰,再选择吃,但有四个相同的先拿走三个
        {
            List<Integer> gangzi_arr = new ArrayList<>(cardSort1.get(3));
            for (int gangzi : gangzi_arr)
                cardSort1.remove(gangzi, gangzi, gangzi);

            List<Integer> cards = cardSort1.toArray();
            Collections.sort(cards);
            for (int startIndex = 0; startIndex < cards.size(); startIndex++) {
                int step7pengChiCount = kezi_arr.size();
                Ref<Integer> baiDaCountRef = new Ref<>();
                baiDaCountRef.set(baiDaCount);
                step7pengChiCount += getLoopPengChiCount(cards, baiDaCountRef, startIndex, indexSet);
                logger.debug("step6pengChiCount=" + step7pengChiCount);
                List<Integer> cloneCards = new ArrayList<>(cards);
                Lists.removeAllIndex(cloneCards, new ArrayList<>(indexSet));
                logger.debug("remain=" + cloneCards);
                step7pengChiCount += check3(cloneCards);

                if (checkOnlyJiangCards(baiDaCountRef, cloneCards)) {
                    logger.debug("hu");
                    if (debug)
                        return true;
                }
            }
        }

        return false;
    }

    /**
     * 检查是否只剩下将牌,凯恩改进查将牌算法，原来写错的在上面
     * 
     * @param baiDaCountRef
     * @param remainCards
     * @return
     * @author wcy 2017年7月24日
     */
    private boolean checkOnlyJiangCards(Ref<Integer> baiDaCountRef, List<Integer> remainCards) {
        int baiDaCount = baiDaCountRef.get();
        int totalCount = baiDaCount + remainCards.size();
        if ((totalCount - 2) % 3 != 0) {
            return false;
        }

        // 如果剩余两张牌，比较一下之间的大小
        if (totalCount == 2) {
            CardSort cardSort = new CardSort(5);
            cardSort.fillCardSort(remainCards);
            Set<Integer> set0 = cardSort.get(0);
            Set<Integer> set1 = cardSort.get(1);
            if (set1.size() == 1) {
                return true;
            }
            if (set0.size() == 1 && set1.size() == 0) {
                return true;
            }
            if (set0.size() == 0) {
                return true;
            }
        } else {
            if (this.remainCards(baiDaCount, totalCount, remainCards)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 只可能存在2 5 8 11 14张总数
     * 
     * @param totalCount
     * @param cards
     * @return
     * @author wcy 2017年8月30日
     */
    private boolean remainCards(int baidaCount, int totalCount, List<Integer> cards) {
        CardSort cardSort = new CardSort(5);
        cardSort.fillCardSort(cards);
        Set<Integer> set0 = cardSort.get(0);
        Set<Integer> set1 = cardSort.get(1);

        int needBaidaCount = needBaiDaCard(totalCount);
        if (needBaidaCount <= baidaCount || needBaidaCount == -1) {
            return false;
        }

        return set1.size() == set0.size();
    }

    /**
     * 剩余牌总数所需要的百搭牌
     * 
     * @param totalCount
     * @return
     * @author wcy 2017年8月30日
     */
    private int needBaiDaCard(int totalCount) {
        switch (totalCount) {
        case 2:
            return 1;
        case 5:
            return 1;
        case 8:
            return 2;
        case 11:
            return 3;
        case 14:
            return 4;
        default:
            return -1;
        }
    }

    private int check3(List<Integer> cloneCards) {
        int count = 0;
        for (int v = cloneCards.size() - 1; v >= 0; v--) {
            int remainCard = cloneCards.get(v);
            int value = Lists.containsCount(cloneCards, remainCard);
            if (value == 3) {
                count++;
                Lists.removeElementByList(cloneCards, Arrays.asList(remainCard, remainCard, remainCard));
                // 复位
                v = cloneCards.size();
            }
        }
        return count;
    }

    public int getLoopChiCount(List<Integer> cards, Ref<Integer> baiDaCountRef, int startIndex, Set<Integer> indexSet) {
        indexSet.clear();
        int count1 = getChiCountAndRecordUseIndex(cards, baiDaCountRef, startIndex, cards.size(), indexSet);
        int count2 = getChiCountAndRecordUseIndex(cards, baiDaCountRef, 0, cards.size(), indexSet);
        return count1 + count2;
    }

    /**
     * 获得吃的数量并记录使用过的位置
     * 
     * @param cards
     * @param baiDaCountRef
     * @param startIndex
     * @param endIndex
     * @param indexSet
     * @return
     * @author wcy 2017年7月21日
     */
    public int getChiCountAndRecordUseIndex(List<Integer> cards, Ref<Integer> baiDaCountRef, int startIndex,
            int endIndex, Set<Integer> indexSet) {
        int count = 0;
        for (int i = startIndex; i < endIndex; i++) {
            // 如果该索引已经使用过了,则直接继续
            if (indexSet.contains(i))
                continue;
            int c1 = cards.get(i);

            // // 超出边界则直接跳过
            // if ((c1 + 1) % 100 >= 10)
            // continue;
            //
            // if ((c1 - 1) % 100 >= 10)
            // continue;

            int c2Index = findUnuseCardIndex(cards, indexSet, i, endIndex, c1 + 1);
            int c3Index = findUnuseCardIndex(cards, indexSet, i, endIndex, c1 + 2);

            // 检查有没有这个吃
            if (c2Index >= 0 && c3Index >= 0) {
                // 加入index
                Sets.add(indexSet, i, c2Index, c3Index);
                count++;
                continue;
            } else if (c2Index == -1 && c3Index != -1) { // 如果有红中,则使用红中
                if (baiDaCountRef.get() >= 1) {
                    baiDaCountRef.set(baiDaCountRef.get() - 1);
                    Sets.add(indexSet, i, c3Index);
                    count++;
                    continue;
                }
            } else if (c2Index != -1 && c3Index == -1) {
                if (baiDaCountRef.get() >= 1) {
                    baiDaCountRef.set(baiDaCountRef.get() - 1);
                    count++;
                    Sets.add(indexSet, i, c2Index);
                    continue;
                }
            } else if (c2Index == -1 && c3Index == -1) {
                if (baiDaCountRef.get() >= 2) {
                    baiDaCountRef.set(baiDaCountRef.get() - 2);
                    count++;
                    Sets.add(indexSet, i);
                    continue;
                }
            }

        }

        return count;
    }

    /**
     * 找到没有使用过的指定对象索引
     * 
     * @param cards
     * @param indexSet
     *            使用过的对象索引
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

    public int getLoopPengChiCount(List<Integer> cards, Ref<Integer> baiDaCountRef, int startIndex,
            Set<Integer> indexSet) {
        indexSet.clear();
        int count1 = getPengAndChiCountAndRecordUnuseIndex(cards, baiDaCountRef, startIndex, cards.size(), indexSet);
        int count2 = getPengAndChiCountAndRecordUnuseIndex(cards, baiDaCountRef, 0, cards.size(), indexSet);
        return count1 + count2;
    }

    public int getPengAndChiCountAndRecordUnuseIndex(List<Integer> cards, Ref<Integer> baiDaCountRef, int startIndex,
            int endIndex, Set<Integer> indexSet) {
        int count = 0;
        for (int i = startIndex; i < endIndex; i++) {
            // 如果该索引已经使用过了,则直接继续
            if (indexSet.contains(i))
                continue;
            int c1 = cards.get(i);

            if (i + 1 < endIndex) {
                // 找对子
                int c2 = cards.get(i + 1);
                if (c1 == c2) {
                    if (baiDaCountRef.get() > 0) {
                        baiDaCountRef.set(baiDaCountRef.get() - 1);
                        Sets.add(indexSet, i, i + 1);
                        count++;
                        continue;
                    }
                }

            }

            // 超出边界则直接跳过
            if ((c1 + 2) % 100 >= 10)
                continue;
            // 找吃
            int c2Index = findUnuseCardIndex(cards, indexSet, i, endIndex, c1 + 1);
            int c3Index = findUnuseCardIndex(cards, indexSet, i, endIndex, c1 + 2);

            // 检查有没有这个吃
            if (c2Index >= 0 && c3Index >= 0) {
                // 加入index
                Sets.add(indexSet, i, c2Index, c3Index);
                count++;
                continue;
            } else if (c2Index == -1 && c3Index != -1) { // 如果有红中,则使用红中
                if (baiDaCountRef.get() >= 1) {
                    baiDaCountRef.set(baiDaCountRef.get() - 1);
                    Sets.add(indexSet, i, c3Index);
                    count++;
                    continue;
                }
            } else if (c2Index != -1 && c3Index == -1) {
                if (baiDaCountRef.get() >= 1) {
                    baiDaCountRef.set(baiDaCountRef.get() - 1);
                    Sets.add(indexSet, i, c2Index);
                    count++;
                    continue;
                }
            } else if (c2Index == -1 && c3Index == -1) {
                if (baiDaCountRef.get() >= 2) {
                    baiDaCountRef.set(baiDaCountRef.get() - 2);
                    Sets.add(indexSet, i);
                    count++;
                    continue;
                }
            }
        }
        return count;
    }

    @Override
    public String toString() {
        return "cardList:hu=>gangkai=" + gangKai + ",isMine=" + isMine + ",card=" + card + "," + super.toString();
    }

    @Override
    public List<Integer> getCards() {
        return null;
    }

    // public static void main(String[] args) {
    //
    // ZLPBaiDaHu hu = new ZLPBaiDaHu();
    // for (int i = 0; i < 6; i++) {
    // int count = hu.getLoopChiCount(Arrays.asList(101, 101, 102, 102, 103,
    // 103), 0, i);
    // System.out.println(count);
    // }
    //
    // }

    public static void main(String[] args) {
        ZLPBaiDaHu hu = new ZLPBaiDaHu();
        CardSort cardSort = new CardSort(5);
        cardSort.fillCardSort(Arrays.asList(101, 102, 103, 104, 105, 201, 302, 101, 102, 201, 302, 801, 801, 302));

        // List<Integer> cards = Arrays.asList(101, 102, 103, 104, 105, 201,
        // 302, 101, 102, 201, 302, 801, 801, 302);
        // List<Integer> cards = Arrays.asList(101, 102, 201, 302, 101, 102,
        // 201, 302, 801, 801, 302);
        // List<Integer> cards = Arrays.asList(101, 102, 103, 104, 105, 201,
        // 302, 101, 102, 201, 302, 801, 801, 302);

        // List<Integer> cards = Arrays.asList(101, 102, 103, 801, 801, 201,
        // 302, 101, 102, 201, 302, 801, 801, 302);

        // List<Integer> cards = Arrays.asList(101, 102, 104, 104, 104, 107,
        // 107, 108, 108, 201, 203, 801, 801, 801);
        // List<Integer> cards = Arrays.asList(103, 104, 105, 205, 205, 206,
        // 801, 206);
        // List<Integer> cards = Arrays.asList(108, 109, 302, 302, 801, 801,
        // 801, 207);
        // List<Integer> cards = Arrays.asList(108, 109, 201, 202, 203, 203,
        // 203, 801);
        // List<Integer> cards = Arrays.asList(102, 103, 104, 305, 306, 307,
        // 307, 304);

        // cardSort.fillCardSort(cards);

        long start = System.currentTimeMillis();
        // boolean b = hu.checkHu(null, cardSort, 801);
        // System.out.println(b);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    @Override
    public boolean checkTing(Game game, CardSort cardSort, List<Integer> waitCards) {

        return false;
    }

    // ////////////////////////////////////////////////

    private void print(List<Integer> pengList, List<Integer> chiList, List<Integer> singleList) {
        pf("碰的列表={0} ", pengList);
        pf("吃的列表={0} ", chiList);
        pfln("剩余的列表={0} ", singleList);
    }

    // TODO 新的胡牌算法
    /** 寻找所有的胡 */
    private boolean findAll;
    /** 调试打印 */
    private boolean print;

    private boolean checkHu(CardSort cardSort, int baidaCard) {

        CardSort cloneCardSort = cardSort.clone();
        // 1.移除所有红中
        int baidaCount = cloneCardSort.removeAll(baidaCard);
        List<Integer> cards = cloneCardSort.toArray();
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
            pfln("多于一个对子{0}", set1);
            CardSort cloneCardSort = singleCardSort.clone();

            List<Integer> twoList = new ArrayList<>(cloneCardSort.get(1));
            for (int i = 0; i < twoList.size(); i++) {
                int c = twoList.get(i);
                cloneCardSort.remove(c, c);
            }

            List<Integer> oneList = new ArrayList<>(cloneCardSort.get(0));

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
     * @param singleList
     *            单牌的存储列表
     * @param cards
     *            牌列表
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

}
