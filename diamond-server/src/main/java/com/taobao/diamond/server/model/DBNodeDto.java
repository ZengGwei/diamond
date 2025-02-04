package com.taobao.diamond.server.model;

/**
 * @Description DBNodeDto
 * @Author fuyou
 * @Version 1.0
 * @Date 2025/2/4-1:56 下午-2025
 */
public class DBNodeDto {
    private String dbKey;
    private String hostAndPort;
    private String status;
    private String rw;



    public String getHostAndPort() {
        return this.hostAndPort;
    }

    public void setHostAndPort(String hostAndPort) {
        this.hostAndPort = hostAndPort;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDbKey() {
        return this.dbKey;
    }

    public void setDbKey(String dbKey) {
        this.dbKey = dbKey;
    }

    public String getRw() {
        return this.rw;
    }

    public void setRw(String rw) {
        this.rw = rw;
    }
}
