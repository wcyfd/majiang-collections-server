package com.randioo.majiang_collections_server.module.role.service;

import java.util.regex.Pattern;

import org.apache.mina.core.session.IoSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.protocol.Entity.RoleData;
import com.randioo.mahjong_public_server.protocol.Error.ErrorCode;
import com.randioo.mahjong_public_server.protocol.Role.RoleGetRoleDataResponse;
import com.randioo.mahjong_public_server.protocol.Role.RoleGetServerTimeResponse;
import com.randioo.mahjong_public_server.protocol.Role.RoleRenameResponse;
import com.randioo.mahjong_public_server.protocol.Role.SCRoleRandiooCoinChange;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.majiang_collections_server.GlobleConstant;
import com.randioo.majiang_collections_server.dao.ConsumeDao;
import com.randioo.majiang_collections_server.dao.RoleDao;
import com.randioo.majiang_collections_server.entity.bo.ConsumeData;
import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.majiang_collections_server.module.ServiceConstant;
import com.randioo.majiang_collections_server.module.login.LoginConstant;
import com.randioo.majiang_collections_server.module.login.service.LoginService;
import com.randioo.majiang_collections_server.module.role.RoleConstant;
import com.randioo.randioo_platform_sdk.RandiooPlatformSdk;
import com.randioo.randioo_platform_sdk.entity.AccountInfo;
import com.randioo.randioo_platform_sdk.exception.AccountErrorException;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.config.GlobleMap;
import com.randioo.randioo_server_base.db.IdClassCreator;
import com.randioo.randioo_server_base.log.L;
import com.randioo.randioo_server_base.module.role.RoleHandler;
import com.randioo.randioo_server_base.module.role.RoleModelService;
import com.randioo.randioo_server_base.sensitive.SensitiveWordDictionary;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.Ref;
import com.randioo.randioo_server_base.utils.SessionUtils;
import com.randioo.randioo_server_base.utils.StringUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("roleService")
public class RoleServiceImpl extends ObserveBaseService implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private IdClassCreator idClassCreator;

    @Autowired
    private RoleModelService roleModelService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private RandiooPlatformSdk randiooPlatformSdk;
    @Autowired
    private ConsumeDao consumeDao;

    @Override
    public void init() {
        LoggerFactory.getLogger(Role.class);
        Integer maxRoleId = roleDao.getMaxRoleId();
        idClassCreator.initId(Role.class, maxRoleId == null ? 0 : maxRoleId);
    }

    @Override
    public void initService() {
        roleModelService.setRoleHandler(new RoleHandler() {

            Pattern p = Pattern.compile(RoleConstant.PATTERN_CN_EN_FORMAT);

            @Override
            public boolean checkNewNameIllege(String name, Ref<Integer> errorCode) {

                if (name.length() >= 10) {
                    errorCode.set(LoginConstant.CREATE_ROLE_NAME_TOO_LONG);
                    return false;
                }

                if (SensitiveWordDictionary.containsSensitiveWord(name)) {
                    errorCode.set(LoginConstant.CREATE_ROLE_NAME_SENSITIVE);
                    return false;
                }

                if (RoleCache.getNameSet().containsKey(name)) {
                    errorCode.set(LoginConstant.CREATE_ROLE_NAME_REPEATED);
                    return false;
                }

                // 检查特殊字符
                if (!p.matcher(name).find()) {
                    errorCode.set(LoginConstant.CREATE_ROLE_NAME_CHAR);
                    return false;
                }

                return true;

            }
        });
    }

    @Override
    public void newRoleInit(Role role) {
        // 设置战场的第一章
        role.setRoleId(idClassCreator.getId(Role.class));
        role.setVolume(50);
        role.setMusicVolume(50);

        initRoleDataFromHttp(role);

        logger.info("新建用户 {}", role.getAccount());
    }

    @Override
    public void roleInit(Role role) {
        initRoleDataFromHttp(role);
    }

    @Override
    public GeneratedMessage rename(Role role, String name) {
        Ref<Integer> errorCode = new Ref<>();
        boolean success = roleModelService.rename(role, name, errorCode);
        if (!success) {
            ErrorCode errorCodeEnum = null;
            switch (errorCode.get()) {
            case LoginConstant.CREATE_ROLE_NAME_SENSITIVE:
                errorCodeEnum = ErrorCode.NAME_SENSITIVE;
                break;
            case LoginConstant.CREATE_ROLE_NAME_REPEATED:
                errorCodeEnum = ErrorCode.NAME_REPEATED;
                break;
            case LoginConstant.CREATE_ROLE_NAME_TOO_LONG:
                errorCodeEnum = ErrorCode.NAME_TOO_LONG;
                break;
            case LoginConstant.CREATE_ROLE_NAME_CHAR:
                errorCodeEnum = ErrorCode.NAME_SPECIAL_CHAR;
            }
            return SC.newBuilder()
                    .setRoleRenameResponse(RoleRenameResponse.newBuilder().setErrorCode(errorCodeEnum.getNumber()))
                    .build();
        }

        return SC.newBuilder().setRoleRenameResponse(RoleRenameResponse.newBuilder()).build();
    }

    @Override
    public void setHeadimgUrl(Role role, String headImgUrl) {

        role.setHeadImgUrl(headImgUrl);
    }

    @Override
    public void setRandiooMoney(Role role, int randiooMoney) {
        role.setRandiooMoney(randiooMoney);
    }

    @Override
    public void initRoleDataFromHttp(Role role) {

        String name = null;
        int money = -1;
        int sex = 0;
        String headImageUrl = "null";

        try {
            logger.info("平台信息准备读取 {}", role.getAccount());

            AccountInfo accountInfo = randiooPlatformSdk.getAccountInfo(role.getAccount());
            money = accountInfo.randiooMoney;
            sex = accountInfo.sex;
            // 使用平台头像
            if (GlobleMap.Boolean(GlobleConstant.ARGS_PLATFORM_HEAD_IMAGE_URL)) {
                headImageUrl = accountInfo.headImgUrl;
                name = accountInfo.nickName;
            }

            logger.info("平台信息为 {}={}", role.getAccount(), accountInfo);
        } catch (AccountErrorException e) {
            logger.error("没有该帐号平台信息 {}", role.getAccount());
        } catch (Exception e) {
            logger.error("{} 读取平台信息失败 {}", role.getAccount(), e);
        }
        if (money == -1)
            money = 0;

        role.setRandiooMoney(money);
        role.setSex(sex);
        if (GlobleMap.Boolean(GlobleConstant.ARGS_PLATFORM_HEAD_IMAGE_URL)) {
            role.setHeadImgUrl(headImageUrl);
            role.setName(StringUtils.isNullOrEmpty(name) ? ServiceConstant.GUEST_PREFIX_NAME + role.getRoleId() : name);
        }
    }

    @Override
    public boolean addRandiooMoney(Role role, int money) {
        try {
            logger.info("改变平台币数量 {}={}", role.getAccount(), money);
            randiooPlatformSdk.addMoney(role.getAccount(), money);
            role.setRandiooMoney(role.getRandiooMoney() + money);
            // 燃点币消耗记录
            consumeDao.insert(new ConsumeData(role.getRoleId(), money));
            logger.info("平台币总量 {}={}", role.getAccount(), role.getRandiooMoney());
        } catch (Exception e) {
            logger.error("没有该帐号,无法改变平台币 {}", role.getAccount());
            return false;
        }

        SCRoleRandiooCoinChange scRoleAddRandiooMoney = SCRoleRandiooCoinChange.newBuilder()
                .setRandiooCoin(role.getRandiooMoney()).build();
        SC sc = SC.newBuilder().setSCRoleRandiooCoinChange(scRoleAddRandiooMoney).build();
        SessionUtils.sc(role.getRoleId(), sc);
        return true;
    }

    @Override
    public GeneratedMessage getRoleData(String account) {
        Role role = loginService.getRoleByAccount(account);
        RoleData roleData = loginService.getRoleData(role);

        return SC.newBuilder().setRoleGetRoleDataResponse(RoleGetRoleDataResponse.newBuilder().setRoleData(roleData))
                .build();
    }

    @Override
    public void getServerTime(IoSession session) {
        RoleGetServerTimeResponse response = RoleGetServerTimeResponse.newBuilder()
                .setServerTime(TimeUtils.getNowTime()).build();
        SC sc = SC.newBuilder().setRoleGetServerTimeResponse(response).build();
        SessionUtils.sc(session, sc);
    }
}
