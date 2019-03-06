package com.yunxi.common.tracer.constants;

import com.yunxi.common.tracer.appender.TimedRollingFileAppender;

/**
 * Tracer日志
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: TracerLogger.java, v 0.1 2017年1月12日 下午4:05:24 leukony Exp $
 */
public enum TracerLogger {

    HTTP_CLIENT_DIGEST("http_client_digest", "httpclient-digest.log",
                       TimedRollingFileAppender.HOURLY_ROLLING_PATTERN,
                       TimedRollingFileAppender.DEFAULT_RESERVE_DAY),

    HTTP_SERVER_DIGEST("http_server_digest", "httpserver-digest.log",
                       TimedRollingFileAppender.HOURLY_ROLLING_PATTERN,
                       TimedRollingFileAppender.DEFAULT_RESERVE_DAY),

    RPC_CLIENT_DIGEST("rpc_client_digest", "rpcclient-digest.log",
                      TimedRollingFileAppender.HOURLY_ROLLING_PATTERN,
                      TimedRollingFileAppender.DEFAULT_RESERVE_DAY),

    RPC_SERVER_DIGEST("rpc_server_digest", "rpcserver-digest.log",
                      TimedRollingFileAppender.HOURLY_ROLLING_PATTERN,
                      TimedRollingFileAppender.DEFAULT_RESERVE_DAY),

    ;

    /** 日志名称 */
    private String name;
    /** 日志文件 */
    private String fileName;
    /** 日志文件滚动格式 */
    private String pattern;
    /** 日志文件保留天数 */
    private int    reserve;

    TracerLogger(String name, String fileName, String pattern, int reserve) {
        this.name = name;
        this.fileName = fileName;
        this.pattern = pattern;
        this.reserve = reserve;
    }

    /**
      * Getter method for property <tt>name</tt>.
      * 
      * @return property value of name
      */
    public String getName() {
        return name;
    }

    /**
      * Getter method for property <tt>fileName</tt>.
      * 
      * @return property value of fileName
      */
    public String getFileName() {
        return fileName;
    }

    /**
      * Getter method for property <tt>pattern</tt>.
      * 
      * @return property value of pattern
      */
    public String getPattern() {
        return pattern;
    }

    /**
      * Getter method for property <tt>reserve</tt>.
      * 
      * @return property value of reserve
      */
    public int getReserve() {
        return reserve;
    }
}