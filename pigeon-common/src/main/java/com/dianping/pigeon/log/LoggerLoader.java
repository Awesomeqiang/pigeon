package com.dianping.pigeon.log;

import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.LoggerContext;

import com.dianping.pigeon.util.AppUtils;

public class LoggerLoader {

    private static LoggerContext context = null;
    private static final String LOG_ROOT_KEY = "pigeon.log.dir";
    private static final String LOG_ROOT_DEFAULT = "/data/applogs/pigeon";

    public static String LOG_ROOT;

    static {
        init();
    }

    private LoggerLoader() {
    }

    public static synchronized void init() {
        if (context == null) {
            if (StringUtils.isBlank(System.getProperty(LOG_ROOT_KEY))) {
                System.setProperty(LOG_ROOT_KEY, LOG_ROOT_DEFAULT);
                LOG_ROOT = LOG_ROOT_DEFAULT;
            }
            String appName = AppUtils.getAppName();
            System.setProperty("app.name", appName);

            URL url = LoggerLoader.class.getResource("log4j2-pigeon.xml");
            LoggerContext ctx;
            if (url == null) {
                ctx = LogManager.getContext(false);
            } else {
                try {
                    ctx = new org.apache.logging.log4j.core.LoggerContext("Pigeon", null, url.toURI());
                    ((org.apache.logging.log4j.core.LoggerContext) ctx).start();
                } catch (Exception e) {
                    System.err.println("failed to initialize log4j2...");
                    e.printStackTrace(System.err);
                    ctx = LogManager.getContext(false);
                }
            }
            context = ctx;
        }
    }

    public static Logger getLogger(Class<?> className) {
        return getLogger(className.getName());
    }

    public static Logger getLogger(String name) {
        if (context == null) {
            init();
        }
        return new SimpleLogger(context.getLogger(name));
    }

    public static LoggerContext getLoggerContext() {
        return context;
    }
}
