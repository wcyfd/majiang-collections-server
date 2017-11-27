package com.randioo.majiang_collections_server.module.playback.component;

import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;

/**
 * 
 * 
 * @author wcy 2017年10月17日
 *
 */
public class PlaybackEntity {
    /** 房间id */
    private int gameId;
    /** 游戏配置 */
    private GameConfigData gameConfigData;
    /** 回放数据<座位号,录像> */
    private SCData scData = new SCData();
    /** 游戏开始时间 */
    private String gameStartTime;
    /** 回合开始的时间 */
    private String roundStartTime;

    public SCData getSCData() {
        return scData;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public GameConfigData getGameConfigData() {
        return gameConfigData;
    }

    public void setGameConfigData(GameConfigData gameConfigData) {
        this.gameConfigData = gameConfigData;
    }

    public String getRoundStartTime() {
        return roundStartTime;
    }

    public void setRoundStartTime(String roundStartTime) {
        this.roundStartTime = roundStartTime;
    }

    public String getGameStartTime() {
        return gameStartTime;
    }

    public void setGameStartTime(String gameStartTime) {
        this.gameStartTime = gameStartTime;
    }

}
