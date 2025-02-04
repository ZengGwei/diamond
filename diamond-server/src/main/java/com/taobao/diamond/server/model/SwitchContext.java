package com.taobao.diamond.server.model;

import com.taobao.diamond.domain.ConfigInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description SwitchContext
 * @Author fuyou
 * @Version 1.0
 * @Date 2025/2/4-1:57 下午-2025
 */
public class SwitchContext {
    private String brokenDBkey;
    private List<ConfigInfo> groups;

    public SwitchContext(String brokenDBkey, List<ConfigInfo> groups) {
        this.brokenDBkey = brokenDBkey;
        this.groups = groups;
    }

    public String getBrokenDBkey() {
        return this.brokenDBkey;
    }

    public void setBrokenDBkey(String brokenDBkey) {
        this.brokenDBkey = brokenDBkey;
    }

    public List<ConfigInfo> getGroups() {
        return this.groups;
    }

    public void setGroups(List<ConfigInfo> groups) {
        this.groups = groups;
    }
}
