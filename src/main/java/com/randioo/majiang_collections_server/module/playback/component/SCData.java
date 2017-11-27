package com.randioo.majiang_collections_server.module.playback.component;

import java.util.ArrayList;
import java.util.List;

import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;

public class SCData {
    /** 主推列表 */
    private List<SC> scList = new ArrayList<>();

    public List<SC> getScList() {
        return scList;
    }
}
