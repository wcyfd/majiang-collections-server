package com.randioo.majiang_collections_server.module.gm.component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.MessageFormat;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import com.randioo.majiang_collections_server.GlobleConstant;
import com.randioo.majiang_collections_server.util.JedisUtils;
import com.randioo.randioo_server_base.config.GlobleMap;

public class GmConsole {

    private Logger logger = LoggerFactory.getLogger(GmConsole.class);

    @Autowired
    private JedisPool jedisPool;

    public void start() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    Jedis jedis = null;
                    try {
                        jedis = jedisPool.getResource();
                        String channel = GlobleMap.String(GlobleConstant.ARGS_PLATFORM_PACKAGE_NAME) + ".cmd";
                        jedis.subscribe(new JedisPubSub() {
                            @Override
                            public void onMessage(String channel, String message) {
                                String consoleChannel = GlobleMap.String(GlobleConstant.ARGS_PLATFORM_PACKAGE_NAME)
                                        + ".console";
                                try {
                                    ScriptEngineManager manager = new ScriptEngineManager();
                                    ScriptEngine scriptEngine = manager.getEngineByName("javascript");

                                    String[] msg = message.split(" ");
                                    setArgs(message, scriptEngine);

                                    String context = readFile(msg[0]);
                                    Object output = scriptEngine.eval(context);
                                    output = output == null ? MessageFormat.format("Command {0} execute complete",
                                            message) : output;
                                    JedisUtils.publish(consoleChannel, String.valueOf(output));
                                } catch (ScriptException e) {
                                    String error = e.getMessage();
                                    logger.error("", e);
                                    JedisUtils.publish(consoleChannel, error);
                                }
                            }
                        }, channel);
                    } catch (Exception e) {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    } finally {
                        JedisUtils.returnResource(jedis);
                    }
                }
            }
        }).start();
    }

    public String readFile(String fileName) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(fileName)))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void setArgs(String message, ScriptEngine scriptEngine) throws ScriptException {
        int startIndex = message.indexOf(" ");
        if (startIndex != -1) {
            String param = message.substring(startIndex, message.length());
            scriptEngine.eval(param);
        }
    }
}
