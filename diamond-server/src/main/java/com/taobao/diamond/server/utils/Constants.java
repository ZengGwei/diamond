package com.taobao.diamond.server.utils;

/**
 * @Description Contants
 * @Author fuyou
 * @Version 1.0
 * @Date 2025/2/4-1:38 下午-2025
 */
public class Constants {
    public static final String CHARSET = "UTF-8";
    public static final String BASE_PATH = "/mysql-ha";
    public static final String MONITOR_PATH = "/mysql-ha/monitor";
    public static final String MONITOR_NODE_PATH = "/mysql-ha/tmpnode";
    public static final String MONITOR_COUNTER = "/mysql-ha/counter";
    public static final String MYSQL_JDBC_URL = "mysql.jdbc.url";
    public static final String MYSQL_JDBC_USERNAME = "mysql.jdbc.username";
    public static final String MYSQL_JDBC_PASSWORD = "mysql.jdbc.password";
    public static final String ZK_SERVERS = "zkServers";
    public static final String MYSQL_STATUS = "status";
    public static final String MYSQL_DESCRIPTION = "description";



    public static enum Status {
        Alive,
        Dead,
        Disconnected;


    }

}
