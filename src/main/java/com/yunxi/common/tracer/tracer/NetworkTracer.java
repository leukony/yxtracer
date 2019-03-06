package com.yunxi.common.tracer.tracer;

import java.util.Map;

import com.yunxi.common.tracer.TracerLocal;
import com.yunxi.common.tracer.context.TracerContext;
import com.yunxi.common.tracer.daemon.TracerWriter;
import com.yunxi.common.tracer.util.TracerSelfLog;

/**
 * 基于网络调用的Tracer的基类
 * <p>如：远程调用、远端缓存、消息、DB等</p>
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: NetworkTracer.java, v 0.1 2017年1月9日 下午2:55:17 leukony Exp $
 */
@SuppressWarnings("rawtypes")
public abstract class NetworkTracer<T extends TracerContext> extends Tracer {

    /** 客户端Tracer日志类型 */
    protected char                clientTracerType;
    /** 服务端Tracer日志类型 */
    protected char                serverTracerType;

    /** 异步日志打印，所有的中间件公用一个 TracerWriter来打印日志 */
    protected static TracerWriter tracerWriter = new TracerWriter(1024);
    static {
        tracerWriter.start("NetworkAppender");
    }

    /**
     * 开始网络调用
     * @return
     */
    public T startInvoke() {
        try {
            T child = null;

            TracerContext ctx = TracerLocal.get();
            if (ctx == null) {
                child = getDefaultContext();
            } else {
                child = createChildContext(ctx);
            }

            child.setStartTime(System.currentTimeMillis());
            child.setThreadName(Thread.currentThread().getName());

            TracerLocal.set(child);

            return child;
        } catch (Throwable t) {
            TracerSelfLog.errorWithTraceId("开始网络调用异常", t);
            return null;
        }
    }

    /**
     * 网络调用完毕
     * @param resultCode
     * @param expectedType
     */
    public void finishInvoke(String resultCode, Class<? extends TracerContext> expectedType) {
        try {
            createClientAppenderIfNecessary();

            TracerContext ctx = TracerLocal.get();
            if (ctx != null) {
                ctx.setResultCode(resultCode);
                ctx.setTracerType(clientTracerType);
                ctx.setFinishTime(System.currentTimeMillis());

                tracerWriter.append(ctx);

                if (ctx.getClass().equals(expectedType)) {
                    TracerLocal.set(ctx.getParentContext());
                }
            }
        } catch (Throwable t) {
            TracerSelfLog.errorWithTraceId("网络调用完毕异常", t);
        }
    }

    /**
     * 开始处理网络调用
     * @return
     */
    @SuppressWarnings("unchecked")
    public T startProcess() {
        try {
            TracerContext ctx = TracerLocal.get();
            if (ctx == null) {
                ctx = getDefaultContext();
                TracerLocal.set(ctx);
            }

            ctx.setStartTime(System.currentTimeMillis());
            ctx.setThreadName(Thread.currentThread().getName());

            return (T) ctx;
        } catch (Throwable t) {
            TracerSelfLog.errorWithTraceId("开始处理网络调用异常", t);
            return null;
        }
    }

    /**
     * 网络调用处理完毕
     * @param resultCode
     */
    public void finishProcess(String resultCode) {
        try {
            createServerAppenderIfNecessary();

            TracerContext ctx = TracerLocal.get();
            if (ctx != null) {
                ctx.setResultCode(resultCode);
                ctx.setTracerType(serverTracerType);
                ctx.setFinishTime(System.currentTimeMillis());

                tracerWriter.append(ctx);
            }
        } catch (Throwable t) {
            TracerSelfLog.errorWithTraceId("网络调用处理完毕异常", t);
        } finally {
            clear();
        }
    }

    /**
     * 清理日志上下文
     */
    public void clear() {
        TracerLocal.clear();
    }

    /**
     * 复制透传属性
     * @param parent
     * @param child
     */
    public void cloneTraceContext(TracerContext parent, T child) {
        // 公共属性
        child.setTraceId(parent.getTraceId());
        child.setRpcId(parent.nextChildRpcId());
        child.setParentContext(parent.getThisAsParent());
        // 系统属性
        // 业务属性
    }

    /**
     * 创建默认的 Tracer日志上下文    
     * @return Tracer日志上下文
     */
    protected abstract T getDefaultContext();

    /**
     * 创建子Tracer日志上下文
     * @param parentCtx 父Tracer日志上下文
     * @return 根据父Tracer日志上下文创建子Tracer日志上下文
     */
    protected abstract T createChildContext(TracerContext parentCtx);

    /**
     * 根据Map创建 TracerContext，并且设置到 ThreadLocal中去
     * @param tracerContext
     * @return
     */
    public abstract T setContext(Map<String, String> tracerContext);

    /**
     * 创建网络调用的Appender
     */
    protected abstract void createClientAppenderIfNecessary();

    /**
     * 创建处理网络调用的Appender 
     */
    protected abstract void createServerAppenderIfNecessary();
}