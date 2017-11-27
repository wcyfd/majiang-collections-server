package com.randioo.majiang_collections_server.module.login.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.mina.core.session.IoSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.mahjong_public_server.protocol.Entity.GameState;
import com.randioo.mahjong_public_server.protocol.Entity.ReconnectData;
import com.randioo.mahjong_public_server.protocol.Entity.RoleData;
import com.randioo.mahjong_public_server.protocol.Error.ErrorCode;
import com.randioo.mahjong_public_server.protocol.Login.LoginGetRoleDataResponse;
import com.randioo.mahjong_public_server.protocol.Login.SCLoginOtherSide;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.majiang_collections_server.cache.local.GameCache;
import com.randioo.majiang_collections_server.dao.OnlineTimeDao;
import com.randioo.majiang_collections_server.dao.RoleDao;
import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.bo.OnlineTimeBO;
import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.majiang_collections_server.module.close.service.CloseService;
import com.randioo.majiang_collections_server.module.login.LoginConstant;
import com.randioo.majiang_collections_server.module.login.component.LoginConfig;
import com.randioo.majiang_collections_server.module.match.service.MatchService;
import com.randioo.majiang_collections_server.module.race.service.RaceService;
import com.randioo.majiang_collections_server.module.role.service.RoleService;
import com.randioo.majiang_collections_server.util.Tool;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.db.GameDB;
import com.randioo.randioo_server_base.entity.RoleInterface;
import com.randioo.randioo_server_base.log.Log;
import com.randioo.randioo_server_base.module.login.LoginHandler;
import com.randioo.randioo_server_base.module.login.LoginInfo;
import com.randioo.randioo_server_base.module.login.LoginModelConstant;
import com.randioo.randioo_server_base.module.login.LoginModelService;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.Ref;
import com.randioo.randioo_server_base.utils.SessionUtils;
import com.randioo.randioo_server_base.utils.StringUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("loginService")
public class LoginServiceImpl extends ObserveBaseService implements LoginService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private OnlineTimeDao onlineTimeDao;

    @Autowired
    private LoginModelService loginModelService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RaceService raceService;

    @Autowired
    private GameDB gameDB;

    @Autowired
    private MatchService matchService;

    @Autowired
    private CloseService closeService;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public void init() {
        LoggerFactory.getLogger(Role.class);
        // 初始化所有已经有过的帐号和昵称
        add(RoleCache.getNameSet(), roleDao.getAllNames());
        add(RoleCache.getAccountSet(), roleDao.getAllAccounts());

        loginModelService.setLoginHandler(new LoginHandlerImpl());
    }

    private void add(Map<String, String> map, List<String> list) {
        for (String str : list) {
            map.put(str, str);
        }
    }

    private class LoginHandlerImpl implements LoginHandler {

        @Override
        public RoleInterface getRoleInterfaceFromDBById(int roleId) {
            return roleDao.get(roleId);
        }

        @Override
        public RoleInterface getRoleInterfaceFromDBByAccount(String account) {
            return roleDao.getRoleByAccount(account);
        }

        @Override
        public void loginRoleModuleDataInit(RoleInterface roleInterface) {
            // 将数据库中的数据放入缓存中
            Role role = (Role) roleInterface;
            role.logger = Log.create(LoggerFactory.getLogger(Role.class), "account:" + roleInterface.getAccount());
            roleService.roleInit(role);
            raceService.raceInit(role);

            logger.info("登陆数据初始化 {}", role.getAccount());
        }

        @Override
        public boolean createRoleCheckAccount(LoginInfo info, Ref<Integer> errorCode) {
            // 账号姓名不可为空
            if (StringUtils.isNullOrEmpty(info.getAccount())) {
                errorCode.set(LoginConstant.CREATE_ROLE_NAME_SENSITIVE);
                return false;
            }

            return true;
        }

        @Override
        public RoleInterface createRole(LoginInfo loginInfo) {
            LoginConfig loginConfig = (LoginConfig) loginInfo;
            String account = loginConfig.getAccount();
            String name = loginConfig.getNickname();
            String lantiLongi = loginConfig.getLantiLongi();
            String voiceId = loginConfig.getVoiceId();
            // 用户数据
            // 创建用户
            Role role = new Role();
            role.logger = Log.create(LoggerFactory.getLogger(Role.class), "account:" + account);
            role.setAccount(account);
            role.setName(name);
            role.setLantiLongi(lantiLongi);
            role.setVoiceId(voiceId);

            roleService.newRoleInit(role);
            raceService.newRaceInit(role);

            logger.info("创建角色成功 {}", role.getAccount());

            SqlSession sqlSession = null;
            try {
                sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);

                sqlSession.getMapper(RoleDao.class).insert(role);

                sqlSession.commit();
                sqlSession.clearCache();
            } catch (Exception e) {
                sqlSession.rollback();
                return null;
            } finally {
                if (sqlSession != null) {
                    sqlSession.close();
                }
            }
            return role;
        }

        @Override
        public void noticeOtherPlaceLogin(IoSession session) {
            SessionUtils.sc(session, SC.newBuilder().setSCLoginOtherSide(SCLoginOtherSide.newBuilder()).build());
        }

        @Override
        public void closeCallback(IoSession session) {
            Role role = (Role) RoleCache.getRoleBySession(session);
            try {
                closeService.asynManipulate(role);
            } catch (Exception e) {
                if (role != null && role.logger != null) {
                    role.logger.error("sessionClosed error:", e);
                }
            }

            if (role != null) {
                // 通知登录
                notifyObservers(LoginConstant.LOGIN_OFFLINE, role);
            }
        }

        @Override
        public void synOffline(IoSession session) {
            Role role = (Role) RoleCache.getRoleBySession(session);
            long createTime = (long) session.getAttribute("loginTime");
            long destroyTime = System.currentTimeMillis();

            long deltaTime = destroyTime - createTime;

            OnlineTimeBO onlineTimeBO = onlineTimeDao.getByRoleId(role.getRoleId());
            try {
                if (onlineTimeBO == null) {
                    onlineTimeBO = new OnlineTimeBO();
                    onlineTimeBO.setRoleId(role.getRoleId());
                    onlineTimeBO.setOnlineTime(deltaTime);
                    onlineTimeDao.insert(onlineTimeBO);
                } else {
                    onlineTimeBO.setOnlineTime(onlineTimeBO.getOnlineTime() + deltaTime);
                    onlineTimeDao.update(onlineTimeBO);
                }
            } catch (DuplicateKeyException e) {
                onlineTimeBO = onlineTimeDao.getByRoleId(role.getRoleId());
                onlineTimeBO.setOnlineTime(onlineTimeBO.getOnlineTime() + deltaTime);
                onlineTimeDao.update(onlineTimeBO);
            }

        }

    }

    @Override
    public GeneratedMessage getRoleData(LoginInfo loginInfo, IoSession ioSession) {

        Ref<Integer> errorCode = new Ref<>();

        RoleInterface roleInterface = loginModelService.getRoleData(loginInfo, errorCode, ioSession);

        if (roleInterface != null) {
            Role role = (Role) roleInterface;

            // 刷新用户头像
            LoginConfig loginConfig = (LoginConfig) loginInfo;
            role.setHeadImgUrl(loginConfig.getHeadImageUrl());
            role.setName(loginConfig.getNickname());
            role.setLantiLongi(loginConfig.getLantiLongi());

            logger.info("登陆成功 {} 头像地址:{} , MAC地址:{}", role.getAccount(), loginConfig.getHeadImageUrl(),
                    loginConfig.getMacAddress());

            // 通知登录
            this.notifyObservers(LoginConstant.LOGIN_GET_ROLE_DATA, role);

            return SC.newBuilder()
                    .setLoginGetRoleDataResponse(LoginGetRoleDataResponse.newBuilder().setRoleData(getRoleData(role)))
                    .build();
        }

        ErrorCode errorEnum = null;
        switch (errorCode.get()) {
        case LoginModelConstant.GET_ROLE_DATA_NOT_EXIST:
            errorEnum = ErrorCode.NO_ROLE_DATA;
            break;
        case LoginModelConstant.GET_ROLE_DATA_IN_LOGIN:
            errorEnum = ErrorCode.IN_LOGIN;
            break;
        case LoginModelConstant.LOGIN_FAILED:// 服务器维护拒绝登陆
            errorEnum = ErrorCode.REJECT_LOGIN;
            break;
        case LoginModelConstant.CREATE_ROLE_FAILED:
            errorEnum = ErrorCode.CREATE_FAILED;
            break;
        }
        SC sc = SC.newBuilder()
                .setLoginGetRoleDataResponse(LoginGetRoleDataResponse.newBuilder().setErrorCode(errorEnum.getNumber()))
                .build();

        return sc;
    }

    @Override
    public RoleData getRoleData(Role role) {
        roleService.roleInit(role);

        int roleId = Tool.regExpression(role.getAccount(), "[0-9]*") ? Integer.parseInt(role.getAccount()) : role.getRoleId();
        Game game = GameCache.getGameMap().get(role.getGameId());
        // 游戏不存在或游戏已经结束,钥匙不存在
        String lockString = game == null || game.getGameState() == GameState.GAME_START_END ? null : matchService.getLockString(game.getLockKey());
        RoleData.Builder builder = RoleData.newBuilder()
                .setRoleId(roleId)
                .setPoint(1000)
                .setSex(1)
                .setName(role.getName())
                .setHeadImageUrl(role.getHeadImgUrl() != null ? role.getHeadImgUrl() : "")
                .setRandiooCoin(role.getRandiooMoney())
                .setServerTime(TimeUtils.getNowTime());

        ByteString gameOverSCBytes = role.getGameOverSC();
        GameConfigData gameConfigData = role.getGameConfigData();

        // 如果有录像数据就放入
        if (gameOverSCBytes != null) {
            ReconnectData.Builder reconnectDataBuilder = ReconnectData.newBuilder();
            reconnectDataBuilder.setGameOverSC(gameOverSCBytes);
            reconnectDataBuilder.setGameConfigData(gameConfigData);
            builder.setReconnectData(reconnectDataBuilder);
        }

        if (lockString != null) {
            builder.setRoomId(lockString);
        }

        return builder.build();
    }

    @Override
    public Role getRoleById(int roleId) {
        RoleInterface roleInterface = loginModelService.getRoleInterfaceById(roleId);
        return roleInterface == null ? null : (Role) roleInterface;
    }

    @Override
    public Role getRoleByAccount(String account) {
        RoleInterface roleInterface = loginModelService.getRoleInterfaceByAccount(account);
        return roleInterface == null ? null : (Role) roleInterface;
    }
}
