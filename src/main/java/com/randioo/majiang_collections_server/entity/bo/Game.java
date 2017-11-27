package com.randioo.majiang_collections_server.entity.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.randioo.mahjong_public_server.protocol.Entity.ClientCard;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.mahjong_public_server.protocol.Entity.GameState;
import com.randioo.mahjong_public_server.protocol.Entity.GameType;
import com.randioo.majiang_collections_server.entity.po.CallCardList;
import com.randioo.majiang_collections_server.entity.po.RoleGameInfo;
import com.randioo.majiang_collections_server.entity.po.env_vars.EnvVars;
import com.randioo.majiang_collections_server.module.fight.component.RuleableGame;
import com.randioo.majiang_collections_server.module.fight.component.score.round.GameOverResult;
import com.randioo.majiang_collections_server.util.key.Key;
import com.randioo.majiang_collections_server.util.vote.VoteBox;

public class Game extends RuleableGame {
    public Logger logger;

    private int gameId;
    /** 玩家id集合 */
    private Map<String, RoleGameInfo> roleIdMap = new LinkedHashMap<>();
    /** 房主id */
    private int masterRoleId;
    /** 房间锁 */
    private Key lockKey;
    /** 游戏开始 */
    private GameState gameState;
    /** 游戏类型 */
    private GameType gameType;
    /** 游戏配置 */
    private GameConfigData gameConfigData;
    /** 在线玩家数量 */
    private int onlineRoleCount;
    /** 玩家id列表，用于换人 */
    private List<String> roleIdList = new ArrayList<>();
    /** 当前玩家id */
    private int currentRoleIdIndex;
    /** 出牌计数 */
    private int sendCardCount;
    /** 出牌的时间戳 */
    private int sendCardTime;
    /** 庄家的玩家id */
    private int zhuangSeat = -1;
    /** 以及打完的回合数 */
    private int finishRoundCount;
    /** 剩余的牌 */
    private List<Integer> remainCards = new ArrayList<>();
    /** 桌上的牌<索引id,出牌的列表> */
    private Map<Integer, List<Integer>> desktopCardMap = new HashMap<>();
    /** 每个人每次叫牌的临时存储 */
    private List<CallCardList> callCardLists = new ArrayList<>();
    /** 每个人胡的牌要另外再存一份 */
    private List<CallCardList> huCallCardLists = new ArrayList<>();
    /** 出牌放在桌上的表 */
    private Map<Integer, List<Integer>> sendDesktopCardMap = new HashMap<>();
    /** 玩家结果统计 */
    private Map<String, GameOverResult> statisticResultMap = new HashMap<>();
    /** 燃点币 */
    private int randiooMoney;
    /** 投票箱 */
    private VoteBox voteBox = new VoteBox();
    /** 客户端调试的卡牌卡牌 */
    private List<ClientCard> clientCards = new ArrayList<>();
    /** 客户端剩余摸牌 */
    public List<Integer> clientRemainCards = new ArrayList<>();
    /** 客户端剩余牌数量 */
    public Integer clientRemainCardsCount;
    /** 客户端调试的摸牌 */
    private int clientTouchCard;
    /** 百搭牌 */
    private int baidaCard;
    /** 第一次产生的白打牌 */
    private int fristBaidaCard;
    /** 出的牌 */
    public int sendCard;
    /** 出牌的座位 */
    public int sendCardSeat;

    /** 当前一回合是否荒番荒番局 */
    private boolean currentRoundIsHuangFan;
    /** 还需荒番的局数 */
    private int huangFanCount;
    /** 当前局是不是荒番 */
    private boolean isHuangFan;
    /** 抢杠的暂存数据 */
    public CallCardList qiangGangCallCardList;
    /** 需要检查别人的座位 */
    public List<Integer> checkOtherCardListSeats = new ArrayList<>();
    /** 骰子 */
    public List<Integer> dice;
    /** 新摸的牌是不是花牌 */
    public boolean touchCardIsFlower;
    /** 能不能听 */
    public boolean canTing;
    /** 环境变量集合 */
    public EnvVars envVars = new EnvVars();
    /** 临时存放ting的callCardList */
    public CallCardList tingCardList;

    private List<CallCardList> autoHuCallCardList = new ArrayList<>();
    /** 是否是流局 */
    public boolean isLiuju;
    /** 回合开始时间 */
    public int roundStartTime;
    /** 回合结束时间 */
    public int roundEndTime;
    /** 第一回合回合开始时间 */
    public int firstRoundStartTime;
    /** 开始下一局标记 */
    public boolean beginNextRound;

    public List<CallCardList> getAutoHuCallCardList() {
        return autoHuCallCardList;
    }

    public boolean isHuangFan() {
        return isHuangFan;
    }

    public void setHuangFan(boolean isHuangFan) {
        this.isHuangFan = isHuangFan;
    }

    public boolean isCurrentRoundIsHuangFan() {
        return currentRoundIsHuangFan;
    }

    public void setCurrentRoundIsHuangFan(boolean currentRoundIsHuangFan) {
        this.currentRoundIsHuangFan = currentRoundIsHuangFan;
    }

    public int getHuangFanCount() {
        return huangFanCount;
    }

    public void setHuangFanCount(int huangFanCount) {
        this.huangFanCount = huangFanCount;
    }

    public VoteBox getVoteBox() {
        return voteBox;
    }

    public int getFinishRoundCount() {
        return finishRoundCount;
    }

    public void setFinishRoundCount(int finishRoundCount) {
        this.finishRoundCount = finishRoundCount;
    }

    public int getOnlineRoleCount() {
        return onlineRoleCount;
    }

    public void setOnlineRoleCount(int onlineRoleCount) {
        this.onlineRoleCount = onlineRoleCount;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getGameId() {
        return gameId;
    }

    public Map<String, RoleGameInfo> getRoleIdMap() {
        return roleIdMap;
    }

    public int getMasterRoleId() {
        return masterRoleId;
    }

    public void setMasterRoleId(int masterRoleId) {
        this.masterRoleId = masterRoleId;
    }

    public Key getLockKey() {
        return lockKey;
    }

    public void setLockKey(Key lockKey) {
        this.lockKey = lockKey;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public GameConfigData getGameConfig() {
        return gameConfigData;
    }

    public void setGameConfig(GameConfigData gameConfigData) {
        this.gameConfigData = gameConfigData;
    }

    public List<String> getRoleIdList() {
        return roleIdList;
    }

    public int getCurrentRoleIdIndex() {
        return currentRoleIdIndex;
    }

    public void setCurrentRoleIdIndex(int currentRoleIdIndex) {
        this.currentRoleIdIndex = currentRoleIdIndex;
    }

    public int getSendCardCount() {
        return sendCardCount;
    }

    public void setSendCardCount(int sendCardCount) {
        this.sendCardCount = sendCardCount;
    }

    public int getSendCardTime() {
        return sendCardTime;
    }

    public void setSendCardTime(int sendCardTime) {
        this.sendCardTime = sendCardTime;
    }

    public int getZhuangSeat() {
        return zhuangSeat;
    }

    public void setZhuangSeat(int zhuangSeat) {
        this.zhuangSeat = zhuangSeat;
    }

    /**
     * 获得剩余牌
     * 
     * @return
     */
    public List<Integer> getRemainCards() {
        return remainCards;
    }

    public Map<Integer, List<Integer>> getDesktopCardMap() {
        return desktopCardMap;
    }

    public Map<Integer, List<Integer>> getSendDesktopCardMap() {
        return sendDesktopCardMap;
    }

    public List<CallCardList> getCallCardLists() {
        return callCardLists;
    }

    public List<CallCardList> getHuCallCardLists() {
        return huCallCardLists;
    }

    public Map<String, GameOverResult> getStatisticResultMap() {
        return statisticResultMap;
    }

    public int getRandiooMoney() {
        return randiooMoney;
    }

    public void setRandiooMoney(int randiooMoney) {
        this.randiooMoney = randiooMoney;
    }

    public List<ClientCard> getClientCards() {
        return clientCards;
    }

    public int getClientTouchCard() {
        return clientTouchCard;
    }

    public void setClientTouchCard(int clientTouchCard) {
        this.clientTouchCard = clientTouchCard;
    }

    public int getBaidaCard() {
        return baidaCard;
    }

    public void setBaidaCard(int baidaCard) {
        this.baidaCard = baidaCard;
    }

    public int getFristBaidaCard() {
        return fristBaidaCard;
    }

    public void setFristBaidaCard(int fristBaidaCard) {
        this.fristBaidaCard = fristBaidaCard;
    }

    @Override
    public String toString() {
        String n = System.getProperty("line.separator");
        String t = "\t";
        StringBuilder sb = new StringBuilder();
        sb.append("Game :[").append(n);
        sb.append(t).append("gameId=>").append(gameId).append(n);
        sb.append(t).append("roleGameInfoMap").append(n);
        for (RoleGameInfo roleGameInfo : roleIdMap.values()) {
            sb.append(t).append(roleGameInfo.toString()).append(n);
        }
        sb.append(t).append("callCardLists=>");
        for (CallCardList callCardList : callCardLists) {
            sb.append(t).append(callCardList).append(n);
        }
        sb.append(t).append("huCallCardList=>").append(n);
        for (CallCardList callCardList : huCallCardLists) {
            sb.append(t).append(callCardList).append(n);
        }
        sb.append(t).append("currentRoleIndex=>").append(currentRoleIdIndex).append(n);
        sb.append(t).append("sendCardCount=>").append(sendCardCount).append(n);
        sb.append(t).append("remainCards=>").append(remainCards).append(n);
        sb.append(t).append("]").append(n);
        return sb.toString();
    }

}
