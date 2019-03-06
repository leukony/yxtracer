package com.yunxi.common.tracer.constants;

/**
 * Tracer常量
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: TracerConstants.java, v 0.1 2017年1月10日 上午10:45:55 leukony Exp $
 */
public class TracerConstants {

    /** TraceId 放在透传上下文中的 key */
    public static final String TRACE_ID            = "yxTraceId";
    /** RpcId 放在透传上下文中的 key */
    public static final String RPC_ID              = "yxRpcId";
    /** RpcId 根 */
    public static final String RPC_ID_ROOT         = "0";
    /** RpcId 分隔符 */
    public static final String RPC_ID_SEPARATOR    = ".";

    /** Trace上下文转义使用到的常量 */
    public static final int    DEFAULT_BUFFER_SIZE = 256;
    public static final String EMPTY               = "";
    public static final String NEWLINE             = "\r\n";

    public static final char   DOT                 = '.';
    public static final char   COMMA               = ',';
    public static final char   AND                 = '&';
    public static final char   EQUAL               = '=';
    public static final char   PERCENT             = '%';
    public static final String COMMA_ESCAPE        = "%2C";
    public static final String AND_ESCAPE          = "%26";
    public static final String EQUAL_ESCAPE        = "%3D";
    public static final String PERCENT_ESCAPE      = "%25";
}