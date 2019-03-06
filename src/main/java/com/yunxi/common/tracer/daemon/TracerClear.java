package com.yunxi.common.tracer.daemon;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.yunxi.common.tracer.appender.TracerAppender;
import com.yunxi.common.tracer.util.TracerSelfLog;

/**
 * 主要用于清理日志
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: TracerClear.java, v 0.1 2017年1月11日 下午3:03:24 leukony Exp $
 */
public class TracerClear implements Runnable {

    private static final long           ONE_HOUR  = 60 * 60;
    private static long                 interval  = ONE_HOUR;
    private static AtomicBoolean        isRunning = new AtomicBoolean(false);
    private static List<TracerAppender> watched   = new CopyOnWriteArrayList<TracerAppender>();

    public void run() {
        while (true) {
            try {
                for (TracerAppender appender : watched) {
                    appender.clear();
                }
                TimeUnit.SECONDS.sleep(interval);
            } catch (Throwable e) {
                TracerSelfLog.error("Trace日志清理守护线程执行异常", e);
            }
        }
    }

    /**
     * 注册Appender
     * @param appender
     */
    public static void watch(TracerAppender appender) {
        watched.add(appender);
    }

    /**
     * 调整扫描周期
     * @param interval
     */
    public static void setScanInterval(long interval) {
        TracerClear.interval = interval;
    }

    /**
     * 启动清理日志
     */
    public static void start() {
        if (isRunning.compareAndSet(false, true)) {
            Thread tracerDelete = new Thread(new TracerClear());
            tracerDelete.setName("Tracer-Delete");
            tracerDelete.setDaemon(true);
            tracerDelete.start();
        }
    }
}