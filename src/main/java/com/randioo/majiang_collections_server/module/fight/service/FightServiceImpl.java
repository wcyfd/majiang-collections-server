package com.randioo.majiang_collections_server.module.fight.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.mahjong_public_server.protocol.Entity.CallCardListData;
import com.randioo.mahjong_public_server.protocol.Entity.CallHuData;
import com.randioo.mahjong_public_server.protocol.Entity.CardListData;
import com.randioo.mahjong_public_server.protocol.Entity.CardListType;
import com.randioo.mahjong_public_server.protocol.Entity.ClientCard;
import com.randioo.mahjong_public_server.protocol.Entity.FightStartRoleData;
import com.randioo.mahjong_public_server.protocol.Entity.FightVoteApplyExit;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.mahjong_public_server.protocol.Entity.GameOverMethod;
import com.randioo.mahjong_public_server.protocol.Entity.GameRoleData;
import com.randioo.mahjong_public_server.protocol.Entity.GameState;
import com.randioo.mahjong_public_server.protocol.Entity.GameType;
import com.randioo.mahjong_public_server.protocol.Entity.HuType;
import com.randioo.mahjong_public_server.protocol.Entity.OverMethod;
import com.randioo.mahjong_public_server.protocol.Entity.RoleGameOverInfoData;
import com.randioo.mahjong_public_server.protocol.Entity.RoleRoundOverInfoData;
import com.randioo.mahjong_public_server.protocol.Entity.RoundCardsData;
import com.randioo.mahjong_public_server.protocol.Entity.ScoreData;
import com.randioo.mahjong_public_server.protocol.Error.ErrorCode;
import com.randioo.mahjong_public_server.protocol.Fight.FightAgreeExitGameResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightApplyExitGameResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightChiResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightClientDispatchResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightClientTouchCardResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightConfirmGameOverResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightExitGameResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightGangResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightGuoResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightHuResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightPengResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightQueryGameConfigResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightReadyResponse;
import com.randioo.mahjong_public_server.protocol.Fight.FightSendCardResponse;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightApplyExitGame;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightApplyExitResult;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightCardList;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightChooseCardListOver;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightClearRoomId;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightCountdown;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightDisconnect;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightExitGame;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightFillFlower;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightFlowerCount;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightGameOver;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightHu;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightNoticeChooseCardList;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightNoticeSendCard;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightPointSeat;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightReady;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightRoomDismiss;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightRoundOver;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightScore;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightSendCard;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightStart;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightTing;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightTing.Builder;
import com.randioo.mahjong_public_server.protocol.Fight.SCFightTouchCard;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.majiang_collections_server.GlobleConstant;
import com.randioo.majiang_collections_server.cache.file.GameRoundConfigCache;
import com.randioo.majiang_collections_server.cache.local.GameCache;
import com.randioo.majiang_collections_server.cache.local.RaceCache;
import com.randioo.majiang_collections_server.cache.local.VideoCache;
import com.randioo.majiang_collections_server.comparator.CallCardListComparator;
import com.randioo.majiang_collections_server.comparator.CardComparator;
import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.majiang_collections_server.entity.file.GameRoundConfig;
import com.randioo.majiang_collections_server.entity.po.AIChooseCallCardListTimeEvent;
import com.randioo.majiang_collections_server.entity.po.AISendCardTimeEvent;
import com.randioo.majiang_collections_server.entity.po.CallCardList;
import com.randioo.majiang_collections_server.entity.po.CardSort;
import com.randioo.majiang_collections_server.entity.po.FillFlowerBox;
import com.randioo.majiang_collections_server.entity.po.Race;
import com.randioo.majiang_collections_server.entity.po.RoleGameInfo;
import com.randioo.majiang_collections_server.entity.po.env_vars.EnvVars;
import com.randioo.majiang_collections_server.module.ServiceConstant;
import com.randioo.majiang_collections_server.module.fight.FightConstant;
import com.randioo.majiang_collections_server.module.fight.component.BaidaMajiangRule;
import com.randioo.majiang_collections_server.module.fight.component.CardChecker;
import com.randioo.majiang_collections_server.module.fight.component.CreateBaiDaCard;
import com.randioo.majiang_collections_server.module.fight.component.DiceCreator;
import com.randioo.majiang_collections_server.module.fight.component.FillFlower;
import com.randioo.majiang_collections_server.module.fight.component.Flow;
import com.randioo.majiang_collections_server.module.fight.component.GameSeat;
import com.randioo.majiang_collections_server.module.fight.component.HongZhongMajiangRule;
import com.randioo.majiang_collections_server.module.fight.component.MajiangRule;
import com.randioo.majiang_collections_server.module.fight.component.MajiangRule.MajiangStateEnum;
import com.randioo.majiang_collections_server.module.fight.component.Processor;
import com.randioo.majiang_collections_server.module.fight.component.QiaoMaRule;
import com.randioo.majiang_collections_server.module.fight.component.RoleGameInfoGetter;
import com.randioo.majiang_collections_server.module.fight.component.SeatIndexCalc;
import com.randioo.majiang_collections_server.module.fight.component.WaitOtherCallCardListChecker;
import com.randioo.majiang_collections_server.module.fight.component.ZhuangJudger;
import com.randioo.majiang_collections_server.module.fight.component.calcuator.HuTypeCalculator;
import com.randioo.majiang_collections_server.module.fight.component.calcuator.ScoreCalculator;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.CardList;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Chi;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Gang;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Hu;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Peng;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Step5Hu;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.ZLPBaiDaHu;
import com.randioo.majiang_collections_server.module.fight.component.dispatch.CardPart;
import com.randioo.majiang_collections_server.module.fight.component.dispatch.ClientDispatcher;
import com.randioo.majiang_collections_server.module.fight.component.dispatch.DebugDispatcher;
import com.randioo.majiang_collections_server.module.fight.component.dispatch.Dispatcher;
import com.randioo.majiang_collections_server.module.fight.component.dispatch.RandomDispatcher;
import com.randioo.majiang_collections_server.module.fight.component.fly.BaidaFlyCreater;
import com.randioo.majiang_collections_server.module.fight.component.fly.BaidaFlyResult;
import com.randioo.majiang_collections_server.module.fight.component.fly.ZhamaChecker;
import com.randioo.majiang_collections_server.module.fight.component.fly.ZhamaResult;
import com.randioo.majiang_collections_server.module.fight.component.parameter.ScoreParameter;
import com.randioo.majiang_collections_server.module.fight.component.rule.SendBaiDaNotAllowHuRule;
import com.randioo.majiang_collections_server.module.fight.component.score.round.GameOverResult;
import com.randioo.majiang_collections_server.module.fight.component.score.round.RoundOverCalculator;
import com.randioo.majiang_collections_server.module.fight.component.score.round.RoundOverParameter;
import com.randioo.majiang_collections_server.module.fight.component.score.round.RoundOverResult;
import com.randioo.majiang_collections_server.module.fight.component.score.round.qiaoma.QiaomaRoundOverCalculator;
import com.randioo.majiang_collections_server.module.login.service.LoginService;
import com.randioo.majiang_collections_server.module.match.service.MatchService;
import com.randioo.majiang_collections_server.module.role.service.RoleService;
import com.randioo.majiang_collections_server.util.Lists;
import com.randioo.majiang_collections_server.util.key.Key;
import com.randioo.majiang_collections_server.util.key.KeyStore;
import com.randioo.majiang_collections_server.util.vote.VoteBox;
import com.randioo.majiang_collections_server.util.vote.VoteBox.VoteResult;
import com.randioo.randioo_platform_sdk.RandiooPlatformSdk;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.config.GlobleMap;
import com.randioo.randioo_server_base.config.GlobleXmlLoader;
import com.randioo.randioo_server_base.log.HttpLogUtils;
import com.randioo.randioo_server_base.scheduler.EventScheduler;
import com.randioo.randioo_server_base.scheduler.SchedulerManager;
import com.randioo.randioo_server_base.scheduler.TimeEvent;
import com.randioo.randioo_server_base.sensitive.SensitiveWordDictionary;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.service.ServiceManager;
import com.randioo.randioo_server_base.template.Function;
import com.randioo.randioo_server_base.template.Observer;
import com.randioo.randioo_server_base.template.Ref;
import com.randioo.randioo_server_base.utils.RandomUtils;
import com.randioo.randioo_server_base.utils.ReflectUtils;
import com.randioo.randioo_server_base.utils.SessionUtils;
import com.randioo.randioo_server_base.utils.SpringContext;
import com.randioo.randioo_server_base.utils.StringUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("fightService")
public class FightServiceImpl extends ObserveBaseService implements FightService {

    @Autowired
    private MatchService matchService;

    @Autowired
    private EventScheduler eventScheduler;

    @Autowired
    private LoginService loginService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RandomDispatcher randomDispatcher;

    @Autowired
    private ClientDispatcher clientDispatcher;

    @Autowired
    private DebugDispatcher debugDispatcher;

    @Autowired
    private ZhamaChecker zhamaChecker;

    @Autowired
    private RoundOverCalculator roundOverCalculator;

    @Autowired
    private WaitOtherCallCardListChecker waitOtherCallCardListChecker;

    @Autowired
    private ZhuangJudger zhuangJudger;

    @Autowired
    private SendBaiDaNotAllowHuRule sendBaiDaNotAllowHuRule;

    @Autowired
    private CallCardListComparator callCardListComparator;

    @Autowired
    private RandiooPlatformSdk randiooPlatformSdk;

    @Autowired
    private HongZhongMajiangRule hongZhongMajiangRule;

    @Autowired
    private Processor processor;

    @Autowired
    private GameSeat gameSeat;

    @Autowired
    private RoleGameInfoGetter roleGameInfoGetter;

    @Autowired
    private FillFlower fillFlower;

    @Autowired
    private SeatIndexCalc seatIndexCalc;

    @Autowired
    private CardChecker cardChecker;

    @Autowired
    private HuTypeCalculator huTypeCalc;

    @Autowired
    private ScoreCalculator scoreCalc;

    @Autowired
    private BaidaFlyCreater baidaFlyCreater;

    @Autowired
    private DiceCreator diceCreator;

    @Autowired
    private CreateBaiDaCard createBaiDaCard;

    @Autowired
    private QiaomaRoundOverCalculator qiaomaRoundOverCalc;

    private Scanner in = new Scanner(System.in);

    @Override
    public void init() {

        // 确定百搭牌
        GameCache.getBaiDaCardNumSet().add(801);

        List<Class<? extends CardList>> lists = new ArrayList<>();
        lists.add(Gang.class);
        lists.add(Peng.class);
        lists.add(Chi.class);
        lists.add(Hu.class);

        Map<Class<? extends CardList>, CardList> cardLists = GameCache.getCardLists();

        cardLists.put(Gang.class, ReflectUtils.newInstance(Gang.class));
        cardLists.put(Peng.class, ReflectUtils.newInstance(Peng.class));
        cardLists.put(Chi.class, ReflectUtils.newInstance(Chi.class));
        cardLists.put(Hu.class, ReflectUtils.newInstance(ZLPBaiDaHu.class));

        GameCache.getCheckCardListSequence().add(Hu.class);
        GameCache.getCheckCardListSequence().add(Gang.class);
        GameCache.getCheckCardListSequence().add(Peng.class);

        GameCache.getCheckCardListOnlyMoHuSequence().add(Gang.class);
        GameCache.getCheckCardListOnlyMoHuSequence().add(Peng.class);

        GameCache.getCheckSelfCardList().add(Hu.class);
        GameCache.getCheckSelfCardList().add(Gang.class);

        GameCache.getCheckGangCardList().add(Hu.class);

        GameCache.getParseCardListToProtoFunctionMap().put(Chi.class, new Function() {
            @Override
            public Object apply(Object... params) {
                return parseChi((Chi) params[0]);
            }
        });

        // 各种转换方法
        GameCache.getParseCardListToProtoFunctionMap().put(Peng.class, new Function() {
            @Override
            public Object apply(Object... params) {
                return parsePeng((Peng) params[0]);
            }
        });
        GameCache.getParseCardListToProtoFunctionMap().put(Gang.class, new Function() {
            @Override
            public Object apply(Object... params) {
                return parseGang((Gang) params[0]);
            }
        });
        GameCache.getParseCardListToProtoFunctionMap().put(Hu.class, new Function() {
            @Override
            public Object apply(Object... params) {
                return parseHu((Hu) params[0]);
            }
        });

        // 添加proto数据结构加入方法
        GameCache.getNoticeChooseCardListFunctionMap().put(Chi.class, new Function() {
            @Override
            public Object apply(Object... params) {
                SCFightNoticeChooseCardList.Builder builder = (SCFightNoticeChooseCardList.Builder) params[0];
                int callId = (Integer) params[1];
                CardListData chiData = (CardListData) params[2];
                builder.addCallCardListData(CallCardListData.newBuilder().setCallId(callId).setCardListData(chiData));
                return null;
            }
        });
        GameCache.getNoticeChooseCardListFunctionMap().put(Peng.class, new Function() {
            @Override
            public Object apply(Object... params) {
                SCFightNoticeChooseCardList.Builder builder = (SCFightNoticeChooseCardList.Builder) params[0];
                int callId = (Integer) params[1];
                CardListData pengData = (CardListData) params[2];
                builder.addCallCardListData(CallCardListData.newBuilder().setCallId(callId).setCardListData(pengData));
                return null;
            }
        });
        GameCache.getNoticeChooseCardListFunctionMap().put(Gang.class, new Function() {
            @Override
            public Object apply(Object... params) {
                SCFightNoticeChooseCardList.Builder builder = (SCFightNoticeChooseCardList.Builder) params[0];
                int callId = (Integer) params[1];
                CardListData gangData = (CardListData) params[2];
                builder.addCallCardListData(CallCardListData.newBuilder().setCallId(callId).setCardListData(gangData));
                return null;
            }
        });
        GameCache.getNoticeChooseCardListFunctionMap().put(Hu.class, new Function() {
            @Override
            public Object apply(Object... params) {
                SCFightNoticeChooseCardList.Builder builder = (SCFightNoticeChooseCardList.Builder) params[0];
                int callId = (Integer) params[1];
                RoundCardsData huData = (RoundCardsData) params[2];
                builder.addCallHuData(CallHuData.newBuilder().setHuData(huData).setCallId(callId));
                return null;
            }
        });
    }

    @Override
    public void update(Observer observer, String msg, Object... args) {

        if (msg.equals(FightConstant.FIGHT_NOTICE_SEND_CARD)) {
            Game game = (Game) args[1];
            int seat = (int) args[2];
            this.ifAIAutoSendCard(game.getGameId(), seat);
            return;
        }

        if (msg.equals(FightConstant.FIGHT_GANG_PENG_HU)) {
            Game game = (Game) args[0];
            int gameId = game.getGameId();
            int seat = (int) args[1];
            SC sc = (SC) args[2];
            SCFightNoticeChooseCardList scFightNoticeChooseCardList = sc.getSCFightNoticeChooseCardList();
            this.ifAIAutoGangPengHu(gameId, seat, scFightNoticeChooseCardList);
            return;
        }

        // if (FightConstant.FIGHT_GANG_MING.equals(msg)) {
        // Game game = (Game) args[0];
        // RoleGameInfo roleGameInfo = (RoleGameInfo) args[1];
        // RoleGameInfo targetRoleGameInfo = (RoleGameInfo) args[2];
        //
        // this.calcMingGangScore(game, roleGameInfo, targetRoleGameInfo);
        // return;
        // }
        //
        // if (FightConstant.FIGHT_GANG_DARK.equals(msg)) {
        // Game game = (Game) args[0];
        // RoleGameInfo roleGameInfo = (RoleGameInfo) args[1];
        //
        // this.calcDarkGangScore(game, roleGameInfo);
        // return;
        // }
        //
        // if (FightConstant.FIGHT_GANG_ADD.equals(msg)) {
        // Game game = (Game) args[0];
        // RoleGameInfo roleGameInfo = (RoleGameInfo) args[1];
        //
        // this.calcAddGangScore(game, roleGameInfo);
        // return;
        // }

        if (FightConstant.FIGHT_APPLY_LEAVE.equals(msg)) {
            Game game = (Game) args[0];
            RoleGameInfo info = (RoleGameInfo) args[1];
            SC sc = (SC) args[2];

            // 如果是机器人,则直接设为同意
            if (info.roleId == 0) {
                this.voteApplyExit(game, info.gameRoleId, sc.getSCFightApplyExitGame().getApplyExitId(),
                        FightVoteApplyExit.VOTE_AGREE);
            }
            return;
        }

    }

    @Override
    public void initService() {
        this.addObserver(this);

        processor.regist(MajiangStateEnum.STATE_INIT_READY, new Flow() {

            @Override
            public void execute(Game game, int operateSeat) {
                initReady(game);
            }
        });

        processor.regist(MajiangStateEnum.STATE_BAIDA_INIT, new Flow() {

            @Override
            public void execute(Game game, int operateSeat) {
                System.out.println("baidaInit(game);");
                baidaInit(game);
            }

        });

        processor.regist(MajiangStateEnum.STATE_QIAOMA_INIT, new Flow() {

            @Override
            public void execute(Game game, int operateSeat) {
                System.out.println("qiaomaInit(game);");
                qiaomaInit(game);
            }
        });

        processor.regist(MajiangStateEnum.STATE_GAME_START, new Flow() {

            @Override
            public void execute(Game game, int seat) {
                System.out.println("gameStart(game);");
                gameStart(game);
            }
        });
        processor.regist(MajiangStateEnum.STATE_CHECK_ZHUANG, new Flow() {

            @Override
            public void execute(Game game, int seat) {
                System.out.println("checkZhuang(game)");
                checkZhuang(game);
            }
        });
        processor.regist(MajiangStateEnum.STATE_DISPATCH, new Flow() {

            @Override
            public void execute(Game game, int seat) {
                System.out.println("dispatchCard(game);");
                dispatchCard(game);
            }
        });
        processor.regist(MajiangStateEnum.STATE_SC_GAME_START, new Flow() {

            @Override
            public void execute(Game game, int seat) {
                System.out.println("noticeGameStart(game);");
                noticeGameStart(game);
            }
        });
        processor.regist(MajiangStateEnum.STATE_TOUCH_CARD, new Flow() {

            @Override
            public void execute(Game game, int seat) {
                System.out.println("touchCard(game);");
                touchCard(game);
            }
        });
        processor.regist(MajiangStateEnum.STATE_CHECK_MINE_CARDLIST, new Flow() {

            @Override
            public void execute(Game game, int seat) {
                System.out.println("checkMineCardList(game);");
                checkMineCardList(game);
            }
        });

        processor.regist(MajiangStateEnum.STATE_CHECK_MINE_CARDLIST_OUTER, new Flow() {

            @Override
            public void execute(Game game, int operateSeat) {
                System.out.println("checkMineCardListOuter(game);");
                checkMineCardListOuter(game);
            }
        });

        processor.regist(MajiangStateEnum.STATE_SC_TOUCH_CARD, new Flow() {

            @Override
            public void execute(Game game, int seat) {
                System.out.println("noticeTouchCard(game);");
                noticeTouchCard(game);
            }
        });
        processor.regist(MajiangStateEnum.STATE_SC_SEND_CARDLIST_2_ROLE, new Flow() {

            @Override
            public void execute(Game game, int seat) {
                System.out.println("sendGangPengHuMsg2Role(game);");
                sendGangPengHuMsg2Role(game);
            }
        });
        processor.regist(MajiangStateEnum.STATE_INIT_READY, new Flow() {

            @Override
            public void execute(Game game, int seat) {
                System.out.println("initReady(game);");
                initReady(game);
            }
        });

        processor.regist(MajiangStateEnum.STATE_SC_SEND_CARD, new Flow() {

            @Override
            public void execute(Game game, int seat) {
                System.out.println("noticeSendCard(game);");
                noticeSendCard(game);
            }
        });
        processor.regist(MajiangStateEnum.STATE_CHECK_OTHER_CARDLIST, new Flow() {

            @Override
            public void execute(Game game, int seat) {
                checkOtherCardList(game);
            }
        });

        processor.regist(MajiangStateEnum.STATE_NEXT_SEAT, new Flow() {

            @Override
            public void execute(Game game, int seat) {
                nextIndex(game);
            }
        });
        processor.regist(MajiangStateEnum.STATE_JUMP_SEAT, new Flow() {

            @Override
            public void execute(Game game, int seat) {
                jumpToIndex(game, seat);
            }
        });

        processor.regist(MajiangStateEnum.STATE_PENG, new Flow() {

            @Override
            public void execute(Game game, int seat) {
                pengProgress(game);
            }

        });

        processor.regist(MajiangStateEnum.STATE_GANG, new Flow() {

            @Override
            public void execute(Game game, int seat) {
                gangProcess(game);
            }

        });

        processor.regist(MajiangStateEnum.STATE_RECOVERY_PENG, new Flow() {
            @Override
            public void execute(Game game, int operateSeat) {
                recoveryGang2Peng(game);
            }
        });

        processor.regist(MajiangStateEnum.STATE_GANG_CAL_SCORE, new Flow() {

            @Override
            public void execute(Game game, int seat) {
                System.out.println("calHongzhongGangScore(game);");
                calHongzhongGangScore(game);
            }

        });

        processor.regist(MajiangStateEnum.STATE_ROUND_OVER, new Flow() {

            @Override
            public void execute(Game game, int seat) {
                System.out.println("game.getRule().executeRoundOver(game, true);");
                game.getRule().executeRoundOverProcess(game, true);
            }
        });
        processor.regist(MajiangStateEnum.STATE_GAME_OVER, new Flow() {

            @Override
            public void execute(Game game, int operateSeat) {
                System.out.println("game.getRule().executeGameOver(game);");
                game.getRule().executeGameOverProcess(game);
            }

        });
        processor.regist(MajiangStateEnum.STATE_TOUCH_FLOWER, new Flow() {

            @Override
            public void execute(Game game, int operateSeat) {
                System.out.println("touchFlower(game);");
                touchFlowerProgress(game, operateSeat);
            }

        });
        processor.regist(MajiangStateEnum.STATE_ADD_FLOWERS, new Flow() {

            @Override
            public void execute(Game game, int operateSeat) {
                System.out.println("addFlowers(game);");
                addFlowersProgress(game, operateSeat);
            }
        });

        processor.regist(MajiangStateEnum.STATE_HU, new Flow() {

            @Override
            public void execute(Game game, int operateSeat) {
                huProgress(game);
            }
        });

        processor.regist(MajiangStateEnum.STATE_CHI, new Flow() {

            @Override
            public void execute(Game game, int seat) {
                chiProgress(game);
            }

        });
        processor.regist(MajiangStateEnum.STATE_FLOWER_SCORE_CHANGE, new Flow() {

            @Override
            public void execute(Game game, int operateSeat) {
                System.out.println("flowerScoreChangeProgress(game, operateSeat);");
                flowerScoreChangeProgress(game, operateSeat);
            }
        });
        processor.regist(MajiangStateEnum.STATE_TING, new Flow() {

            @Override
            public void execute(Game game, int operateSeat) {
                System.out.println("ting(game,operateSeat);");
                ting(game, operateSeat);
            }

        });
    }

    /**
     * @param game
     * @param operateSeat
     */
    protected void ting(Game game, int operateSeat) {
        QiaoMaRule rule = (QiaoMaRule) game.getRule();
        RoleGameInfo roleGameInfo = roleGameInfoGetter.getCurrentRoleGameInfo(game);
        CardSort cardSort = new CardSort(4);
        cardSort.fillCardSort(roleGameInfo.cards);

        Step5Hu hu = (Step5Hu) rule.allCardListMap.get(Hu.class);
        boolean canTing = hu.checkTing(game, cardSort, roleGameInfo.tingCards);
        // 如果不能听，出牌的时候再处理
        game.canTing = canTing;
        if (canTing) {
            roleGameInfo.isTing = true;
            Builder fightTingBuilder = SCFightTing.newBuilder().setSeat(game.getCurrentRoleIdIndex());
            SC sc = SC.newBuilder().setSCFightTing(fightTingBuilder).build();
            this.sendAllSeatSC(game, sc);
        }
    }

    /**
     * @param game
     */
    protected void qiaomaInit(Game game) {
        GameConfigData gameConfig = game.getGameConfig();
        // 丢色子 确定荒番
        int dice[] = diceCreator.create(game);
        game.dice = dice;
        // 荒番计数器
        if (gameConfig.getHuangFan()) {
            if (dice[0] == dice[1]) {
                if (dice[0] == 1 || dice[0] == 4) {
                    game.setHuangFanCount(game.getHuangFanCount() + 2);
                } else {
                    game.setHuangFanCount(game.getHuangFanCount() + 1);
                }
            }
            // 设置当前局是否位荒番
            game.setHuangFan(game.getHuangFanCount() > 0 ? true : false);
        }
    }

    /**
     * 百搭麻将的初始化，产生百搭牌，荒番
     * 
     * @param game
     */
    protected void baidaInit(Game game) {
        GameConfigData gameConfig = game.getGameConfig();
        createBaiDaCard.createBaiDaCard(game);
        // game.setBaidaCard(309);
        // game.setFristBaidaCard(308);
        // 丢色子 确定荒番
        int dice[] = diceCreator.create(game);
        game.dice = dice;
        // 荒番计数器
        if (gameConfig.getHuangFan()) {
            if (dice[0] == dice[1]) {
                game.setHuangFanCount(game.getHuangFanCount() + 1);
            }
            // 设置当前局是否位荒番
            game.setHuangFan(game.getHuangFanCount() > 0 ? true : false);
        }
    }

    /**
     * 风向碰 或杠时花分数的变化
     * 
     * @param game
     * @param operateSeat
     */
    protected void flowerScoreChangeProgress(Game game, int seat) {
        RoleGameInfo roleGameInfo = roleGameInfoGetter.getCurrentRoleGameInfo(game);
        List<CardList> showCardLists = roleGameInfo.showCardLists;
        // 最新的杠或碰
        CardList cardList = showCardLists.get(showCardLists.size() - 1);
        if (cardList instanceof Peng) {
            Peng peng = (Peng) cardList;
            // 判断风向碰
            if (cardChecker.isFeng(peng.card)) {
                roleGameInfo.flowerCount++;
            }
        }
        if (cardList instanceof Gang) {
            Gang gang = (Gang) cardList;
            if (gang.dark) {
                // 风向暗杠+3 暗杠+2
                roleGameInfo.flowerCount += (cardChecker.isFeng(gang.card) ? 3 : 2);
                roleGameInfo.roundOverResult.darkGangCountPlus++;
            } else {
                if (gang.peng == null) { // 明杠
                    // 风向明杠+2 明杠+1
                    roleGameInfo.flowerCount += (cardChecker.isFeng(gang.card) ? 2 : 1);
                    roleGameInfo.roundOverResult.mingGangCountPlus++;
                } else {// 补杠
                        // 补杠不管是不是风向牌都+1
                    roleGameInfo.flowerCount++;
                    roleGameInfo.roundOverResult.addGangCountPlus++;
                }
            }
        }
        SC sc = SC
                .newBuilder()
                .setSCFightFlowerCount(
                        SCFightFlowerCount.newBuilder().setFlowerCount(roleGameInfo.flowerCount).setSeat(seat)).build();
        // 通知其他玩家花的变化
        this.sendAllSeatSC(game, sc);
        this.notifyObservers(FightConstant.FIGHT_FLOWER_COUNT, sc, game);
    }

    /**
     * 补花
     * 
     * @param game
     * @param operateSeat
     */
    protected void addFlowersProgress(Game game, int operateSeat) {
        game.isAddFlowerState = true;

        RoleGameInfo roleGameInfo = roleGameInfoGetter.getCurrentRoleGameInfo(game);
        FillFlowerBox flowerBox = fillFlower.fill(game.getRemainCards(), roleGameInfo);
        // 设置补花后的手牌
        roleGameInfo.cards.addAll(flowerBox.getNomalCards());
        roleGameInfo.sendFlowrCards.addAll(flowerBox.getFlowerCards());
        roleGameInfo.flowerCount += flowerBox.getFlowerCards().size();
        // 发送通知
        List<List<Integer>> cardList = flowerBox.getCardList();
        for (int i = 0; i < cardList.size(); i++) {
            for (RoleGameInfo info : game.getRoleIdMap().values()) {
                SCFightFillFlower.Builder scFightFillFlower = SCFightFillFlower.newBuilder();

                scFightFillFlower
                        .addAllCards(info.gameRoleId.equals(roleGameInfo.gameRoleId) ? flowerBox.getLine(i) : flowerBox
                                .getHideCards(i));
                scFightFillFlower.setSeat(game.getCurrentRoleIdIndex());
                SC sc = SC.newBuilder().setSCFightFillFlower(scFightFillFlower).build();
                // 发送通知
                SessionUtils.sc(info.roleId, sc);
                notifyObservers(FightConstant.FIGHT_ADD_FLOWER, sc, game, info);
            }
        }
        SC sc = SC
                .newBuilder()
                .setSCFightFlowerCount(
                        SCFightFlowerCount.newBuilder().setFlowerCount(roleGameInfo.flowerCount)
                                .setSeat(game.getCurrentRoleIdIndex())).build();
        // 通知其他玩家花的变化
        this.sendAllSeatSC(game, sc);
        this.notifyObservers(FightConstant.FIGHT_FLOWER_COUNT, sc, game);
    }

    @Override
    public void queryGameConfig(Role role) {
        int gameId = role.getGameId();
        Game game = this.getGameById(gameId);
        if (game == null) {
            FightQueryGameConfigResponse response = FightQueryGameConfigResponse.newBuilder()
                    .setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()).build();
            SC responseSC = SC.newBuilder().setFightQueryGameConfigResponse(response).build();
            SessionUtils.sc(role.getRoleId(), responseSC);
            return;
        }

        GameConfigData gameConfigData = game.getGameConfig();
        int currentRoundNumber = game.getFinishRoundCount();

        FightQueryGameConfigResponse response = FightQueryGameConfigResponse.newBuilder()
                .setGameConfigData(gameConfigData).setCurrentRoundNumber(currentRoundNumber).build();
        SC responseSC = SC.newBuilder().setFightQueryGameConfigResponse(response).build();

        SessionUtils.sc(role.getRoleId(), responseSC);

    }

    @Override
    public void readyGame(Role role) {
        loggerinfo(role, "readyGame " + role.getAccount());
        Game game = getGameById(role.getGameId());
        if (game == null) {
            FightReadyResponse response = FightReadyResponse.newBuilder()
                    .setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()).build();
            SC responseSC = SC.newBuilder().setFightReadyResponse(response).build();
            SessionUtils.sc(role.getRoleId(), responseSC);
            return;
        }

        String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());
        RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);

        // 游戏准备
        // 返回本玩家收到该消息
        SC responseSc = SC.newBuilder().setFightReadyResponse(FightReadyResponse.newBuilder()).build();
        SessionUtils.sc(roleGameInfo.roleId, responseSc);

        // 清空上局缓存
        roleGameInfo.roundSCList.clear();

        // 游戏准备
        roleGameInfo.ready = true;

        SC scFightReady = SC.newBuilder()
                .setSCFightReady(SCFightReady.newBuilder().setSeat(game.getRoleIdList().indexOf(gameRoleId))).build();

        synchronized (game) {
            // 通知其他所有玩家，该玩家准备完毕
            this.sendAllSeatSC(game, scFightReady);
            notifyObservers(FightConstant.FIGHT_READY, scFightReady, game);
        }

        boolean matchAI = game.envVars.Boolean(GlobleConstant.ARGS_MATCH_AI);
        // boolean matchAI = GlobleMap.Boolean(GlobleConstant.ARGS_MATCH_AI);
        // 检查是否全部都准备完毕,全部准备完毕
        if (matchAI) {
            matchService.fillAI(game);
        }

        processor.pop(game);
        processor.push(game, MajiangStateEnum.STATE_ROLE_GAME_READY);

        processor.process(game);

        // if (this.checkAllReady(game)) {
        // loggerinfo("game=>" + game.getGameId() + "=>startGame");
        // processor.process(game);
        // }

    }

    /**
     * 检查全部准备完毕
     * 
     * @param gameId
     * @return
     * @author wcy 2017年5月31日
     */
    @Override
    public boolean checkAllReady(Game game) {
        loggerinfo("game=>" + game.getGameId() + "=>checkAllReady");
        GameConfigData gameConfigData = game.getGameConfig();
        if (game.getRoleIdMap().size() < gameConfigData.getMaxCount())
            return false;

        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            if (!info.ready)
                return false;
        }
        return true;
    }

    @Override
    public void gameStart(Game game) {

        loggerinfo("gameStart=>game:" + game.getGameId());

        game.setGameState(GameState.GAME_START_START);
        GameConfigData config = game.getGameConfig();

        // 卡牌初始化
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            // 杠标记附空
            info.isGang = false;
            // 清除手牌
            info.cards.clear();
            // 听牌清理
            info.tingCards.clear();
            // 清空已经组成的牌组
            info.showCardLists.clear();
            // 新拿的牌清空
            info.newCard = 0;
            // 清空操作栈
            info.operations.clear();
            // 清空每个人的花牌
            info.sendFlowrCards.clear();
            // 花计数清空
            info.flowerCount = 0;
            // 每个人摸得牌
            info.everybodyTouchCard = 0;
            // 清空听状态
            info.isTing = false;

            // 如果该玩家没有结果集,则创建结果集
            Map<String, GameOverResult> resultMap = game.getStatisticResultMap();
            if (!resultMap.containsKey(info.gameRoleId)) {
                GameOverResult result = this.createRoleGameResult(info);
                resultMap.put(info.gameRoleId, result);
            }
        }

        // 重置发牌数据
        this.resetSendCard(game);
        // 剩余牌清空
        game.getRemainCards().clear();

        game.qiangGangCallCardList = null;
        game.checkOtherCardListSeats.clear();
        game.touchCardIsFlower = false;
        game.isAddFlowerState = false;
        game.canTing = true;

        // 清空桌上的牌
        Map<Integer, List<Integer>> sendDesktopCardMap = game.getSendDesktopCardMap();
        for (int i = 0; i < config.getMaxCount(); i++) {
            if (!sendDesktopCardMap.containsKey(i)) {
                List<Integer> sendList = new ArrayList<>();
                sendDesktopCardMap.put(i, sendList);
            }
            sendDesktopCardMap.get(i).clear();
        }

        // 临时列表清空
        game.getCallCardLists().clear();
        game.getHuCallCardLists().clear();

    }

    /**
     * 通知游戏开始
     * 
     * @param game
     * @author wcy 2017年8月24日
     */
    private void noticeGameStart(Game game) {
        GameConfigData gameConfigData = game.getGameConfig();
        // 获得百搭牌
        MajiangRule rule = game.getRule();
        int baida = rule.getBaidaCard(game);
        // 每个人的分数
        SCFightScore.Builder scFightScoreBuilder = SCFightScore.newBuilder();
        // 设置每个人的座位和卡牌的数量
        SCFightStart.Builder scFightStartBuilder = SCFightStart.newBuilder();
        scFightStartBuilder.setRemainCardCount(game.getRemainCards().size());
        scFightStartBuilder.setZhuangSeat(game.getZhuangSeat());
        scFightStartBuilder.setMaxRound(gameConfigData.getRoundCount());
        scFightStartBuilder.setCurrentRoundNum(game.getFinishRoundCount());
        scFightStartBuilder.setBaidaCard(baida);
        scFightStartBuilder.setFirstBaiDaCard(game.getFristBaidaCard());
        scFightStartBuilder.setRemainHuangFan(game.getHuangFanCount());

        for (int i = 0; i < gameConfigData.getMaxCount(); i++) {
            RoleGameInfo gameRoleInfo = game.getRoleIdMap().get(game.getRoleIdList().get(i));

            // 准备一下所有人的分数
            GameOverResult gameOverResult = game.getStatisticResultMap().get(gameRoleInfo.gameRoleId);
            scFightScoreBuilder.addScoreData(ScoreData.newBuilder().setScore(gameOverResult.score).setSeat(i));
        }
        SCFightStart scFightStart = scFightStartBuilder.build();

        // 发给玩家别人的卡组
        List<Integer> hideCards = new ArrayList<>(FightConstant.EVERY_INIT_CARD_COUNT);

        // 发送给每个玩家
        for (int i = 0; i < gameConfigData.getMaxCount(); i++) {
            RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, i);
            SCFightStart.Builder builder = scFightStart.toBuilder();
            // 隐藏别人的卡组
            this.hideOtherGameRoleInfoCards(roleGameInfo, hideCards, builder, game);

            SC sc = SC.newBuilder().setSCFightStart(builder).build();
            // 通知所有人游戏开始，并把自己的牌告诉场上的玩家
            SessionUtils.sc(roleGameInfo.roleId, sc);

            this.notifyObservers(FightConstant.FIGHT_START, sc, game, roleGameInfo);
            // 每次的隐藏卡组都不一样
            hideCards.clear();
        }
        // 通知一下所有人的分数
        SC fightScoreSC = SC.newBuilder().setSCFightScore(scFightScoreBuilder).build();
        this.sendAllSeatSC(game, fightScoreSC);
        this.notifyObservers(FightConstant.FIGHT_SCORE, fightScoreSC, game);

    }

    /**
     * 把不是自己牌都清空，以防客户端作弊
     * 
     * @param selfSeat
     * @param scFightStartBuilder
     * @author wcy 2017年8月7日
     */
    private void hideOtherGameRoleInfoCards(RoleGameInfo self, List<Integer> hideCards,
            SCFightStart.Builder scFightStartBuilder, Game game) {

        for (int seat = 0; seat < game.getGameConfig().getMaxCount(); seat++) {
            // 当前roleGameInfo
            RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, seat);
            boolean isSelf = self.gameRoleId.equals(roleGameInfo.gameRoleId);
            if (!isSelf) {
                // 填充隐藏卡组
                for (Integer card : roleGameInfo.cards) {
                    if (game.getRule().isFlower(card)) {
                        hideCards.add(card);

                    } else {
                        hideCards.add(0);
                    }
                }
            }
            List<Integer> cards = isSelf ? self.cards : hideCards;
            FightStartRoleData fightStartRoleData = FightStartRoleData.newBuilder().addAllPai(cards).setSeat(seat)
                    .build();
            scFightStartBuilder.addFightStartRoleData(fightStartRoleData);
            hideCards.clear();
        }

        // for (int i = 0; i < maxCount; i++) {
        // // 填充隐藏牌
        //
        // // 如果是自己则要显示卡牌的内容
        // List<Integer> cards = selfSeat == i ? selfCards : hideCards;
        // FightStartRoleData fightStartRoleData =
        // FightStartRoleData.newBuilder().addAllPai(cards).setSeat(i).build();
        // scFightStartBuilder.addFightStartRoleData(fightStartRoleData);
        // }
    }

    /**
     * 创建游戏结果集
     * 
     * @param roleGameInfo
     * @return
     * @author wcy 2017年7月12日
     */
    private GameOverResult createRoleGameResult(RoleGameInfo roleGameInfo) {
        GameOverResult gameOverResult = new GameOverResult();
        return gameOverResult;
    }

    /**
     * 检查庄家是否存在，不存在就赋值
     * 
     * @param gameId
     */
    private void checkZhuang(Game game) {
        // if (GlobleMap.Boolean(GlobleConstant.ARGS_ZHUANG)) {
        if (game.envVars.Boolean(GlobleConstant.ARGS_ZHUANG)) {
            game.setZhuangSeat(0);
        } else {
            int zhuangGameRoleId = game.getZhuangSeat();
            // 如果没有庄家，则随机一个
            if (zhuangGameRoleId == -1) {
                int index = RandomUtils.getRandomNum(game.getRoleIdMap().size());
                game.setZhuangSeat(index);
            }
        }

        // 设置出牌玩家索引
        game.setCurrentRoleIdIndex(game.getZhuangSeat());

    }

    @Override
    public void dispatchCard(Game game) {
        // 赋值所有牌,然后随机一个个取
        List<Integer> remainCards = game.getRemainCards();
        MajiangRule rule = game.getRule();
        List<Integer> allCards = rule.getCards();
        remainCards.addAll(allCards);

        // 选用指定的分牌器
        Dispatcher dispatcher = null;
        // if (GlobleMap.Boolean(GlobleConstant.ARGS_DISPATCH)) {
        if (game.envVars.Boolean(GlobleConstant.ARGS_DISPATCH)) {
            // if (GlobleMap.Boolean(GlobleConstant.ARGS_CLIENT_DISPATCH)) {
            if (game.envVars.Boolean(GlobleConstant.ARGS_CLIENT_DISPATCH)) {
                dispatcher = clientDispatcher;
            } else {
                dispatcher = debugDispatcher;
            }
        } else {
            dispatcher = randomDispatcher;
        }

        List<CardPart> cardParts = dispatcher.dispatch(game, remainCards, game.getRoleIdMap().size(),
                FightConstant.EVERY_INIT_CARD_COUNT);

        for (int i = 0; i < game.getRoleIdMap().size(); i++) {
            RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, i);
            CardPart cardPart = cardParts.get(i);
            roleGameInfo.cards.addAll(cardPart);
        }

        // 每个玩家卡牌排序
        CardComparator cardComparator = new CardComparator();
        int baidaCard = rule.getBaidaCard(game);
        cardComparator.getBaidaCardSet().add(baidaCard);
        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            Collections.sort(roleGameInfo.cards, cardComparator);
            loggerinfo("game=>" + game.getGameId() + "=>" + roleGameInfo.gameRoleId + "," + roleGameInfo.cards);
        }

    }

    @Override
    public void clientDispatchCards(Role role, List<ClientCard> cards) {
        Game game = this.getGameById(role.getGameId());

        if (game == null) {
            FightClientDispatchResponse response = FightClientDispatchResponse.newBuilder()
                    .setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()).build();
            SC sc = SC.newBuilder().setFightClientDispatchResponse(response).build();
            SessionUtils.sc(role.getRoleId(), sc);
            return;
        }
        game.getClientCards().clear();
        game.getClientCards().addAll(cards);

        SC sc = SC.newBuilder().setFightClientDispatchResponse(FightClientDispatchResponse.newBuilder()).build();
        SessionUtils.sc(role.getRoleId(), sc);
    }

    @Override
    public void exitGame(Role role) {
        Game game = GameCache.getGameMap().get(role.getGameId());
        if (game == null) {
            FightExitGameResponse response = FightExitGameResponse.newBuilder().build();
            SC sc = SC.newBuilder().setFightExitGameResponse(response).build();
            SessionUtils.sc(role.getRoleId(), sc);
            return;
        }

        // // 游戏从来没有开始,则直接退出
        // if (!this.checkGameNeverStart(game)) {
        // SessionUtils.sc(role.getRoleId(),
        // SC.newBuilder().setFightExitGameResponse(FightExitGameResponse.newBuilder()).build());
        // return;
        // }

        SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightExitGameResponse(FightExitGameResponse.newBuilder())
                .build());

        // 如果游戏没有开始则可以随时退出,如果是好友对战,并且是房主,则解散
        // 若是房主，则直接解散
        if (game.getMasterRoleId() == role.getRoleId()) {
            // // 游戏从来没有开始,则直接退出
            // if (this.checkGameNeverStart(game)) {
            // // 退还钱款
            // roleService.addRandiooMoney(role,
            // game.getGameConfig().getRoundCount() / 3 * 10);
            // roleService.initRoleDataFromHttp(role);
            // }

            // 标记比赛结束
            game.setGameState(GameState.GAME_START_END);
            VideoCache.getVideoMap().remove(game.getGameId()); // 同时删除视频
            SCFightRoomDismiss scFightRoomDismiss = SCFightRoomDismiss.newBuilder().build();
            SC scFightRoomDismissSC = SC.newBuilder().setSCFightRoomDismiss(scFightRoomDismiss).build();
            // 通知所有人比赛结束，并把游戏id标记变成0
            SC scClearRoomId = SC.newBuilder().setSCFightClearRoomId(SCFightClearRoomId.newBuilder()).build();
            // 通知房间解散
            this.sendAllSeatSC(game, scFightRoomDismissSC);
            // 清除房间号(客户端底层数据清空,与界面无关)
            this.sendAllSeatSC(game, scClearRoomId);

            for (RoleGameInfo info : game.getRoleIdMap().values()) {
                Role tempRole = (Role) RoleCache.getRoleById(info.roleId);
                if (tempRole != null) {
                    tempRole.setGameId(0);
                }
                notifyObservers(FightConstant.FIGHT_DISMISS, scClearRoomId, game.getGameId(), info);
            }

            this.destroyGame(game);
        } else {
            String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());
            // 该玩家直接退出
            // 清除房间号
            SessionUtils.sc(role.getRoleId(), SC.newBuilder().setSCFightClearRoomId(SCFightClearRoomId.newBuilder())
                    .build());

            SC scExit = SC.newBuilder()
                    .setSCFightExitGame(SCFightExitGame.newBuilder().setSeat(game.getRoleIdList().indexOf(gameRoleId)))
                    .build();
            for (RoleGameInfo info : game.getRoleIdMap().values()) {
                SessionUtils.sc(info.roleId, scExit);
                this.notifyObservers(FightConstant.FIGHT_GAME_EXIT, info, scExit);
            }
            game.getRoleIdMap().remove(gameRoleId);
            matchService.clearSeatByGameRoleId(game, gameRoleId);

            role.setGameId(0);
        }

    }

    // /**
    // * 检查游戏是否从未开始过
    // *
    // * @param game
    // * @return
    // * @author wcy 2017年6月29日
    // */
    // private boolean checkGameNeverStart(Game game) {
    // GameState gameState = game.getGameState();
    // GameConfigData gameConfig = game.getGameConfig();
    // int currentRound = game.getFinishRoundCount();
    // int maxRound = gameConfig.getRoundCount();
    // return gameState == GameState.GAME_STATE_PREPARE && currentRound ==
    // maxRound;
    // }

    @Override
    public void agreeExit(Role role, FightVoteApplyExit vote, int voteId) {
        Game game = GameCache.getGameMap().get(role.getGameId());
        if (game == null) {
            SessionUtils.sc(
                    role.getRoleId(),
                    SC.newBuilder()
                            .setFightAgreeExitGameResponse(
                                    FightAgreeExitGameResponse.newBuilder().setErrorCode(
                                            ErrorCode.GAME_NOT_EXIST.getNumber())).build());
            return;
        }

        String roleInfoStr = matchService.getGameRoleId(game.getGameId(), role.getRoleId());
        SessionUtils.sc(role.getRoleId(),
                SC.newBuilder().setFightAgreeExitGameResponse(FightAgreeExitGameResponse.newBuilder()).build());

        this.voteApplyExit(game, roleInfoStr, voteId, vote);
    }

    /**
     * 
     * @param game
     * @param voteGameRoleId
     * @param applyExitId
     * @param vote
     * @author wcy 2017年7月17日
     */
    private void voteApplyExit(Game game, String voteGameRoleId, int applyExitId, FightVoteApplyExit vote) {
        VoteBox voteBox = game.getVoteBox();

        synchronized (voteBox) {
            if (voteBox.getVoteId() != applyExitId)
                return;
            if (vote == FightVoteApplyExit.VOTE_AGREE) {
                voteBox.vote(voteGameRoleId, true, applyExitId);
            } else if (vote == FightVoteApplyExit.VOTE_REJECT) {
                voteBox.vote(voteGameRoleId, false, applyExitId);
            }

            VoteResult voteResult = voteBox.getResult();
            System.out.println("voteBox=>" + voteResult);

            if (voteResult == VoteResult.PASS || voteResult == VoteResult.REJECT) {
                SCFightApplyExitResult.Builder builder = SCFightApplyExitResult.newBuilder();
                for (Map.Entry<String, Boolean> entrySet : voteBox.getVoteMap().entrySet()) {
                    String key = entrySet.getKey();
                    boolean value = entrySet.getValue();
                    RoleGameInfo roleGameInfo = game.getRoleIdMap().get(key);
                    Role role = (Role) RoleCache.getRoleById(roleGameInfo.roleId);
                    String name = role == null ? ServiceConstant.ROBOT_PREFIX_NAME + game.getRoleIdList().indexOf(key) : role
                            .getName();

                    if (value)
                        builder.addAgreeName(name);
                    else
                        builder.addRejectName(name);

                }

                if (voteResult == VoteResult.PASS) {
                    // 游戏结束
                    this.cancelGame(game);
                } else if (voteResult == VoteResult.REJECT) {
                    RoleGameInfo applyerInfo = game.getRoleIdMap().get(voteBox.getApplyer());
                    applyerInfo.lastRejectedExitTime = TimeUtils.getNowTime();
                }

                this.sendAllSeatSC(game, SC.newBuilder().setSCFightApplyExitResult(builder).build());

                voteBox.reset();
            }
        }
    }

    /**
     * 取消游戏
     * 
     * @param game
     * @author wcy 2017年6月29日
     */
    private void cancelGame(Game game) {

        if (game.getGameState() != GameState.GAME_START_END) {
            synchronized (game) {
                if (game.getGameState() == GameState.GAME_START_END)
                    return;

                game.setGameState(GameState.GAME_START_END);

                this.roundOverHongZhong(game, false);
                this.gameOverHongZhong(game);
            }
        }

        this.notifyObservers(FightConstant.FIGHT_CANCEL_GAME, game);

    }

    @Override
    public void applyExitGame(Role role) {
        Game game = this.getGameById(role.getGameId());
        if (game == null) {
            SessionUtils.sc(role.getRoleId(),
                    SC.newBuilder().setFightApplyExitGameResponse(FightApplyExitGameResponse.newBuilder()).build());
            return;
        }

        String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());

        // 1.距离上次拒绝时间到现在的间隔时间内不能连续发起申请退出
        // 2.有人在申请退出时不能发布自己的申请退出
        int deltaTime = 30;
        int nowTime = TimeUtils.getNowTime();

        // 是否允许申请退出
        try {
            if (!isAllowApplyExit(nowTime, game, gameRoleId, deltaTime)) {
                SessionUtils.sc(
                        role.getRoleId(),
                        SC.newBuilder()
                                .setFightApplyExitGameResponse(
                                        FightApplyExitGameResponse.newBuilder().setErrorCode(
                                                ErrorCode.GAME_EXITING.getNumber())).build());
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SessionUtils.sc(role.getRoleId(),
                SC.newBuilder().setFightApplyExitGameResponse(FightApplyExitGameResponse.newBuilder()).build());

        VoteBox voteBox = game.getVoteBox();
        // 投票箱重置
        voteBox.reset();
        // 如果投票人是空的,则加入参与投票的人
        if (voteBox.getJoinVoteSet().size() == 0)
            voteBox.getJoinVoteSet().addAll(game.getRoleIdMap().keySet());

        // 设置申请退出的玩家id
        int voteId = voteBox.applyVote(gameRoleId);

        SC scApplyExit = SC
                .newBuilder()
                .setSCFightApplyExitGame(
                        SCFightApplyExitGame.newBuilder().setName(role.getName()).setApplyExitId(voteId)
                                .setCountDown(FightConstant.COUNTDOWN)).build();

        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            // 申请人跳过
            if (info.roleId == role.getRoleId())
                continue;

            SessionUtils.sc(info.roleId, scApplyExit);
            // 发送玩家申请退出的通知
            notifyObservers(FightConstant.FIGHT_APPLY_LEAVE, game, info, scApplyExit);
        }

        // 检查投票是否所有人都不在线
        if (this.checkVoteExitAllOffline(game, role.getRoleId())) {
            cancelGame(game);
        }
    }

    /**
     * 检查退出所有人都不在线
     * 
     * @return
     * @author wcy 2017年7月24日
     */
    private boolean checkVoteExitAllOffline(Game game, int applyRoleId) {
        int agreeExitCount = 0;
        for (RoleGameInfo roleInfo : game.getRoleIdMap().values()) {
            // 如果是机器人
            if (roleInfo.roleId == 0) {
                agreeExitCount++;
                continue;
            }
            // 不是申请人
            if (roleInfo.roleId != applyRoleId) {
                IoSession session = SessionCache.getSessionById(roleInfo.roleId);
                String gameRoleId = matchService.getGameRoleId(game.getGameId(), roleInfo.roleId);
                VoteBox voteBox = game.getVoteBox();
                // 如果这个人没有连接并且还没有投票,则算是掉线
                Boolean result = voteBox.getVoteMap().get(gameRoleId);
                if (!session.isConnected() && result == null) {
                    agreeExitCount++;
                }
            }
        }

        return agreeExitCount >= game.getRoleIdMap().size() - 1;
    }

    private boolean isAllowApplyExit(int nowTime, Game game, String applyExitRoleGameId, int deltaTime) {
        VoteBox voteBox = game.getVoteBox();
        RoleGameInfo roleGameInfo = game.getRoleIdMap().get(applyExitRoleGameId);
        int lastRejectExitTime = roleGameInfo.lastRejectedExitTime;
        Role role = loginService.getRoleById(roleGameInfo.roleId);
        // 有人在申请退出时，不能让另一个人申请退出
        // 现在的时间与上次被拒绝的时间差不能小于规定间隔绿帽

        if (game.getGameType() == GameType.GAME_TYPE_FRIEND) {
            return true;
        }
        loggerinfo(role, "applyExitGameRoleId->" + voteBox.getApplyer());
        if (StringUtils.isNullOrEmpty(voteBox.getApplyer())) {
            loggerinfo(role, "nowTime - lastRejectExitTime <= deltaTime" + nowTime + " " + lastRejectExitTime + " "
                    + deltaTime);
            if (nowTime - lastRejectExitTime <= deltaTime) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }

    // @Override
    // public void touchCard(Game game) {
    // int seat = game.getCurrentRoleIdIndex();
    // RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, seat);
    //
    // List<Integer> remainCards = game.getRemainCards();
    // if (remainCards.size() > 0) {
    //
    // if (GlobleMap.Boolean(GlobleConstant.ARGS_ARTIFICIAL)) {
    // final Game finalGame = game;
    // final RoleGameInfo finalRoleGameInfo = roleGameInfo;
    // Thread t = new Thread(new Runnable() {
    // public void run() {
    // input_TouchCard(finalGame, finalRoleGameInfo);
    // checkMineCardList(finalGame);
    // };
    //
    // });
    // t.start();
    // } else {
    // // 如果客户端要求自己摸牌
    // if (GlobleMap.Boolean(GlobleConstant.ARGS_CLIENT_TOUCH_CARD)) {
    // int clientTouchCard = game.getClientTouchCard();
    //
    // int index = remainCards.indexOf(clientTouchCard);
    //
    // // 没有这张牌
    // if (index == -1) {
    // index = RandomUtils.getRandomNum(remainCards.size());
    // }
    // roleGameInfo.newCard = remainCards.remove(index);
    // } else {
    // roleGameInfo.newCard =
    // remainCards.remove(RandomUtils.getRandomNum(remainCards.size()));
    // }
    // checkMineCardList(game);
    // }
    // } else {// 牌出完了，则游戏结束
    // this.over(game, seat);
    // }
    //
    // }

    @Override
    public void touchCard(Game game) {
        int seat = game.getCurrentRoleIdIndex();
        RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, seat);

        List<Integer> remainCards = game.getRemainCards();

        if (GlobleMap.Boolean(GlobleConstant.ARGS_ARTIFICIAL)) {
            // final Game finalGame = game;
            // final RoleGameInfo finalRoleGameInfo = roleGameInfo;
            // Thread t = new Thread(new Runnable() {
            // public void run() {
            // input_TouchCard(finalGame, finalRoleGameInfo);
            // noticeTouchCard(finalGame);
            // };
            //
            // });
            // t.start();

            input_TouchCard(game, roleGameInfo);
            // noticeTouchCard(game);
        } else {
            // 如果客户端要求自己摸牌
            // if (GlobleMap.Boolean(GlobleConstant.ARGS_CLIENT_TOUCH_CARD)) {
            if (game.envVars.Boolean(GlobleConstant.ARGS_CLIENT_TOUCH_CARD)) {
                int clientTouchCard = game.getClientTouchCard();

                int index = remainCards.indexOf(clientTouchCard);

                // 没有这张牌
                if (index == -1) {
                    index = RandomUtils.getRandomNum(remainCards.size());
                }
                roleGameInfo.newCard = remainCards.remove(index);
            } else {
                roleGameInfo.newCard = remainCards.remove(0);
            }

            // this.noticeTouchCard(game);
        }

        // 每个人都存一下
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            info.everybodyTouchCard = roleGameInfo.newCard;
        }

    }

    /**
     * 通知摸到的牌
     * 
     * @param game
     * @author wcy 2017年8月22日
     */
    private void noticeTouchCard(Game game) {
        int seat = game.getCurrentRoleIdIndex();

        // boolean isFlower = game.getRule().isFlower(game,
        // roleGameInfo.newCard);
        // for (RoleGameInfo info : game.getRoleIdMap().values()) {
        // if (info.gameRoleId.equals(roleGameInfo.gameRoleId) || isFlower) {
        //
        // } else {
        // info.everybodyTouchCard = 0;
        // }
        // }

        // 通知该玩家摸到的是什么牌
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            int touchCard = info.everybodyTouchCard;

            SCFightTouchCard scFightTouchCard = SCFightTouchCard.newBuilder().setSeat(seat)
                    .setIsFlower(game.touchCardIsFlower).setRemainCardCount(game.getRemainCards().size())
                    .setTouchCard(touchCard).build();
            SC sc = SC.newBuilder().setSCFightTouchCard(scFightTouchCard).build();

            SessionUtils.sc(info.roleId, sc);

            notifyObservers(FightConstant.FIGHT_TOUCH_CARD, sc, game, info);
        }

    }

    /**
     * 检查我的杠碰胡
     * 
     * @param game
     * @author wcy 2017年8月22日
     */
    private void checkMineCardList(Game game) {
        int seat = game.getCurrentRoleIdIndex();
        RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, seat);

        // 清空临时卡牌
        game.getCallCardLists().clear();
        game.getHuCallCardLists().clear();

        List<Class<? extends CardList>> list = game.getRule().getMineCardListSequence(roleGameInfo, game);
        // 检查杠胡卡牌
        this.checkMineCallCardList(game, game.getCurrentRoleIdIndex(), roleGameInfo.newCard, list);

        this.noticeCountDown(game, 10);

        // TODO
        // // 检查有没有可以杠胡的牌
        // if (game.getCallCardLists().size() > 0) {
        // this.sendGangPengHuMsg2Role(game);
        // } else {
        // // 通知出牌
        // this.noticeSendCard(game);
        // }
        // o
        //
        // processor.nextProcess(game);

    }

    /**
     * 外检
     * 
     * @param game
     * @author wcy 2017年8月25日
     */
    private void checkMineCardListOuter(Game game) {
        if (game.sendCard == 0) {
            return;
        }
        RoleGameInfo roleGameInfo = roleGameInfoGetter.getCurrentRoleGameInfo(game);
        int seat = game.getCurrentRoleIdIndex();
        int sendCard = game.sendCard;
        MajiangRule rule = game.getRule();
        List<Class<? extends CardList>> list = rule.getOtherCardListSequence(roleGameInfo, game);
        this.checkOtherCallCardList(game, seat, sendCard, list);
    }

    private void input_TouchCard(Game game, RoleGameInfo roleGameInfo) {
        List<Integer> remainCards = game.getRemainCards();
        boolean success = false;
        loggerinfo(game.toString());
        loggerinfo("gameId=>" + game.getGameId() + "=>gameRoleId:" + roleGameInfo.gameRoleId
                + " please server touch a card to " + roleGameInfo.gameRoleId + ":1<int remainCard>");
        while (!success) {
            try {
                String command = in.nextLine();
                String[] args = command.split(" ");
                int card = Integer.parseInt(args[0]);

                GET_SUCCESS: {
                    for (int i = remainCards.size() - 1; i >= 0; i--) {
                        if (remainCards.get(i) == card) {
                            success = true;
                            remainCards.remove(i);
                            break GET_SUCCESS;
                        }
                    }
                    success = false;
                }
                if (success) {
                    roleGameInfo.newCard = card;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void clientTouchCard(Role role, int card) {
        Game game = this.getGameById(role.getGameId());
        if (game == null) {
            FightClientTouchCardResponse response = FightClientTouchCardResponse.newBuilder()
                    .setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()).build();
            SC sc = SC.newBuilder().setFightClientTouchCardResponse(response).build();
            SessionUtils.sc(role.getRoleId(), sc);

            return;
        }

        // 如果卡牌是0,就不使用摸牌
        if (card == 0) {
            game.setClientTouchCard(card);
            FightClientTouchCardResponse response = FightClientTouchCardResponse.newBuilder().build();
            SC sc = SC.newBuilder().setFightClientTouchCardResponse(response).build();
            SessionUtils.sc(role.getRoleId(), sc);
            return;
        }
        List<Integer> remainCards = game.getRemainCards();
        int index = remainCards.indexOf(card);
        // 卡牌不存在
        if (index == -1) {
            FightClientTouchCardResponse response = FightClientTouchCardResponse.newBuilder()
                    .setErrorCode(ErrorCode.GAME_CARD_NOT_EXIST.getNumber()).build();
            SC sc = SC.newBuilder().setFightClientTouchCardResponse(response).build();
            SessionUtils.sc(role.getRoleId(), sc);
            return;
        }

        // 设置客户端摸牌
        game.setClientTouchCard(card);

        FightClientTouchCardResponse response = FightClientTouchCardResponse.newBuilder().build();
        SC sc = SC.newBuilder().setFightClientTouchCardResponse(response).build();
        SessionUtils.sc(role.getRoleId(), sc);
    }

    /**
     * 通知出牌
     * 
     * @param gameId
     * @author wcy 2017年6月16日
     */
    private void noticeSendCard(Game game) {
        RoleGameInfo roleGameInfo = this.getCurrentRoleGameInfo(game);
        int index = game.getRoleIdList().indexOf(roleGameInfo.gameRoleId);

        SC noticeSendCard = SC.newBuilder().setSCFightNoticeSendCard(SCFightNoticeSendCard.newBuilder().setSeat(index))
                .build();
        this.sendAllSeatSC(game, noticeSendCard);
        this.notifyObservers(FightConstant.FIGHT_NOTICE_SEND_CARD, noticeSendCard, game, index);

    }

    private void ifAIAutoSendCard(int gameId, int seat) {
        Game game = this.getGameById(gameId);
        RoleGameInfo nextRoleGameInfo = this.getCurrentRoleGameInfo(game);
        if (nextRoleGameInfo.roleId != 0) {
            return;
        }
        try {
            if (GlobleMap.Boolean(GlobleConstant.ARGS_ARTIFICIAL)) {
                final Game finalGame = game;
                final RoleGameInfo finalNextRoleGameInfo = nextRoleGameInfo;
                Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        input_SendCard(finalGame, finalNextRoleGameInfo);

                    }
                });
                t.start();
                // input_SendCard(game, nextRoleGameInfo);
            } else {
                AISendCardTimeEvent evt = new AISendCardTimeEvent() {

                    @Override
                    public void update(TimeEvent timeEvent) {
                        Game game = getGameById(gameId);
                        if (game == null)
                            return;
                        RoleGameInfo roleGameInfo = getCurrentRoleGameInfo(game);

                        int cardIndex = RandomUtils.getRandomNum(roleGameInfo.cards.size());
                        gameRoleIdSendCard(roleGameInfo.cards.get(cardIndex), game, roleGameInfo.gameRoleId, false);

                        for (RoleGameInfo info : game.getRoleIdMap().values()) {
                            loggerinfo("gameId=>" + game.getGameId() + "=>" + info);
                        }
                    }
                };
                evt.setEndTime(TimeUtils.getNowTime() + 1);
                evt.setGameId(game.getGameId());
                eventScheduler.addEvent(evt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void input_SendCard(Game game, RoleGameInfo nextRoleGameInfo) {
        boolean success = false;
        loggerinfo(game.toString());
        loggerinfo("gameRoleId:" + nextRoleGameInfo.gameRoleId
                + " please send a card:1<int card> 2<bool isSendTouchCard>");
        while (!success) {
            try {
                String command = in.nextLine();
                String[] args = command.split(" ");
                String cardStr = args[0];
                int card = Integer.parseInt(cardStr);
                String isSendTouchCardStr = args[1];
                boolean isSendTouchCard = Boolean.parseBoolean(isSendTouchCardStr);
                success = true;
                gameRoleIdSendCard(card, game, nextRoleGameInfo.gameRoleId, isSendTouchCard);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendCard(Role role, int card, boolean isTouchCard, List<Integer> tingCards) {
        int gameId = role.getGameId();
        Game game = this.getGameById(gameId);

        if (game == null) {
            SessionUtils.sc(
                    role.getRoleId(),
                    SC.newBuilder()
                            .setFightSendCardResponse(
                                    FightSendCardResponse.newBuilder().setErrorCode(
                                            ErrorCode.GAME_NOT_EXIST.getNumber())).build());
            return;
        }
        if (!game.canTing) {
            SessionUtils.sc(
                    role.getRoleId(),
                    SC.newBuilder()
                            .setFightSendCardResponse(
                                    FightSendCardResponse.newBuilder().setErrorCode(
                                            ErrorCode.GAME_TING_FAIL.getNumber())).build());
            game.canTing = true;
            return;
        }

        String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
        RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);

        if (roleGameInfo.roleId != role.getRoleId()) {
            SessionUtils.sc(
                    role.getRoleId(),
                    SC.newBuilder()
                            .setFightSendCardResponse(
                                    FightSendCardResponse.newBuilder()
                                            .setErrorCode(ErrorCode.NOT_YOUR_TURN.getNumber())).build());
            return;
        }

        if (!roleGameInfo.cards.contains(card)) {
            if (roleGameInfo.newCard != card) {
                SessionUtils.sc(
                        role.getRoleId(),
                        SC.newBuilder()
                                .setFightSendCardResponse(
                                        FightSendCardResponse.newBuilder().setErrorCode(
                                                ErrorCode.FIGHT_MORE_CARD.getNumber())).build());
                return;
            }
        }
        roleGameInfo.tingCards = tingCards;
        // 发送卡牌
        loggerinfo(role, SC.newBuilder().setFightSendCardResponse(FightSendCardResponse.newBuilder()).build());
        SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightSendCardResponse(FightSendCardResponse.newBuilder())
                .build());

        // 自动出牌解除
        roleGameInfo.auto = 0;

        // 该玩家出牌
        this.gameRoleIdSendCard(card, game, gameRoleId, isTouchCard);

    }

    private void sendGangPengHuMsg2Role(Game game) {
        Map<Integer, SCFightNoticeChooseCardList.Builder> map = new HashMap<>();

        List<CallCardList> callCardLists = game.getCallCardLists();
        for (CallCardList callCardList : callCardLists) {
            SCFightNoticeChooseCardList.Builder builder = map.get(callCardList.masterSeat);
            if (builder == null) {
                builder = SCFightNoticeChooseCardList.newBuilder();
                map.put(callCardList.masterSeat, builder);
            }

            CardList cardList = callCardList.cardList;

            Class<? extends CardList> clazz = getCardListPrototype(cardList);

            Function parseCardListToProtoFunction = GameCache.getParseCardListToProtoFunctionMap().get(clazz);
            Function addProtoFunction = GameCache.getNoticeChooseCardListFunctionMap().get(clazz);

            Object cardListProtoData = parseCardListToProtoFunction.apply(callCardList.cardList);
            addProtoFunction.apply(builder, callCardList.cardListId, cardListProtoData);
        }

        // 发送给对应的人
        for (Map.Entry<Integer, SCFightNoticeChooseCardList.Builder> entrySet : map.entrySet()) {
            int sendSeat = entrySet.getKey();
            SCFightNoticeChooseCardList.Builder builder = entrySet.getValue();

            int roleId = this.getRoleGameInfoBySeat(game, sendSeat).roleId;
            SCFightNoticeChooseCardList scFightNoticeChooseCardList = builder.setTempGameCount(game.getSendCardCount())
                    .build();

            SC sc = SC.newBuilder().setSCFightNoticeChooseCardList(scFightNoticeChooseCardList).build(); // PengGangHu
                                                                                                         // SC
            SessionUtils.sc(roleId, sc);

            if (game.getGameState() != GameState.GAME_START_START)
                break;
            this.notifyObservers(FightConstant.FIGHT_GANG_PENG_HU, game, sendSeat, sc,
                    getRoleGameInfoBySeat(game, sendSeat));
        }

    }

    /**
     * 取消选择杠碰吃胡
     * 
     * @param roleGameInfo
     * @author wcy 2017年8月2日
     */
    private void sendChooseCardListOver(Game game, RoleGameInfo roleGameInfo) {
        SC sc = SC.newBuilder().setSCFightChooseCardListOver(SCFightChooseCardListOver.newBuilder()).build();
        SessionUtils.sc(roleGameInfo.roleId, sc);
        this.notifyObservers(FightConstant.FIGHT_GANG_PENG_HU_OVER, sc, game, roleGameInfo);
    }

    /**
     * 通知所有人都要消除杠碰吃胡
     * 
     * @param game
     * @author wcy 2017年8月2日
     */
    private void sendAllChooseCardListOver(Game game) {
        Map<String, RoleGameInfo> roleGameInfoMap = game.getRoleIdMap();
        for (RoleGameInfo roleGameInfo : roleGameInfoMap.values()) {
            this.sendChooseCardListOver(game, roleGameInfo);
        }
    }

    private void ifAIAutoGangPengHu(int gameId, int seat, SCFightNoticeChooseCardList scFightNoticeChooseCardList) {
        Game game = this.getGameById(gameId);
        RoleGameInfo tempRoleGameInfo = this.getRoleGameInfoBySeat(game, seat);
        int gameSendCount = scFightNoticeChooseCardList.getTempGameCount();

        if (tempRoleGameInfo.roleId != 0) {
            return;
        }
        if (GlobleMap.Boolean(GlobleConstant.ARGS_ARTIFICIAL)) {
            final int finalSeat = seat;
            final Game finalGame = game;
            final RoleGameInfo finalRoleGameInfo = tempRoleGameInfo;
            final int finalGameSendCount = gameSendCount;

            input_SendHuGangPengGuo(finalSeat, finalGame, finalRoleGameInfo, finalGameSendCount);
        } else {
            // 机器人处理杠碰胡
            AIChooseCallCardListTimeEvent chooseTimeEvent = new AIChooseCallCardListTimeEvent() {

                @Override
                public void update(TimeEvent timeEvent) {
                    SCFightNoticeChooseCardList sc = (SCFightNoticeChooseCardList) message;
                    Game game = getGameById(gameId);
                    guo(game, AISeat, sc.getTempGameCount());
                }
            };
            chooseTimeEvent.setEndTime(TimeUtils.getNowTime() + 1);
            chooseTimeEvent.setMessage(scFightNoticeChooseCardList);
            chooseTimeEvent.setGameId(game.getGameId());
            chooseTimeEvent.setAISeat(seat);
            eventScheduler.addEvent(chooseTimeEvent);
        }
    }

    private void input_SendHuGangPengGuo(int seat, Game game, RoleGameInfo tempRoleGameInfo, int gameSendCount) {
        boolean success = false;
        loggerinfo(game.toString());
        loggerinfo("gameRoleId:" + tempRoleGameInfo.gameRoleId
                + " please choose gang peng guo:1<int callCardListId> 2<string hu,gang,peng,guo>");
        while (!success) {
            try {
                String command = in.nextLine();
                String[] args = command.split(" ");
                int callCardListId = Integer.parseInt(args[0]);
                String choose = args[1];
                switch (choose) {
                case "hu": {
                    success = true;
                    this.hu(game, seat, gameSendCount, callCardListId);
                    break;
                }
                case "gang": {
                    success = true;
                    this.gang(game, seat, gameSendCount, callCardListId);
                    break;
                }
                case "peng": {
                    success = true;
                    this.peng(game, seat, gameSendCount, callCardListId);
                    break;
                }
                case "guo":
                    success = true;
                    this.guo(game, seat, gameSendCount);
                    break;
                }
                loggerinfo("callCardListId=>" + callCardListId + " chooes=>" + choose);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 移除我方的所有选择
     * 
     * @param callCardLists
     * @param seatIndex
     * @author wcy 2017年6月17日
     */
    private void deleteAllCallCardListBySeat(List<CallCardList> callCardLists, int seatIndex) {
        this.deleteCallCardListBySeatBesidesCallCardListId(callCardLists, seatIndex, 0, true);
    }

    /**
     * 移除自己除了选定的牌型id之外的所有牌，并获取 callCardListId的卡组
     * 
     * @param callCardLists
     * @param seatIndex
     * @param callCardListId
     * @return
     */
    private CallCardList deleteCallCardListBySeatBesidesCallCardListId(List<CallCardList> callCardLists, int seatIndex,
            int callCardListId) {
        return this.deleteCallCardListBySeatBesidesCallCardListId(callCardLists, seatIndex, callCardListId, false);
    }

    /**
     * 移除自己除了选定的牌型id之外的所有牌
     * 
     * @param callCardLists
     * @param seatIndex
     * @param callCardListId
     * @param allDelete 如果为true，则全部删除
     * @author wcy 2017年6月17日
     */
    private CallCardList deleteCallCardListBySeatBesidesCallCardListId(List<CallCardList> callCardLists, int seatIndex,
            int callCardListId, boolean allDelete) {
        CallCardList targetCallCardList = null;
        for (int i = callCardLists.size() - 1; i >= 0; i--) {
            CallCardList callCardList = callCardLists.get(i);
            if (callCardList.masterSeat == seatIndex) {
                if (callCardListId != callCardList.cardListId || allDelete) {
                    callCardLists.remove(i);
                } else {
                    targetCallCardList = callCardList;
                }
            }
        }
        return targetCallCardList;
    }

    @Override
    public void chi(Role role, int gameSendCount, int callCardListId) {
        int gameId = role.getGameId();
        Game game = this.getGameById(gameId);

        String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
        int seatIndex = game.getRoleIdList().indexOf(gameRoleId);

        this.chi(game, seatIndex, gameSendCount, callCardListId);
    }

    /**
     * @param game
     * @param seatIndex
     * @param gameSendCount
     * @param callCardListId
     */
    private void chi(Game game, int seat, int gameSendCount, int callCardListId) {
        RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, seat);
        // 杠标记取消
        roleGameInfo.isGang = false;

        // 出牌数必须相同
        if (game.getSendCardCount() != gameSendCount) {
            SessionUtils.sc(
                    roleGameInfo.roleId,
                    SC.newBuilder()
                            .setFightChiResponse(
                                    FightChiResponse.newBuilder().setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()))
                            .build());
            return;
        }
        synchronized (game.getCallCardLists()) {
            if (game.getSendCardCount() != gameSendCount) {
                SessionUtils.sc(
                        roleGameInfo.roleId,
                        SC.newBuilder()
                                .setFightChiResponse(
                                        FightChiResponse.newBuilder().setErrorCode(
                                                ErrorCode.FIGHT_TIME_PASS.getNumber())).build());
                return;
            }
            SessionUtils.sc(roleGameInfo.roleId, SC.newBuilder().setFightChiResponse(FightChiResponse.newBuilder())
                    .build());

            CallCardList callCardList = this.deleteCallCardListBySeatBesidesCallCardListId(game.getCallCardLists(),
                    seat, callCardListId);

            // 标记为已经叫过了
            callCardList.call = true;

            // 取消选择杠碰吃胡
            this.sendChooseCardListOver(game, roleGameInfo);

            if (waitOtherCallCardListChecker.needWaitOtherChoice(game.getCallCardLists(), seat)) {
                return;
            }
            // 所有人取消
            this.sendAllChooseCardListOver(game);
            resetSendCard(game);
        }

        processor.pop(game);
        processor.push(game, MajiangStateEnum.STATE_ROLE_CHOSEN_CARDLIST);

        List<CallCardList> callCardLists = game.getCallCardLists();
        processor.process(game, callCardLists.size() > 0 ? callCardLists.get(0).masterSeat : 0);
    }

    private void chiProgress(Game game) {
        // TODO 等待他人选项需要改动
        // 获得第一个人的卡组
        CallCardList callCardList = game.getCallCardLists().get(0);
        int seat = callCardList.masterSeat;
        RoleGameInfo roleGameInfo = roleGameInfoGetter.getRoleGameInfoBySeat(game, seat);

        Chi chi = (Chi) callCardList.cardList;
        // 牌归自己
        roleGameInfo.cards.add(chi.targetCard);
        // 移除手牌
        Lists.removeElementByList(roleGameInfo.cards, chi.getCards());
        // 显示到我方已碰的桌面上
        roleGameInfo.showCardLists.add(chi);

        CardListData chiData = this.parseChi(chi);

        SC sc = SC.newBuilder().setSCFightCardList(SCFightCardList.newBuilder().setCardListData(chiData).setSeat(seat))
                .build();

        // 通知其他玩家自己吃
        this.sendAllSeatSC(game, sc);
        this.notifyObservers(FightConstant.FIGHT_CHI, sc, game);
    }

    @Override
    public void peng(Role role, int gameSendCount, int cardListId) {

        int gameId = role.getGameId();
        Game game = this.getGameById(gameId);

        String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
        int seatIndex = game.getRoleIdList().indexOf(gameRoleId);

        this.peng(game, seatIndex, gameSendCount, cardListId);
    }

    /**
     * 碰
     * 
     * @param seat
     * @param gameSendCount
     * @author wcy 2017年6月17日
     */
    private void peng(Game game, int seat, int gameSendCount, int callCardListId) {
        RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, seat);
        // 杠标记取消
        roleGameInfo.isGang = false;

        // 出牌数必须相同
        if (game.getSendCardCount() != gameSendCount) {
            FightPengResponse response = FightPengResponse.newBuilder()
                    .setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()).build();
            SC responseSC = SC.newBuilder().setFightPengResponse(response).build();
            SessionUtils.sc(roleGameInfo.roleId, responseSC);
            return;
        }
        synchronized (game.getCallCardLists()) {
            if (game.getSendCardCount() != gameSendCount) {
                FightPengResponse response = FightPengResponse.newBuilder()
                        .setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()).build();
                SC responseSC = SC.newBuilder().setFightPengResponse(response).build();
                SessionUtils.sc(roleGameInfo.roleId, responseSC);
                return;
            }

            SessionUtils.sc(roleGameInfo.roleId, SC.newBuilder().setFightPengResponse(FightPengResponse.newBuilder())
                    .build());

            CallCardList callCardList = this.deleteCallCardListBySeatBesidesCallCardListId(game.getCallCardLists(),
                    seat, callCardListId);
            this.deleteAllCallCardListBySeat(game.getHuCallCardLists(), seat);

            // 标记为已经叫过了
            callCardList.call = true;

            // 取消选择杠碰吃胡
            this.sendChooseCardListOver(game, roleGameInfo);

            if (waitOtherCallCardListChecker.needWaitOtherChoice(game.getCallCardLists(), seat)) {
                return;
            }

            // 所有人取消
            this.sendAllChooseCardListOver(game);

            this.resetSendCard(game);
        }

        processor.pop(game);
        processor.push(game, MajiangStateEnum.STATE_ROLE_CHOSEN_CARDLIST);

        List<CallCardList> callCardLists = game.getCallCardLists();
        processor.process(game, callCardLists.size() > 0 ? callCardLists.get(0).masterSeat : 0);

        // this.resetSendCard(game);
        //
        // // 检查是否可以胡,别人可以胡就不算杠
        // CallCardList prevCallCardList =
        // this.getPreviousCallCardList(game.getCallCardLists());
        // if (prevCallCardList.cardList instanceof Hu) {
        // this.huProgress(game, seat, roleGameInfo, prevCallCardList);
        // return;
        // }
        //
        // // 牌归自己
        // Peng peng = (Peng) callCardList.cardList;
        //
        // roleGameInfo.cards.add(peng.card);
        //
        // // 移除手牌
        // Lists.removeElementByList(roleGameInfo.cards, peng.getCards());
        //
        // // 显示到我方已碰的桌面上
        // roleGameInfo.showCardLists.add(peng);
        //
        // CardListData pengData = this.parsePeng(peng);
        //
        // SC sc = SC.newBuilder()
        // .setSCFightCardList(SCFightCardList.newBuilder().setCardListData(pengData).setSeat(seat)).build();
        // // 通知其他玩家自己碰
        // this.sendAllSeatSC(game, sc);
        // // 发送通知
        // this.notifyObservers(FightConstant.FIGHT_PENG, sc, game);
        //
        // // // 跳转到当前碰的人
        // // this.jumpToIndex(game, seat);
        // // // 倒计时
        // // this.noticeCountDown(game, 10);
        // // 通知转向
        // this.noticePointSeat(game, seat);
        // processor.pop(game);
        // processor.push(game, MajiangStateEnum.STATE_ROLE_PENG);
        //
        // processor.process(game, seat);
        //
        // // 通知出牌
        // // this.noticeSendCard(game);

        // }

    }

    /**
     * 碰的流程
     * 
     * @param game
     * @param seat
     */
    private void pengProgress(Game game) {
        // 获得第一个人的卡组
        CallCardList callCardList = game.getCallCardLists().get(0);
        int seat = callCardList.masterSeat;
        RoleGameInfo roleGameInfo = roleGameInfoGetter.getRoleGameInfoBySeat(game, seat);
        // 牌归自己
        Peng peng = (Peng) callCardList.cardList;

        roleGameInfo.cards.add(peng.card);

        // 移除手牌
        Lists.removeElementByList(roleGameInfo.cards, peng.getCards());

        // 显示到我方已碰的桌面上
        roleGameInfo.showCardLists.add(peng);

        CardListData pengData = this.parsePeng(peng);

        SC sc = SC.newBuilder()
                .setSCFightCardList(SCFightCardList.newBuilder().setCardListData(pengData).setSeat(seat)).build();

        // 通知其他玩家自己碰
        this.sendAllSeatSC(game, sc);
        // 发送通知
        this.notifyObservers(FightConstant.FIGHT_PENG, sc, game);

        // // 跳转到当前碰的人
        // this.jumpToIndex(game, seat);
        // // 倒计时
        // this.noticeCountDown(game, 10);
        // 通知转向
        // this.noticePointSeat(game, seat);
    }

    @Override
    public void gang(Role role, int gameSendCount, int callCardListId) {
        int gameId = role.getGameId();
        Game game = this.getGameById(gameId);

        String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
        int seatIndex = game.getRoleIdList().indexOf(gameRoleId);

        this.gang(game, seatIndex, gameSendCount, callCardListId);
    }

    private void gang(Game game, int seat, int gameSendCount, int callCardListId) {
        RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, seat);
        // 出牌数必须相同
        if (game.getSendCardCount() != gameSendCount) {
            FightGangResponse.Builder responseBuilder = FightGangResponse.newBuilder();
            FightGangResponse response = responseBuilder.setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()).build();
            SC responseSC = SC.newBuilder().setFightGangResponse(response).build();
            SessionUtils.sc(roleGameInfo.roleId, responseSC);
            return;
        }
        synchronized (game.getCallCardLists()) {
            if (game.getSendCardCount() != gameSendCount) {
                FightGangResponse.Builder responseBuilder = FightGangResponse.newBuilder();
                FightGangResponse response = responseBuilder.setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber())
                        .build();
                SC responseSC = SC.newBuilder().setFightGangResponse(response).build();
                SessionUtils.sc(roleGameInfo.roleId, responseSC);
                return;
            }

            SessionUtils.sc(roleGameInfo.roleId, SC.newBuilder().setFightGangResponse(FightGangResponse.newBuilder())
                    .build());

            CallCardList callCardList = this.deleteCallCardListBySeatBesidesCallCardListId(game.getCallCardLists(),
                    seat, callCardListId);
            this.deleteAllCallCardListBySeat(game.getHuCallCardLists(), seat);

            // 标记为已经叫过了
            callCardList.call = true;

            this.sendChooseCardListOver(game, roleGameInfo);

            if (waitOtherCallCardListChecker.needWaitOtherChoice(game.getCallCardLists(), seat)) {
                return;
            }

            this.sendAllChooseCardListOver(game);
            Gang gang = (Gang) callCardList.cardList;
            // 暗杠不重置
            if (!gang.dark) {
                this.resetSendCard(game);
            }
        }

        processor.pop(game);
        processor.push(game, MajiangStateEnum.STATE_ROLE_CHOSEN_CARDLIST);

        List<CallCardList> callCardLists = game.getCallCardLists();
        processor.process(game, callCardLists.size() > 0 ? callCardLists.get(0).masterSeat : 0);
    }

    /**
     * 杠
     * 
     * @param game
     * @author wcy 2017年8月28日
     */
    private void gangProcess(Game game) {

        this.accumlateSendCardCount(game);
        // 牌归自己

        CallCardList callCardList = game.getCallCardLists().get(0);

        int seat = callCardList.masterSeat;
        RoleGameInfo roleGameInfo = roleGameInfoGetter.getRoleGameInfoBySeat(game, seat);
        Gang gang = (Gang) callCardList.cardList;

        if (gang.peng != null) {
            this.addGangSuccess(game, roleGameInfo, gang);
        } else {
            // 明杠或暗杠
            if (!gang.dark) {
                // 明杠
                roleGameInfo.cards.add(gang.card);
            } else {
                // 暗杠
                // 如果新摸得牌是用于暗杠,则新摸得牌赋值成空，否则新摸的牌加入手牌
                if (roleGameInfo.newCard == gang.card) {
                    roleGameInfo.cards.add(gang.card);
                    roleGameInfo.newCard = 0;
                } else {
                    this.newCardAdd2Cards(game, roleGameInfo);
                }
            }
            Lists.removeElementByList(roleGameInfo.cards, gang.getCards());
        }

        // ////////////////////////////////////
        roleGameInfo.showCardLists.add(gang);
        // 标记杠
        roleGameInfo.isGang = true;

        CardListData gangData = this.parseGang(gang);

        SC sc = SC.newBuilder()
                .setSCFightCardList(SCFightCardList.newBuilder().setCardListData(gangData).setSeat(seat)).build();
        // 通知其他玩家自己杠
        this.sendAllSeatSC(game, sc);

        this.notifyObservers(FightConstant.FIGHT_GANG, sc, game);
    }

    /**
     * 补杠成功
     * 
     * @param roleGameInfo
     * @param gang
     * @author wcy 2017年8月24日
     */
    @Override
    public void addGangSuccess(Game game, RoleGameInfo roleGameInfo, Gang gang) {
        // 补杠
        this.removeGangTargetCard(game, roleGameInfo, gang);
        gang.setTargetSeat(gang.peng.getTargetSeat());
        roleGameInfo.showCardLists.remove(gang.peng);
    }

    /**
     * 把杠恢复成
     * 
     * @param game
     * @author wcy 2017年8月31日
     */
    public void recoveryGang2Peng(Game game) {
        RoleGameInfo roleGameInfo = roleGameInfoGetter.getCurrentRoleGameInfo(game);
        List<CardList> cardLists = roleGameInfo.showCardLists;
        // 移除最后的补杠
        Gang gang = (Gang) cardLists.remove(cardLists.size() - 1);
        // 变回碰
        cardLists.add(gang.peng);
    }

    /**
     * 计算杠分
     * 
     * @param game
     * @author wcy 2017年8月28日
     */
    private void calHongzhongGangScore(Game game) {
        RoleGameInfo roleGameInfo = roleGameInfoGetter.getCurrentRoleGameInfo(game);
        List<CardList> cardLists = roleGameInfo.showCardLists;
        // 获取当前这个人的最后一个杠，必定就是刚刚的杠
        Gang gang = (Gang) cardLists.get(cardLists.size() - 1);
        // 获得杠的类型
        CardListType cardListType = gang.dark ? CardListType.CARD_LIST_TYPE_GANG_DARK : gang.peng == null ? CardListType.CARD_LIST_TYPE_GANG_LIGHT : CardListType.CARD_LIST_TYPE_GANG_ADD;

        // 明杠
        if (cardListType == CardListType.CARD_LIST_TYPE_GANG_LIGHT) {
            int targetSeat = gang.getTargetSeat();
            RoleGameInfo targetRoleGameInfo = this.getRoleGameInfoBySeat(game, targetSeat);

            this.calcMingGangScore(game, roleGameInfo, targetRoleGameInfo);
            // this.notifyObservers(FightConstant.FIGHT_GANG_MING, game,
            // roleGameInfo, targetRoleGameInfo);
        }
        // 补杠
        if (cardListType == CardListType.CARD_LIST_TYPE_GANG_ADD) {
            this.calcAddGangScore(game, roleGameInfo);
            // this.notifyObservers(FightConstant.FIGHT_GANG_ADD, game,
            // roleGameInfo);
        }
        // 暗杠
        if (cardListType == CardListType.CARD_LIST_TYPE_GANG_DARK) {
            this.calcDarkGangScore(game, roleGameInfo);
            // this.notifyObservers(FightConstant.FIGHT_GANG_DARK, game,
            // roleGameInfo);
        }

        // 发送分数变化
        SCFightScore.Builder scFightScoreBuilder = SCFightScore.newBuilder();
        for (int i = 0; i < game.getRoleIdList().size(); i++) {
            RoleGameInfo info = this.getRoleGameInfoBySeat(game, i);
            int score = info.roundOverResult.score;
            scFightScoreBuilder.addScoreData(ScoreData.newBuilder().setScore(score).setSeat(i));
        }
        SC scFightScoreSC = SC.newBuilder().setSCFightScore(scFightScoreBuilder).build();
        // 通知所有玩家
        this.sendAllSeatSC(game, scFightScoreSC);

        this.notifyObservers(FightConstant.FIGHT_SCORE, scFightScoreSC, game);
    }

    //
    // private void gang(Game game, int seat, int gameSendCount, int
    // callCardListId) {
    // RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, seat);
    // // 出牌数必须相同
    // if (game.getSendCardCount() != gameSendCount) {
    // FightGangResponse.Builder responseBuilder =
    // FightGangResponse.newBuilder();
    // FightGangResponse response =
    // responseBuilder.setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()).build();
    // SC responseSC = SC.newBuilder().setFightGangResponse(response).build();
    // SessionUtils.sc(roleGameInfo.roleId, responseSC);
    // return;
    // }
    // synchronized (game.getCallCardLists()) {
    // if (game.getSendCardCount() != gameSendCount) {
    // FightGangResponse.Builder responseBuilder =
    // FightGangResponse.newBuilder();
    // FightGangResponse response =
    // responseBuilder.setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber())
    // .build();
    // SC responseSC = SC.newBuilder().setFightGangResponse(response).build();
    // SessionUtils.sc(roleGameInfo.roleId, responseSC);
    // return;
    // }
    //
    // SessionUtils.sc(roleGameInfo.roleId,
    // SC.newBuilder().setFightGangResponse(FightGangResponse.newBuilder())
    // .build());
    //
    // CallCardList callCardList =
    // this.deleteCallCardListBySeatBesidesCallCardListId(game.getCallCardLists(),
    // seat, callCardListId);
    // this.deleteAllCallCardListBySeat(game.getHuCallCardLists(), seat);
    //
    // // 标记为已经叫过了
    // callCardList.call = true;
    //
    // this.sendChooseCardListOver(game, roleGameInfo);
    //
    // // if (needWaitOtherCallCardListAction(game.getCallCardLists(),
    // // seat)) {
    // // return;
    // // }
    //
    // if
    // (waitOtherCallCardListChecker.needWaitOtherChoice(game.getCallCardLists(),
    // seat)) {
    // return;
    // }
    //
    // this.sendAllChooseCardListOver(game);
    //
    // // 检查是否可以胡,别人可以胡就不算杠，这种情况是，自己可杠可胡，好死不死的选了个杠，结果就让别人胡了
    // CallCardList prevCallCardList =
    // this.getPreviousCallCardList(game.getCallCardLists());
    // if (prevCallCardList.cardList instanceof Hu) {
    // this.huProgress(game, seat, roleGameInfo, prevCallCardList);
    // return;
    // }
    //
    // // 牌归自己
    // Gang gang = (Gang) callCardList.cardList;
    // // 如果是补杠或者暗杠,就要把别人的牌清空
    // if (gang.dark || gang.peng != null) {
    // this.resetSendCard(game);
    // }
    // if (gang.peng != null) {
    // // 检查抢杠
    // if (this.checkQiangGang(game, seat, roleGameInfo, gang))
    // return;
    //
    // this.addGangSuccess(roleGameInfo, gang);
    // } else {
    // // 明杠或暗杠
    // if (!gang.dark) {
    // // 明杠
    // roleGameInfo.cards.add(gang.card);
    // } else {
    // // 暗杠
    // // 如果新摸得牌是用于暗杠,则新摸得牌赋值成空，否则新摸的牌加入手牌
    // if (roleGameInfo.newCard == gang.card) {
    // roleGameInfo.cards.add(gang.card);
    // roleGameInfo.newCard = 0;
    // } else {
    // this.newCardAdd2Cards(roleGameInfo);
    // }
    // }
    // Lists.removeElementByList(roleGameInfo.cards, gang.getCards());
    // }
    //
    // this.accumlateSendCardCount(game);
    // this.gangProcess2(game, seat, roleGameInfo, gang);
    // }
    // }

    //
    // @Override
    // public void gangProcess2(Game game, int seat, RoleGameInfo roleGameInfo,
    // Gang gang) {
    // roleGameInfo.showCardLists.add(gang);
    // // 标记杠
    // roleGameInfo.isGang = true;
    //
    // CardListData gangData = this.parseGang(gang);
    // CardListType cardListType = gangData.getCardListType();
    // // 明杠
    // if (cardListType == CardListType.CARD_LIST_TYPE_GANG_LIGHT) {
    // int targetSeat = gang.getTargetSeat();
    // RoleGameInfo targetRoleGameInfo = this.getRoleGameInfoBySeat(game,
    // targetSeat);
    //
    // this.notifyObservers(FightConstant.FIGHT_GANG_MING, game, roleGameInfo,
    // targetRoleGameInfo);
    // }
    // // 补杠
    // if (cardListType == CardListType.CARD_LIST_TYPE_GANG_ADD) {
    // this.notifyObservers(FightConstant.FIGHT_GANG_ADD, game, roleGameInfo);
    // }
    // // 暗杠
    // if (cardListType == CardListType.CARD_LIST_TYPE_GANG_DARK) {
    // this.notifyObservers(FightConstant.FIGHT_GANG_DARK, game, roleGameInfo);
    // }
    //
    // SC sc = SC.newBuilder()
    // .setSCFightCardList(SCFightCardList.newBuilder().setCardListData(gangData).setSeat(seat)).build();
    // // 通知其他玩家自己杠
    // this.sendAllSeatSC(game, sc);
    //
    // // 发送分数变化
    // SCFightScore.Builder scFightScoreBuilder = SCFightScore.newBuilder();
    // for (int i = 0; i < game.getRoleIdList().size(); i++) {
    // RoleGameInfo info = this.getRoleGameInfoBySeat(game, i);
    // int score = info.roundOverResult.score;
    // scFightScoreBuilder.addScoreData(ScoreData.newBuilder().setScore(score).setSeat(i));
    // }
    // SC scFightScoreSC =
    // SC.newBuilder().setSCFightScore(scFightScoreBuilder).build();
    // // 通知所有玩家
    // this.sendAllSeatSC(game, scFightScoreSC);
    //
    // this.notifyObservers(FightConstant.FIGHT_GANG, sc, game);
    // this.notifyObservers(FightConstant.FIGHT_SCORE, scFightScoreSC, game);
    //
    // // 跳转到当前杠的人
    // this.jumpToIndex(game, seat);
    // // 通知转向
    // this.noticePointSeat(game, seat);
    //
    // processor.pop(game);
    // processor.push(game, MajiangStateEnum.STATE_ROLE_CHOSEN_CARDLIST);
    //
    // List<CallCardList> callCardLists = game.getCallCardLists();
    // processor.process(game, callCardLists.size() > 0 ?
    // callCardLists.get(0).masterSeat : 0);
    // // 摸一张牌
    // // this.touchCard(game);
    //
    // }

    /**
     * 计算明牌分数
     * 
     * @param game
     * @param roleGameInfo
     * @param targetRoleGameInfo
     * @author wcy 2017年7月12日
     */
    private void calcMingGangScore(Game game, RoleGameInfo roleGameInfo, RoleGameInfo targetRoleGameInfo) {
        /** 猪卵泡突然发现别人的麻将杠分都是用游戏底分,觉得自己很不合群,所以让我改了 */
        // int gangScore = game.getGameConfig().getBaseScore();
        /** 猪卵泡突然又后悔了，要求改回来了，我改了 */
        int gangScore = game.getGameConfig().getGangScore();
        int score = gangScore * 3;

        roleGameInfo.roundOverResult.score += score;
        roleGameInfo.roundOverResult.mingGangScorePlus += score;
        roleGameInfo.roundOverResult.mingGangCountPlus++;

        targetRoleGameInfo.roundOverResult.score -= score;
        targetRoleGameInfo.roundOverResult.mingGangScoreMinus -= score;
        targetRoleGameInfo.roundOverResult.mingGangCountMinus++;

    }

    /**
     * 计算补杠分数
     * 
     * @param game
     * @param roleGameInfo
     * @author wcy 2017年7月12日
     */
    private void calcAddGangScore(Game game, RoleGameInfo roleGameInfo) {
        /** 猪卵泡突然发现别人的麻将杠分都是用游戏底分,觉得自己很不合群,所以让我改了 */
        // int gangScore = game.getGameConfig().getBaseScore();
        /** 猪卵泡突然又后悔了，要求改回来了，我改了 */
        int gangScore = game.getGameConfig().getGangScore();
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            if (info.gameRoleId.equals(roleGameInfo.gameRoleId)) {
                int addGangScore = gangScore * 3;
                info.roundOverResult.score += addGangScore;
                info.roundOverResult.addGangScorePlus += addGangScore;
                info.roundOverResult.addGangCountPlus++;
            } else {
                info.roundOverResult.score -= gangScore;
                info.roundOverResult.addGangScoreMinus -= gangScore;
                info.roundOverResult.addGangCountMinus++;
            }
        }
    }

    /**
     * 计算暗杠分数
     * 
     * @param game
     * @param roleGameInfo
     * @author wcy 2017年7月12日
     */
    private void calcDarkGangScore(Game game, RoleGameInfo roleGameInfo) {
        /** 猪卵泡突然发现别人的麻将杠分都是用游戏底分,觉得自己很不合群,所以让我改了 */
        // int gangScore = game.getGameConfig().getBaseScore();
        /** 猪卵泡突然又后悔了，要求改回来了，我改了 */
        int gangScore = game.getGameConfig().getGangScore();
        // TODO 从配置表中获取是否暗杠翻倍
        boolean darkGangDouble = true;
        gangScore = darkGangDouble ? gangScore * 2 : gangScore;

        int darkAddScore = gangScore * 3;
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            if (info.gameRoleId.equals(roleGameInfo.gameRoleId)) {
                info.roundOverResult.score += darkAddScore;
                info.roundOverResult.darkGangScorePlus += darkAddScore;
                info.roundOverResult.darkGangCountPlus++;
            } else {
                info.roundOverResult.score -= gangScore;
                info.roundOverResult.darkGangScoreMinus -= gangScore;
                info.roundOverResult.darkGangCountMinus++;
            }
        }
    }

    /**
     * 移除杠的
     * 
     * @param roleGameInfo
     * @param gang
     * @author wcy 2017年6月26日
     */
    private void removeGangTargetCard(Game game, RoleGameInfo roleGameInfo, Gang gang) {
        // 如果新摸得牌用于补杠，则新牌复制成空，否则要把新摸得牌放到手牌中
        if (roleGameInfo.newCard == gang.card)
            roleGameInfo.newCard = 0;
        else {
            this.newCardAdd2Cards(game, roleGameInfo);
            Lists.removeElementByList(roleGameInfo.cards, Arrays.asList(gang.card));
        }
    }

    @Override
    public boolean checkQiangGang(Game game) {
        CallCardList callCardList0 = game.getCallCardLists().get(0);
        int seat = callCardList0.masterSeat;
        Gang gang = (Gang) callCardList0.cardList;

        // 暂存抢杠
        game.qiangGangCallCardList = callCardList0;

        // 清空
        game.getCallCardLists().clear();
        game.getHuCallCardLists().clear();

        List<Class<? extends CardList>> huLists = new ArrayList<>(1);
        huLists.add(Hu.class);

        for (int i : game.checkOtherCardListSeats) {
            this.checkOtherCallCardList(game, i, gang.card, huLists);
        }
        List<CallCardList> callCardLists = game.getCallCardLists();
        if (callCardLists.size() != 0) {
            List<CallCardList> huCallCardList = game.getHuCallCardLists();
            for (CallCardList callCardList : huCallCardList) {
                Hu hu = (Hu) callCardList.cardList;
                hu.gangChong = true;
                hu.gangChongTargetSeat = seat;
            }
            return true;
        }

        // 如果没有抢杠就把数据加回来
        game.getCallCardLists().add(callCardList0);
        // 把抢杠暂存清空
        game.qiangGangCallCardList = null;

        return false;

    }

    // /**
    // * 检查抢杠(杠冲)
    // *
    // * @param game
    // * @param seat
    // * @param roleGameInfo
    // * @param gang
    // * @return
    // * @author wcy 2017年6月26日
    // */
    // private boolean checkQiangGang(Game game, int seat, RoleGameInfo
    // roleGameInfo, Gang gang) {
    // // 清空
    // game.getCallCardLists().clear();
    // game.getHuCallCardLists().clear();
    //
    // // 有红中不能抢杠
    // if (hasBaiDa(roleGameInfo, 0)) {
    // return false;
    // }
    //
    // for (int i = 0; i < game.getRoleIdList().size(); i++) {
    // if (seat == i)
    // continue;
    // this.checkOtherCallCardList(game, i, gang.card,
    // GameCache.getCheckGangCardList());
    // }
    // List<CallCardList> callCardLists = game.getCallCardLists();
    // if (callCardLists.size() != 0) {
    // roleGameInfo.qiangGang = gang;
    // List<CallCardList> huCallCardList = game.getHuCallCardLists();
    // for (CallCardList callCardList : huCallCardList) {
    // Hu hu = (Hu) callCardList.cardList;
    // hu.gangChong = true;
    // hu.gangChongTargetSeat = seat;
    // }
    // this.sendGangPengHuMsg2Role(game);
    // return true;
    // }
    // return false;
    // }

    @Override
    public void hu(Role role, int gameSendCount, int callCardListId) {
        int gameId = role.getGameId();
        Game game = GameCache.getGameMap().get(gameId);

        String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
        int seatIndex = game.getRoleIdList().indexOf(gameRoleId);

        this.hu(game, seatIndex, gameSendCount, callCardListId);
    }

    private void hu(Game game, int seat, int gameSendCount, int callCardListId) {
        RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, seat);
        // 出牌数必须相同
        if (game.getSendCardCount() != gameSendCount || game.getGameState() != GameState.GAME_START_START) {
            FightHuResponse response = FightHuResponse.newBuilder().setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber())
                    .build();
            SC responseSC = SC.newBuilder().setFightHuResponse(response).build();
            SessionUtils.sc(roleGameInfo.roleId, responseSC);
            return;
        }
        synchronized (game.getCallCardLists()) {
            if (game.getSendCardCount() != gameSendCount || game.getGameState() != GameState.GAME_START_START) {
                FightHuResponse response = FightHuResponse.newBuilder()
                        .setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()).build();
                SC responseSC = SC.newBuilder().setFightHuResponse(response).build();
                SessionUtils.sc(roleGameInfo.roleId, responseSC);
                return;
            }

            SessionUtils.sc(roleGameInfo.roleId, SC.newBuilder().setFightHuResponse(FightHuResponse.newBuilder())
                    .build());

            // 移除自己除了选定的牌型id之外的所有牌
            CallCardList callCardList = this.deleteCallCardListBySeatBesidesCallCardListId(game.getCallCardLists(),
                    seat, callCardListId);

            // 标记为已经叫过了
            callCardList.call = true;

            this.sendChooseCardListOver(game, roleGameInfo);

            // 检查是否要等待其他人做选择
            if (waitOtherCallCardListChecker.needWaitOtherChoice(game.getCallCardLists(), seat)) {
                return;
            }

            this.sendAllChooseCardListOver(game);

        }

        processor.pop(game);
        processor.push(game, MajiangStateEnum.STATE_ROLE_CHOSEN_CARDLIST);

        List<CallCardList> callCardLists = game.getCallCardLists();
        processor.process(game, callCardLists.size() > 0 ? callCardLists.get(0).masterSeat : 0);
    }

    /**
     * 胡的流程
     * 
     * @param game
     * @param seat
     * @param roleGameInfo
     * @param callCardList
     * @author wcy 2017年8月3日
     */
    private void huProgress(Game game) {
        CallCardList callCardList = game.getCallCardLists().get(0);
        int seat = callCardList.masterSeat;
        RoleGameInfo roleGameInfo = roleGameInfoGetter.getRoleGameInfoBySeat(game, seat);
        Hu hu = (Hu) callCardList.cardList;
        // 如果前面玩家杠了又胡则为杠开
        hu.gangKai = roleGameInfo.isGang;

        this.accumlateSendCardCount(game);
        // 其他同样可以胡的人都胡
        List<CallCardList> huCallCardLists = game.getHuCallCardLists();
        for (CallCardList huCallCardList : huCallCardLists) {
            Hu everyHu = (Hu) huCallCardList.cardList;
            loggerinfo("gameId=>" + game.getGameId() + "=>" + hu.toString());
            int masterSeat = huCallCardList.masterSeat;
            RoundCardsData huData = this.parseHu(everyHu);
            RoleGameInfo huRoleGameInfo = getRoleGameInfoBySeat(game, masterSeat);
            huRoleGameInfo.roundCardsData = huData;

            SC sc = SC.newBuilder().setSCFightHu(SCFightHu.newBuilder().setSeat(masterSeat).setHuData(huData)).build();
            this.sendAllSeatSC(game, sc);
            this.notifyObservers(FightConstant.FIGHT_HU, sc, game);
        }
        // // 如果胡的牌是抢杠, 杠的人要移除杠
        // for (RoleGameInfo info : game.getRoleIdMap().values()) {
        // if (info.qiangGang != null) {
        // this.removeGangTargetCard(game, info, info.qiangGang);
        // }
        // }
    }

    @Override
    public void guo(Role role, int gameSendCount) {

        int gameId = role.getGameId();
        Game game = getGameById(gameId);
        String gameRoleId = matchService.getGameRoleId(gameId, role.getRoleId());
        int seatIndex = game.getRoleIdList().indexOf(gameRoleId);

        this.guo(game, seatIndex, gameSendCount);
    }

    // /**
    // *
    // * @param gameId
    // * @param seatIndex
    // * 发送过的人的座位号
    // * @param gameSendCount
    // * 有客户端传送过来进行验证的标记
    // * @author wcy 2017年6月17日
    // */
    // private void guo(Game game, int seatIndex, int gameSendCount) {
    // RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, seatIndex);
    // // 杠标记取消
    // roleGameInfo.isGang = false;
    //
    // if (game.getSendCardCount() != gameSendCount) {
    // FightGuoResponse.Builder responseBuilder = FightGuoResponse.newBuilder();
    // responseBuilder.setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()).build();
    // SC responseSC =
    // SC.newBuilder().setFightGuoResponse(responseBuilder).build();
    // SessionUtils.sc(roleGameInfo.roleId, responseSC);
    // return;
    // }
    //
    // synchronized (game.getCallCardLists()) {
    // if (game.getSendCardCount() != gameSendCount) {
    // FightGuoResponse.Builder responseBuilder = FightGuoResponse.newBuilder();
    // responseBuilder.setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()).build();
    // SC responseSC =
    // SC.newBuilder().setFightGuoResponse(responseBuilder).build();
    // SessionUtils.sc(roleGameInfo.roleId, responseSC);
    // return;
    // }
    //
    // SessionUtils.sc(roleGameInfo.roleId,
    // SC.newBuilder().setFightGuoResponse(FightGuoResponse.newBuilder()).build());
    //
    // // 喊过就删除自己所有的叫牌
    // this.deleteAllCallCardListBySeat(game.getCallCardLists(), seatIndex);
    // this.deleteAllCallCardListBySeat(game.getHuCallCardLists(), seatIndex);
    //
    // this.sendChooseCardListOver(game, roleGameInfo);
    //
    // // if (!needWaitOtherCallCardListAction(game.getCallCardLists(),
    // // seatIndex)) {
    //
    // // 等待其他人选择
    // if
    // (waitOtherCallCardListChecker.needWaitOtherChoice(game.getCallCardLists(),
    // seatIndex)) {
    // return;
    // }
    //
    // this.sendAllChooseCardListOver(game);
    //
    // int index = game.getCurrentRoleIdIndex();
    // // 如果过的是自己，那要再出一张牌
    // if (index == seatIndex) {
    // this.noticeSendCard(game);
    // } else {
    // // 获得之前一个人的牌
    // CallCardList preCallCardList =
    // this.getPreviousCallCardList(game.getCallCardLists());
    //
    // if (preCallCardList == null) {
    // // 如果没有其他人有选择权,先检查这人当前的人有没有摸牌权力，有则摸牌，并通知，则直接顺序下一个人
    // RoleGameInfo currentRoleGameInfo = this.getCurrentRoleGameInfo(game);
    // if (currentRoleGameInfo.qiangGang != null) {
    // this.addGangSuccess(currentRoleGameInfo, currentRoleGameInfo.qiangGang);
    // Gang gang = currentRoleGameInfo.qiangGang;
    // currentRoleGameInfo.qiangGang = null;
    // this.gangProcess2(game, game.getCurrentRoleIdIndex(), roleGameInfo,
    // gang);
    // } else {
    // this.nextIndex(game);
    // this.touchCard(game);
    // }
    // } else {
    // // 如果做过判断了，则就是这个人的选择了
    // if (preCallCardList.call) {
    // this.jumpToIndex(game, preCallCardList.masterSeat);
    // this.touchCard(game);
    // }
    // }
    // }
    // // }
    // }
    // }

    /**
     * 
     * @param gameId
     * @param seatIndex 发送过的人的座位号
     * @param gameSendCount 有客户端传送过来进行验证的标记
     * @author wcy 2017年6月17日
     */
    private void guo(Game game, int seatIndex, int gameSendCount) {

        RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, seatIndex);
        // 杠标记取消
        roleGameInfo.isGang = false;

        if (game.getSendCardCount() != gameSendCount) {
            FightGuoResponse.Builder responseBuilder = FightGuoResponse.newBuilder();
            responseBuilder.setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()).build();
            SC responseSC = SC.newBuilder().setFightGuoResponse(responseBuilder).build();
            SessionUtils.sc(roleGameInfo.roleId, responseSC);
            return;
        }

        synchronized (game.getCallCardLists()) {
            if (game.getSendCardCount() != gameSendCount) {
                FightGuoResponse.Builder responseBuilder = FightGuoResponse.newBuilder();
                responseBuilder.setErrorCode(ErrorCode.FIGHT_TIME_PASS.getNumber()).build();
                SC responseSC = SC.newBuilder().setFightGuoResponse(responseBuilder).build();
                SessionUtils.sc(roleGameInfo.roleId, responseSC);
                return;
            }

            SessionUtils.sc(roleGameInfo.roleId, SC.newBuilder().setFightGuoResponse(FightGuoResponse.newBuilder())
                    .build());

            // 喊过就删除自己所有的叫牌
            this.deleteAllCallCardListBySeat(game.getCallCardLists(), seatIndex);
            this.deleteAllCallCardListBySeat(game.getHuCallCardLists(), seatIndex);

            this.sendChooseCardListOver(game, roleGameInfo);

            // 等待其他人选择
            if (waitOtherCallCardListChecker.needWaitOtherChoice(game.getCallCardLists(), seatIndex)) {
                return;
            }

            this.sendAllChooseCardListOver(game);
        }
        processor.pop(game);
        processor.push(game, MajiangStateEnum.STATE_ROLE_CHOSEN_CARDLIST);

        List<CallCardList> list = game.getCallCardLists();
        if (list.size() > 0) {
            processor.process(game, list.get(0).masterSeat);
        } else {
            processor.process(game);
        }

    }

    @Override
    public CallCardList getPreviousCallCardList(List<CallCardList> callCardLists) {
        for (CallCardList callCardList : callCardLists) {
            if (callCardList.call) {
                return callCardList;
            }
            continue;
        }

        return null;
    }

    /**
     * 初始化准备
     * 
     * @param game
     * @author wcy 2017年8月23日
     */
    private void initReady(Game game) {
        // 除npc外所有玩家重置准备
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            if (info.roleId == 0) {
                continue;
            }
            info.ready = false;
        }
    }

    /**
     * 消费燃点币
     * 
     * @param game
     * @author wcy 2017年8月23日
     */
    private void consumeRandiooCoin(Game game) {
        if (game.getFinishRoundCount() >= 1) {
            return;
        }

        // 大于一局就扣除燃点币
        GameRoundConfig config = GameRoundConfigCache.getGameRoundByRoundCount(game.getGameConfig().getRoundCount());
        Role role = (Role) RoleCache.getRoleById(game.getMasterRoleId());

        // TODO
        roleService.addRandiooMoney(role, -config.needMoney);
    }

    /**
     * 增加活跃度
     * 
     * @param game
     * @author wcy 2017年8月23日
     */
    private void addRandiooActive(Game game) {

        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            if (roleGameInfo.roleId == 0) {
                continue;
            }

            Role role = (Role) RoleCache.getRoleById(roleGameInfo.roleId);
            if (role == null) {
                continue;
            }

            try {
                randiooPlatformSdk.addActive(role.getAccount());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isGameOver(Game game) {
        GameConfigData gameConfigData = game.getGameConfig();
        GameOverMethod gameOverMethod = gameConfigData.getGameOverMethod();

        if (gameOverMethod == GameOverMethod.GAME_OVER_ROUND) {
            int roundCount = gameConfigData.getRoundCount();
            int finshRoundCount = game.getFinishRoundCount();

            return finshRoundCount >= roundCount;
        } else {
            String endTimeStr = gameConfigData.getEndTime();
            String nowTimeStr = TimeUtils.get_HHmmss_DateFormat().format(new Date());
            boolean isPassTime = false;
            try {
                isPassTime = TimeUtils.compareHHmmss(nowTimeStr, endTimeStr) >= 0;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return isPassTime;
        }
    }

    /**
     * 玩家是否胡
     * 
     * @param overMethod
     * @return
     * @author wcy 2017年8月2日
     */
    private boolean overMethodIsHu(OverMethod overMethod) {
        return overMethod == OverMethod.MO_HU || overMethod == OverMethod.ZHUA_HU
                || overMethod == OverMethod.QIANG_GANG;
    }

    /**
     * 回合结束
     * 
     * @param game
     * @param checkHu
     * @author wcy 2017年8月16日
     */
    @Override
    public void roundOverHongZhong(Game game, boolean checkHu) {
        // 完成回合数+1
        game.setFinishRoundCount(game.getFinishRoundCount() + 1);

        this.consumeRandiooCoin(game);

        SCFightRoundOver.Builder scFightRoundOverBuilder = SCFightRoundOver.newBuilder();
        GameConfigData config = game.getGameConfig();
        // 剩余几局没有打,如果要检查胡,才有剩余次数
        int maxCount = config.getMaxCount();
        int finishRoundCount = game.getFinishRoundCount();
        scFightRoundOverBuilder.setMaxRoundCount(maxCount);
        scFightRoundOverBuilder.setFinishRoundCount(finishRoundCount);

        // 房间号
        Key key = game.getLockKey();
        String roomId = matchService.getLockString(key);
        scFightRoundOverBuilder.setRoomId(roomId);

        int minScore = config.getBaseScore();

        // 抓苍蝇
        ZhamaResult zhamaResult = checkHu ? zhamaChecker.calculateZhamas(game) : new ZhamaResult();
        int zhamaScore = zhamaResult.getScore();
        // 暂时不用，前端说要自己算
        // List<Integer> resultZhamas = zhamaResult.getResultZhamas();

        scFightRoundOverBuilder.addAllZhamaCards(zhamaResult.getTouchCards());

        // 初始化结分器
        RoundOverParameter roundOverParameter = new RoundOverParameter();
        roundOverParameter.setCheckHu(checkHu);
        roundOverParameter.setZhamaScore(zhamaScore);
        roundOverParameter.setMinScore(minScore);
        roundOverParameter.getHuCallCardList().addAll(game.getHuCallCardLists());
        roundOverParameter.getRoleIdList().addAll(game.getRoleIdList());

        SCFightScore.Builder scFightScoreBuilder = SCFightScore.newBuilder();

        Map<Integer, RoundOverResult> roundOverResultMap = roundOverCalculator.getRoundOverResults(roundOverParameter);
        for (int i = 0; i < game.getRoleIdList().size(); i++) {
            RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, i);
            RoundOverResult roundOverResult = roundOverResultMap.get(i);

            GameRoleData gameRoleData = matchService.parseGameRoleData(roleGameInfo, game);
            RoundCardsData gameCardsData = this.parseRoundCardsData(game, roleGameInfo);

            RoleRoundOverInfoData.Builder roleRoundOverInfoBuilder = RoleRoundOverInfoData.newBuilder()
                    .setGameRoleData(gameRoleData).setRoundCardsData(gameCardsData).setBaseScore(minScore)
                    .setOverMethod(roundOverResult.overMethod);

            // 获得总结分对象
            this.checkGameOverResult(game, roleGameInfo);
            GameOverResult gameOverResult = game.getStatisticResultMap().get(roleGameInfo.gameRoleId);
            // 是否结局是胡
            if (this.overMethodIsHu(roundOverResult.overMethod)) {
                if (roundOverResult.gangKai) {
                    roleRoundOverInfoBuilder.addHuTypeList(HuType.GANG_KAI);
                }
            }
            switch (roundOverResult.overMethod) {
            case CHU_CHONG:// 点冲
                gameOverResult.dianChong++;
                break;
            case LOSS:// 正常输
                break;
            case GANG_CHONG:
                gameOverResult.gangChongCount++;
                break;
            case ZHUA_HU:// 胡
                gameOverResult.zhuaHuCount++;
                gameOverResult.huCount++;
                break;
            case MO_HU: // 摸胡
                gameOverResult.moHuCount++;
                gameOverResult.huCount++;
                break;
            case QIANG_GANG:// 抢杠
                gameOverResult.qiangGangCount++;
                gameOverResult.huCount++;
                break;
            default:
                break;
            }

            // 玩家回合分数
            roleGameInfo.roundOverResult.score             += roundOverResult.score;
            roleGameInfo.roundOverResult.mingGangScorePlus += roundOverResult.mingGangScorePlus;
            roleGameInfo.roundOverResult.mingGangCountPlus += roundOverResult.mingGangCountPlus;
            roleGameInfo.roundOverResult.darkGangScorePlus += roundOverResult.darkGangScorePlus;
            roleGameInfo.roundOverResult.darkGangCountPlus += roundOverResult.darkGangCountPlus;
            roleGameInfo.roundOverResult.addGangScorePlus  += roundOverResult.addGangScorePlus;
            roleGameInfo.roundOverResult.addGangCountPlus  += roundOverResult.addGangCountPlus;
            
            roleGameInfo.roundOverResult.mingGangScoreMinus += roundOverResult.mingGangScoreMinus;
            roleGameInfo.roundOverResult.mingGangCountMinus += roundOverResult.mingGangCountMinus;
            roleGameInfo.roundOverResult.darkGangScoreMinus += roundOverResult.darkGangScoreMinus;
            roleGameInfo.roundOverResult.darkGangCountMinus += roundOverResult.darkGangCountMinus;
            roleGameInfo.roundOverResult.addGangScoreMinus  += roundOverResult.addGangScoreMinus;
            roleGameInfo.roundOverResult.addGangCountMinus  += roundOverResult.addGangCountMinus;
            
            roleGameInfo.roundOverResult.moScore        += roundOverResult.moScore;
            roleGameInfo.roundOverResult.zhuaHuScore    += roundOverResult.zhuaHuScore   ;
            roleGameInfo.roundOverResult.qiangGangScore += roundOverResult.qiangGangScore;
            roleGameInfo.roundOverResult.chuChongScore  += roundOverResult.chuChongScore ;
            roleGameInfo.roundOverResult.gangChongScore += roundOverResult.gangChongScore;
            roleGameInfo.roundOverResult.zhaMaScore     += roundOverResult.zhaMaScore    ;
            roleGameInfo.roundOverResult.cangYingScore  += roundOverResult.cangYingScore ;

            // 分数增量
            RoundOverResult gameRoundOverResult = roleGameInfo.roundOverResult;
            int deltaScore = gameRoundOverResult.score - gameOverResult.score;
            // 计算所有杠的次数和所获得的分数
            // ====================================加的分============================================

            int mingGangDeltaScorePlus = gameRoundOverResult.mingGangScorePlus - gameOverResult.mingGangScorePlus;
            int mingGangDeltaCountPlus = gameRoundOverResult.mingGangCountPlus - gameOverResult.mingGangCountPlus;
            int darkGangDeltaScorePlus = gameRoundOverResult.darkGangScorePlus - gameOverResult.darkGangScorePlus;
            int darkGangDeltaCountPlus = gameRoundOverResult.darkGangCountPlus - gameOverResult.darkGangCountPlus;
            int addGangDeltaScorePlus =  gameRoundOverResult.addGangScorePlus  - gameOverResult.addGangScorePlus;
            int addGangDeltaCountPlus =  gameRoundOverResult.addGangCountPlus  - gameOverResult.addGangCountPlus;

            // =================================减的分===========================================
            int mingGangDeltaScoreMinus = gameRoundOverResult.mingGangScoreMinus - gameOverResult.mingGangScoreMinus;
            int mingGangDeltaCountMinus = gameRoundOverResult.mingGangCountMinus - gameOverResult.mingGangCountMinus;
            int darkGangDeltaScoreMinus = gameRoundOverResult.darkGangScoreMinus - gameOverResult.darkGangScoreMinus;
            int darkGangDeltaCountMinus = gameRoundOverResult.darkGangCountMinus - gameOverResult.darkGangCountMinus;
            int addGangDeltaScoreMinus =  gameRoundOverResult.addGangScoreMinus -  gameOverResult.addGangScoreMinus;
            int addGangDeltaCountMinus =  gameRoundOverResult.addGangCountMinus -  gameOverResult.addGangCountMinus;
            
//            // =================================分数=============================================
//            int moDeltaScore        = gameRoundOverResult.moScore          -   gameOverResult.moScore       ;
//            int zhuaHuDeltaScore    = gameRoundOverResult.zhuaHuScore      -   gameOverResult.zhuaHuScore   ;
//            int qiangGangDeltaScore = gameRoundOverResult.qiangGangScore   -   gameOverResult.qiangGangScore;
//            int chuChongDeltaScore  = gameRoundOverResult.chuChongScore    -   gameOverResult.chuChongScore ;
//            int gangChongDeltaScore = gameRoundOverResult.gangChongScore   -   gameOverResult.gangChongScore;
//            int zhaMaDeltaScore     = gameRoundOverResult.zhaMaScore       -   gameOverResult.zhaMaScore    ;
//            int cangYingDeltaScore  = gameRoundOverResult.cangYingScore    -   gameOverResult.cangYingScore ;
            
            // 设置回合分数
            roleRoundOverInfoBuilder.setRoundScore(deltaScore);

            roleRoundOverInfoBuilder.setAnGangCountPlus(darkGangDeltaCountPlus)
                    .setAnGangScorePlus(darkGangDeltaScorePlus).setBuGangCountPlus(addGangDeltaCountPlus)
                    .setBuGangScorePlus(addGangDeltaScorePlus).setMingGangCountPlus(mingGangDeltaCountPlus)
                    .setMingGangScorePlus(mingGangDeltaScorePlus);

            roleRoundOverInfoBuilder.setAnGangCountMinus(darkGangDeltaCountMinus)
                    .setAnGangScoreMinus(darkGangDeltaScoreMinus).setBuGangCountPlus(addGangDeltaCountMinus)
                    .setBuGangScorePlus(addGangDeltaScoreMinus).setMingGangCountMinus(mingGangDeltaCountMinus)
                    .setMingGangScoreMinus(mingGangDeltaScoreMinus);

            roleRoundOverInfoBuilder
                      .setZiMoScore(roundOverResult.moScore       )
                    .setZhuaHuScore(roundOverResult.zhuaHuScore   )
                 .setQiangGangScore(roundOverResult.qiangGangScore)
                  .setChuChongScore(roundOverResult.chuChongScore )
                 .setGangChongScore(roundOverResult.gangChongScore)
                     .setZhaMaScore(roundOverResult.zhaMaScore    )
                  .setCangYingScore(roundOverResult.cangYingScore );

            scFightRoundOverBuilder.addRoleRoundOverInfoData(roleRoundOverInfoBuilder);

            // 二次结分汇总
            gameOverResult.score = roleGameInfo.roundOverResult.score;
            // 杠的汇总
            //记录加减分
            gameOverResult. addGangCountPlus = gameRoundOverResult.addGangCountPlus;
            gameOverResult.darkGangCountPlus=  gameRoundOverResult.darkGangCountPlus;
            gameOverResult.mingGangCountPlus=  gameRoundOverResult.mingGangCountPlus;
            gameOverResult. addGangScorePlus = gameRoundOverResult.addGangScorePlus;
            gameOverResult.darkGangScorePlus=  gameRoundOverResult.darkGangScorePlus;
            gameOverResult.mingGangScorePlus=  gameRoundOverResult.mingGangScorePlus;
            
            gameOverResult. addGangCountMinus = gameRoundOverResult.addGangCountMinus;
            gameOverResult.darkGangCountMinus=  gameRoundOverResult.darkGangCountMinus;
            gameOverResult.mingGangCountMinus=  gameRoundOverResult.mingGangCountMinus;
            gameOverResult. addGangScoreMinus = gameRoundOverResult.addGangScoreMinus;
            gameOverResult.darkGangScoreMinus=  gameRoundOverResult.darkGangScoreMinus;
            gameOverResult.mingGangScoreMinus=  gameRoundOverResult.mingGangScoreMinus;
            

            //记录分数
             gameOverResult. addGangCount = gameRoundOverResult.addGangCountPlus; 
             gameOverResult.darkGangCount = gameRoundOverResult.darkGangCountPlus;
             gameOverResult.mingGangCount = gameRoundOverResult.mingGangCountPlus;
             gameOverResult. addGangScore = gameRoundOverResult.addGangScorePlus; 
             gameOverResult.darkGangScore = gameRoundOverResult.darkGangScorePlus;
             gameOverResult.mingGangScore = gameRoundOverResult.mingGangScorePlus;
            

            scFightScoreBuilder.addScoreData(ScoreData.newBuilder().setSeat(i).setScore(gameOverResult.score));
        }

        SC scFightRoundOverSC = SC.newBuilder().setSCFightRoundOver(scFightRoundOverBuilder).build();
        SC scFightScoreSC = SC.newBuilder().setSCFightScore(scFightScoreBuilder).build();

        // 所有人发结算通知
        this.sendAllSeatSC(game, scFightRoundOverSC);

        // 所有人发送分数通知
        this.sendAllSeatSC(game, scFightScoreSC);

        this.notifyObservers(FightConstant.ROUND_OVER, scFightRoundOverSC, game, checkHu);
        this.notifyObservers(FightConstant.FIGHT_SCORE, scFightScoreSC, game);

        // 庄判断器
        int nextRoundZhuangSeat = zhuangJudger.getZhuangSeat(roundOverResultMap);
        // 返回-1表示沿用这一局的庄家
        if (nextRoundZhuangSeat != -1) {
            game.setZhuangSeat(nextRoundZhuangSeat);
        }

    }

    private void checkGameOverResult(Game game, RoleGameInfo roleGameInfo) {
        Map<String, GameOverResult> resultMap = game.getStatisticResultMap();
        if (!resultMap.containsKey(roleGameInfo.gameRoleId)) {
            GameOverResult result = this.createRoleGameResult(roleGameInfo);
            resultMap.put(roleGameInfo.gameRoleId, result);
        }
    }

    /**
     * 游戏结束
     * 
     * @param game
     * @author wcy 2017年6月22日
     */
    @Override
    public void gameOverHongZhong(Game game) {
        this.addRandiooActive(game);
        // 一定要发这一句，让前端把钥匙清空
        this.sendAllSeatSC(game, SC.newBuilder().setSCFightClearRoomId(SCFightClearRoomId.newBuilder()).build());

        SCFightGameOver scFightGameOver = this.parseGameOverData(game);
        SC fightGameOverSC = SC.newBuilder().setSCFightGameOver(scFightGameOver).build();
        this.sendAllSeatSC(game, fightGameOverSC);
        // this.notifyObservers(FightConstant.FIGHT_GAME_OVER, scFightGameOver,
        // game);
        this.notifyObservers(FightConstant.FIGHT_GAME_OVER, fightGameOverSC, game);

        this.destroyGame(game);
    }

    @Override
    public void confirmGameOver(Role role) {
        FightConfirmGameOverResponse response = FightConfirmGameOverResponse.newBuilder().build();
        SC sc = SC.newBuilder().setFightConfirmGameOverResponse(response).build();
        SessionUtils.sc(role.getRoleId(), sc);

        role.setGameOverSC(null);
    }

    /**
     * 
     * @param game
     * @return
     * @author wcy 2017年7月14日
     */
    private SCFightGameOver parseGameOverData(Game game) {
        SCFightGameOver.Builder fightGameOverBuilder = SCFightGameOver.newBuilder();
        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            GameRoleData gameRoleData = matchService.parseGameRoleData(roleGameInfo, game);
            GameOverResult gameOverResult = game.getStatisticResultMap().get(roleGameInfo.gameRoleId);

            int huCount = gameOverResult.huCount;
            int moHuCount = gameOverResult.moHuCount;
            int dianChong = gameOverResult.dianChong;
            int zhuaHuCount = huCount - moHuCount;
            int totalGameScore = gameOverResult.score;
            int anGangCount = gameOverResult.darkGangCount;
            int anGangScore = gameOverResult.darkGangScore;
            int mingGangCount = gameOverResult.mingGangCount + gameOverResult.addGangCount;
            int mingGangScore = gameOverResult.mingGangScore + gameOverResult.addGangScore;

            RoleGameOverInfoData roleGameOverInfoData = RoleGameOverInfoData.newBuilder().setGameRoleData(gameRoleData)
                    .setHuCount(huCount).setZhuaHuCount(zhuaHuCount).setMoHuCount(moHuCount)
                    .setDianChongCount(dianChong).setGameScore(totalGameScore).setAnGangCount(anGangCount)
                    .setAnGangScore(anGangScore).setMingGangCount(mingGangCount).setMingGangScore(mingGangScore)
                    .build();

            fightGameOverBuilder.addRoleGameOverInfoData(roleGameOverInfoData);
        }
        // 通过钥匙获得房间号
        Key lockKey = game.getLockKey();
        String roomId = matchService.getLockString(lockKey);

        fightGameOverBuilder.setRoomId(roomId);
        int maxRoundCount = game.getGameConfig().getRoundCount();
        fightGameOverBuilder.setMaxRoundCount(maxRoundCount);
        fightGameOverBuilder.setFinishRoundCount(game.getFinishRoundCount());

        // 所有人发结算通知绿
        SCFightGameOver fightGameOver = fightGameOverBuilder.build();
        return fightGameOver;
    }

    /**
     * 销毁游戏
     * 
     * @param game
     * @author wcy 2017年7月13日
     */
    private void destroyGame(Game game) {

        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            Role role = (Role) RoleCache.getRoleById(roleGameInfo.roleId);
            if (role != null) {
                role.setGameId(0);

            }
        }

        Key key = game.getLockKey();
        if (key != null) {
            String lockString = matchService.getLockString(key);
            GameCache.getGameLockStringMap().remove(lockString);
            key.recycle();
        }

        // 将游戏从缓存池中移除
        GameCache.getGameMap().remove(game.getGameId());
    }

    private RoundCardsData parseRoundCardsData(Game game, RoleGameInfo roleGameInfo) {
        List<CardList> cardLists = roleGameInfo.showCardLists;
        RoundCardsData.Builder gameCardsDataBuilder = RoundCardsData
                .newBuilder()
                .setHuCard(
                        roleGameInfo.roundCardsData == null ? roleGameInfo.newCard : roleGameInfo.roundCardsData
                                .getHuCard()).addAllHandCards(roleGameInfo.cards);
        for (CardList cardList : cardLists) {

            Class<? extends CardList> clazz = getCardListPrototype(cardList);
            Function function = GameCache.getParseCardListToProtoFunctionMap().get(clazz);
            CardListData cardListData = (CardListData) function.apply(cardList);
            gameCardsDataBuilder.addCardListData(cardListData);
        }

        return gameCardsDataBuilder.build();
    }

    /**
     * 某玩家出牌
     * 
     * @param card
     * @param gameId
     * @param gameRoleId
     */
    private void gameRoleIdSendCard(int card, Game game, String gameRoleId, boolean isSendTouchCard) {
        RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
        // 杠标记取消
        roleGameInfo.isGang = false;
        // 设置当前的牌
        List<Integer> sendDesktopCards = game.getSendDesktopCardMap().get(game.getCurrentRoleIdIndex());
        sendDesktopCards.add(card);

        // 从手上减掉牌
        if (isSendTouchCard) {
            roleGameInfo.newCard = 0;
        } else {
            Lists.removeElementByList(roleGameInfo.cards, Arrays.asList(card));
        }

        // 出牌协议
        SCFightSendCard scFightSendCard = SCFightSendCard.newBuilder().setSeat(game.getCurrentRoleIdIndex())
                .setCard(card).setIsTouchCard(isSendTouchCard).build();
        SC scFightSendCardSC = SC.newBuilder().setSCFightSendCard(scFightSendCard).build();

        // 通知所有人,此人出的牌
        this.sendAllSeatSC(game, scFightSendCardSC);

        this.notifyObservers(FightConstant.FIGHT_SEND_CARD, scFightSendCardSC, game);

        // 如果有摸得牌还在要加入到手牌
        if (!isSendTouchCard) {
            this.newCardAdd2Cards(game, roleGameInfo);
        }

        // 清空临时列表
        game.getCallCardLists().clear();
        game.getHuCallCardLists().clear();

        int currentSeat = game.getCurrentRoleIdIndex();

        // 出牌记录
        game.sendCard = card;
        game.sendCardSeat = currentSeat;

        // wait pop
        processor.pop(game);
        // 玩家出牌
        processor.push(game, MajiangStateEnum.STATE_ROLE_SEND_CARD);
        processor.process(game, currentSeat);
        // ===========================================================
        // 不是白搭牌才能杠碰胡
        // boolean isBaiDaCard = GameCache.getBaiDaCardNumSet().contains(card);
        // if (!isBaiDaCard) {
        // // 保存场上除了本人的杠碰胡
        // for (int index = 0; index < game.getRoleIdList().size(); index++) {
        // // 自己不能碰自己
        // if (index == game.getCurrentRoleIdIndex())
        // continue;
        //
        // // 能否抓胡
        // boolean zhuaHuConfig = false;
        // List<Class<? extends CardList>> checkList =
        // GameCache.getCheckCardListSequence();
        // // checkList = zhuaHuConfig ?
        // // GameCache.getCheckCardListSequence() : GameCache
        // // .getCheckCardListOnlyMoHuSequence();
        // this.checkOtherCallCardList(game, index, card, checkList);
        // }
        // }
        // ===========================================================

        // GameConfigData gameConfigData = game.getGameConfig();
        // boolean zhuaHu = gameConfigData.getZhuaHu() ?
        // gameConfigData.getBaidaZhuaHu() ? true : false : false;
        // List<Class<? extends CardList>> checkList = zhuaHu ?
        // GameCache.getCheckCardListSequence() : GameCache
        // .getCheckCardListOnlyMoHuSequence();
        // for (int index = 0; index < game.getRoleIdList().size(); index++) {
        // if (index == game.getCurrentRoleIdIndex()) {
        // continue;
        // }
        // RoleGameInfo info = this.getRoleGameInfoBySeat(game, index);
        // // 如果有百搭
        // if (this.hasBaiDa(info, card)) {
        // checkList = GameCache.getCheckCardListOnlyMoHuSequence();
        // }
        //
        // this.checkOtherCallCardList(game, index, card, checkList);
        // }
        //
        // // 叫牌排序
        // Collections.sort(game.getCallCardLists(), callCardListComparator);
        //
        // // 其他人杠碰胡过或下一个人
        // this.otherRoleGangPengHuOrNextOne(game);

    }

    /**
     * 重置出牌
     * 
     * @param game
     * @author wcy 2017年8月25日
     */
    private void resetSendCard(Game game) {
        if (game.sendCardSeat == 0 && game.sendCard == 0) {
            return;
        }

        List<Integer> desktopCardList = game.getSendDesktopCardMap().get(game.sendCardSeat);
        desktopCardList.remove(desktopCardList.size() - 1);

        game.sendCard = 0;
        game.sendCardSeat = 0;
    }

    @Override
    public void roundOverQiaoMa(Game game, boolean checkHu) {
        // 完成回合数+1
        game.setFinishRoundCount(game.getFinishRoundCount() + 1);
        this.consumeRandiooCoin(game);
        GameConfigData gameConfig = game.getGameConfig();

        SCFightRoundOver.Builder scFightRoundOverBuilder = SCFightRoundOver.newBuilder();
        GameConfigData config = game.getGameConfig();
        // 剩余几局没有打,如果要检查胡,才有剩余次数
        int maxCount = config.getMaxCount();
        int finishRoundCount = game.getFinishRoundCount();
        scFightRoundOverBuilder.setMaxRoundCount(maxCount);
        scFightRoundOverBuilder.setFinishRoundCount(finishRoundCount);

        // 房间号
        Key key = game.getLockKey();
        String roomId = matchService.getLockString(key);
        scFightRoundOverBuilder.setRoomId(roomId);

        int flyScore = 0;
        // 抓苍蝇
        if (checkHu && gameConfig.getFlyCount() > 0) {
            BaidaFlyResult flyResult = baidaFlyCreater.fly(game);
            scFightRoundOverBuilder.addAllFlyCards(Arrays.asList(flyResult.getFlys()));
            flyScore = flyResult.getFlyScore();
        }
        SCFightScore.Builder scFightScoreBuilder = SCFightScore.newBuilder();

        // 获得结果集
        Map<Integer, RoundOverResult> resMap = qiaomaRoundOverCalc.getRoundOverResult(game, flyScore, checkHu);
        for (int i = 0; i < game.getRoleIdList().size(); i++) {
            RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, i);
            RoundOverResult roundOverResult = resMap.get(i);

            GameRoleData gameRoleData = matchService.parseGameRoleData(roleGameInfo, game);
            RoundCardsData gameCardsData = this.parseRoundCardsData(game, roleGameInfo);

            RoleRoundOverInfoData.Builder roleRoundOverInfoBuilder = RoleRoundOverInfoData.newBuilder()
                    .setGameRoleData(gameRoleData).setRoundCardsData(gameCardsData)
                    .addAllHuTypeList(roundOverResult.huTypeList).setBaseScore(game.getGameConfig().getBaseScore())
                    .setOverMethod(roundOverResult.overMethod);

            // 获得总结分对象
            this.checkGameOverResult(game, roleGameInfo);
            GameOverResult gameOverResult = game.getStatisticResultMap().get(roleGameInfo.gameRoleId);

            switch (roundOverResult.overMethod) {
            case CHU_CHONG:// 点冲
                gameOverResult.dianChong++;
                break;
            case LOSS:// 正常输
                break;
            case GANG_CHONG:
                gameOverResult.gangChongCount++;
                break;
            case ZHUA_HU:// 胡
                gameOverResult.zhuaHuCount++;
                gameOverResult.huCount++;
                break;
            case MO_HU: // 摸胡
                gameOverResult.moHuCount++;
                gameOverResult.huCount++;
                break;
            case QIANG_GANG:// 抢杠
                gameOverResult.qiangGangCount++;
                gameOverResult.huCount++;
                break;
            default:
                break;
            }
            // 玩家回合分数
            roleGameInfo.roundOverResult.score += roundOverResult.score;

            // 设置回合分数
            roleRoundOverInfoBuilder.setRoundScore(roundOverResult.score);
            scFightRoundOverBuilder.addRoleRoundOverInfoData(roleRoundOverInfoBuilder);

            // 二次结分汇总
            gameOverResult.score = roleGameInfo.roundOverResult.score;
            // 杠的汇总
            gameOverResult.darkGangCount = roleGameInfo.roundOverResult.darkGangCountPlus;
            gameOverResult.mingGangCount = roleGameInfo.roundOverResult.mingGangCountPlus;
            gameOverResult.addGangCount = roleGameInfo.roundOverResult.addGangCountPlus;
            
            scFightScoreBuilder.addScoreData(ScoreData.newBuilder().setSeat(i).setScore(gameOverResult.score));

        }

        SC scFightRoundOverSC = SC.newBuilder().setSCFightRoundOver(scFightRoundOverBuilder).build();
        SC scFightScoreSC = SC.newBuilder().setSCFightScore(scFightScoreBuilder).build();

        // 所有人发结算通知
        this.sendAllSeatSC(game, scFightRoundOverSC);

        // 所有人发送分数通知
        this.sendAllSeatSC(game, scFightScoreSC);

        this.notifyObservers(FightConstant.ROUND_OVER, scFightRoundOverSC, game, checkHu);
        this.notifyObservers(FightConstant.FIGHT_SCORE, scFightScoreSC, game);

        // 庄判断器
        int nextRoundZhuangSeat = zhuangJudger.getZhuangSeat(resMap);
        // 返回-1表示沿用这一局的庄家
        if (nextRoundZhuangSeat != -1) {
            game.setZhuangSeat(nextRoundZhuangSeat);
        }
    }

    @Override
    public void roundOverBaida(Game game, boolean checkHu) {
        // 完成回合数+1
        game.setFinishRoundCount(game.getFinishRoundCount() + 1);
        this.consumeRandiooCoin(game);

        BaidaMajiangRule rule = (BaidaMajiangRule) game.getRule();
        SCFightRoundOver.Builder scFightRoundOverBuilder = SCFightRoundOver.newBuilder();
        GameConfigData gameConfig = game.getGameConfig();
        String lockString = matchService.getLockString(game.getLockKey());
        scFightRoundOverBuilder.setRoomId(lockString);

        List<RoleRoundOverInfoData.Builder> roleRoundOverInfoDataBuilderList = new ArrayList<>(game.getRoleIdList()
                .size());

        int flyScore = 0;
        // 抓苍蝇
        if (checkHu && gameConfig.getFlyCount() > 0) {
            BaidaFlyResult flyResult = baidaFlyCreater.fly(game);
            scFightRoundOverBuilder.addAllFlyCards(Arrays.asList(flyResult.getFlys()));
            flyScore = flyResult.getFlyScore();
        }
        // 如果有人胡 荒番局数大于0 计数减一
        if (game.getHuCallCardLists().size() > 0 && checkHu) {
            int huangFanCount = game.getHuangFanCount();
            if (huangFanCount > 0) {
                // 是否是荒番局
                scFightRoundOverBuilder.setHuangFan(true);
                game.setHuangFanCount(huangFanCount - 1);
            }
        }
        Map<Integer, RoundOverResult> roundOverResultMap = new HashMap<>();
        for (int i = 0; i < game.getRoleIdList().size(); i++) {
            RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, i);

            GameRoleData gameRoleData = matchService.parseGameRoleData(roleGameInfo, game);
            RoundCardsData gameCardsData = this.parseRoundCardsData(game, roleGameInfo);
            boolean containsHu = false;
            GameOverResult gameOverResult = game.getStatisticResultMap().get(roleGameInfo.gameRoleId);
            if (checkHu) {
                // 查胡
                for (CallCardList callCardList : game.getHuCallCardLists()) {
                    if (callCardList.masterSeat != i)
                        continue;

                    containsHu = true;
                    ZLPBaiDaHu hu = (ZLPBaiDaHu) callCardList.cardList;

                    Ref<Integer> fanNum = new Ref<Integer>();
                    fanNum.set(0);

                    int huaNum = roleGameInfo.flowerCount + rule.getDarkFlowerCount(roleGameInfo);
                    List<HuType> huTypeList = huTypeCalc.getHuTypeList(game, roleGameInfo, hu, fanNum);
                    loggerinfo("hu_type_list " + huTypeList.toString());

                    ScoreParameter scoreParameter = new ScoreParameter();
                    scoreParameter.setBaseScore(gameConfig.getBaseScore());
                    scoreParameter.setHuaMultiple(gameConfig.getHuaScore());
                    scoreParameter.setHuangFan(gameConfig.getHuangFan());
                    scoreParameter.setFanNum(fanNum.get());
                    scoreParameter.setHuaNum(huaNum);
                    scoreParameter.setFlyScore(flyScore);
                    scoreParameter.setLimit(gameConfig.getMaxScore());
                    int score = scoreCalc.cal(scoreParameter);

                    OverMethod overMethod = OverMethod.MO_HU;
                    gameOverResult.huCount++;
                    gameOverResult.moHuCount++;

                    roleGameInfo.roundOverResult.score += (score * 3);
                    roleGameInfo.roundOverResult.overMethod = overMethod;
                    // 其他人减分
                    for (RoleGameInfo info : game.getRoleIdMap().values()) {
                        if (info.gameRoleId.equals(roleGameInfo.gameRoleId))
                            continue;

                        info.roundOverResult.score += -score;
                    }

                    RoleRoundOverInfoData.Builder builder = RoleRoundOverInfoData.newBuilder().setFlowerCount(huaNum)
                            .addAllHuTypeList(huTypeList).setGameRoleData(gameRoleData)
                            .setRoundCardsData(gameCardsData).setBaseScore(gameConfig.getBaseScore())
                            .setOverMethod(overMethod);

                    roleRoundOverInfoDataBuilderList.add(builder);
                }
            }

            // 没胡就是输，只能自摸没有点冲
            if (!containsHu) {
                OverMethod overMethod = OverMethod.LOSS;
                // if (checkHu) {
                // // 检查是否被点冲
                // for (CallCardList huCallCardList : game.getHuCallCardLists())
                // {
                // Hu hu = (Hu) huCallCardList.cardList;
                // if (hu.getTargetSeat() == i) {
                // // 点冲
                // overMethod = OverMethod.OVER_CHONG;
                // break;
                // }
                // }
                // }
                roleGameInfo.roundOverResult.overMethod = overMethod;

                RoleRoundOverInfoData.Builder builder = RoleRoundOverInfoData.newBuilder()
                        .setGameRoleData(gameRoleData).setRoundCardsData(gameCardsData).setOverMethod(overMethod)
                        .setBaseScore(gameConfig.getBaseScore());
                roleRoundOverInfoDataBuilderList.add(builder);
            }

        }

        SCFightScore.Builder scFightScoreBuilder = SCFightScore.newBuilder();

        // 注意上面循环的数序必须与接下来的循环顺序相同
        for (int i = 0; i < game.getRoleIdList().size(); i++) {
            RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, i);
            // 加入map用于庄的判断
            roundOverResultMap.put(i, roleGameInfo.roundOverResult);
            // 设置局的分数
            RoleRoundOverInfoData.Builder builder = roleRoundOverInfoDataBuilderList.get(i).setRoundScore(
                    roleGameInfo.roundOverResult.score);
            scFightRoundOverBuilder.addRoleRoundOverInfoData(builder);

            // 将分数加入总结算分数中
            Map<String, GameOverResult> resultMap = game.getStatisticResultMap();
            if (!resultMap.containsKey(roleGameInfo.gameRoleId)) {
                GameOverResult result = this.createRoleGameResult(roleGameInfo);
                resultMap.put(roleGameInfo.gameRoleId, result);
            }
            GameOverResult gameOverResult = game.getStatisticResultMap().get(roleGameInfo.gameRoleId);
            gameOverResult.score  = roleGameInfo.roundOverResult.score;
            
            gameOverResult.mingGangCountPlus = roleGameInfo.roundOverResult.mingGangCountPlus;
            gameOverResult.darkGangCountPlus = roleGameInfo.roundOverResult.darkGangCountPlus;
            gameOverResult.addGangCountPlus = roleGameInfo.roundOverResult.addGangCountPlus;
            

            // 分数结算汇总
            scFightScoreBuilder.addScoreData(ScoreData.newBuilder().setSeat(i).setScore(gameOverResult.score));
        }
        // 设置回合信息
        scFightRoundOverBuilder.setFinishRoundCount(game.getFinishRoundCount());
        scFightRoundOverBuilder.setMaxRoundCount(gameConfig.getRoundCount());
        scFightRoundOverBuilder.setFlyScore(flyScore);

        SC scFightRoundOverSC = SC.newBuilder().setSCFightRoundOver(scFightRoundOverBuilder).build();
        SC scFightScoreSC = SC.newBuilder().setSCFightScore(scFightScoreBuilder).build();
        // 所有人发结算通知
        this.sendAllSeatSC(game, scFightRoundOverSC);

        // 所有人发送分数通知
        this.sendAllSeatSC(game, scFightScoreSC);

        notifyObservers(FightConstant.ROUND_OVER, scFightRoundOverSC, game, checkHu);
        notifyObservers(FightConstant.FIGHT_SCORE, scFightScoreSC, game);

        // 庄判断器
        int nextRoundZhuangSeat = zhuangJudger.getZhuangSeat(roundOverResultMap);
        // 返回-1表示沿用这一局的庄家
        if (nextRoundZhuangSeat != -1)
            game.setZhuangSeat(nextRoundZhuangSeat);

    }

    public void checkOtherCardList(Game game) {
        int card = game.sendCard;
        MajiangRule rule = game.getRule();
        // for (int index = 0; index < game.getRoleIdList().size(); index++) {
        // if (index == game.getCurrentRoleIdIndex()) {
        // continue;
        // }
        // RoleGameInfo info = this.getRoleGameInfoBySeat(game, index);
        // // 如果有百搭
        // List<Class<? extends CardList>> checkList =
        // rule.getOtherCardListSequence(info, game);
        //
        // // 如果我是下家并且有花，则跳过我
        // if (index == nextSeat && containsFlowers(game, info)) {
        // continue;
        // }
        // this.checkOtherCallCardList(game, index, card, checkList);
        // }
        for (int seat : game.checkOtherCardListSeats) {

            RoleGameInfo info = this.getRoleGameInfoBySeat(game, seat);
            // 如果有百搭
            List<Class<? extends CardList>> checkList = rule.getOtherCardListSequence(info, game);

            System.out.println("checkOtherCardList:" + info.cards);
            System.out.println(checkList);
            this.checkOtherCallCardList(game, seat, card, checkList);
        }

        // 叫牌排序
        Collections.sort(game.getCallCardLists(), callCardListComparator);

    }

    //
    // /**
    // * 检查听牌绿
    // *
    // * @param game
    // * @param gameRoleId
    // * @return
    // * @author wcy 2017年7月10日
    // */
    // private boolean checkTing(Game game, String gameRoleId) {
    // Class<? extends CardList> clazz =
    // GameCache.getCheckGangCardList().get(0);
    // Hu hu = (Hu) GameCache.getCardLists().get(clazz);
    //
    // RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
    // roleGameInfo.tingCards.clear();
    //
    // List<Integer> cards = new ArrayList<>(roleGameInfo.cards);
    // CardSort cardSort = new CardSort(4);
    //
    // cardSort.fillCardSort(cards);
    //
    // hu.checkTing(cardSort, roleGameInfo.tingCards, game.getGameConfig());
    // boolean containsWaitCards = roleGameInfo.tingCards.size() > 0;
    //
    // return containsWaitCards;
    // }

    /**
     * 新牌加入到手牌
     * 
     * @param roleGameInfo
     * @author wcy 2017年6月19日
     */
    private void newCardAdd2Cards(Game game, RoleGameInfo roleGameInfo) {
        if (roleGameInfo.newCard == 0) {
            return;
        }

        int baidaCard = game.getRule().getBaidaCard(game);
        roleGameInfo.cards.add(roleGameInfo.newCard);
        roleGameInfo.newCard = 0;
        CardComparator comparator = new CardComparator();
        comparator.getBaidaCardSet().add(baidaCard);
        Collections.sort(roleGameInfo.cards, comparator);
    }

    /**
     * 发送倒计时
     * 
     * @param gameId
     * @param countdown
     * @author wcy 2017年6月17日
     */
    private void noticeCountDown(Game game, int countdown) {
        // 发送倒计时
        SCFightCountdown scFightCountdown = SCFightCountdown.newBuilder().setCountdown(10).build();
        SC sc = SC.newBuilder().setSCFightCountdown(scFightCountdown).build();

        this.sendAllSeatSC(game, sc);
        this.notifyObservers(FightConstant.FIGHT_COUNT_DOWN, sc, game);
    }

    /**
     * 座位指针
     * 
     * @param game
     * @param seat
     * @author wcy 2017年6月21日
     */
    private void noticePointSeat(Game game, int seat) {
        SC sc = SC
                .newBuilder()
                .setSCFightPointSeat(
                        SCFightPointSeat.newBuilder().setSeat(seat).setTempGameCount(game.getSendCardCount())).build();
        this.sendAllSeatSC(game, sc);
        this.notifyObservers(FightConstant.FIGHT_POINT_SEAT, sc, game);

    }

    /**
     * 检查叫碰杠胡的绿动作
     * 
     * @param game
     * @param hasGangPengHuSeatedIndex
     * @param card
     * @param list 需要获得的牌绿型
     * @author wcy 2017年6月14日
     */
    private void checkMineCallCardList(Game game, int hasGangPengHuSeatedIndex, int card,
            List<Class<? extends CardList>> list) {
        int currentRoleIdSeat = game.getCurrentRoleIdIndex();
        // 获得该卡组的人
        RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, hasGangPengHuSeatedIndex);

        // 填充卡组
        CardSort cardSort = new CardSort(4);
        List<CardList> cardLists = new ArrayList<>();

        List<Integer> cards = new ArrayList<>(roleGameInfo.cards);
        cards.add(card);

        cardSort.fillCardSort(cards);

        List<CallCardList> callCardLists = game.getCallCardLists();
        List<CallCardList> huCallCardLists = game.getHuCallCardLists();

        Map<Class<? extends CardList>, CardList> cardListMap = game.getRule().getCardListMap();
        for (Class<? extends CardList> clazz : list) {

            CardList templateCardList = cardListMap.get(clazz);
            templateCardList.check(game, cardLists, cardSort, card, roleGameInfo.showCardLists, true);

            for (CardList cardList : cardLists) {
                cardList.setTargetSeat(currentRoleIdSeat);

                CallCardList callCardList = new CallCardList();
                callCardList.cardListId = callCardLists.size() + 1;
                callCardList.masterSeat = hasGangPengHuSeatedIndex;
                callCardList.cardList = cardList;

                callCardLists.add(callCardList);
                // 如果是胡放到另一个数组中
                if (cardList instanceof Hu)
                    huCallCardLists.add(callCardList);
            }

            cardLists.clear();
        }
    }

    /**
     * 检查叫碰杠胡的动作
     * 
     * @param game
     * @param hasGangPengHuSeatedIndex
     * @param card
     * @param list 需要获得的牌型
     * @author wcy 2017年6月14日
     */
    private void checkOtherCallCardList(Game game, int hasGangPengHuSeatedIndex, int card,
            List<Class<? extends CardList>> list) {
        int currentRoleIdSeat = game.getCurrentRoleIdIndex();
        // 获得该卡组的人
        RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, hasGangPengHuSeatedIndex);

        // 填充卡组
        CardSort cardSort = new CardSort(4);
        List<CardList> cardLists = new ArrayList<>();

        List<Integer> cards = new ArrayList<>(roleGameInfo.cards);
        cards.add(card);

        cardSort.fillCardSort(cards);

        List<CallCardList> callCardLists = game.getCallCardLists();
        List<CallCardList> huCallCardLists = game.getHuCallCardLists();

        Map<Class<? extends CardList>, CardList> map = game.getRule().getCardListMap();

        for (Class<? extends CardList> clazz : list) {
            CardList templateCardList = map.get(clazz);

            templateCardList.check(game, cardLists, cardSort, card, roleGameInfo.showCardLists, false);

            for (CardList cardList : cardLists) {
                cardList.setTargetSeat(currentRoleIdSeat);

                CallCardList callCardList = new CallCardList();
                callCardList.cardListId = callCardLists.size() + 1;
                callCardList.masterSeat = hasGangPengHuSeatedIndex;
                callCardList.cardList = cardList;

                callCardLists.add(callCardList);
                // 如果是胡放到另一个绿数组中
                if (cardList instanceof Hu) {
                    huCallCardLists.add(callCardList);
                }
            }

            cardLists.clear();
        }
    }

    /*
     * scType 为通知类型
     */
    private void sendAllSeatSC(Game game, SC sc) {
        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            SessionUtils.sc(roleGameInfo.roleId, sc);
        }
    }

    /**
     * 跳转到下一个人
     * 
     * @param gameId
     * @return
     * @author wcy 2017年6月14日
     */
    private void nextIndex(Game game) {
        int index = game.getCurrentRoleIdIndex();
        jumpToIndex(game, (index + 1) >= game.getRoleIdList().size() ? 0 : index + 1);
    }

    /**
     * 跳转到固定的某个人
     * 
     * @param gameId
     * @param seatedIndex
     * @return
     * @author wcy 2017年6月14日
     */
    private void jumpToIndex(Game game, int seatedIndex) {
        game.setCurrentRoleIdIndex(seatedIndex);
        // 出牌次数加1
        this.accumlateSendCardCount(game);
        // 通知转向
        this.noticePointSeat(game, seatedIndex);
    }

    // 累计出牌数
    private void accumlateSendCardCount(Game game) {
        game.setSendCardCount(game.getSendCardCount() + 1);
    }

    /**
     * 获得当前玩家的信息
     * 
     * @param gameId
     * @return
     * @author wcy 2017年6月2日
     */
    private RoleGameInfo getCurrentRoleGameInfo(Game game) {
        int index = game.getCurrentRoleIdIndex();
        RoleGameInfo roleGameInfo = this.getRoleGameInfoBySeat(game, index);
        return roleGameInfo;
    }

    private RoleGameInfo getRoleGameInfoBySeat(Game game, int seat) {
        String gameRoleId = game.getRoleIdList().get(seat);
        return game.getRoleIdMap().get(gameRoleId);
    }

    /**
     * 获得游戏
     * 
     * @param gameId
     * @return
     */
    @Override
    public Game getGameById(int gameId) {
        return GameCache.getGameMap().get(gameId);
    }

    public CardListData parseChi(Chi chi) {
        CardListData.Builder chiDataBuilder = CardListData.newBuilder();
        chiDataBuilder.setCard(chi.card);
        chiDataBuilder.setTargetCard(chi.targetCard);
        chiDataBuilder.setTargetSeat(chi.getTargetSeat());
        chiDataBuilder.setCardListType(CardListType.CARD_LIST_TYPE_CHI);

        return chiDataBuilder.build();
    }

    private CardListData parseGang(Gang gang) {
        CardListData.Builder gangDataBuilder = CardListData.newBuilder();
        gangDataBuilder.setCard(gang.card);
        gangDataBuilder.setTargetCard(gang.card);
        gangDataBuilder.setTargetSeat(gang.getTargetSeat());
        gangDataBuilder
                .setCardListType(gang.dark ? CardListType.CARD_LIST_TYPE_GANG_DARK : gang.peng == null ? CardListType.CARD_LIST_TYPE_GANG_LIGHT : CardListType.CARD_LIST_TYPE_GANG_ADD);

        return gangDataBuilder.build();
    }

    private CardListData parsePeng(Peng peng) {
        CardListData.Builder pengDataBuilder = CardListData.newBuilder();
        pengDataBuilder.setCardListType(CardListType.CARD_LIST_TYPE_PENG);
        pengDataBuilder.setTargetSeat(peng.getTargetSeat());
        pengDataBuilder.setCard(peng.card);
        pengDataBuilder.setTargetCard(peng.card);

        return pengDataBuilder.build();
    }

    private RoundCardsData parseHu(Hu hu) {
        RoundCardsData.Builder huDataBuilder = RoundCardsData.newBuilder();
        huDataBuilder.setTargetSeat(hu.getTargetSeat());
        huDataBuilder.setHuCard(hu.card);
        huDataBuilder.setTouchCard(hu.isMine ? hu.card : 0);
        huDataBuilder.addAllHandCards(hu.handCards);
        for (CardList cardList : hu.showCardList) {
            Class<? extends CardList> clazz = getCardListPrototype(cardList);
            CardListData cardListData = (CardListData) GameCache.getParseCardListToProtoFunctionMap().get(clazz)
                    .apply(cardList);

            huDataBuilder.addCardListData(cardListData);
        }
        return huDataBuilder.build();
    }

    private Class<? extends CardList> getCardListPrototype(CardList cardList) {
        if (cardList instanceof Peng) {
            return Peng.class;
        } else if (cardList instanceof Gang) {
            return Gang.class;
        } else if (cardList instanceof Chi) {
            return Chi.class;
        } else if (cardList instanceof Hu) {
            return Hu.class;
        }
        return null;
    }

    /*
     * 排队
     */
    public void changeRole(int gameId, int roleId) {
        Game game = GameCache.getGameMap().get(gameId);
        Race race = RaceCache.getRaceMap().get(gameId);

        game.getRoleIdMap().remove(gameId + "_" + roleId);
        matchService.joinGameProcess1((Role) RoleCache.getRoleById(race.getRoleIdQueue().get(0)), gameId);

        race.getRoleIdQueue().remove(0);
        race.getRoleIdQueue().add(roleId);

    }

    /**
     * 摸到花
     * 
     * @param game
     * @param operateSeat
     */
    protected void touchFlowerProgress(Game game, int seat) {

        // Integer card = game.getRemainCards().get(0);
        // boolean isFlower = game.getRule().getFlowers(game).contains(card);
        // if (!isFlower) {
        // return;
        // }

        RoleGameInfo roleGameInfo = roleGameInfoGetter.getCurrentRoleGameInfo(game);
        // 摸到花之后再摸一张牌，胡了就是杠开
        roleGameInfo.isGang = true;
        Integer card = game.getRemainCards().remove(0);
        // 加入花牌集合
        roleGameInfo.sendFlowrCards.add(card);

        SC sc = SC
                .newBuilder()
                .setSCFightTouchCard(
                        SCFightTouchCard.newBuilder().setSeat(game.getCurrentRoleIdIndex()).setIsFlower(true)
                                .setRemainCardCount(game.getRemainCards().size()).setTouchCard(card)).build();

        // 通知摸到花牌
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            SessionUtils.sc(info.roleId, sc);
            notifyObservers(FightConstant.FIGHT_TOUCH_CARD, sc, game, info);
        }
        roleGameInfo.flowerCount++;
    }

    @Override
    public void disconnect(Role role) {
        int gameId = role.getGameId();
        if (role.getGameId() <= 0)
            return;

        Game game = this.getGameById(gameId);
        if (game == null) {
            return;
        }
        String gameRoleId = matchService.getGameRoleId(game.getGameId(), role.getRoleId());

        SC sc = SC.newBuilder()
                .setSCFightDisconnect(SCFightDisconnect.newBuilder().setSeat(game.getRoleIdList().indexOf(gameRoleId)))
                .build();
        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            if (roleGameInfo.roleId == role.getRoleId())
                continue;

            SessionUtils.sc(roleGameInfo.roleId, sc);
        }
    }

    // 重连
    // @Override
    // public void rejoin(Role role) {
    // int gameId = role.getGameId();
    // /* System.out.println("@@@@"+gameId); */
    // Game game = GameCache.getGameMap().get(gameId);
    // if (game == null) {
    // SessionUtils
    // .sc(role.getRoleId(),
    // SC.newBuilder()
    // .setFightRejoinResponse(
    // FightRejoinResponse.newBuilder().setErrorCode(
    // ErrorCode.GAME_NOT_EXIST.getNumber())).build());
    // return;
    // }
    // RoleGameInfo myInfo = null;
    // for (RoleGameInfo info : game.getRoleIdMap().values()) {
    // if (info.roleId == role.getRoleId()) {
    // myInfo = info;
    // break;
    // }
    // }
    // if (myInfo == null) {
    // SessionUtils.sc(
    // role.getRoleId(),
    // SC.newBuilder()
    // .setFightRejoinResponse(
    // FightRejoinResponse.newBuilder().setErrorCode(ErrorCode.NO_ROLE_DATA.getNumber()))
    // .build());
    // return;
    // }
    // List<ByteString> scList = new ArrayList<>();

    // 重连后逐条发通知

    // 进入游戏玩家的sc

    // VideoData videoData = myInfo.videoData;
    // List<List<SC>> scs = videoData.getScList();
    // if (scs != null) {
    // for (SC sc : scs.get(0)) {
    // scList.add(sc.toByteString());
    // }
    // }
    //
    // // 单局内的sc
    // for (SC sc : myInfo.roundSCList) {
    // scList.add(sc.toByteString());
    // }
    // System.out.println(scList);
    // System.out.println(scList.size());
    // RoundVideoData roundVideoData =
    // RoundVideoData.newBuilder().addAllSc(scList).build();
    // SessionUtils.sc(
    // role.getRoleId(),
    // SC.newBuilder()
    // .setFightRejoinResponse(
    // FightRejoinResponse.newBuilder().setRoundVideoData(roundVideoData)
    // .setLockString(matchService.getLockString(game.getLockKey()))).build());
    // // myInfo.online = true;
    // for (RoleGameInfo info : game.getRoleIdMap().values()) {// 通知其他玩家我已经重上好了
    // SessionUtils.sc(
    // info.roleId,
    // SC.newBuilder()
    // .setSCFightRejoin(
    // SCFightRejoin.newBuilder().setSeated(
    // game.getRoleIdList().indexOf(myInfo.gameRoleId))).build());
    //
    // }
    //
    // // if (myInfo.auto >= 2) {// 重连后解除托管状态
    // // this.auto(role);
    // // }
    // }

    public static void main(String[] args) {

        GlobleXmlLoader.init("./server.xml");

        GlobleMap.putParam(GlobleConstant.ARGS_PORT, 100012);
        GlobleMap.putParam(GlobleConstant.ARGS_LOGIN, false);

        String projectName = GlobleMap.String(GlobleConstant.ARGS_PROJECT_NAME)
                + GlobleMap.Int(GlobleConstant.ARGS_PORT);
        HttpLogUtils.setProjectName(projectName);

        SensitiveWordDictionary.readAll("./sensitive.txt");

        SpringContext.initSpringCtx("ApplicationContext.xml");

        ServiceManager serviceManager = SpringContext.getBean(ServiceManager.class);
        serviceManager.initServices();

        SchedulerManager schedulerManager = SpringContext.getBean(SchedulerManager.class);
        schedulerManager.start();

        Game game = new Game();
        game.setGameId(1);
        KeyStore keyStore = SpringContext.getBean(KeyStore.class);
        Key key = keyStore.getRandomKey();
        game.setLockKey(key);

        GameCache.getGameMap().put(1, game);
        GameConfigData config = GameConfigData.newBuilder().setEndTime("4:00:00").setMaxCount(4)
                .setGameOverMethod(GameOverMethod.GAME_OVER_ROUND).setRoundCount(4).build();
        game.setGameConfig(config);
        BaidaMajiangRule rule = SpringContext.getBean(BaidaMajiangRule.class);
        game.setRule(rule);
        //
        // MatchServiceImpl matchService = new MatchServiceImpl();
        // VideoServiceImpl videoService = new VideoServiceImpl();

        RoleGameInfo r1 = new RoleGameInfo();
        RoleGameInfo r2 = new RoleGameInfo();
        RoleGameInfo r3 = new RoleGameInfo();
        RoleGameInfo r4 = new RoleGameInfo();

        r1.gameRoleId = "1_0_0";
        r2.gameRoleId = "1_0_1";
        r3.gameRoleId = "1_0_2";
        r4.gameRoleId = "1_0_3";

        game.getRoleIdMap().put(r1.gameRoleId, r1);
        game.getRoleIdMap().put(r2.gameRoleId, r2);
        game.getRoleIdMap().put(r3.gameRoleId, r3);
        game.getRoleIdMap().put(r4.gameRoleId, r4);

        game.getRoleIdList().add(r1.gameRoleId);
        game.getRoleIdList().add(r2.gameRoleId);
        game.getRoleIdList().add(r3.gameRoleId);
        game.getRoleIdList().add(r4.gameRoleId);

        // FightServiceImpl fightService = new FightServiceImpl();
        // fightService.matchService = matchService;
        // fightService.videoService = videoService;
        //
        // fightService.init();
        // fightService.initService();

        long t1 = System.currentTimeMillis();
        r1.ready = true;

        Processor processor = SpringContext.getBean(Processor.class);
        processor.push(game, MajiangStateEnum.STATE_GAME_START);
        processor.process(game);

        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);
        // game.setGameConfig(GameConfigData.newBuilder().setEndTime("19:02:00").build());
        // fightService.over(game, 1);

    }
    // public static void main(String[] args) {
    // FightServiceImpl fightService = new FightServiceImpl();
    // fightService.init();
    // fightService.initService();
    //
    // Game game = new Game();
    //
    // game.setGameConfig(GameConfigData.newBuilder().setEndTime("19:02:00").build());
    // fightService.over(game, 1);
    // }

}
