package com.randioo.randioo_server_base.module.login;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.randioo.randioo_server_base.GlobleConstant;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.config.GlobleMap;
import com.randioo.randioo_server_base.entity.RoleInterface;
import com.randioo.randioo_server_base.lock.CacheLockUtil;
import com.randioo.randioo_server_base.service.BaseService;
import com.randioo.randioo_server_base.template.Ref;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("loginModelService")
public class LoginModelServiceImpl extends BaseService implements LoginModelService {
    private static final Logger logger = LoggerFactory.getLogger(LoginModelService.class);

    private LoginHandler loginHandler;

    @Override
    public void setLoginHandler(LoginHandler loginHandler) {
        this.loginHandler = loginHandler;
    }

    @Override
    public RoleInterface getRoleData(LoginInfo loginInfo, Ref<Integer> errorCode, IoSession session) {
        if (!GlobleMap.Boolean(GlobleConstant.ARGS_LOGIN)) {
            errorCode.set(LoginModelConstant.LOGIN_FAILED);
            return null;
        }

        String account = loginInfo.getAccount();
        String nowTime = TimeUtils.getDetailTimeStr();
        IoSession lastSession = null;
        ReentrantLock reentrantLock = CacheLockUtil.getLock(String.class, account);

        try {
            reentrantLock.lock();
            // 获得玩家对象
            RoleInterface roleInterface = this.getRoleInterfaceByAccount(account);
            if (roleInterface == null) {
                // 账号不存在，检查帐号格式是否合法
                boolean checkAccount = loginHandler.createRoleCheckAccount(loginInfo, errorCode);
                // 账号格式不合法,返回错误码
                if (!checkAccount) {
                    errorCode.set(LoginModelConstant.GET_ROLE_DATA_NOT_EXIST);
                    return null;
                } else {
                    if (RoleCache.getAccountSet().containsKey(account)) {
                        errorCode.set(LoginModelConstant.CREATE_ROLE_FAILED);
                        return null;
                    }
                    try {
                        // 帐号合法,创建用户
                        roleInterface = loginHandler.createRole(loginInfo);
                        roleInterface.setCreateTimeStr(TimeUtils.getDetailTimeStr());
                    } catch (Exception e) {
                        logger.error("Error loginInfo=" + loginInfo, e);
                        errorCode.set(LoginModelConstant.CREATE_ROLE_FAILED);
                        return null;
                    }
                }

            }

            int roleId = roleInterface.getRoleId();

            lastSession = SessionCache.getSessionById(roleId);

            if (lastSession != null) {
                String lastMac = getMacAttribute(lastSession);
                if (lastMac == null || !lastMac.equals(loginInfo.getMacAddress())) {
                    // 通知异地登录
                    loginHandler.noticeOtherPlaceLogin(lastSession);
                }
            }

            // 设置登陆时间
            roleInterface.setLoginTimeStr(nowTime);

            // session绑定ID
            setRoleIdAttribute(session, roleId);
            // session绑定mac
            setMacAttribute(session, loginInfo);
            // session绑定玩家登录时间
            setLoginTimeAttribute(session, System.currentTimeMillis());

            // session放入缓存
            SessionCache.addSession(roleId, session);

            // 将数据库中的数据放入缓存中
            RoleCache.putRoleCache(roleInterface);

            return roleInterface;
        } catch (Exception e) {
            logger.error("Error loginInfo=" + loginInfo, e);
            return null;
        } finally {
            reentrantLock.unlock();
            if (lastSession != null) {
                lastSession.close(true);
            }
        }
    }

    private void setLoginTimeAttribute(IoSession session, long currentTimeMillis) {
        session.setAttribute("loginTime", currentTimeMillis);
    }

    private void setRoleIdAttribute(IoSession session, Object value) {
        session.setAttribute("roleId", value);
    }

    /**
     * 设置mac地址属性
     * 
     * @param session
     * @param macAddress
     * @author wcy 2017年10月20日
     */
    private void setMacAttribute(IoSession session, LoginInfo loginInfo) {
        String macAddress = loginInfo.getMacAddress();
        if (macAddress == null) {
            return;
        }
        session.setAttribute("mac", macAddress);
    }

    /**
     * 获得mac地址
     * 
     * @param session
     * @return
     * @author wcy 2017年10月20日
     */
    private String getMacAttribute(IoSession session) {
        Object value = session.getAttribute("mac");
        return value != null ? (String) value : null;
    }

    @Override
    public RoleInterface getRoleInterfaceById(int roleId) {
        RoleInterface role = RoleCache.getRoleById(roleId);
        if (role == null) {
            role = loginHandler.getRoleInterfaceFromDBById(roleId);
            if (role == null) {
                return null;
            }
            Lock lock = CacheLockUtil.getLock(String.class, role.getAccount());
            try {
                lock.lock();
                RoleInterface role2 = RoleCache.getRoleById(roleId);
                if (role2 != null) {
                    return role2;
                }

                loginHandler.loginRoleModuleDataInit(role);
                RoleCache.putRoleCache(role);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

        }
        return role;
    }

    @Override
    public RoleInterface getRoleInterfaceByAccount(String account) {
        RoleInterface role = RoleCache.getRoleByAccount(account);
        if (role == null) {
            role = loginHandler.getRoleInterfaceFromDBByAccount(account);
            if (role == null) {
                return null;
            }
            Lock lock = CacheLockUtil.getLock(String.class, account);
            try {
                lock.lock();
                RoleInterface role2 = RoleCache.getRoleByAccount(account);
                if (role2 != null) {
                    return role2;
                }

                loginHandler.loginRoleModuleDataInit(role);
                RoleCache.putRoleCache(role);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

        }
        return role;
    }

    @Override
    public void offline(IoSession session) {
        Object obj = session.getAttribute("roleId");
        if (obj == null) {
            return;
        }

        int roleId = (int) obj;
        RoleInterface roleInterface = RoleCache.getRoleById(roleId);
        if (roleInterface == null || roleInterface.getAccount() == null) {
            return;
        }

        loginHandler.synOffline(session);

        ReentrantLock reentrantLock = CacheLockUtil.getLock(String.class, roleInterface.getAccount());
        try {
            reentrantLock.lock();

            loginHandler.closeCallback(session);

            IoSession currentSession = SessionCache.getSessionById(roleId);
            if (currentSession == session) {
                SessionCache.removeSessionById(roleId);
            }
        } catch (Exception e) {
            logger.error("offline error{}", e);
        } finally {
            reentrantLock.unlock();
        }
    }

}
