package com.randioo.randioo_server_base.log;

import java.lang.reflect.Proxy;

import org.slf4j.Logger;

public class Log {

    public static Logger create(Logger logger, String target) {
        LogInvocationHandler handler = new LogInvocationHandler(logger, target);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Object obj = Proxy.newProxyInstance(loader, new Class[] { Logger.class }, handler);
        Logger proxyLogger = (Logger) obj;
        return proxyLogger;
    }
}
