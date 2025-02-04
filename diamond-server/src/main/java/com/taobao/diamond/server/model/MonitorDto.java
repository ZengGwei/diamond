package com.taobao.diamond.server.model;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description MonitorDto
 * @Author fuyou
 * @Version 1.0
 * @Date 2025/2/4-1:56 下午-2025
 */
public class MonitorDto {
    private long configId;
    private String groupName;
    private String dbNodes;
    private DBNodeDto master;
    private List<DBNodeDto> slaves = new ArrayList();

    public MonitorDto() {
    }

    public void addSlave(DBNodeDto dto) {
        this.slaves.add(dto);
    }

    public long getConfigId() {
        return this.configId;
    }

    public void setConfigId(long configId) {
        this.configId = configId;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDbNodes() {
        return this.dbNodes;
    }

    public void setDbNodes(String dbNodes) {
        this.dbNodes = dbNodes;
    }

    public DBNodeDto getMaster() {
        return this.master;
    }

    public void setMaster(DBNodeDto master) {
        this.master = master;
    }

    public List<DBNodeDto> getSlaves() {
        return this.slaves;
    }

    public void setSlaves(List<DBNodeDto> slaves) {
        this.slaves = slaves;
    }

}
