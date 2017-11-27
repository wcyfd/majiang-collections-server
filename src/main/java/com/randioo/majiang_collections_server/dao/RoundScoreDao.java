package com.randioo.majiang_collections_server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.randioo.majiang_collections_server.entity.bo.RoundScore;
import com.randioo.randioo_server_base.annotation.MyBatisGameDaoAnnotation;
import com.randioo.randioo_server_base.db.BaseDao;

@MyBatisGameDaoAnnotation
public interface RoundScoreDao extends BaseDao<RoundScore> {
    /**
     * 获得某玩家的游戏分数
     * 
     * @param roleId
     * @return
     * @author wcy 2017年10月18日
     */
    List<RoundScore> getAllRoomByRoleId(int roleId);

    /**
     * 某玩家在某房间的所有分值
     * 
     * @param roleId
     * @param roomId
     * @param gameStartTime
     * @return
     * @author wcy 2017年10月18日
     */
    List<RoundScore> getRoundScoreByRoleIdAndRoomId(@Param("roleId") int roleId, @Param("roomId") int roomId,
            @Param("gameStartTime") String gameStartTime);

    /**
     * 获得一场游戏数据
     * 
     * @param roleId
     * @param roomId
     * @param gameStartTime
     * @return
     * @author wcy 2017年10月18日
     */
    List<RoundScore> getGameScoreByRoomIdAndStartTime(@Param("room_id") int roomId,
            @Param("game_start_time") String gameStartTime);

    /**
     * 
     * @param playbackId
     * @return
     * @author wcy 2017年10月23日
     */
    RoundScore getRoundScoreByPlaybackId(int playbackId);

    /**
     * 
     * @param roleId
     * @param limit
     * @return
     * @author wcy 2017年10月24日
     */
    List<RoundScore> getLimitRoomRoundScore(@Param("roleId") int roleId, @Param("limit") int limit);

    /**
     * 
     * @param playbackId
     * @return
     * @author wcy 2017年10月26日
     */
    String getRoundStartTimeByPlackbackId(@Param("playbackId") int playbackId);
}
