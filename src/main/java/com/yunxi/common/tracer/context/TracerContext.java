package com.yunxi.common.tracer.context;

import static com.yunxi.common.tracer.constants.TracerConstants.RPC_ID;
import static com.yunxi.common.tracer.constants.TracerConstants.TRACE_ID;
import static com.yunxi.common.tracer.constants.TracerConstants.DOT;
import static com.yunxi.common.tracer.constants.TracerConstants.RPC_ID_SEPARATOR;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.yunxi.common.tracer.constants.TracerConstants;
import com.yunxi.common.tracer.util.TracerSelfLog;
import com.yunxi.common.tracer.util.TracerUtils;

/**
 * Tracer日志上下文
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: TracerContext.java, v 0.1 2017年1月9日 下午2:49:33 leukony Exp $
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class TracerContext<T extends TracerContext> {

    /** Tracer上下文嵌套的最大深度 */
    private static final int TRACE_MAX_LAYER = 100;

    /**
     * 默认实例
     * @return 默认实例
     */
    public abstract T def();

    /**
     * 复制实例
     * @return 本实例的一个克隆
     */
    public abstract T clone();

    /**
     * 判断结果是成功的还是失败的
     * @return
     */
    public abstract boolean isSuccess();

    /**
     * 复制Tracer基本属性
     * @param to
     * @return
     */
    public T cloneTo(T to) {
        to.setStartTime(this.startTime);
        to.setFinishTime(this.finishTime);
        to.setCurrentApp(this.currentApp);
        to.setResultCode(this.resultCode);
        to.setTracerType(this.tracerType);
        to.setParentContext(this.parentContext);
        to.setChildRpcIdIndex(this.childRpcIdIndex);
        to.putAllTrace(this.traceContext);
        return to;
    }

    /**
     * 返回自身作为下一个上下文的 parent
     * @return
     */
    public T getThisAsParent() {
        if (TracerUtils.countMatches(getRpcId(), DOT) + 1 > TRACE_MAX_LAYER) {
            TracerSelfLog.errorWithTraceId("日志上下文嵌套过深，超过：" + TRACE_MAX_LAYER);
            // 系统属性
            // 业务属性
            return def();
        } else {
            return (T) this;
        }
    }

    /** 存放各种Trace的数据 */
    private Map<String, String> traceContext = new HashMap<String, String>();

    public String getTraceId() {
        return getTrace(TRACE_ID);
    }

    public void setTraceId(String traceId) {
        putTrace(TRACE_ID, traceId == null ? TracerConstants.EMPTY : traceId);
    }

    public String getRpcId() {
        return getTrace(RPC_ID);
    }

    public void setRpcId(String rpcId) {
        putTrace(RPC_ID, rpcId == null ? TracerConstants.EMPTY : rpcId);
    }

    public void putTrace(String key, String value) {
        traceContext.put(key, value);
    }

    public String getTrace(String key) {
        return traceContext.get(key);
    }

    public void putAllTrace(Map<String, String> traceContext) {
        this.traceContext.putAll(traceContext);
    }

    /** 上下文计数器 */
    private AtomicInteger childRpcIdIndex = new AtomicInteger(0);

    /**
     * 获取下一个子上下文的RpcId
     * @return
     */
    public String nextChildRpcId() {
        StringBuilder sb = new StringBuilder();
        sb.append(getRpcId());
        sb.append(RPC_ID_SEPARATOR);
        sb.append(childRpcIdIndex.incrementAndGet());
        return sb.toString();
    }

    /**
     * 获取上一个子上下文的RpcId
     * @return
     */
    public String lastChildRpcId() {
        StringBuilder sb = new StringBuilder();
        sb.append(getRpcId());
        sb.append(RPC_ID_SEPARATOR);
        sb.append(childRpcIdIndex.get());
        return sb.toString();
    }

    /**
      * Getter method for property <tt>childRpcIdIndex</tt>.
      * 
      * @return property value of childRpcIdIndex
      */
    public AtomicInteger getChildRpcIdIndex() {
        return childRpcIdIndex;
    }

    /**
      * Setter method for property <tt>childRpcIdIndex</tt>.
      * 
      * @param childRpcIdIndex value to be assigned to property childRpcIdIndex
      */
    public void setChildRpcIdIndex(AtomicInteger childRpcIdIndex) {
        this.childRpcIdIndex = childRpcIdIndex;
    }

    /** 开始时间  */
    private long          startTime;
    /** 结束时间 */
    private long          finishTime;
    /** 类型 */
    private char          tracerType;
    /** 应用名 */
    private String        currentApp;
    /** 线程名 */
    private String        threadName;
    /** 返回结果码 */
    private String        resultCode;
    /** 父Trace日志上下文  */
    private TracerContext parentContext;

    /**
     * 获取耗时
     * @return
     */
    public long getUsedTime() {
        if (finishTime == 0L) {
            finishTime = System.currentTimeMillis();
        }
        return finishTime - startTime;
    }

    /**
      * Getter method for property <tt>startTime</tt>.
      * 
      * @return property value of startTime
      */
    public long getStartTime() {
        return startTime;
    }

    /**
      * Setter method for property <tt>startTime</tt>.
      * 
      * @param startTime value to be assigned to property startTime
      */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
      * Getter method for property <tt>finishTime</tt>.
      * 
      * @return property value of finishTime
      */
    public long getFinishTime() {
        return finishTime;
    }

    /**
      * Setter method for property <tt>finishTime</tt>.
      * 
      * @param finishTime value to be assigned to property finishTime
      */
    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    /**
      * Getter method for property <tt>tracerType</tt>.
      * 
      * @return property value of tracerType
      */
    public char getTracerType() {
        return tracerType;
    }

    /**
      * Setter method for property <tt>tracerType</tt>.
      * 
      * @param tracerType value to be assigned to property tracerType
      */
    public void setTracerType(char tracerType) {
        this.tracerType = tracerType;
    }

    /**
      * Getter method for property <tt>currentApp</tt>.
      * 
      * @return property value of currentApp
      */
    public String getCurrentApp() {
        return currentApp;
    }

    /**
      * Setter method for property <tt>currentApp</tt>.
      * 
      * @param currentApp value to be assigned to property currentApp
      */
    public void setCurrentApp(String currentApp) {
        this.currentApp = currentApp;
    }

    /**
      * Getter method for property <tt>threadName</tt>.
      * 
      * @return property value of threadName
      */
    public String getThreadName() {
        return threadName;
    }

    /**
      * Setter method for property <tt>threadName</tt>.
      * 
      * @param threadName value to be assigned to property threadName
      */
    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    /**
      * Getter method for property <tt>resultCode</tt>.
      * 
      * @return property value of resultCode
      */
    public String getResultCode() {
        return resultCode;
    }

    /**
      * Setter method for property <tt>resultCode</tt>.
      * 
      * @param resultCode value to be assigned to property resultCode
      */
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    /**
      * Getter method for property <tt>parentContext</tt>.
      * 
      * @return property value of parentContext
      */
    public TracerContext getParentContext() {
        return parentContext;
    }

    /**
      * Setter method for property <tt>parentContext</tt>.
      * 
      * @param parentContext value to be assigned to property parentContext
      */
    public void setParentContext(TracerContext parentContext) {
        this.parentContext = parentContext;
    }
}