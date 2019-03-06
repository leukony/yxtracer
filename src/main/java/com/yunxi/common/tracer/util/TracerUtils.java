package com.yunxi.common.tracer.util;

import static com.yunxi.common.tracer.constants.TracerConstants.*;

import java.lang.management.ManagementFactory;
import java.util.Map;

import com.yunxi.common.tracer.TracerLocal;
import com.yunxi.common.tracer.context.TracerContext;

/**
 * Tracer工具类
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: TracerUtils.java, v 0.1 2017年1月9日 下午2:40:13 leukony Exp $
 */
@SuppressWarnings("rawtypes")
public class TracerUtils {
    
    /**
     * 从上下文中获取TraceId
     * @return
     */
    public static String getTraceId() {
        TracerContext<?> context = TracerLocal.get();
        
        if (context == null) {
            return EMPTY;
        }
        
        String traceId = context.getTraceId();
        
        return traceId == null ? EMPTY : traceId;
    }
    
    /**
     * 从上下文中获取RpcId
     * @return
     */
    public static String getRpcId() {
        TracerContext<?> context = TracerLocal.get();
        
        if (context == null) {
            return EMPTY;
        }
        
        String rpcId = context.getRpcId();
        
        return rpcId == null ? EMPTY : rpcId;
    }
    
    /**
     * 从上下文中获取上一个RpcId
     * @return
     */
    public static String getLastRpcId() {
        TracerContext<?> context = TracerLocal.get();
        
        if (context == null) {
            return EMPTY;
        }
        
        String lastRpcId = context.lastChildRpcId();
        
        return lastRpcId == null ? EMPTY : lastRpcId;
    }
    
    /**
     * 从上下文中获取下一个RpcId
     * @return
     */
    public static String getNextRpcId() {
        TracerContext<?> context = TracerLocal.get();
        
        if (context == null) {
            return EMPTY;
        }
        
        String nextRpcId = context.nextChildRpcId();
        
        return nextRpcId == null ? EMPTY : nextRpcId;
    }
    
    /**
     * 计算匹配字符出现次数
     * @param str
     * @param c
     * @return
     */
    public static int countMatches(String str, char c) {
        if (str == null || str.length() == 0) {
            return 0;
        }

        int count = 0;

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }

        return count;
    }

    /**
     * 克隆Tracer上下文
     * @return
     */
    public static TracerContext cloneContext() {
        TracerContext tracerContext = TracerLocal.get();
        return tracerContext == null ? null : tracerContext.clone();
    }

    /**
     * 获取PID
     * <p>适用于JDK6，JDK7，JDK8，JDK9提供了更简便的实现</p>
     * @return
     */
    public static String getPID() {
        String process = ManagementFactory.getRuntimeMXBean().getName();

        if (process == null || process.trim().length() == 0) {
            return EMPTY;
        }

        String[] processSplit = process.split("@");

        if (processSplit.length == 0) {
            return EMPTY;
        }

        String pid = processSplit[0];

        if (pid == null || pid.trim().length() == 0) {
            return EMPTY;
        }

        return pid;
    }

    /**
     * 将map转成string, 如{"k1":"v1", "k2":"v2"} => k1=v1&k2=v2&
     */
    public static String mapToString(Map<String, String> map) {
        if (map == null) {
            return EMPTY;
        }

        StringBuffer sb = new StringBuffer(DEFAULT_BUFFER_SIZE);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = escapePercentAndEqual(entry.getKey());
            String val = escapePercentAndEqual(entry.getValue());
            sb.append(key).append(EQUAL).append(val).append(AND);
        }
        return sb.toString();
    }

    /**
     * 替换str中的"&"，"=" 和 "%"
     */
    private static String escapePercentAndEqual(String str) {
        str = escape(str, String.valueOf(PERCENT), PERCENT_ESCAPE);
        str = escape(str, String.valueOf(AND), AND_ESCAPE);
        str = escape(str, String.valueOf(EQUAL), EQUAL_ESCAPE);
        return str;
    }

    /**
     * 将str中的oldStr替换为newStr
     */
    private static String escape(String str, String oldStr, String newStr) {
        return str == null ? EMPTY : str.replace(oldStr, newStr);
    }
}