package com.randioo.majiang_collections_server.module.playback.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.ByteString;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.mahjong_public_server.protocol.Entity.PlaybackCatelogRoundData;
import com.randioo.mahjong_public_server.protocol.Entity.PlaybackScoreData;
import com.randioo.mahjong_public_server.protocol.Entity.RoundVideoData;
import com.randioo.mahjong_public_server.protocol.Error.ErrorCode;
import com.randioo.mahjong_public_server.protocol.Match.SCMatchJoinGame;
import com.randioo.mahjong_public_server.protocol.Playback.PlaybackCatelogResponse;
import com.randioo.mahjong_public_server.protocol.Playback.PlaybackGetBinaryResponse;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.majiang_collections_server.GlobleConstant;
import com.randioo.majiang_collections_server.cache.local.GameCache;
import com.randioo.majiang_collections_server.dao.PlaybackDao;
import com.randioo.majiang_collections_server.dao.RoundScoreDao;
import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.bo.Playback;
import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.majiang_collections_server.entity.bo.RoundScore;
import com.randioo.majiang_collections_server.entity.po.PlayerScoreData;
import com.randioo.majiang_collections_server.entity.po.RoleGameInfo;
import com.randioo.majiang_collections_server.module.fight.FightConstant;
import com.randioo.majiang_collections_server.module.fight.component.MajiangRule;
import com.randioo.majiang_collections_server.module.fight.service.FightService;
import com.randioo.majiang_collections_server.module.match.MatchConstant;
import com.randioo.majiang_collections_server.module.match.service.MatchService;
import com.randioo.majiang_collections_server.module.playback.component.PlaybackManager;
import com.randioo.randioo_server_base.config.GlobleMap;
import com.randioo.randioo_server_base.db.IdClassCreator;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.Observer;
import com.randioo.randioo_server_base.utils.SessionUtils;

@Service("playbackService")
public class PlaybackServiceImpl extends ObserveBaseService implements PlaybackService {
    @Autowired
    private FightService fightService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private PlaybackManager playbackManager;

    @Autowired
    private IdClassCreator idClassCreator;

    @Autowired
    private PlaybackDao playbackDao;

    @Autowired
    private RoundScoreDao roundScoreDao;

    @Override
    public void init() {
        Integer id = playbackDao.getMaxId();

        int initId = 1000;
        id = id == null || id < initId ? initId : id;
        idClassCreator.initId(Playback.class, id);
    }

    @Override
    public void initService() {
        fightService.addObserver(this);
        matchService.addObserver(this);
    }

    @Override
    public void update(Observer observer, String msg, Object... args) {
        // 开始游戏
        if (FightConstant.FIGHT_START.equals(msg)) {

            SC scHide = (SC) args[0];
            Game game = (Game) args[1];
            RoleGameInfo roleGameInfo = (RoleGameInfo) args[2];
            SC scShow = (SC) args[3];
            logger.info("FIGHT_START");
            playbackManager.record(game, roleGameInfo, scShow);
            return;
        }

        if (FightConstant.FIGHT_DICE.equals(msg)) {
            allRecord(args);
            return;
        }

        if (FightConstant.FIGHT_SCORE.equals(msg)) {
            allRecord(args);
            return;
        }

        // 摸牌
        if (FightConstant.FIGHT_TOUCH_CARD.equals(msg)) {
            SC sc = (SC) args[0];
            Game game = (Game) args[1];
            RoleGameInfo roleGameInfo = (RoleGameInfo) args[2];
            SC showSC = (SC) args[3];

            playbackManager.record(game, roleGameInfo, showSC);
            return;
        }
        // 出牌
        if (FightConstant.FIGHT_SEND_CARD.equals(msg)) {
            allRecord(args);
            return;
        }

        // 座位指针
        if (FightConstant.FIGHT_POINT_SEAT.equals(msg)) {
            allRecord(args);
            return;
        }

        // 通知投票结果
        if (FightConstant.FIFHT_APPLY_EXIT_RESULT.equals(msg)) {
            allRecord(args);
            return;
        }
        // 补花
        if (FightConstant.FIGHT_ADD_FLOWER.equals(msg)) {

            SC sc = (SC) args[0];
            Game game = (Game) args[1];
            RoleGameInfo roleGameInfo = (RoleGameInfo) args[2];
            SC showSC = (SC) args[3];
            playbackManager.record(game, roleGameInfo, showSC);
            return;
        }
        // 花计数的变化
        if (FightConstant.FIGHT_FLOWER_COUNT.equals(msg)) {
            allRecord(args);
            return;
        }
        // 杠
        if (FightConstant.FIGHT_GANG.equals(msg)) {
            allRecord(args);
            return;
        }
        // 碰
        if (FightConstant.FIGHT_PENG.equals(msg)) {
            allRecord(args);
            return;
        }
        // 吃
        if (FightConstant.FIGHT_CHI.equals(msg)) {
            allRecord(args);
            return;
        }
        // 胡
        if (FightConstant.FIGHT_HU.equals(msg)) {
            allRecord(args);
            return;
        }
        // 过
        if (FightConstant.FIGHT_GUO.equals(msg)) {
            return;
        }
        // 听
        if (FightConstant.FIGHT_TING.equals(msg)) {
            allRecord(args);
            return;
        }

        if (FightConstant.ROUND_OVER.equals(msg)) {
            SC sc = (SC) args[0];
            Game game = (Game) args[1];

            playbackManager.record(game.getGameId(), sc);
            playbackManager.save(game, sc);
            logger.info("录像保存完毕");
            return;
        }

        if (MatchConstant.MATCH_CREATE_GAME.equals(msg)) {
            Game game = (Game) args[0];
            playbackManager.createGamePlayback(game);
            return;
        }

        if (FightConstant.FIGHT_GAME_START.equals(msg)) {
            Game game = (Game) args[0];
            playbackManager.initRoundPlayback(game.getGameId());
            return;
        }

    }

    // 所有执行的操作
    private void allRecord(Object... args) {
        SC sc = (SC) args[0];
        Game game = (Game) args[1];
        // for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
        // List<SC> list = getCurrentSCList(roleGameInfo,
        // game.getFinishRoundCount() + 1); // 此时玩家进入游戏时，认为
        // list.add(sc);
        // roleGameInfo.roundSCList.add(sc);
        // }
        playbackManager.record(game.getGameId(), sc);
    }

    // 所有执行的操作
    private void OnlyOneRecord(Object... args) {
        SC sc = (SC) args[0];
        Game game = (Game) args[1];
        // RoleGameInfo info = (RoleGameInfo) args[2];
        // info.roundSCList.add(sc);
        // for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
        // List<SC> list = getCurrentSCList(roleGameInfo,
        // game.getFinishRoundCount() + 1); // 此时玩家进入游戏时，认为
        // list.add(sc);
        // }

        // playbackManager.recordOne(game.getGameId(), sc);
    }

    @Override
    public void getPlaybackCatelogById(Role role) {
        // 获取此人所有参与过的房间号
        List<RoundScore> roundScores = roundScoreDao.getLimitRoomRoundScore(role.getRoleId(), 0);
        PlaybackCatelogResponse.Builder responseSC = PlaybackCatelogResponse//
        .newBuilder();

        // 该玩家参与过的所有房间
        for (RoundScore roundScore : roundScores) {
            PlaybackCatelogRoundData.Builder roundDataBuilder = PlaybackCatelogRoundData.newBuilder();
            roundDataBuilder.setGameStartTime(roundScore.getGameStartTime());
            roundDataBuilder.setStartTime(roundScore.getRoundStartTime());
            roundDataBuilder.setRoomId(String.valueOf(roundScore.getRoomId()));
            roundDataBuilder.setPlaybackId(roundScore.getPlaybackId());

            List<PlayerScoreData> list = roundScore.getScoreDataList().getList();
            for (PlayerScoreData playerScoreData : list) {
                PlaybackScoreData playbackScoreData = PlaybackScoreData.newBuilder()
                        .setName(playerScoreData.name)
                        .setScore(playerScoreData.score)
                        .setAccount(playerScoreData.account)
                        .build();

                roundDataBuilder.addPlaybackScoreData(playbackScoreData);
            }
            responseSC.addData(roundDataBuilder);
        }

        SC sc = SC.newBuilder()//
                .setPlaybackCatelogResponse(responseSC)
                //
                .build();
        SessionUtils.sc(role.getRoleId(), sc);
    }

    @Override
    public void getPlaybackById(Role role, int playbackId, boolean needSCStream) {
        Playback playback = playbackManager.getPlaybackById(playbackId, needSCStream);
        String roundStartTime = playbackManager.getRoundStartTime(playbackId);

        if (playback == null) {
            SessionUtils.sc(role.getRoleId(), SC.newBuilder()//
                    .setPlaybackGetBinaryResponse(//
                            PlaybackGetBinaryResponse//
                            .newBuilder()
                                    //
                                    .setErrorCode(ErrorCode.PLAYBACK_NOT_EXIST.getNumber())//
                    )
                    .build());
            return;
        }

        RoundVideoData roundVideoData = null;
        GameConfigData gameConfigData = null;

        int viewSeat = playback.getViewSeat();
        try {
            if (needSCStream) {
                roundVideoData = RoundVideoData.parseFrom(playback.getScStream());
                List<ByteString> list = roundVideoData.getScList();
                for (int i = 0; i < list.size(); i++) {
                    ByteString bytes = list.get(i);
                    SC sc = SC.parseFrom(bytes);
                    if (sc.hasSCMatchJoinGame()) {
                        SCMatchJoinGame scMatchJoinGame = sc.getSCMatchJoinGame();
                        if (role.getAccount().equals(scMatchJoinGame.getGameRoleData().getAccount())) {
                            viewSeat = scMatchJoinGame.getGameRoleData().getSeat();
                            break;
                        }
                    }
                    System.out.println(sc);
                }
            }
            gameConfigData = GameConfigData.parseFrom(playback.getConfigStream());
        } catch (Exception e) {
            SessionUtils.sc(
                    role.getRoleId(),
                    SC.newBuilder()
                            .setPlaybackGetBinaryResponse(
                                    PlaybackGetBinaryResponse.newBuilder().setErrorCode(
                                            ErrorCode.PLAYBACK_DAMAGED.getNumber()))
                            .build());
            role.logger.error("查看录像反序列化失败", e);
            return;
        }

        MajiangRule majiangRule = GameCache.getRuleMap().get(
                GlobleMap.String(GlobleConstant.ARGS_PLATFORM_PACKAGE_NAME));

        PlaybackGetBinaryResponse.Builder responseBuilder = PlaybackGetBinaryResponse.newBuilder()
                .setGameConfigData(gameConfigData)
                .setRoomId(gameConfigData.getRoomId())
                .addAllAllCards(majiangRule.getCards())
                .addAllAllFlowers(majiangRule.getFlowers())
                .setViewSeat(viewSeat)
                .setRoundStartTime(String.valueOf(roundStartTime));

        // 如果需要则放入回放数据
        if (needSCStream) {
            responseBuilder.setRoundVideoData(roundVideoData);
        }

        SessionUtils.sc(role.getRoleId(), SC.newBuilder()//
                .setPlaybackGetBinaryResponse(responseBuilder)
                .build());
    }
}
