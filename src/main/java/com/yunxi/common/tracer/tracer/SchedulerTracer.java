package com.yunxi.common.tracer.tracer;

import java.util.Map;

import com.yunxi.common.tracer.TracerLocal;
import com.yunxi.common.tracer.appender.TimedRollingFileAppender;
import com.yunxi.common.tracer.appender.TracerAppender;
import com.yunxi.common.tracer.constants.TracerConstants;
import com.yunxi.common.tracer.constants.TracerLogger;
import com.yunxi.common.tracer.constants.TracerType;
import com.yunxi.common.tracer.context.SchedulerContext;
import com.yunxi.common.tracer.context.TracerContext;
import com.yunxi.common.tracer.encoder.SchedulerReceiveEncoder;
import com.yunxi.common.tracer.encoder.SchedulerSendEncoder;
import com.yunxi.common.tracer.util.TraceIdGenerator;

/**
 * 定时任务调度或分发的Tracer
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: SchedulerTracer.java, v 0.1 2019年6月28日 上午11:07:10 leukony Exp $
 */
public class SchedulerTracer extends NetworkTracer<SchedulerContext> {

    private volatile TracerAppender schedulerSendAppender;
    private volatile TracerAppender schedulerReceiveAppender;

    public SchedulerTracer() {
        clientTracerType = TracerType.SCHEDULER_SEND.getType();
        serverTracerType = TracerType.SCHEDULER_RECEIVE.getType();
    }

    /** 
     * @see com.yunxi.common.tracer.tracer.NetworkTracer#getDefaultContext()
     */
    @Override
    protected SchedulerContext getDefaultContext() {
        SchedulerContext schedulerContext = new SchedulerContext();
        schedulerContext.setTraceId(TraceIdGenerator.generate());
        schedulerContext.setRpcId(TracerConstants.RPC_ID_ROOT);
        return schedulerContext;
    }

    /** 
     * @see com.yunxi.common.tracer.tracer.NetworkTracer#createChildContext(com.yunxi.common.tracer.context.TracerContext)
     */
    @Override
    @SuppressWarnings("rawtypes")
    protected SchedulerContext createChildContext(TracerContext parentCtx) {
        SchedulerContext schedulerContext = new SchedulerContext();
        cloneTraceContext(parentCtx, schedulerContext);
        return schedulerContext;
    }

    /** 
     * @see com.yunxi.common.tracer.tracer.NetworkTracer#setContext(java.util.Map)
     */
    @Override
    public SchedulerContext setContext(Map<String, String> tracerContext) {
        if (tracerContext != null) {
            SchedulerContext schedulerContext = new SchedulerContext();
            schedulerContext.putAllTrace(tracerContext);
            TracerLocal.set(schedulerContext);
            return schedulerContext;
        }
        return null;
    }

    /** 
     * @see com.yunxi.common.tracer.tracer.NetworkTracer#createClientAppenderIfNecessary()
     */
    @Override
    protected void createClientAppenderIfNecessary() {
        if (schedulerSendAppender == null) {
            synchronized (this) {
                if (schedulerSendAppender == null) {
                    TracerLogger logger = TracerLogger.SCHEDULER_SEND_DIGEST;
                    schedulerSendAppender = new TimedRollingFileAppender(
                        genLoggingPath(logger.getFileName()), logger.getPattern(),
                        logger.getReserve());
                    tracerWriter.addAppender(clientTracerType, schedulerSendAppender,
                        new SchedulerSendEncoder());
                }
            }
        }
    }

    /** 
     * @see com.yunxi.common.tracer.tracer.NetworkTracer#createServerAppenderIfNecessary()
     */
    @Override
    protected void createServerAppenderIfNecessary() {
        if (schedulerReceiveAppender == null) {
            synchronized (this) {
                if (schedulerReceiveAppender == null) {
                    TracerLogger logger = TracerLogger.SCHEDULER_RECEIVE_DIGEST;
                    schedulerReceiveAppender = new TimedRollingFileAppender(
                        genLoggingPath(logger.getFileName()), logger.getPattern(),
                        logger.getReserve());
                    tracerWriter.addAppender(serverTracerType, schedulerReceiveAppender,
                        new SchedulerReceiveEncoder());
                }
            }
        }
    }
}