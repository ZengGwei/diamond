package com.taobao.diamond.server.zk;

import com.taobao.diamond.server.utils.Constants.Status;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description SwitchZKClient
 * @Author fuyou
 * @Version 1.0
 * @Date 2025/2/4-1:48 下午-2025
 */
@Component
public class SwitchZKClient {

    private static final Log LOG = LogFactory.getLog(SwitchZKClient.class);
    private CuratorFramework curator;
    private PathChildrenCache pathCache;
    private DistributedAtomicInteger counter;
    @Value("${zkServers}")
    private String zkServers;
    @Autowired
    private PathChildrenCacheListener listener;

    public SwitchZKClient() {
    }

    @PostConstruct
    public void init() {
        try {
            if (StringUtils.isBlank(this.zkServers)) {
                return;
            }

            LOG.info("初始化zk客户端并监控节点/mysql-ha/tmpnode变化");
            this.curator = CuratorFrameworkFactory.builder().connectString(this.zkServers).retryPolicy(new RetryNTimes(10, 1000)).defaultData("".getBytes()).build();
            this.pathCache = new PathChildrenCache(this.curator, "/mysql-ha/tmpnode", false);
            this.pathCache.getListenable().addListener(this.listener);
            this.counter = new DistributedAtomicInteger(this.curator, "/mysql-ha/counter", new RetryNTimes(10, 1000));
            this.curator.start();
            this.pathCache.start();
            this.counter.trySet(0);
            LOG.info("初始化zk客户端结束");
        } catch (Exception var2) {
            this.destroy();
            LOG.error("启动zk客户端失败", var2);
        }

    }

    public String getData(String hostAndPort) {
        String status = Status.Disconnected.name();

        try {
            String path = "/mysql-ha/tmpnode/" + hostAndPort;
            Stat stat = (Stat)this.curator.checkExists().forPath(path);
            if (stat != null) {
                byte[] data = (byte[])this.curator.getData().forPath(path);
                status = new String(data, "UTF-8");
            }
        } catch (Exception var6) {
            LOG.error("获取状态异常", var6);
        }

        return status;
    }

    public boolean getRight() {
        try {
            int preVal = (Integer)this.counter.get().preValue();
            int postVal = (Integer)this.counter.increment().postValue();
            int offset = postVal - preVal;
            return offset == 1;
        } catch (Exception var4) {
            LOG.error("zookeeper计算原子自增异常", var4);
            return true;
        }
    }

    @PreDestroy
    public void destroy() {
        CloseableUtils.closeQuietly(this.pathCache);
        CloseableUtils.closeQuietly(this.curator);
    }
}
