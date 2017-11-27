package com.randioo.majiang_collections_server.entity.po;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.randioo.mahjong_public_server.protocol.Entity.RoundCardsData;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.majiang_collections_server.entity.bo.VideoData;
import com.randioo.majiang_collections_server.module.fight.component.MajiangRule.MajiangStateEnum;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.CardList;
import com.randioo.majiang_collections_server.module.fight.component.score.round.RoundOverResult;

public class RoleGameInfo {
    /** 游戏中的玩家id */
    public String gameRoleId;
    /** 全局玩家id */
    public int roleId;
    /** 手上别人看不到的牌 */
    public List<Integer> cards = new ArrayList<>();
    /** 已经碰过或杠过的牌 */
    public List<CardList> showCardLists = new ArrayList<>();
    /** 出过的花牌 */
    public List<Integer> sendFlowrCards = new ArrayList<>();
    /** 新拿的牌 */
    public int newCard;
    /** 是否准备完成 */
    public boolean ready;
    /** 自动出牌标记 */
    public int auto;
    /** 杠标记 */
    public boolean isGang;
    /** 胡牌记录 */
    public RoundCardsData roundCardsData;

    /** 回合结果集 */
    public RoundOverResult roundOverResult = new RoundOverResult();
    /** 录像数据 */
    public VideoData videoData = new VideoData();
    /***/
    public List<SC> roundSCList = new ArrayList<>();
    /** 申请退出时间 */
    public int lastRejectedExitTime;
    /** 显示的花的数量 */
    public int flowerCount;
    /** 个人缓存的操作栈 */
    public Stack<MajiangStateEnum> operations = new Stack<>();

    /** 一局游戏暗杠的次数 */
    public int darkGangCount = 0;
    /** 一局游戏明杠的次数 */
    public int ligthGangCount = 0;
    /** 每个人摸的牌 */
    public int everybodyTouchCard;
    /** 是不是听状态 */
    public boolean isTing;
    /** 听的牌 */
    public List<Integer> tingCards = new ArrayList<>();

    /** 不显示的花的数量 */
    public int darkFlowerCount;
    /** 进入补花流程后为true */
    public boolean isAddFlowerState;
    /** 本圈吃碰的牌,吃过的牌本回合不能再出这张牌 */
    public int chiCard;
    /** 通知碰，选择过之后记录这样碰的牌 */
    public int pengGuoCard;

    @Override
    public String toString() {
        String n = System.getProperty("line.separator");
        String t = "\t";
        StringBuilder sb = new StringBuilder();
        sb.append("GameRoleInfo:[").append(n);
        sb.append(t).append("gameRoleId=>").append(gameRoleId).append(n);
        sb.append(t).append("roleId=>").append(roleId).append(n);
        sb.append(t).append("ready=>").append(ready).append(n);
        sb.append(t).append("cards=>").append(cards).append(n);
        sb.append(t).append("sendFlowerCards=>").append(sendFlowrCards).append(n);
        sb.append(t).append("newCard=>").append(newCard).append(n);
        sb.append(t).append("showCardLists=>").append(showCardLists).append(n);
        sb.append(t).append("flowerCount=>").append(flowerCount).append(n);
        sb.append(t).append("dardFlowerCount=>").append(darkFlowerCount).append(n);
        sb.append(t).append("darkGangCount=>").append(darkGangCount).append(n);
        sb.append(t).append("ligthGangCount=>").append(ligthGangCount).append(n);
        sb.append(t).append("isTing=>").append(isTing).append(n);
        sb.append(t).append("tingCards=>").append(tingCards).append(n);
        sb.append(t).append("operation=>").append(operations).append(n);
        sb.append(t).append("everybodyTouchCard=>").append(everybodyTouchCard).append(n);
        sb.append(t).append("]");

        return sb.toString();
    }
}
