package com.randioo.majiang_collections_server.handler;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.mahjong_public_server.protocol.ClientMessage.CS;
import com.randioo.mahjong_public_server.protocol.Heart.HeartResponse;
import com.randioo.mahjong_public_server.protocol.Heart.SCHeart;
import com.randioo.mahjong_public_server.protocol.Match.MatchJoinGameResponse;
import com.randioo.majiang_collections_server.cache.local.GameCache;
import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.majiang_collections_server.module.close.service.CloseService;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.handler.GameServerHandlerAdapter;
import com.randioo.randioo_server_base.module.login.LoginModelService;

@Component
public class GameServerHandler extends GameServerHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private String scHeartStr = SCHeart.class.getSimpleName();
    private String heartResponseStr = HeartResponse.class.getSimpleName();
    private String matchJoinGameResponse = MatchJoinGameResponse.class.getSimpleName();
    @Autowired
    private CloseService closeService;

    @Autowired
    private LoginModelService loginModelService;

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        // logger.info("roleId:" + session.getAttribute("roleId") +
        // " sessionCreated");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        // logger.info("roleId:" + session.getAttribute("roleId") +
        // " sessionOpened");
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        // logger.info("roleId:" + session.getAttribute("roleId") +
        // " sessionClosed");

        loginModelService.offline(session);

    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {

    }

    @Override
    public void exceptionCaught(IoSession session, Throwable e) throws Exception {
        Role role = (Role) RoleCache.getRoleBySession(session);
        if (role != null && role.logger != null) {
            role.logger.error("exceptionCaught ", e);
        }
        session.close(true);
    }

    @Override
    public void messageReceived(IoSession session, Object messageObj) throws Exception {

        Role role = (Role) RoleCache.getRoleBySession(session);
        try {
            CS message = (CS) messageObj;
            logger.info("{}", message);
            if (role != null) {
                role.logger.info("{}", message);
                Game game = GameCache.getGameMap().get(role.getGameId());
                if (game != null) {
                    game.logger.info("{} {} {}", role.getAccount(), role.getName(), message);
                }
            }
            this.actionDispatcher(message, session);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            logger.error("逻辑报错 {}", sw);
            if (role != null) {
                role.logger.error("{}", sw);
                Game game = GameCache.getGameMap().get(role.getGameId());
                if (game != null) {
                    game.logger.error("{} {} {}", role.getAccount(), role.getName(), sw);
                }
            }
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        String messageStr = message.toString();
        if (messageStr.contains(scHeartStr) || messageStr.contains(heartResponseStr))
            return;

        String msg = getMessage(messageStr, session);

        Role role = (Role) RoleCache.getRoleBySession(session);
        if (role != null) {
            Game game = GameCache.getGameMap().get(role.getGameId());
            if (game != null) {
                game.logger.info("{} {} {}", role.getAccount(), role.getName(), msg);
            }

            role.logger.info(msg);
        } else {
            logger.info(msg);
        }

    }

    private String getMessage(String message, IoSession session) {

        if (message.length() < 120) {
            message = message.replaceAll("\n", " ").replace("\t", " ").replace("  ", "");
        }
        return message;

    }

}
