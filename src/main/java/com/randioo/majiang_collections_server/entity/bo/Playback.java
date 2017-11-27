package com.randioo.majiang_collections_server.entity.bo;

import java.util.Arrays;

import com.randioo.randioo_server_base.db.DataEntity;

public class Playback extends DataEntity {
    /** 回放id */
    private int playbackId;
    /** 主推列表 */
    private byte[] scStream;
    /** 配置 */
    private byte[] configStream;
    /** 视角座位 */
    private int viewSeat;

    public int getPlaybackId() {
        return playbackId;
    }

    public void setPlaybackId(int playbackId) {
        this.playbackId = playbackId;
    }

    public byte[] getConfigStream() {
        return configStream;
    }

    public void setConfigStream(byte[] configStream) {
        this.configStream = configStream;
    }

    public byte[] getScStream() {
        return scStream;
    }

    public void setScStream(byte[] scStream) {
        this.scStream = scStream;
    }

    public int getViewSeat() {
        return viewSeat;
    }

    public void setViewSeat(int viewSeat) {
        this.viewSeat = viewSeat;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Playback [playbackId=").append(playbackId).append(", scStream=")
                .append(Arrays.toString(scStream)).append(", configStream=").append(Arrays.toString(configStream))
                .append(", viewSeat=").append(viewSeat).append("]");
        return builder.toString();
    }

}
