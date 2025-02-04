package com.taobao.diamond.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @Description PropertiesUtils
 * @Author fuyou
 * @Version 1.0
 * @Date 2025/2/4-4:07 下午-2025
 */
public class PropertiesUtils {

    private static final Log logger = LogFactory.getLog(PropertiesUtils.class);

    public static void setValueFromConf(Object obj, String relativePath) throws IOException {
        InputStream is = null;
        BufferedInputStream in = null;

        try {
            is = PropertiesUtils.class.getResourceAsStream("/" + relativePath);
            if (is != null) {
                in = new BufferedInputStream(is);
                Properties properties = new Properties();
                properties.load(in);
                properties2Object(properties, obj);
                return;
            }

            logger.warn("file not exsits,/" + relativePath);
        } catch (Exception var8) {
            logger.error(" PropertiesUtils.setValueFromConf(..) error", var8);
            return;
        } finally {
            if (in != null) {
                in.close();
            }

            if (is != null) {
                in.close();
            }

        }

    }

    public static void properties2Object(Properties p, Object object) {
        Method[] methods = object.getClass().getMethods();
        Method[] var3 = methods;
        int var4 = methods.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Method method = var3[var5];
            String mn = method.getName();
            if (mn.startsWith("set")) {
                try {
                    String tmp = mn.substring(4);
                    String first = mn.substring(3, 4);
                    String key = first.toLowerCase() + tmp;
                    String property = p.getProperty(key);
                    if (property != null) {
                        Class<?>[] pt = method.getParameterTypes();
                        if (pt != null && pt.length > 0) {
                            String cn = pt[0].getSimpleName();
                            Object arg = null;
                            if (cn.equals("int")) {
                                arg = Integer.parseInt(property);
                            } else if (cn.equals("long")) {
                                arg = Long.parseLong(property);
                            } else if (cn.equals("double")) {
                                arg = Double.parseDouble(property);
                            } else if (cn.equals("boolean")) {
                                arg = Boolean.parseBoolean(property);
                            } else {
                                if (!cn.equals("String")) {
                                    continue;
                                }

                                arg = property;
                            }

                            method.invoke(object, arg);
                        }
                    }
                } catch (Throwable var15) {
                }
            }
        }

    }
}
