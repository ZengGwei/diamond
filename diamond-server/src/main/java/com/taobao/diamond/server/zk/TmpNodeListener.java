package com.taobao.diamond.server.zk;

import com.taobao.diamond.server.service.MysqlHAService;
import com.taobao.diamond.server.utils.Utils;
import com.taobao.diamond.server.utils.Constants.Status;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * @Description TmpNodeListener
 * @Author fuyou
 * @Version 1.0
 * @Date 2025/2/4-1:53 下午-2025
 */
@Component
public class TmpNodeListener implements PathChildrenCacheListener {
    private static final Log LOG = LogFactory.getLog(TmpNodeListener.class);
    @Autowired
    private SwitchZKClient zkService;
    @Autowired
    private MysqlHAService haService;



    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
        Type eventType = event.getType();
        ChildData childData = event.getData();
        String path = childData.getPath();
        if (!this.zkService.getRight()) {
            LOG.info(String.format("临时目录%s触发%s事件没有获得执行权", path, eventType));
        } else {
            switch(eventType) {
                case CHILD_REMOVED:
                    LOG.info(String.format("临时目录%s触发事件%s, 数据库状态%s", path, eventType, Status.Disconnected));
                    this.haService.handlerMysqlStatusChangeEvent(Utils.getHostAndPort(path), Status.Disconnected);
                    break;
                case CHILD_UPDATED:
                    byte[] dataArr = (byte[])client.getData().forPath(path);
                    String data = new String(dataArr, "UTF-8");
                    LOG.info(String.format("临时目录%s触发事件%s, 数据库状态%s", path, eventType, data));
                    this.haService.handlerMysqlStatusChangeEvent(Utils.getHostAndPort(path), Status.valueOf(data));
                    break;
                default:
                    LOG.info(String.format("忽略临时目录%s事件%s", path, eventType));
            }

        }
    }

}
