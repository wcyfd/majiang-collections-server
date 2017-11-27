package com.randioo.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.randioo.randioo_server_base.log.L;
import com.randioo.randioo_server_base.utils.HttpUtils;

public class HttpUtilsTest {
    @Test
    public void testConnect() {
        try {
            
            String value = "gameName=HttpUtilsTest&key=f4f3f65d6d804d138043fbbd1843d510&userId=1&logInfo=test";
            value = null;
            String str = HttpUtils.post("http://10.0.51.18/APPadmin/gateway/PhpServices/Log/insertGameLog.php", value);
            System.out.println(str);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testGetParam() {
        Method method = ReflectionUtils.findMethod(HttpUtils.class, "getParam", Map.class);
        ReflectionUtils.makeAccessible(method);
        Map<String,List<String>> map = new HashMap<>();
        

        map.put("key", new ArrayList<String>(1));
        map.get("key").add("f4f3f65d6d804d138043fbbd1843d510");

        List<String> gameList = new ArrayList<>(1);
        gameList.add("HttpUtilsTest");

        // 调试模式进内网
       String url = "http://10.0.51.18/APPadmin/gateway/PhpServices/Log/insertGameLog.php";

        map.put("gameName", gameList);
        map.put("userId", new ArrayList<String>(1));
        map.get("userId").add("1");
        map.put("logInfo", new ArrayList<String>(1));
        map.get("logInfo").add("test");
        
        
       Object obj = ReflectionUtils.invokeMethod(method, null, map);
        System.out.println(obj);
    }
}
