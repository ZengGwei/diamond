package com.taobao.diamond.server.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description ThreadCleanupListener
 * @Author fuyou
 * @Version 1.0
 * @Date 2025/2/6-6:16 下午-2025
 */
@WebListener
public class ThreadCleanupListener implements ServletContextListener {
    private ExecutorService executorService;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        executorService = Executors.newSingleThreadExecutor();
        // 启动线程的逻辑
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
}
