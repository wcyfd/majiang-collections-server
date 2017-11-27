package com.randioo.majiang_collections_server.module.playback.component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.mahjong_public_server.protocol.Entity.GameRoleData;
import com.randioo.mahjong_public_server.protocol.Entity.RoleRoundOverInfoData;
import com.randioo.mahjong_public_server.protocol.Entity.RoundVideoData;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightRoundOver;
import com.randioo.mahjong_public_server.protocol.Match.SCMatchJoinGame;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.majiang_collections_server.dao.PlaybackDao;
import com.randioo.majiang_collections_server.dao.RoundScoreDao;
import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.bo.Playback;
import com.randioo.majiang_collections_server.entity.bo.RoundScore;
import com.randioo.majiang_collections_server.entity.po.PlayerScoreData;
import com.randioo.majiang_collections_server.entity.po.RoleGameInfo;
import com.randioo.majiang_collections_server.module.match.service.MatchService;
import com.randioo.randioo_server_base.db.IdClassCreator;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Component
public class PlaybackManager {

    @Autowired
    private IdClassCreator idClassCreator;

    @Autowired
    private PlaybackDao playbackDao;

    @Autowired
    private RoundScoreDao roundScoreDao;

    @Autowired
    private MatchService matchService;

    private Map<Integer, PlaybackEntity> map = new ConcurrentHashMap<>();

    public Playback getPlaybackById(int id, boolean needSCStream) {
        return playbackDao.getById(id, needSCStream);
    }

    public RoundScore getRoundScoreByPlaybackId(int playbackId) {
        return roundScoreDao.getRoundScoreByPlaybackId(playbackId);
    }

    /**
     * 创建一个回放实体
     * 
     * @return
     * @author wcy 2017年10月17日
     */
    public void createGamePlayback(Game game) {
        PlaybackEntity entity = new PlaybackEntity();
        entity.setGameConfigData(game.getGameConfig());
        entity.setGameId(game.getGameId());

        // 创建游戏开始时间
        entity.setGameStartTime(TimeUtils.getDetailTimeStr());

        map.put(game.getGameId(), entity);
    }

    public String getRoundStartTime(int playbackId) {
        return roundScoreDao.getRoundStartTimeByPlackbackId(playbackId);
    }

    /**
     * 创建一轮的回放实体
     * 
     * @param game
     * @author wcy 2017年10月17日
     */
    public void initRoundPlayback(int gameId) {
        PlaybackEntity entity = map.get(gameId);// 获得该游戏的实体

        String startTime = TimeUtils.getDetailTimeStr();
        entity.setRoundStartTime(startTime);

        entity.getSCData().getScList().clear();
    }

    /**
     * 创建id
     * 
     * @return
     * @author wcy 2017年10月17日
     */
    private int createId() {
        return idClassCreator.getId(Playback.class);
    }

    /**
     * 保存回放
     * 
     * @param roleId
     * @author wcy 2017年10月17日
     */
    public void save(Game game, SC sc) {
        // 保存录像
        int playbackId = savePlayback(game);
        // 保存分数7
        saveScore(game, sc, playbackId);
    }

    public void remove(Game game) {
        map.remove(game.getGameId());
    }

    /**
     * 保存录像
     * 
     * @param game
     * @return 录像id
     * @author wcy 2017年10月23日
     */
    private int savePlayback(Game game) {
        int gameId = game.getGameId();
        PlaybackEntity entity = map.get(gameId);
        GameConfigData config = entity.getGameConfigData();

        Playback playback = new Playback();
        String gameRoleId = matchService.getGameRoleId(game.getGameId(), game.getMasterRoleId());
        int viewSeat = game.getRoleIdList().indexOf(gameRoleId);

        playback.setViewSeat(viewSeat);
        playback.setPlaybackId(this.createId());
        {
            byte[] configStream = config.toByteArray();
            playback.setConfigStream(configStream);
        }
        {
            for (RoleGameInfo info : game.getRoleIdMap().values()) {
                GameRoleData gameRoleData = matchService.parseGameRoleData(info, game);
                SC scMatchJoinGame = SC.newBuilder().setSCMatchJoinGame(SCMatchJoinGame//
                        .newBuilder()//
                        .setGameRoleData(gameRoleData)//
                        .setIsMe(false)//
                        ).build();
                entity.getSCData().getScList().add(0, scMatchJoinGame);
            }
            byte[] roundVideoDataStream = getRoundVideoDataStream(entity.getSCData());
            playback.setScStream(roundVideoDataStream);
        }
        playbackDao.insert(playback);

        return playback.getPlaybackId();
    }

    private byte[] getRoundVideoDataStream(SCData data) {
        RoundVideoData.Builder roundVideoDataBuilder = RoundVideoData.newBuilder();
        List<SC> list = data.getScList();
        for (SC sc : list) {
            roundVideoDataBuilder.addSc(sc.toByteString());
        }
        RoundVideoData roundVideoData = roundVideoDataBuilder.build();
        return roundVideoData.toByteArray();
    }

    /**
     * 保存分数及回放录像id
     * 
     * @param game
     * @param sc
     * @param playbackMap
     * @author wcy 2017年10月18日
     */
    private void saveScore(Game game, SC sc, int playbackId) {
        PlaybackEntity entity = map.get(game.getGameId());

        SCFightRoundOver roundOverSC = sc.getSCFightRoundOver();

        RoundScore roundScore = new RoundScore();
        roundScore.setGameStartTime(entity.getGameStartTime());
        roundScore.setRoundStartTime(entity.getRoundStartTime());
        roundScore.setRoomId(Integer.parseInt(entity.getGameConfigData().getRoomId()));
        roundScore.setRoundCount(roundOverSC.getFinishRoundCount());
        roundScore.setPlaybackId(playbackId);
        roundScore.setRoundEndTime(TimeUtils.getDetailTimeStr());
        // 如果是最后一局了设置游戏结束时间
        if (game.getGameConfig().getRoundCount() == roundOverSC.getFinishRoundCount()) {
            roundScore.setGameEndTime(TimeUtils.getDetailTimeStr());
        }

        List<RoleRoundOverInfoData> list = sc.getSCFightRoundOver().getRoleRoundOverInfoDataList();

        List<PlayerScoreData> playerScoreDataList = roundScore.getScoreDataList().getList();
        for (RoleRoundOverInfoData info : list) {
            GameRoleData gameRoleData = info.getGameRoleData();
            PlayerScoreData data = new PlayerScoreData();
            data.name = gameRoleData.getName();
            data.account = gameRoleData.getAccount();
            data.score = info.getRoundScore();

            playerScoreDataList.add(data);
        }

        for (RoleRoundOverInfoData info : list) {
            int score = info.getRoundScore();
            GameRoleData gameRoleData = info.getGameRoleData();
            String gameRoleId = gameRoleData.getGameRoleId();
            RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);

            roundScore.setRoleId(roleGameInfo.roleId);
            roundScore.setScore(score);

            roundScoreDao.insert(roundScore);
        }
    }

    /**
     * 记录给一个人
     * 
     * @param gameId
     * @param roleId
     * @param sc
     * @author wcy 2017年10月17日
     */
    public void record(Game game, RoleGameInfo roleGameInfo, SC sc) {
        int seat = game.getRoleIdList().indexOf(roleGameInfo.gameRoleId);
        if (seat != 0) {
            return;
        }

        record(game.getGameId(), sc);
    }

    public void record(int gameId, SC sc) {
        PlaybackEntity entity = map.get(gameId);
        entity.getSCData().getScList().add(sc);
    }

}
