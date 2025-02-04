package com.taobao.diamond.server.service;

import com.taobao.diamond.domain.ConfigInfo;
import com.taobao.diamond.server.model.DBNodeDto;
import com.taobao.diamond.server.model.MonitorDto;
import com.taobao.diamond.server.model.SwitchContext;
import com.taobao.diamond.server.utils.Utils;
import com.taobao.diamond.server.utils.Constants.Status;
import com.taobao.diamond.server.zk.SwitchZKClient;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description MysqlHAService
 * @Author fuyou
 * @Version 1.0
 * @Date 2025/2/4-1:54 下午-2025
 */
@Service
public class MysqlHAService {
    private static final Log LOG = LogFactory.getLog(MysqlHAService.class);
    @Autowired
    private SwitchZKClient zkClient;
    @Autowired
    private PersistService persistService;
    @Autowired
    private ConfigService configService;

    public MysqlHAService() {
    }

    public void handlerMysqlStatusChangeEvent(String hostAndPort, Status status) {
        switch(status) {
            case Alive:
                LOG.info(String.format("数据库%s处于状态%s不做任何操作", hostAndPort, status));
                break;
            case Dead:
            case Disconnected:
                LOG.info(String.format("数据库%s处于状态%s", hostAndPort, status));
                this.disasterRecovery(hostAndPort);
        }

    }

    private void disasterRecovery(String hostAndPort) {
        String[] arr = StringUtils.split(hostAndPort, ":");
        List<ConfigInfo> atoms = this.persistService.getAtomConfigs(arr[0], arr[1]);
        if (atoms != null && !atoms.isEmpty()) {
            List<SwitchContext> needSwitch = new ArrayList();
            Iterator var5 = atoms.iterator();

            while(var5.hasNext()) {
                ConfigInfo atom = (ConfigInfo)var5.next();
                List<ConfigInfo> temp = this.persistService.getGroupConfigs(atom.getGroup(), Utils.getDbKey(atom.getDataId()));
                if (temp != null && !temp.isEmpty()) {
                    SwitchContext context = new SwitchContext(Utils.getDbKey(atom.getDataId()), temp);
                    needSwitch.add(context);
                }
            }

            if (needSwitch.isEmpty()) {
                LOG.info("在diamond中没有找到要切换的group");
            } else {
                var5 = needSwitch.iterator();

                while(var5.hasNext()) {
                    SwitchContext context = (SwitchContext)var5.next();
                    this.switchDB(context);
                }

            }
        } else {
            LOG.info(String.format("没有找到%s对应的atom配置", hostAndPort));
        }
    }

    private void switchDB(SwitchContext context) {
        List<ConfigInfo> groups = context.getGroups();
        Iterator var3 = groups.iterator();

        while(true) {
            while(var3.hasNext()) {
                ConfigInfo group = (ConfigInfo)var3.next();
                String content = StringUtils.trim(group.getContent());
                LOG.info(String.format("group[%s]配置[%s]坏库[%s]", group.getDataId(), content, context.getBrokenDBkey()));
                if (!this.isBrokenMaster(content, context.getBrokenDBkey())) {
                    LOG.info(String.format("[%s]在group[%s]配置[%s]中不是主库不进行切换", context.getBrokenDBkey(), group.getDataId(), content));
                } else {
                    String[] dbArr = StringUtils.split(content, ",");
                    DBNodeDto master = new DBNodeDto();
                    List<DBNodeDto> bakups = new ArrayList();
                    List<DBNodeDto> slaves = new ArrayList();
                    String[] var10 = dbArr;
                    int i = dbArr.length;

                    for(int var12 = 0; var12 < i; ++var12) {
                        String dbRW = var10[var12];
                        String[] strArr = StringUtils.split(dbRW, ":");
                        if (StringUtils.equals(strArr[0], context.getBrokenDBkey())) {
                            master.setDbKey(strArr[0]);
                            master.setRw(strArr[1]);
                        } else {
                            DBNodeDto temp;
                            if (StringUtils.indexOfIgnoreCase(strArr[1], "b") != -1) {
                                temp = new DBNodeDto();
                                temp.setDbKey(strArr[0]);
                                temp.setRw(strArr[1]);
                                bakups.add(temp);
                            } else {
                                temp = new DBNodeDto();
                                temp.setDbKey(strArr[0]);
                                temp.setRw(strArr[1]);
                                slaves.add(temp);
                            }
                        }
                    }

                    if (bakups.isEmpty()) {
                        LOG.info(String.format("group[%s]配置[%s]没有备库不进行切换", group.getDataId(), group.getContent()));
                    } else {
                        StringBuilder builder = new StringBuilder();
                        builder.append(((DBNodeDto)bakups.get(0)).getDbKey()).append(":").append(master.getRw());

                        for(i = 0; i < slaves.size(); ++i) {
                            builder.append(",");
                            builder.append(((DBNodeDto)slaves.get(i)).getDbKey()).append(":").append(((DBNodeDto)slaves.get(i)).getRw());
                        }

                        for(i = 1; i < bakups.size(); ++i) {
                            builder.append(",");
                            builder.append(((DBNodeDto)bakups.get(i)).getDbKey()).append(":").append(((DBNodeDto)bakups.get(i)).getRw());
                        }

                        builder.append(",");
                        builder.append(master.getDbKey()).append(":").append(((DBNodeDto)bakups.get(0)).getRw());
                        this.configService.updateConfigInfo(group.getDataId(), group.getGroup(), builder.toString(), String.format("配置%s切换为%s", group.getContent(), builder.toString()));
                        LOG.info(String.format("切换后group[%s]的配置为[%s]", group.getDataId(), builder.toString()));
                    }
                }
            }

            return;
        }
    }

    public boolean isBrokenMaster(String content, String brokenDBKey) {
        String[] dbArr = StringUtils.split(content, ",");
        String[] var4 = dbArr;
        int var5 = dbArr.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String dbRW = var4[var6];
            String[] strArr = StringUtils.split(dbRW, ":");
            if (StringUtils.equals(strArr[0], brokenDBKey) && StringUtils.indexOfIgnoreCase(strArr[1], "b") == -1 && StringUtils.indexOfIgnoreCase(strArr[1], "w") != -1) {
                return true;
            }
        }

        return false;
    }

    public List<MonitorDto> findMonitorList() {
        List<MonitorDto> monitorList = new ArrayList();
        List<ConfigInfo> groups = this.persistService.getGroupConfigs();
        Iterator var3 = groups.iterator();

        while(var3.hasNext()) {
            ConfigInfo configInfo = (ConfigInfo)var3.next();
            MonitorDto dto = new MonitorDto();
            dto.setConfigId(configInfo.getId());
            dto.setGroupName(Utils.getGroupName(configInfo.getDataId()));
            dto.setDbNodes(configInfo.getContent());
            monitorList.add(dto);
        }

        return monitorList;
    }

    public MonitorDto detailOfMonitor(long configId) {
        ConfigInfo configInfo = this.persistService.findConfigInfo(configId);
        if (configInfo == null) {
            return null;
        } else {
            String content = configInfo.getContent();
            if (StringUtils.isBlank(content)) {
                return null;
            } else {
                MonitorDto dto = new MonitorDto();
                dto.setGroupName(Utils.getGroupName(configInfo.getDataId()));
                content = StringUtils.trim(content);
                String[] atomStrArr = StringUtils.split(content, ",");
                String[] var7 = atomStrArr;
                int var8 = atomStrArr.length;

                for(int var9 = 0; var9 < var8; ++var9) {
                    String atomStr = var7[var9];
                    String[] atom = StringUtils.split(StringUtils.trim(atomStr), ":");
                    if (StringUtils.indexOfIgnoreCase(atom[1], "b") != -1) {
                        dto.addSlave(this.createDBNodeDto(atom[0], configInfo.getGroup(), atom[1]));
                    } else {
                        dto.setMaster(this.createDBNodeDto(atom[0], configInfo.getGroup(), atom[1]));
                    }
                }

                return dto;
            }
        }
    }

    private DBNodeDto createDBNodeDto(String dbKey, String groupId, String rw) {
        DBNodeDto dto = new DBNodeDto();
        dto.setDbKey(dbKey);
        ConfigInfo configInfo = this.persistService.findConfigInfo(Utils.getAtomDataId(dbKey), groupId);
        String content = StringUtils.trim(configInfo.getContent());
        String ip = Utils.getHost(content);
        String port = Utils.getPort(content);
        String hostAndPort = ip + ":" + port;
        dto.setHostAndPort(hostAndPort);
        dto.setStatus(this.zkClient.getData(hostAndPort));
        dto.setRw(rw);
        return dto;
    }
}
