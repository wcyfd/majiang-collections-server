package com.randioo.majiang_collections_server.dao;

import org.apache.ibatis.annotations.Param;

import com.randioo.majiang_collections_server.entity.bo.Playback;
import com.randioo.randioo_server_base.annotation.MyBatisGameDaoAnnotation;
import com.randioo.randioo_server_base.db.BaseDao;

@MyBatisGameDaoAnnotation
public interface PlaybackDao extends BaseDao<Playback> {
    Playback getById(@Param("playbackId") int id, @Param("needSCStream") boolean needSCStream);

    /**
     * 获取最大的id
     * 
     * @return
     * @author wcy 2017年10月23日
     */
    Integer getMaxId();

    void cleanPlaybackByDays(@Param("days") int days);
}
