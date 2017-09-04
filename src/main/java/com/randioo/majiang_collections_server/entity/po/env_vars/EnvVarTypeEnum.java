package com.randioo.majiang_collections_server.entity.po.env_vars;

public enum EnvVarTypeEnum {
    ENV_VAR_TYPE_INT("int"), ENV_VAR_TYPE_STRING("string"), ENV_VAR_TYPE_DOUBLE("double"), ENV_VAR_TYPE_FLOAT("float"), ENV_VAR_TYPE_BOOLEAN(
            "boolean");

    public String name;

    EnvVarTypeEnum(String name) {
        this.name = name;
    }

    public static EnvVarTypeEnum getType(String name) {
        switch (name) {
        case "int":
            return ENV_VAR_TYPE_INT;
        case "string":
            return ENV_VAR_TYPE_STRING;
        case "double":
            return ENV_VAR_TYPE_DOUBLE;
        case "float":
            return ENV_VAR_TYPE_FLOAT;
        case "boolean":
            return ENV_VAR_TYPE_BOOLEAN;
        default:
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("(").append(name).append(")");
        return sb.toString();
    }

}
