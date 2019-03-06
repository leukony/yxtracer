package com.yunxi.common.tracer.tracer;

import java.util.Map;

import com.yunxi.common.tracer.TracerLocal;
import com.yunxi.common.tracer.appender.TimedRollingFileAppender;
import com.yunxi.common.tracer.appender.TracerAppender;
import com.yunxi.common.tracer.constants.TracerConstants;
import com.yunxi.common.tracer.constants.TracerLogger;
import com.yunxi.common.tracer.constants.TracerType;
import com.yunxi.common.tracer.context.HttpContext;
import com.yunxi.common.tracer.context.TracerContext;
import com.yunxi.common.tracer.encoder.HttpClientEncoder;
import com.yunxi.common.tracer.encoder.HttpServerEncoder;
import com.yunxi.common.tracer.util.TraceIdGenerator;

/**
 * 调用WEB服务或处理WEB请求的Tracer
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: HttpServiceTracer.java, v 0.1 2017年1月9日 下午3:31:30 leukony Exp $
 */
public class HttpTracer extends NetworkTracer<HttpContext> {

    private volatile TracerAppender httpClientAppender;
    private volatile TracerAppender httpServerAppender;

    public HttpTracer() {
        clientTracerType = TracerType.HTTP_CLIENT.getType();
        serverTracerType = TracerType.HTTP_SERVER.getType();
    }

    /** 
     * @see com.yunxi.common.tracer.tracer.NetworkTracer#getDefaultContext()
     */
    @Override
    protected HttpContext getDefaultContext() {
        HttpContext httpContext = new HttpContext();
        httpContext.setTraceId(TraceIdGenerator.generate());
        httpContext.setRpcId(TracerConstants.RPC_ID_ROOT);
        return httpContext;
    }

    /** 
     * @see com.yunxi.common.tracer.tracer.NetworkTracer#createChildContext(com.yunxi.common.tracer.context.TracerContext)
     */
    @Override
    @SuppressWarnings("rawtypes")
    protected HttpContext createChildContext(TracerContext parentCtx) {
        HttpContext httpContext = new HttpContext();
        cloneTraceContext(parentCtx, httpContext);
        return httpContext;
    }

    /** 
     * @see com.yunxi.common.tracer.tracer.NetworkTracer#setContext(java.util.Map)
     */
    @Override
    public HttpContext setContext(Map<String, String> tracerContext) {
        if (tracerContext != null) {
            HttpContext httpContext = new HttpContext();
            httpContext.putAllTrace(tracerContext);
            TracerLocal.set(httpContext);
            return httpContext;
        }
        return null;
    }

    /** 
     * @see com.yunxi.common.tracer.tracer.NetworkTracer#createClientAppenderIfNecessary()
     */
    @Override
    protected void createClientAppenderIfNecessary() {
        if (httpClientAppender == null) {
            synchronized (this) {
                if (httpClientAppender == null) {
                    TracerLogger logger = TracerLogger.HTTP_CLIENT_DIGEST;
                    httpClientAppender = new TimedRollingFileAppender(
                        genLoggingPath(logger.getFileName()), logger.getPattern(),
                        logger.getReserve());
                    tracerWriter.addAppender(clientTracerType, httpClientAppender,
                        new HttpClientEncoder());
                }
            }
        }
    }

    /** 
     * @see com.yunxi.common.tracer.tracer.NetworkTracer#createServerAppenderIfNecessary()
     */
    @Override
    protected void createServerAppenderIfNecessary() {
        if (httpServerAppender == null) {
            synchronized (this) {
                if (httpServerAppender == null) {
                    TracerLogger logger = TracerLogger.HTTP_SERVER_DIGEST;
                    httpServerAppender = new TimedRollingFileAppender(
                        genLoggingPath(logger.getFileName()), logger.getPattern(),
                        logger.getReserve());
                    tracerWriter.addAppender(serverTracerType, httpServerAppender,
                        new HttpServerEncoder());
                }
            }
        }
    }
}