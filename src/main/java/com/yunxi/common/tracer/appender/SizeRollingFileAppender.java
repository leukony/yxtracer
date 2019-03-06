package com.yunxi.common.tracer.appender;

/**
 * 基于文件大小滚动的Tracer的日志打印
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: SizeRollingFileAppender.java, v 0.1 2017年2月15日 下午2:59:09 leukony Exp $
 */
public class SizeRollingFileAppender extends RollingFileAppender {

    /**
     * @param fileName
     */
    public SizeRollingFileAppender(String fileName) {
        super(fileName);
    }

    /** 
     * @see com.yunxi.common.tracer.appender.TracerAppender#clear()
     */
    @Override
    public void clear() {
    }

    /** 
     * @see com.yunxi.common.tracer.appender.RollingFileAppender#checkRollOver()
     */
    @Override
    protected boolean checkRollOver() {
        return false;
    }

    /** 
     * @see com.yunxi.common.tracer.appender.RollingFileAppender#rollOver()
     */
    @Override
    protected void rollOver() {
    }
}