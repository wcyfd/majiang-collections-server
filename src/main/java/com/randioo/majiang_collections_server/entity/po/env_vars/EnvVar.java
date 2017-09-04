package com.randioo.majiang_collections_server.entity.po.env_vars;

/**
 * 环境变量
 * 
 * @author wcy 2017年9月4日
 *
 */
public class EnvVar {
    /** 键 */
    protected String key;
    /** 值 */
    protected String value;
    /** 类型 */
    protected EnvVarTypeEnum type;

    public void setKey(String key) {
        this.key = key;
    }

    public void setType(EnvVarTypeEnum type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EnvVarParameter [key=").append(key).append(", value=").append(value).append(", type=")
                .append(type).append("]");
        return builder.toString();
    }

}
