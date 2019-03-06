package com.yunxi.common.tracer.constants;

/**
 * Tracer日志上下文类型
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: TracerType.java, v 0.1 2017年1月12日 下午3:59:48 leukony Exp $
 */
public enum TracerType {

    /** HTTPClient日志 */
    HTTP_CLIENT('1'),

    /** HTTPServer日志 */
    HTTP_SERVER('2'),
    
    /** RPCClient日志 */
    RPC_CLIENT('3'),

    /** RPCServer日志 */
    RPC_SERVER('4'), ;

    private char type;

    TracerType(char type) {
        this.type = type;
    }

    /**
      * Getter method for property <tt>type</tt>.
      * 
      * @return property value of type
      */
    public char getType() {
        return type;
    }
    
    /**
     * 根据类型获取TracerType
     * @param type
     * @return
     */
    public static TracerType getTracerType(char type) {
        TracerType[] tracerTypes = TracerType.values();
        for (TracerType tracerType : tracerTypes) {
            if (type == tracerType.type) {
                return tracerType;
            }
        }
        return null;
    }
}