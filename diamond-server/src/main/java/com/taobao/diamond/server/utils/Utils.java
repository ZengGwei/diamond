package com.taobao.diamond.server.utils;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description Utils
 * @Author fuyou
 * @Version 1.0
 * @Date 2025/2/4-1:47 下午-2025
 */
public class Utils {

    private static final Pattern ipAndPort = Pattern.compile("((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?):\\d+");
    private static final Pattern groupName = Pattern.compile("com.taobao.tddl.jdbc.group_V2.4.1_(.+)");
    private static final Pattern ip = Pattern.compile("((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)");
    private static final Pattern port = Pattern.compile("(port=\\d+)");
    private static final Pattern dbKey = Pattern.compile("com.taobao.tddl.atom.global.(.+)");



    public static String getHostAndPort(String jdbcUrl) {
        Matcher matcher = ipAndPort.matcher(jdbcUrl);
        matcher.find();
        return matcher.group();
    }

    public static String getGroupName(String dataId) {
        Matcher matcher = groupName.matcher(dataId);
        matcher.find();
        return matcher.group(1);
    }

    public static String getAtomDataId(String dbKey) {
        return String.format("com.taobao.tddl.atom.global.%s", dbKey);
    }

    public static String getHost(String content) {
        Matcher matcher = ip.matcher(content);
        matcher.find();
        return matcher.group();
    }

    public static String getPort(String content) {
        Matcher matcher = port.matcher(content);
        matcher.find();
        String portStr = matcher.group();
        String[] strArr = StringUtils.split(portStr, "=");
        return strArr[1];
    }

    public static String getDbKey(String atomDataId) {
        Matcher matcher = dbKey.matcher(atomDataId);
        matcher.find();
        return matcher.group(1);
    }
}
