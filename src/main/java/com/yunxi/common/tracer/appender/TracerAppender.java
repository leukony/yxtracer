package com.yunxi.common.tracer.appender;

import java.io.IOException;

/**
 * Tracer的日志打印基类
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: TracerAppender.java, v 0.1 2017年1月10日 下午3:04:43 leukony Exp $
 */
public interface TracerAppender {

    /**
     * 刷新日志
     * @throws IOException
     */
    void flush() throws IOException;

    /**
     * 追加日志
     * @param log
     * @throws IOException
     */
    void append(String log) throws IOException;

    /**
     * 清理日志
     */
    void clear();
}