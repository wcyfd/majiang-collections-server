package com.randioo.majiang_collections_server.entity.bo;

import org.slf4j.Logger;

import com.google.protobuf.ByteString;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.majiang_collections_server.entity.po.RoleRaceInfo;
import com.randioo.randioo_server_base.entity.DefaultRole;

public class Role extends DefaultRole {
    public Logger logger;
    private int money;
    private int gameId;
    private int sex;
    private int volume = 0;
    private int musicVolume = 0;
    private String moneyExchangeTimeStr;
    private String headImgUrl;
    private int randiooMoney;
    private int moneyExchangeNum;
    /** 玩家比赛信息 */
    private RoleRaceInfo roleRaceInfo;
    private ByteString gameOverSC = null;
    private GameConfigData gameConfigData = null;

    /** 比赛底分 */
    private int raceScore;
    /** 积分 */
    private int point;
    /** 经纬度 */
    private String lantiLongi;
    /** 声音id */
    private String voiceId;

    public GameConfigData getGameConfigData() {
        return gameConfigData;
    }

    public void setGameConfigData(GameConfigData gameConfigData) {
        this.gameConfigData = gameConfigData;
    }

    public int getRandiooMoney() {
        return randiooMoney;
    }

    public void setRandiooMoney(int randiooMoney) {
        setChange(true);
        this.randiooMoney = randiooMoney;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        setChange(true);
        this.volume = volume;
    }

    public int getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(int musicVolume) {
        this.musicVolume = musicVolume;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        setChange(true);
        this.money = money;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getGameId() {
        return gameId;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        setChange(true);
        this.sex = sex;
    }

    public String getMoneyExchangeTimeStr() {
        return moneyExchangeTimeStr;
    }

    public void setMoneyExchangeTimeStr(String moneyExchangeTimeStr) {
        this.moneyExchangeTimeStr = moneyExchangeTimeStr;
    }

    public int getMoneyExchangeNum() {
        return moneyExchangeNum;
    }

    public void setMoneyExchangeNum(int moneyExchangeNum) {
        this.moneyExchangeNum = moneyExchangeNum;
    }

    public int getRaceScore() {
        return raceScore;
    }

    public void setRaceScore(int raceScore) {
        this.raceScore = raceScore;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public RoleRaceInfo getRoleRaceInfo() {
        return roleRaceInfo;
    }

    public void setRoleRaceInfo(RoleRaceInfo roleRaceInfo) {
        this.roleRaceInfo = roleRaceInfo;
    }

    public ByteString getGameOverSC() {
        return gameOverSC;
    }

    public void setGameOverSC(ByteString gameOverSC) {
        this.gameOverSC = gameOverSC;
    }

    /**
     * @return the lantiLongi
     */
    public String getLantiLongi() {
        return lantiLongi;
    }

    /**
     * @param lantiLongi the lantiLongi to set
     */
    public void setLantiLongi(String lantiLongi) {
        this.lantiLongi = lantiLongi;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Role [money=").append(money).append(", gameId=").append(gameId).append(", sex=").append(sex)
                .append(", volume=").append(volume).append(", musicVolume=").append(musicVolume)
                .append(", moneyExchangeTimeStr=").append(moneyExchangeTimeStr).append(", headImgUrl=")
                .append(headImgUrl).append(", randiooMoney=").append(randiooMoney).append(", moneyExchangeNum=")
                .append(moneyExchangeNum).append(", roleRaceInfo=").append(roleRaceInfo).append(", gameOverSC=")
                .append(gameOverSC).append(", raceScore=").append(raceScore).append(", point=").append(point)
                .append(", lantiLongi=").append(lantiLongi).append(", voiceId=").append(voiceId).append("]");
        return builder.toString();
    }

}
