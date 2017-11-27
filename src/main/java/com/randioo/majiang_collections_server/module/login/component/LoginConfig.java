package com.randioo.majiang_collections_server.module.login.component;

import com.randioo.randioo_server_base.module.login.LoginInfo;

/**
 * 登录配置
 * 
 * @author wcy 2017年8月4日
 *
 */
public class LoginConfig extends LoginInfo {
    /** 头像 */
    private String headImageUrl;
    /** 昵称 */
    private String nickname;
    /** 经纬度坐标 */
    private String lantiLongi;
    /** 声音id */
    public String voiceId;

    public String getHeadImageUrl() {
        return headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return the lantiLongi
     */
    public String getLantiLongi() {
        return lantiLongi;
    }

    /**
     * @param lantiLongi
     *            the lantiLongi to set
     */
    public void setLantiLongi(String lantiLongi) {
        this.lantiLongi = lantiLongi;
    }

    /**
     * @return the voiceId
     */
    public String getVoiceId() {
        return voiceId;
    }

    /**
     * @param voiceId
     *            the voiceId to set
     */
    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LoginConfig [headImageUrl=").append(headImageUrl).append(", nickname=").append(nickname)
                .append(", lantiLongi=").append(lantiLongi).append(", voiceId=").append(voiceId).append("]");
        return builder.toString();
    }

}
