package com.taobao.diamond.server.model;

import java.util.Date;

/**
 * @Description Monitor
 * @Author fuyou
 * @Version 1.0
 * @Date 2025/2/4-2:00 下午-2025
 */
public class Monitor {
    private long id;
    private long configId;
    private Date createTime;
    private Date updateTime;

    public Monitor() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getConfigId() {
        return this.configId;
    }

    public void setConfigId(long configId) {
        this.configId = configId;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
