package com.randioo.majiang_collections_server.module.match.service;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.mahjong_public_server.protocol.Entity.GameRoleData;
import com.randioo.mahjong_public_server.protocol.Entity.GameState;
import com.randioo.mahjong_public_server.protocol.Entity.GameType;
import com.randioo.mahjong_public_server.protocol.Entity.RoundVideoData;
import com.randioo.mahjong_public_server.protocol.Error.ErrorCode;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightNoticeReady;
import com.randioo.mahjong_public_server.protocol.Match.MatchCheckRoomResponse;
import com.randioo.mahjong_public_server.protocol.Match.MatchCreateGameResponse;
import com.randioo.mahjong_public_server.protocol.Match.MatchJoinGameResponse;
import com.randioo.mahjong_public_server.protocol.Match.MatchPreJoinResponse;
import com.randioo.mahjong_public_server.protocol.Match.SCMatchJoinGame;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.majiang_collections_server.GlobleConstant;
import com.randioo.majiang_collections_server.cache.file.GameRoundConfigCache;
import com.randioo.majiang_collections_server.cache.local.GameCache;
import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.majiang_collections_server.entity.bo.VideoData;
import com.randioo.majiang_collections_server.entity.file.GameRoundConfig;
import com.randioo.majiang_collections_server.entity.po.RoleGameInfo;
import com.randioo.majiang_collections_server.entity.po.RoleMatchRule;
import com.randioo.majiang_collections_server.module.ServiceConstant;
import com.randioo.majiang_collections_server.module.fight.FightConstant;
import com.randioo.majiang_collections_server.module.fight.component.BaidaMajiangRule;
import com.randioo.majiang_collections_server.module.fight.component.HongZhongMajiangRule;
import com.randioo.majiang_collections_server.module.fight.component.MajiangRule;
import com.randioo.majiang_collections_server.module.fight.component.MajiangRule.MajiangStateEnum;
import com.randioo.majiang_collections_server.module.fight.component.Processor;
import com.randioo.majiang_collections_server.module.fight.component.QiaoMaRule;
import com.randioo.majiang_collections_server.module.fight.component.score.round.GameOverResult;
import com.randioo.majiang_collections_server.module.login.service.LoginService;
import com.randioo.majiang_collections_server.module.match.MatchConstant;
import com.randioo.majiang_collections_server.module.playback.component.PlaybackManager;
import com.randioo.majiang_collections_server.module.role.service.RoleService;
import com.randioo.majiang_collections_server.module.video.service.VideoService;
import com.randioo.majiang_collections_server.util.key.Key;
import com.randioo.majiang_collections_server.util.key.KeyStore;
import com.randioo.majiang_collections_server.util.key.RoomKey;
import com.randioo.majiang_collections_server.util.vote.OneRejectEndExceptApplyerStrategy;
import com.randioo.majiang_collections_server.util.vote.VoteBox.VoteResult;
import com.randioo.randioo_platform_sdk.utils.StringUtils;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.config.GlobleMap;
import com.randioo.randioo_server_base.db.IdClassCreator;
import com.randioo.randioo_server_base.entity.RoleInterface;
import com.randioo.randioo_server_base.log.Log;
import com.randioo.randioo_server_base.module.match.MatchHandler;
import com.randioo.randioo_server_base.module.match.MatchModelService;
import com.randioo.randioo_server_base.module.match.MatchRule;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.utils.SessionUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("matchService")
public class MatchServiceImpl extends ObserveBaseService implements MatchService {

    @Autowired
    private IdClassCreator idClassCreator;

    @Autowired
    private LoginService loginService;

    @Autowired
    private MatchModelService matchModelService;

    @Autowired
    private KeyStore keyStore;

    @Autowired
    private VideoService videoService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private HongZhongMajiangRule hongZhongMajiangRule;

    @Autowired
    private BaidaMajiangRule baidaMajiangRule;

    @Autowired
    private QiaoMaRule qiaomaRule;

    @Autowired
    private Processor processor;

    @Override
    public void init() {
        LoggerFactory.getLogger(Game.class);
        // 初始化钥匙
        for (int i = 100000; i < 200000; i++) {
            Key key = new RoomKey();
            key.setValue(i);
            keyStore.putKey(key);
        }

        GameCache.getRuleMap().put(ServiceConstant.COM_RANDIOO_RDMJ_ZHONG_HUA, hongZhongMajiangRule);
        GameCache.getRuleMap().put(ServiceConstant.COM_RANDIOO_RDMJ_BAI_DA, baidaMajiangRule);
        GameCache.getRuleMap().put(ServiceConstant.COM_RANDIOO_RDMJ_QIAO_MA, qiaomaRule);
    }

    @Override
    public void initService() {
        matchModelService.setMatchHandler(new MatchHandler() {

            @Override
            public void outOfTime(MatchRule matchRule) {
                RoleMatchRule roleMatchRule = (RoleMatchRule) matchRule;
                int roleId = roleMatchRule.getRoleId();

                Role role = (Role) RoleCache.getRoleById(roleId);
                logger.info("匹配超时 {}", roleMatchRule.toString());
            }

            @Override
            public void matchSuccess(Map<String, MatchRule> matchMap) {
                List<RoleMatchRule> list = new ArrayList<>(matchMap.size());
                for (MatchRule matchRule : matchMap.values())
                    list.add((RoleMatchRule) matchRule);

                Collections.sort(list);
                GameConfigData config = GameConfigData.newBuilder().build();
                Game game = createGame(list.get(0).getRoleId(), config);
                game.setGameType(GameType.GAME_TYPE_MATCH);

                for (MatchRule matchRule : matchMap.values()) {
                    RoleMatchRule rule = (RoleMatchRule) matchRule;

                    addAccountRole(game, rule.getRoleId());
                }

            }

            @Override
            public boolean checkMatchRule(MatchRule rule1, MatchRule rule2) {
                RoleMatchRule roleRule1 = (RoleMatchRule) rule1;
                RoleMatchRule roleRule2 = (RoleMatchRule) rule2;

                return roleRule1.getMaxCount() == roleRule2.getMaxCount();
            }

            @Override
            public boolean checkArriveMaxCount(MatchRule rule, Map<String, MatchRule> matchRuleMap) {
                RoleMatchRule roleRule = (RoleMatchRule) rule;

                return matchRuleMap.size() == roleRule.getMaxCount();
            }
        });

        matchModelService.initService();
        this.addObserver(videoService);
    }

    @Override
    public void createRoom(Role role, GameConfigData gameConfigData) {
        // 检查配置是否可以创建游戏
        gameConfigData = addPropGameConfigData(gameConfigData);
        if (!this.checkConfig(gameConfigData)) {
            MatchCreateGameResponse response = MatchCreateGameResponse.newBuilder()
                    .setErrorCode(ErrorCode.CREATE_FAILED.getNumber())
                    .build();
            SC sc = SC.newBuilder().setMatchCreateGameResponse(response).build();
            SessionUtils.sc(role.getRoleId(), sc);
            return;
        }
        // 扣除燃点币
        int roundCount = gameConfigData.getRoundCount();
        GameRoundConfig config = GameRoundConfigCache.getGameRoundByRoundCount(roundCount);

        // 重读平台用户信息
        roleService.initRoleDataFromHttp(role);
        if (role.getRandiooMoney() < config.needMoney) {
            MatchCreateGameResponse response = MatchCreateGameResponse.newBuilder()
                    .setErrorCode(ErrorCode.NOT_RANDIOOMONEY_ENOUGH.getNumber())
                    .setNeedMoney(config.needMoney)
                    .build();
            SC sc = SC.newBuilder().setMatchCreateGameResponse(response).build();
            SessionUtils.sc(role.getRoleId(), sc);
            return;
        }

        // 创建游戏1
        Game game = this.createGame(role.getRoleId(), gameConfigData);
        // 标记房间为好友房间
        game.setGameType(GameType.GAME_TYPE_FRIEND);
        game.logger.info("创建房间 {} 房间ID{} 房间号{} ,游戏类型: 好友房", gameConfigData, game.getGameId(),
                this.getLockString(game.getLockKey()));
        // 获得该玩家的id
        String gameRoleId = this.getGameRoleId(game.getGameId(), role.getRoleId());
        // 获得该玩家的游戏数据
        RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
        // 游戏数据转协议游戏数据
        GameRoleData myGameRoleData = this.parseGameRoleData(roleGameInfo, game);

        // 获得房间锁
        String lockString = this.getLockString(game.getLockKey());

        // 创建游戏消息返回
        MatchCreateGameResponse response = MatchCreateGameResponse.newBuilder()
                .setRoomId(lockString)
                .setGameRoleData(myGameRoleData)
                .build();
        SC matchCreateGameResponse = SC.newBuilder().setMatchCreateGameResponse(response).build();
        SessionUtils.sc(role.getRoleId(), matchCreateGameResponse);

        // 发送创建游戏的通知
        this.notifyObservers(MatchConstant.MATCH_CREATE_GAME, game, roleGameInfo);

        processor.push(game, MajiangStateEnum.STATE_WAIT_OPERATION);
        processor.process(game);

        // 当收到创建房间的主推时就要显示准备按钮
        // SessionUtils.sc(
        // role.getRoleId(),
        // SC.newBuilder()
        // .setSCMatchCreateGame(
        // SCMatchCreateGame.newBuilder().setRoomId(lockString).setGameRoleData(myGameRoleData)
        // .setRoomType(GameType.GAME_TYPE_FRIEND.getNumber())
        // .setRoundNum(gameConfigData.getRoundCount())).build());

    }

    private boolean checkConfig(GameConfigData gameConfigData) {
        // TODO
        boolean check = true;
        check &= gameConfigData.getMaxCount() >= 2;// 检查人数大于2
        check &= !StringUtils.isNullOrEmpty(gameConfigData.getEndTime());// 必须要有结束时间
        return check;
    }

    /**
     * 为配置表增加属性
     * 
     * @param config
     * @return
     */
    private GameConfigData addPropGameConfigData(GameConfigData config) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, MatchConstant.HOURS);
        Date date = calendar.getTime();

        String endTime = TimeUtils.get_HHmmss_DateFormat().format(date);

        config = config.toBuilder().setMaxCount(4).setEndTime(endTime).build();
        return config;
    }

    /**
     * 创建游戏
     * 
     * @param roleId
     * @return
     * @author wcy 2017年5月26日
     */
    @Override
    public Game createGame(int roleId, GameConfigData gameConfigData) {
        // 通过配置表创建游戏
        Game game = this.createGameByGameConfig(gameConfigData);

        // 初始化所有的玩家信息,只放在RoleIdList中，因为列表删除后索引会发生变化
        int maxCount = gameConfigData.getMaxCount();
        for (int i = 0; i < maxCount; i++) {
            String nullGameRoleId = this.getNullGameRoleId(game.getGameId());
            game.getRoleIdList().add(nullGameRoleId);
        }
        // 将创建房间的人加入到该房间
        this.addAccountRole(game, roleId);
        // 设置房主
        game.setMasterRoleId(roleId);

        return game;
    }

    @Override
    public Game createGameByGameConfig(GameConfigData gameConfigData) {
        Game game = new Game();
        int gameId = idClassCreator.getId(Game.class);
        game.setGameId(gameId);
        game.setGameState(GameState.GAME_STATE_PREPARE);
        game.setFinishRoundCount(0);

        // 获得钥匙
        RoomKey key = (RoomKey) this.getLockKey();
        key.setGameId(gameId);
        game.setLockKey(key);
        String lockString = this.getLockString(key);
        String loggerTag = "roomId=" + lockString + " time=" + TimeUtils.getDetailTimeStr();
        game.logger = Log.create(LoggerFactory.getLogger(Game.class), loggerTag);

        // 设置房间号
        gameConfigData = gameConfigData.toBuilder().setRoomId(lockString).build();
        game.setGameConfig(gameConfigData);

        game.getVoteBox().setStrategy(new OneRejectEndExceptApplyerStrategy() {

            @Override
            public VoteResult waitVote(String joiner) {
                int roleId = Integer.parseInt(joiner.split("_")[1]);
                IoSession session = SessionCache.getSessionById(roleId);
                if (session == null || !session.isConnected() || session.isClosing()) {
                    return VoteResult.PASS;
                }
                return VoteResult.WAIT;
            }
        });

        // 设置麻将规则
        String gameString = GlobleMap.String(GlobleConstant.ARGS_PLATFORM_PACKAGE_NAME);

        MajiangRule rule = GameCache.getRuleMap().get(gameString);
        game.setRule(rule);

        // 复制环境变量
        this.copyGlobleMap(game);

        GameCache.getGameMap().put(gameId, game);
        GameCache.getGameLockStringMap().put(lockString, gameId);

        return game;
    }

    /**
     * 复制环境变量
     * 
     * @param game
     * @author wcy 2017年9月4日
     */
    private void copyGlobleMap(Game game) {
        Field field = ReflectionUtils.findField(GlobleMap.class, "paramMap");
        ReflectionUtils.makeAccessible(field);
        @SuppressWarnings("unchecked")
        Map<String, Object> paramMap = (Map<String, Object>) ReflectionUtils.getField(field, null);
        game.envVars.putParam(paramMap);
    }

    /**
     * 加入玩家
     * 
     * @param game
     * @param roleId
     * @author wcy 2017年5月26日
     */
    private void addAccountRole(Game game, int roleId) {
        String gameRoleId = getGameRoleId(game.getGameId(), roleId);
        game.logger.debug("加入玩家{}", gameRoleId);

        this.addRole(game, roleId, gameRoleId);
    }

    /**
     * 加入ai
     * 
     * @param game
     * @author wcy 2017年5月26日
     */
    private String addAIRole(Game game) {
        String gameRoleId = this.getAIGameRoleId(game.getGameId());

        addRole(game, 0, gameRoleId);
        // 机器人自动准备完毕
        game.getRoleIdMap().get(gameRoleId).ready = true;
        return gameRoleId;
    }

    /**
     * 添加玩家的接口,无论是否是人工智能
     * 
     * @param game
     * @param roleId
     * @param gameRoleId
     */
    private void addRole(Game game, int roleId, String gameRoleId) {
        // 创建玩家游戏数据
        if (game.getRoleIdMap().containsKey(gameRoleId))
            return;
        String lockString = this.getLockString(game.getLockKey());
        RoleGameInfo roleGameInfo = this.createRoleGameInfo(roleId, gameRoleId);
        RoleInterface roleInterface = RoleCache.getRoleById(roleGameInfo.roleId);
        game.logger.info(" 房间{} 加入 account:{} ", lockString, roleInterface == null ? "ai" : roleInterface.getAccount());
        game.getRoleIdMap().put(gameRoleId, roleGameInfo);

        String nullGameRoleId = this.getNullGameRoleId(game.getGameId());
        // 获取空玩家的第一个索引
        int seat = game.getRoleIdList().indexOf(nullGameRoleId);
        game.getRoleIdList().set(seat, gameRoleId);

        if (roleId != 0) {
            Role role = loginService.getRoleById(roleId);
            role.setGameId(game.getGameId());
            game.logger.info("加入房间{}", lockString);
        }
    }

    /**
     * 创建用户在游戏中的数据结构
     * 
     * @param roleId
     * @param gameId
     * @return
     * @author wcy 2017年5月25日
     */
    private RoleGameInfo createRoleGameInfo(int roleId, String gameRoleId) {
        RoleGameInfo roleGameInfo = new RoleGameInfo();
        roleGameInfo.roleId = roleId;
        roleGameInfo.gameRoleId = gameRoleId;

        return roleGameInfo;
    }

    @Override
    public void preJoinGame(Role role, String lockString) {
        Integer gameId = GameCache.getGameLockStringMap().get(lockString);
        MatchPreJoinResponse.Builder responseBuilder = MatchPreJoinResponse.newBuilder();
        if (gameId == null) {
            responseBuilder.setErrorCode(ErrorCode.GAME_JOIN_ERROR.getNumber());
            SessionUtils.sc(role.getRoleId(), SC.newBuilder().setMatchPreJoinResponse(responseBuilder).build());
            return;
        }

        Game game = GameCache.getGameMap().get(gameId);
        if (game == null) {
            responseBuilder.setErrorCode(ErrorCode.GAME_JOIN_ERROR.getNumber());
            SessionUtils.sc(role.getRoleId(), SC.newBuilder().setMatchPreJoinResponse(responseBuilder).build());

            return;
        }

        synchronized (game) {
            // 检查到达房间最大人数green hat
            boolean reachMaxRoleCount = this.checkRoomMaxCount(game);

            String gameRoleId = this.getGameRoleId(gameId, role.getRoleId());
            boolean inRoom = game.getRoleIdMap().containsKey(gameRoleId);
            // 房间到达上限且该人不在房间中
            if (reachMaxRoleCount && !inRoom) {
                responseBuilder.setErrorCode(ErrorCode.MATCH_MAX_ROLE_COUNT.getNumber());
                SessionUtils.sc(role.getRoleId(), SC.newBuilder().setMatchPreJoinResponse(responseBuilder).build());
                return;
            }

            boolean isIpLimit = game.getGameConfig().getIpLimit();
            if (isIpLimit) {
                boolean hasSameIp = this.checkIp(game, role);
                // ip相同不能进入
                if (hasSameIp) {
                    responseBuilder.setErrorCode(ErrorCode.IP_LIMIT.getNumber());
                    SessionUtils.sc(role.getRoleId(), SC.newBuilder().setMatchPreJoinResponse(responseBuilder).build());
                    return;
                }
            }

            responseBuilder.setRoomId(lockString);
            SessionUtils.sc(role.getRoleId(), SC.newBuilder().setMatchPreJoinResponse(responseBuilder).build());

            // 预加入房间成功清空上一局数据
            role.setGameOverSC(null);
            role.setGameConfigData(null);

            this.joinGameProcess1(role, gameId);
        }

    }

    @Override
    public void joinGame(Role role, String lockString) {
        Integer gameId = GameCache.getGameLockStringMap().get(lockString);
        role.logger.debug("加入房间 {}", lockString);
        if (gameId == null) {
            SessionUtils.sc(
                    role.getRoleId(),
                    SC.newBuilder()
                            .setMatchJoinGameResponse(
                                    MatchJoinGameResponse.newBuilder().setErrorCode(
                                            ErrorCode.GAME_JOIN_ERROR.getNumber()))
                            .build());
            return;
        }

        Game game = GameCache.getGameMap().get(gameId);
        if (game == null) {
            SessionUtils.sc(
                    role.getRoleId(),
                    SC.newBuilder()
                            .setMatchJoinGameResponse(
                                    MatchJoinGameResponse.newBuilder().setErrorCode(
                                            ErrorCode.GAME_JOIN_ERROR.getNumber()))
                            .build());
            return;
        }

        synchronized (game) {
            // 如果锁相同则可以进
            if (!game.getGameConfig().getRoomId().equals(lockString)) {
                SessionUtils.sc(
                        role.getRoleId(),
                        SC.newBuilder()
                                .setMatchJoinGameResponse(
                                        MatchJoinGameResponse.newBuilder().setErrorCode(
                                                ErrorCode.MATCH_ERROR_LOCK.getNumber()))
                                .build());
                return;
            }

            String gameRoleId = this.getGameRoleId(gameId, role.getRoleId());
            boolean inRoom = game.getRoleIdMap().containsKey(gameRoleId);
            // 获得自己的录像没有返回空的列表
            game.logger.info("{} 尝试获得重连数据,是否在房间中 {}", gameRoleId, inRoom);
            List<ByteString> scList = inRoom ? getRejoinSCList(game, gameRoleId) : new ArrayList<ByteString>();
            RoundVideoData roundVideoData = RoundVideoData.newBuilder().addAllSc(scList).build();

            RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
            // 加入房间流程2
            try {
                this.joinGameProcess2(role, roleGameInfo, game);
            } catch (Exception e) {
                MatchJoinGameResponse matchJoinGameResponse = MatchJoinGameResponse.newBuilder()
                        .setErrorCode(ErrorCode.GAME_JOIN_ERROR.getNumber())
                        .build();
                SC sc = SC.newBuilder().setMatchJoinGameResponse(matchJoinGameResponse).build();
                SessionUtils.sc(role.getRoleId(), sc);
            }

            MajiangRule majiangRule = game.getRule();
            List<Integer> cards = majiangRule.getCards();
            List<Integer> flowers = majiangRule.getFlowers();
            String roomId = game.getGameConfig().getRoomId();
            MatchJoinGameResponse response = MatchJoinGameResponse.newBuilder()
                    .setRoundVideoData(roundVideoData)
                    .setGameConfigData(game.getGameConfig())
                    .setRoomId(roomId)
                    .addAllAllCards(cards)
                    .addAllAllFlowers(flowers)
                    .build();

            SC responseSC = SC.newBuilder().setMatchJoinGameResponse(response).build();

            SessionUtils.sc(role.getRoleId(), responseSC);
        }
    }

    private boolean checkIp(Game game, Role role) {
        String joinRoleIp = getIp(role.getRoleId());
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            String ip = getIp(info.roleId);
            if (joinRoleIp.equals(ip)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查到达房间最大人数
     * 
     * @param game
     * @return
     * @author wcy 2017年7月28日
     */
    private boolean checkRoomMaxCount(Game game) {
        GameConfigData config = game.getGameConfig();
        return game.getRoleIdMap().size() >= config.getMaxCount();
    }

    @Override
    public void clearSeatByGameRoleId(Game game, String gameRoleId) {
        int gameId = game.getGameId();
        int index = game.getRoleIdList().indexOf(gameRoleId);
        if (index == -1) {
            return;
        }
        String nullGameRoleId = this.getNullGameRoleId(gameId);
        game.getRoleIdList().set(index, nullGameRoleId);
    }

    /**
     * 获得重连的SC列表
     * 
     * @param game
     * @param gameRoleId
     * @author wcy 2017年7月27日
     */
    @Override
    public List<ByteString> getRejoinSCList(Game game, String gameRoleId) {
        List<ByteString> scList = new ArrayList<>();
        RoleGameInfo myInfo = game.getRoleIdMap().get(gameRoleId);
        VideoData videoData = myInfo.videoData;
        List<List<SC>> scs = videoData.getScList();
        if (scs != null) {
            for (SC sc : scs.get(0)) {
                scList.add(sc.toByteString());
            }
        }
        // 单局内的sc
        for (SC sc : myInfo.roundSCList) {
            scList.add(sc.toByteString());
        }
        game.logger.info("{} 重连数据 \n 重连数据结束", gameRoleId);

        return scList;
    }

    @Override
    public void joinGameProcess1(Role role, int gameId) {

        Game game = GameCache.getGameMap().get(gameId);

        game.logger.info("预加入房间 {}", role.getAccount());

        // 已经在房间里的人不进行加入房间操作
        String gameRoleId = this.getGameRoleId(game.getGameId(), role.getRoleId());
        boolean containsGameRoleId = game.getRoleIdMap().containsKey(gameRoleId);
        if (!containsGameRoleId) {
            this.addAccountRole(game, role.getRoleId());
        }

    }

    @Override
    public void joinGameProcess2(Role role, RoleGameInfo roleGameInfo, Game game) {

        // 先把自己的信息返回给客户端
        GameRoleData myGameRoleData = this.parseGameRoleData(roleGameInfo, game);
        int gameId = game.getGameId();

        SC scJoinGame = SC.newBuilder()
                .setSCMatchJoinGame(SCMatchJoinGame.newBuilder().setGameRoleData(myGameRoleData).setIsMe(true))
                .build();
        SessionUtils.sc(role.getRoleId(), scJoinGame);

        // 如果玩家没有准备,提示准备
        if (!roleGameInfo.ready && game.getFinishRoundCount() == 0) {

            SC scNoticeReady = SC.newBuilder().setSCFightNoticeReady(SCFightNoticeReady.newBuilder()).build();
            // 告诉该玩家准备
            SessionUtils.sc(role.getRoleId(), scNoticeReady);
            this.notifyObservers(FightConstant.FIGHT_NOTICE_READY, scNoticeReady, game, roleGameInfo);
            game.logger.info("通知准备 {} {}", role.getAccount(), roleGameInfo.gameRoleId);
        }
        // 设置游戏配置，用于重连
        role.setGameConfigData(game.getGameConfig());

        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            if (role.getRoleId() == info.roleId) {
                // 如果人没有满则要记录加入的人的信息
                this.notifyObservers(MatchConstant.JOIN_GAME, scJoinGame, gameId, info);
                continue;
            }

            // 通知自己当前房间里面其他玩家的信息
            GameRoleData gameRoleData = this.parseGameRoleData(info, game);

            SessionUtils.sc(
                    role.getRoleId(),
                    SC.newBuilder()
                            .setSCMatchJoinGame(
                                    SCMatchJoinGame.newBuilder().setGameRoleData(gameRoleData).setIsMe(false))
                            .build());

            game.logger.info("第二次加入房间号:{}", game.getGameConfig().getRoomId());

            // 告诉其他玩家自己进入房间
            SessionUtils.sc(
                    info.roleId,
                    SC.newBuilder()
                            .setSCMatchJoinGame(
                                    SCMatchJoinGame.newBuilder().setGameRoleData(myGameRoleData).setIsMe(false))
                            .build());
            this.notifyObservers(MatchConstant.JOIN_GAME, scJoinGame, gameId, info);
        }
    }

    @Override
    public void fillAI(Game game) {
        GameConfigData gameConfigData = game.getGameConfig();
        // 检查是否可以加入npc
        int needAllAICount = gameConfigData.getMaxCount() - game.getRoleIdMap().size();
        // 先检查要发送给多少个真人
        List<RoleGameInfo> realRoleGameInfos = new ArrayList<>(game.getRoleIdMap().values());
        for (int i = 0; i < needAllAICount; i++) {
            String aiGameRoleId = addAIRole(game);

            RoleGameInfo info = game.getRoleIdMap().get(aiGameRoleId);
            GameRoleData aiGameRoleData = this.parseGameRoleData(info, game);

            SC scJoinGame = SC.newBuilder()
                    .setSCMatchJoinGame(SCMatchJoinGame.newBuilder().setGameRoleData(aiGameRoleData))
                    .build();
            for (RoleGameInfo roleGameInfo : realRoleGameInfos) {
                SessionUtils.sc(roleGameInfo.roleId, scJoinGame);
                this.notifyObservers(MatchConstant.JOIN_GAME, scJoinGame, game.getGameId(), info);
            }
        }
    }

    @Override
    public GeneratedMessage match(Role role) {
        RoleMatchRule matchRule = new RoleMatchRule();
        matchRule.setId(idClassCreator.getId(RoleMatchRule.class) + "_" + role.getRoleId());
        matchRule.setWaitTime(60);
        matchRule.setAi(false);
        matchRule.setMatchTime(TimeUtils.getNowTime());
        matchModelService.matchRole(matchRule);
        return null;
    }

    @Override
    public void cancelMatch(Role role) {

    }

    @Override
    public void serviceCancelMatch(Role role) {

    }

    @Override
    public GameRoleData parseGameRoleData(RoleGameInfo info, Game game) {
        // FIXME
        int index = game.getRoleIdList().indexOf(info.gameRoleId);

        boolean ready = info.ready;
        if (game.getGameState() == GameState.GAME_START_START) {
            ready = false;
        }

        // 如果是机器人，则都是上线状态
        if (info.roleId <= 0) {
            GameRoleData aiGameRoleData = GameRoleData.newBuilder()
                    .setGameRoleId(info.gameRoleId)
                    .setReady(ready)
                    .setSeat(index)
                    .setName(ServiceConstant.ROBOT_PREFIX_NAME + info.gameRoleId)
                    .setOnline(true)
                    .setVoiceId(MatchConstant.ROBOT_VOICE_ID)
                    .build();
            return aiGameRoleData;
        }
        Role role = loginService.getRoleById(info.roleId);
        IoSession session = SessionCache.getSessionById(info.roleId);
        boolean online = session != null && session.isConnected();

        // 设置玩家平台id，就是帐号，如果是机器人使用默认字符串
        String platformRoleId = role == null ? ServiceConstant.ROBOT_PLATFORM_ID : role.getAccount();

        GameOverResult gameOverResult = game.getStatisticResultMap().get(info.gameRoleId);
        int score = gameOverResult == null ? 0 : gameOverResult.score;
        int flowerCount = info.flowerCount;
        // 经纬度
        String lantilongi = StringUtils.isNullOrEmpty(role.getLantiLongi()) ? "" : role.getLantiLongi();
        String voiceId = StringUtils.isNullOrEmpty(role.getVoiceId()) ? "0" : role.getVoiceId();
        return GameRoleData.newBuilder()
                .setGameRoleId(info.gameRoleId)
                .setReady(ready)
                .setSeat(index)
                .setAccount(getAccount(info.roleId))
                .setIp(getIp(info.roleId))
                .setName(role.getName())
                .setHeadImgUrl(role.getHeadImgUrl())
                .setMoney(role.getRandiooMoney())
                .setSex(role.getSex())
                .setScore(score)
                .setOnline(online)
                .setPlatformRoleId(platformRoleId)
                .setFlower(flowerCount)
                .setLatiLongi(lantilongi)
                .setVoiceId(voiceId)
                .build();
    }

    private String getIp(int roleId) {
        Role role = (Role) RoleCache.getRoleById(roleId);
        if (role == null) {
            return "";
        }
        try {
            IoSession ioSession = SessionCache.getSessionById(roleId);
            if (ioSession == null) {
                return "";
            }
            SocketAddress address = ioSession.getRemoteAddress();
            if (address == null) {
                return "";
            }
            InetSocketAddress inetAddress = (InetSocketAddress) address;
            return inetAddress.getAddress().getHostAddress();
        } catch (Exception e) {
            role.logger.error("无法获取ip地址");
            return "";
        }
    }

    private String getAccount(int roleId) {
        RoleInterface role = RoleCache.getRoleById(roleId);
        if (role == null) {
            return "";
        }
        return role.getAccount();
    }

    /**
     * 游戏内使用的玩家id
     * 
     * @param gameId
     * @param roleId
     * @return
     * @author wcy 2017年5月24日
     */
    @Override
    public String getGameRoleId(int gameId, int roleId) {
        String gameRoleId = MessageFormat.format(MatchConstant.GAME_ROLE_ID_FORMAT, gameId, roleId);
        return gameRoleId;
    }

    /**
     * 
     * @param gameId
     * @param roleId
     * @return
     * @author wcy 2017年5月24日
     */
    private String getAIGameRoleId(int gameId) {
        Game game = GameCache.getGameMap().get(gameId);
        int aiCount = 0;
        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            if (roleGameInfo.roleId == 0) {
                aiCount++;
            }
        }
        return MessageFormat.format(MatchConstant.AI_GAME_ROLE_ID_FORMAT, gameId, aiCount);
    }

    /**
     * 获得游戏中的空游戏玩家id
     * 
     * @param gameId
     * @return
     * @author wcy 2017年7月28日
     */
    private String getNullGameRoleId(int gameId) {
        return MessageFormat.format(MatchConstant.NULL_GAME_ROLE_ID_FORMAT, gameId);
    }

    private Key getLockKey() {
        Key key = keyStore.getRandomKey();
        return key;
    }

    @Override
    public String getLockString(Key key) {
        return key.getValue() + "";
    }

    @Override
    public void checkRoom(String roomId, IoSession session) {
        Role role = (Role) RoleCache.getRoleBySession(session);

        Integer gameId = GameCache.getGameLockStringMap().get(roomId);
        if (gameId == null) {
            SessionUtils.sc(session,
                    SC.newBuilder()
                            .setMatchCheckRoomResponse(MatchCheckRoomResponse.newBuilder().setExist(false))
                            .build());
            return;
        }

        Game game = GameCache.getGameMap().get(gameId);
        if (game == null) {
            SessionUtils.sc(session,
                    SC.newBuilder()
                            .setMatchCheckRoomResponse(MatchCheckRoomResponse.newBuilder().setExist(false))
                            .build());
            return;
        }

        boolean reachRoomMaxCount = checkRoomMaxCount(game);
        String gameRoleId = this.getGameRoleId(gameId, role.getRoleId());
        boolean inRoom = game.getRoleIdMap().containsKey(gameRoleId);
        if (reachRoomMaxCount && !inRoom) {
            SessionUtils.sc(session,
                    SC.newBuilder()
                            .setMatchCheckRoomResponse(MatchCheckRoomResponse.newBuilder().setExist(false))
                            .build());
            return;
        }
        SessionUtils.sc(session,
                SC.newBuilder().setMatchCheckRoomResponse(MatchCheckRoomResponse.newBuilder().setExist(true)).build());

    }
}
