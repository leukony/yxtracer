package com.yunxi.common.tracer.appender;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 基于滚动的Tracer的日志打印
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: RollingFileAppender.java, v 0.1 2017年1月10日 下午4:35:15 leukony Exp $
 */
public abstract class RollingFileAppender implements TracerAppender {

    /** 默认日志刷新间隔：1s */
    public static final long       DEFAULT_INTERVAL = TimeUnit.SECONDS.toMillis(1);

    /** 默认日志编码：utf-8 */
    public static final Charset    DEFAULT_CHARSET  = StandardCharsets.UTF_8;

    /** 默认缓冲大小：8KB */
    public static final int        DEFAULT_BUFFER   = 8 * 1024;

    /** 日志文件名 */
    protected final String         fileName;

    /** 日志缓冲大小 */
    protected final int            bufferSize;

    protected long                 flushTime        = 0L;

    protected File                 logFile          = null;

    protected BufferedOutputStream buffer           = null;

    protected final AtomicBoolean  isRolling        = new AtomicBoolean(false);

    public RollingFileAppender(String fileName) {
        this(fileName, DEFAULT_BUFFER);
    }

    public RollingFileAppender(String fileName, int bufferSize) {
        this.fileName = fileName;
        this.bufferSize = bufferSize;
        this.setFile();
    }

    protected void setFile() {
        try {
            this.logFile = new File(fileName);
            if (!logFile.exists()) {
                File parentFile = logFile.getParentFile();
                if (!parentFile.exists() && !parentFile.mkdirs()) {
                    System.out.println("[Tracer] [创建文件目录失败：" + parentFile.getAbsolutePath() + "]");
                    return;
                }
                if (!logFile.createNewFile()) {
                    System.out.println("[Tracer] [创建日志文件失败：" + logFile.getAbsolutePath() + "]");
                    return;
                }
            }
            if (!logFile.isFile() || !logFile.canWrite()) {
                System.out.println("[Tracer] [日志文件异常：" + logFile.getAbsolutePath() + "]");
                return;
            }
            OutputStream os = new FileOutputStream(logFile, true);
            buffer = new BufferedOutputStream(os, bufferSize);
        } catch (Throwable e) {
            // ignore
            e.printStackTrace();
        }
    }

    /** 
     * @see com.yunxi.common.tracer.appender.TracerAppender#flush()
     */
    @Override
    public void flush() throws IOException {
        if (buffer != null) {
            buffer.flush();
        }
    }

    /** 
     * @see com.yunxi.common.tracer.appender.TracerAppender#append(java.lang.String)
     */
    @Override
    public void append(String log) throws IOException {
        if (buffer != null) {

            waitRollOver();

            if (checkRollOver() && isRolling.compareAndSet(false, true)) {
                try {
                    rollOver();
                    long current = System.currentTimeMillis();
                    flushTime = current + DEFAULT_INTERVAL;
                } finally {
                    isRolling.set(false);
                }
            } else {
                // 达到指定刷新时间，自动刷新一次
                long current = System.currentTimeMillis();
                if (current >= flushTime) {
                    buffer.flush();
                    flushTime = current + DEFAULT_INTERVAL;
                }
            }

            buffer.write(log.getBytes(DEFAULT_CHARSET));
        }
    }

    /**
     * 等待日志滚动完成
     */
    private void waitRollOver() {
        while (isRolling.get()) {
            try {
                Thread.sleep(1L);
            } catch (Exception e) {
                // ignore
                e.printStackTrace();
            }
        }
    }

    /**
     * 检查是否进行日志滚动
     * @return
     */
    protected abstract boolean checkRollOver();

    /**
     * 进行日志滚动
     */
    protected abstract void rollOver();
}