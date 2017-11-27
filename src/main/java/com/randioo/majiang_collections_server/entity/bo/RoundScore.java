package com.randioo.majiang_collections_server.entity.bo;

import com.google.gson.Gson;
import com.randioo.majiang_collections_server.entity.po.PlayerScoreDataList;
import com.randioo.randioo_server_base.db.DataEntity;
import com.randioo.randioo_server_base.utils.StringUtils;

public class RoundScore extends DataEntity {
    private int id;
    private int roleId;
    private int roomId;
    private String gameStartTime;
    private String roundStartTime;
    private String gameEndTime;
    private String roundEndTime;
    private int roundCount;
    private int score;
    private int playbackId;
    private PlayerScoreDataList scoreDataList = new PlayerScoreDataList();
    private String roundScoreStr;

    public String getGameEndTime() {
        return gameEndTime;
    }

    public void setGameEndTime(String gameEndTime) {
        this.gameEndTime = gameEndTime;
    }

    public String getRoundEndTime() {
        return roundEndTime;
    }

    public void setRoundEndTime(String roundEndTime) {
        this.roundEndTime = roundEndTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getGameStartTime() {
        return gameStartTime;
    }

    public void setGameStartTime(String gameStartTime) {
        this.gameStartTime = gameStartTime;
    }

    public int getRoundCount() {
        return roundCount;
    }

    public void setRoundCount(int roundCount) {
        this.roundCount = roundCount;
    }

    public String getRoundStartTime() {
        return roundStartTime;
    }

    public void setRoundStartTime(String roundStartTime) {
        this.roundStartTime = roundStartTime;
    }

    public int getPlaybackId() {
        return playbackId;
    }

    public void setPlaybackId(int playbackId) {
        this.playbackId = playbackId;
    }

    public String getRoundScoreStr() {
        Gson gson = new Gson();
        String str = gson.toJson(scoreDataList);
        this.roundScoreStr = str;
        return roundScoreStr;
    }

    public void setRoundScoreStr(String roundScoreStr) {
        scoreDataList.getList().clear();
        this.roundScoreStr = roundScoreStr;
        if (StringUtils.isNullOrEmpty(roundScoreStr)) {
            return;
        }
        Gson gson = new Gson();
        PlayerScoreDataList list = gson.fromJson(roundScoreStr, PlayerScoreDataList.class);
        scoreDataList.getList().addAll(list.getList());
    }

    public PlayerScoreDataList getScoreDataList() {
        return scoreDataList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RoundScore [id=").append(id).append(", roleId=").append(roleId).append(", roomId=")
                .append(roomId).append(", gameStartTime=").append(gameStartTime).append(", roundStartTime=")
                .append(roundStartTime).append(", roundCount=").append(roundCount).append(", score=").append(score)
                .append(", playbackId=").append(playbackId).append(", scoreDataList=").append(scoreDataList)
                .append(", roundScoreStr=").append(roundScoreStr).append("]");
        return builder.toString();
    }

}
