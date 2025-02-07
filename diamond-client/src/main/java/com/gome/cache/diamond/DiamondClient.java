package com.gome.cache.diamond;

import com.taobao.diamond.manager.DiamondManager;
import com.taobao.diamond.manager.ManagerListener;
import com.taobao.diamond.manager.impl.DefaultDiamondManager;

import com.taobao.diamond.manager.DiamondManager;
import com.taobao.diamond.manager.ManagerListener;
import com.taobao.diamond.manager.impl.DefaultDiamondManager;
import java.util.concurrent.Executor;

/**
 * @Description DiamondClient
 * @Author fuyou
 * @Version 1.0
 * @Date 2025/2/4-3:37 下午-2025
 */
public class DiamondClient {


    public static void main(String[] str) {
        DiamondManager manager = new DefaultDiamondManager("DEFAULT_GROUP", "microants.unit", new ManagerListener() {
            public void receiveConfigInfo(String configInfo) {
                System.out.println("changed config: " + configInfo);
            }

            public Executor getExecutor() {
                return null;
            }
        }, "192.168.10.106");
        String availableConfigureInfomation = manager.getAvailableConfigureInfomation(5000L);
        System.out.println("start config: " + availableConfigureInfomation);


    }
}
