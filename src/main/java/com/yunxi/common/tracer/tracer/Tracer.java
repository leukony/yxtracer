package com.yunxi.common.tracer.tracer;

import java.io.File;

import com.yunxi.common.tracer.daemon.TracerClear;
import com.yunxi.common.tracer.util.TracerSelfLog;
import com.yunxi.common.tracer.util.TracerUtils;

/**
 * Tracer基类
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: Tracer.java, v 0.1 2017年1月9日 下午2:30:54 leukony Exp $
 */
public abstract class Tracer {

    private static final String TRACE_LOGGINGROOT_KEY = "tracer.loggingroot";
    private static final String TRACE_APPENDPID_KEY   = "tracer.loggingroot.appendpid";

    public static final String TRACR_LOGGING_ROOT;

    static {
        // 获取Trace日志根目录，默认为"当前用户根目录/trace"
        String loggingRoot = System.getProperty(TRACE_LOGGINGROOT_KEY);
        if (loggingRoot == null || loggingRoot.trim().length() == 0) {
            loggingRoot = System.getProperty("user.home") + File.separator + "tracer";
        }

        // 获取Trace日志根目录是否需要追加PID，解决单机多应用部署日志目录冲突
        String appendPid = System.getProperty(TRACE_APPENDPID_KEY);
        if (Boolean.TRUE.toString().equalsIgnoreCase(appendPid)) {
            loggingRoot = loggingRoot + File.separator + TracerUtils.getPID();
        }

        // 设置Trace日志根目录
        TRACR_LOGGING_ROOT = loggingRoot;

        // 启动Trace日志清理守护线程
        try {
            TracerClear.start();
        } catch (Exception e) {
            TracerSelfLog.error("启动Trace日志清理守护线程失败", e);
        }
    }

    /**
     * 生成日志目录
     * @return
     */
    protected String genLoggingPath(String fileName) {
        return TRACR_LOGGING_ROOT + File.separator + fileName;
    }
}