package com.yunxi.common.tracer.tracer;

import java.util.Map;

import com.yunxi.common.tracer.TracerLocal;
import com.yunxi.common.tracer.appender.TimedRollingFileAppender;
import com.yunxi.common.tracer.appender.TracerAppender;
import com.yunxi.common.tracer.constants.TracerConstants;
import com.yunxi.common.tracer.constants.TracerLogger;
import com.yunxi.common.tracer.constants.TracerType;
import com.yunxi.common.tracer.context.RpcContext;
import com.yunxi.common.tracer.context.TracerContext;
import com.yunxi.common.tracer.encoder.RpcClientEncoder;
import com.yunxi.common.tracer.encoder.RpcServerEncoder;
import com.yunxi.common.tracer.util.TraceIdGenerator;

/**
 * 调用远程服务或处理远程请求的Tracer
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: RpcTracer.java, v 0.1 2017年2月28日 下午4:56:36 leukony Exp $
 */
public class RpcTracer extends NetworkTracer<RpcContext> {

    private volatile TracerAppender rpcClientAppender;
    private volatile TracerAppender rpcServerAppender;

    public RpcTracer() {
        clientTracerType = TracerType.RPC_CLIENT.getType();
        serverTracerType = TracerType.RPC_SERVER.getType();
    }

    /** 
     * @see com.yunxi.common.tracer.tracer.NetworkTracer#getDefaultContext()
     */
    @Override
    protected RpcContext getDefaultContext() {
        RpcContext rpcContext = new RpcContext();
        rpcContext.setTraceId(TraceIdGenerator.generate());
        rpcContext.setRpcId(TracerConstants.RPC_ID_ROOT);
        return rpcContext;
    }

    /** 
     * @see com.yunxi.common.tracer.tracer.NetworkTracer#createChildContext(com.yunxi.common.tracer.context.TracerContext)
     */
    @Override
    @SuppressWarnings("rawtypes")
    protected RpcContext createChildContext(TracerContext parentCtx) {
        RpcContext rpcContext = new RpcContext();
        cloneTraceContext(parentCtx, rpcContext);
        return rpcContext;
    }

    /** 
     * @see com.yunxi.common.tracer.tracer.NetworkTracer#setContext(java.util.Map)
     */
    @Override
    public RpcContext setContext(Map<String, String> tracerContext) {
        if (tracerContext != null) {
            RpcContext rpcContext = new RpcContext();
            rpcContext.putAllTrace(tracerContext);
            TracerLocal.set(rpcContext);
            return rpcContext;
        }
        return null;
    }

    /** 
     * @see com.yunxi.common.tracer.tracer.NetworkTracer#createClientAppenderIfNecessary()
     */
    @Override
    protected void createClientAppenderIfNecessary() {
        if (rpcClientAppender == null) {
            synchronized (this) {
                if (rpcClientAppender == null) {
                    TracerLogger logger = TracerLogger.RPC_CLIENT_DIGEST;
                    rpcClientAppender = new TimedRollingFileAppender(
                        genLoggingPath(logger.getFileName()), logger.getPattern(),
                        logger.getReserve());
                    tracerWriter.addAppender(clientTracerType, rpcClientAppender,
                        new RpcClientEncoder());
                }
            }
        }
    }

    /** 
     * @see com.yunxi.common.tracer.tracer.NetworkTracer#createServerAppenderIfNecessary()
     */
    @Override
    protected void createServerAppenderIfNecessary() {
        if (rpcServerAppender == null) {
            synchronized (this) {
                if (rpcServerAppender == null) {
                    TracerLogger logger = TracerLogger.RPC_SERVER_DIGEST;
                    rpcServerAppender = new TimedRollingFileAppender(
                        genLoggingPath(logger.getFileName()), logger.getPattern(),
                        logger.getReserve());
                    tracerWriter.addAppender(serverTracerType, rpcServerAppender,
                        new RpcServerEncoder());
                }
            }
        }
    }
}