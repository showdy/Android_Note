package com.medisana.plugin;

public class JiaguExt {
    //用户名
    private String username;
    //密码
    private String userPwd;
    //签名路径
    private String keyStorePath;
    //签名密码
    private String keyStorePwd;
    //签名别名
    private String keyStoreKeyAlias;
    //签名别名密码
    private String getKeyStoreKeyAliasPwd;
    private String jiaguToolPath;

    public String getJiaguToolPath() {
        return jiaguToolPath;
    }

    public void setJiaguToolPath(String jiaguToolPath) {
        this.jiaguToolPath = jiaguToolPath;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getKeyStorePath() {
        return keyStorePath;
    }

    public void setKeyStorePath(String keyStorePath) {
        this.keyStorePath = keyStorePath;
    }

    public String getKeyStorePwd() {
        return keyStorePwd;
    }

    public void setKeyStorePwd(String keyStorePwd) {
        this.keyStorePwd = keyStorePwd;
    }

    public String getKeyStoreKeyAlias() {
        return keyStoreKeyAlias;
    }

    public void setKeyStoreKeyAlias(String keyStoreKeyAlias) {
        this.keyStoreKeyAlias = keyStoreKeyAlias;
    }

    public String getGetKeyStoreKeyAliasPwd() {
        return getKeyStoreKeyAliasPwd;
    }

    public void setGetKeyStoreKeyAliasPwd(String getKeyStoreKeyAliasPwd) {
        this.getKeyStoreKeyAliasPwd = getKeyStoreKeyAliasPwd;
    }

    @Override
    public String toString() {
        return "JiaguExt{" +
                "username='" + username + '\'' +
                ", userPwd='" + userPwd + '\'' +
                ", keyStorePath='" + keyStorePath + '\'' +
                ", keyStorePwd='" + keyStorePwd + '\'' +
                ", keyStoreKeyAlias='" + keyStoreKeyAlias + '\'' +
                ", getKeyStoreKeyAliasPwd='" + getKeyStoreKeyAliasPwd + '\'' +
                ", jiaguToolPath='" + jiaguToolPath + '\'' +
                '}';
    }
}
