package com.randioo.majiang_collections_server.entity.po.env_vars;

import java.util.HashMap;
import java.util.Map;

/**
 * 环境变量集合
 * 
 * @author wcy 2017年9月4日
 *
 */
public class EnvVars {
    protected Map<String, Object> paramMap = new HashMap<>();

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public int Int(String key) {
        Object result = paramMap.get(key);
        if (result == null)
            return 0;
        return (int) result;
    }

    public String String(String key) {
        Object result = paramMap.get(key);
        if (result == null)
            return null;
        return (String) result;
    }

    public boolean Boolean(String key) {
        Object result = paramMap.get(key);
        if (result == null || !(Boolean) result)
            return false;
        return true;
    }

    public void putParam(EnvVar parameter) {
        String key = parameter.key;
        String value = parameter.value;

        switch (parameter.type) {
        case ENV_VAR_TYPE_BOOLEAN:
            paramMap.put(key, Boolean.valueOf(value));
            break;
        case ENV_VAR_TYPE_DOUBLE:
            paramMap.put(key, Double.parseDouble(value));
            break;
        case ENV_VAR_TYPE_FLOAT:
            paramMap.put(key, Float.parseFloat(value));
            break;
        case ENV_VAR_TYPE_INT:
            paramMap.put(key, Integer.parseInt(value));
            break;
        case ENV_VAR_TYPE_STRING:
            paramMap.put(key, value);
            break;
        default:
            break;
        }

    }

    public void putParam(String key, Object value) {
        paramMap.put(key, value);
    }

    public void putParam(Map<String, Object> map) {
        for (Map.Entry<String, Object> entrySet : map.entrySet()) {
            String key = entrySet.getKey();
            Object value = entrySet.getValue();
            paramMap.put(key, value);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EnvVars [paramMap=").append(paramMap).append("]");
        return builder.toString();
    }

}
