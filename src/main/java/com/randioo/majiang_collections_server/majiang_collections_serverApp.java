package com.randioo.majiang_collections_server;

import java.io.IOException;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.keepalive.KeepAliveFilter;

import com.randioo.mahjong_public_server.protocol.ClientMessage.CS;
import com.randioo.mahjong_public_server.protocol.Heart.CSHeart;
import com.randioo.mahjong_public_server.protocol.Heart.HeartRequest;
import com.randioo.mahjong_public_server.protocol.Heart.HeartResponse;
import com.randioo.mahjong_public_server.protocol.Heart.SCHeart;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.majiang_collections_server.handler.HeartTimeOutHandler;
import com.randioo.majiang_collections_server.util.JedisUtils;
import com.randioo.randioo_server_base.config.ConfigLoader;
import com.randioo.randioo_server_base.config.GlobleArgsLoader;
import com.randioo.randioo_server_base.config.GlobleMap;
import com.randioo.randioo_server_base.config.GlobleXmlLoader;
import com.randioo.randioo_server_base.heart.ProtoHeartFactory;
import com.randioo.randioo_server_base.init.GameServerInit;
import com.randioo.randioo_server_base.init.LogSystem;
import com.randioo.randioo_server_base.sensitive.SensitiveWordDictionary;
import com.randioo.randioo_server_base.utils.SpringContext;

/**
 * Hello world!
 *
 */
public class majiang_collections_serverApp {

    /**
     * @param args
     * @author wcy 2017年8月17日
     * @throws IOException
     */
    public static void main(String[] args) {

        GlobleXmlLoader.init("./server.xml");
        GlobleArgsLoader.init(args);

        LogSystem.init(majiang_collections_serverApp.class);

        ConfigLoader.loadConfig("com.randioo.majiang_collections_server.entity.file", "./config.zip");

        SensitiveWordDictionary.readAll("./sensitive.txt");

        SpringContext.initSpringCtx("classpath:ApplicationContext.xml");

        GameServerInit gameServerInit = ((GameServerInit) SpringContext.getBean(GameServerInit.class));
        // 设置CS
        gameServerInit.setMessageLite(CS.getDefaultInstance());

        // 心跳工厂
        ProtoHeartFactory protoHeartFactory = new ProtoHeartFactory();
        protoHeartFactory.setHeartRequest(CS.newBuilder().setHeartRequest(HeartRequest.newBuilder()).build());
        protoHeartFactory.setHeartResponse(SC.newBuilder().setHeartResponse(HeartResponse.newBuilder()).build());
        protoHeartFactory.setScHeart(SC.newBuilder().setSCHeart(SCHeart.newBuilder()).build());
        protoHeartFactory.setCsHeart(CS.newBuilder().setCSHeart(CSHeart.newBuilder()).build());

        HeartTimeOutHandler heartTimeOutHandler = SpringContext.getBean(HeartTimeOutHandler.class);
        gameServerInit.setKeepAliveFilter(new KeepAliveFilter(protoHeartFactory, IdleStatus.READER_IDLE,
                heartTimeOutHandler, 8, 10));
        gameServerInit.start();

        GlobleMap.putParam(GlobleConstant.ARGS_LOGIN, true);

    }
}
