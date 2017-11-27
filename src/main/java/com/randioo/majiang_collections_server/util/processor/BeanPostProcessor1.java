/**
 * 
 */
package com.randioo.majiang_collections_server.util.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.randioo.majiang_collections_server.GlobleConstant;
import com.randioo.randioo_platform_sdk.RandiooPlatformSdk;
import com.randioo.randioo_server_base.config.GlobleMap;

/**
 * @Description:
 * @author zsy
 * @date 2017年9月26日 下午3:21:30
 */
public class BeanPostProcessor1 implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RandiooPlatformSdk) {
            RandiooPlatformSdk sdk = (RandiooPlatformSdk) bean;
            sdk.setActiveProjectName(GlobleMap.String(GlobleConstant.ARGS_PLATFORM_PACKAGE_NAME));
        }
        return bean;
    }

}
