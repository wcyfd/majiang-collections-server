package com.randioo.majiang_collections_server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.randioo.randioo_server_base.utils.SpringContext;

public class JedisUtils {
    private final static Logger logger = LoggerFactory.getLogger(JedisUtils.class);
    private static JedisPool jedisPool = SpringContext.getBean(JedisPool.class);

    /**
     * 设置值
     * 
     * @param key
     * @param value
     * @author wcy 2017年10月25日
     */
    public static void set(String key, String value) {
        Jedis j = null;
        try {
            j = jedisPool.getResource();
            j.set(key, value);
        } catch (Exception e) {
            // logger.error("", e);
        } finally {
            returnResource(j);
        }
    }

    public static void publish(String channel, String message) {
        Jedis j = null;
        try {
            j = jedisPool.getResource();
            j.publish(channel, message);
        } catch (Exception e) {

        } finally {
            returnResource(j);
        }
    }

    public static void returnResource(Jedis j) {
        if (j != null) {
            j.close();
        }
    }
}
