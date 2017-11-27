package com.randioo.majiang_collections_server.module.playback.service;

import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface PlaybackService extends ObserveBaseServiceInterface {

    /**
     * 获得回放记录
     * 
     * @param role
     * @param playBackId
     * @author wcy 2017年10月16日
     */
    void getPlaybackById(Role role, int playBackId,boolean needSCStream);

    /**
     * 根据id
     * 
     * @param role
     * @param playbackId
     * @author wcy 2017年10月16日
     */
    void getPlaybackCatelogById(Role role);

}
