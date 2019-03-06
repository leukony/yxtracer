package com.yunxi.common.tracer.context;

import com.yunxi.common.lang.enums.RpcCode;
import com.yunxi.common.tracer.constants.TracerConstants;

/**
 * Rpc服务日志上下文
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: RpcContext.java, v 0.1 2017年2月28日 下午4:57:44 leukony Exp $
 */
public class RpcContext extends TracerContext<RpcContext> {
    
    /** 服务名称  */
    private String serviceName;
    /** 方法名称 */
    private String methodName;
    /** RPC 的协议 , 如 dubbo、drpc */
    private String protocol;
    /** RPC 的类型, 如 sync、callback */
    private String rpcType;
    /** 客户端地址*/
    private String callIP;
    /** 客户端应用名 */
    private String callApp;
    /** 服务端地址*/
    private String targetIP;
    /** 服务端应用名 */
    private String targetApp;
    /** 请求大小 */
    private long   requestSize;
    /** 响应大小 */
    private long   responseSize;

    /** 
     * @see com.yunxi.common.tracer.context.TracerContext#def()
     */
    @Override
    public RpcContext def() {
        RpcContext rpcContext = new RpcContext();
        rpcContext.setTraceId(getTraceId());
        rpcContext.setRpcId(TracerConstants.RPC_ID_ROOT);
        return rpcContext;
    }

    /** 
     * @see com.yunxi.common.tracer.context.TracerContext#clone()
     */
    @Override
    public RpcContext clone() {
        RpcContext rpcContext = super.cloneTo(new RpcContext());
        rpcContext.setServiceName(getServiceName());
        rpcContext.setMethodName(getMethodName());
        rpcContext.setProtocol(getProtocol());
        rpcContext.setRpcType(getRpcType());
        rpcContext.setCallIP(getCallIP());
        rpcContext.setCallApp(getCallApp());
        rpcContext.setTargetIP(getTargetIP());
        rpcContext.setTargetApp(getTargetApp());
        rpcContext.setRequestSize(getRequestSize());
        rpcContext.setResponseSize(getResponseSize());
        return rpcContext;
    }

    /** 
     * @see com.yunxi.common.tracer.context.TracerContext#isSuccess()
     */
    @Override
    public boolean isSuccess() {
        // 为空或者00的结果码算是成功的，其他算是失败的
        String code = super.getResultCode();
        return code == null || code.length() == 0 || RpcCode.RPC_SUCCESS.getCode().equals(code);
    }

    /**
      * Getter method for property <tt>serviceName</tt>.
      * 
      * @return property value of serviceName
      */
    public String getServiceName() {
        return serviceName;
    }

    /**
      * Setter method for property <tt>serviceName</tt>.
      * 
      * @param serviceName value to be assigned to property serviceName
      */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
      * Getter method for property <tt>methodName</tt>.
      * 
      * @return property value of methodName
      */
    public String getMethodName() {
        return methodName;
    }

    /**
      * Setter method for property <tt>methodName</tt>.
      * 
      * @param methodName value to be assigned to property methodName
      */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
      * Getter method for property <tt>protocol</tt>.
      * 
      * @return property value of protocol
      */
    public String getProtocol() {
        return protocol;
    }

    /**
      * Setter method for property <tt>protocol</tt>.
      * 
      * @param protocol value to be assigned to property protocol
      */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
      * Getter method for property <tt>rpcType</tt>.
      * 
      * @return property value of rpcType
      */
    public String getRpcType() {
        return rpcType;
    }
    
    /**
      * Setter method for property <tt>rpcType</tt>.
      * 
      * @param rpcType value to be assigned to property rpcType
      */
    public void setRpcType(String rpcType) {
        this.rpcType = rpcType;
    }

    /**
      * Getter method for property <tt>callIP</tt>.
      * 
      * @return property value of callIP
      */
    public String getCallIP() {
        return callIP;
    }

    /**
      * Setter method for property <tt>callIP</tt>.
      * 
      * @param callIP value to be assigned to property callIP
      */
    public void setCallIP(String callIP) {
        this.callIP = callIP;
    }

    /**
      * Getter method for property <tt>callApp</tt>.
      * 
      * @return property value of callApp
      */
    public String getCallApp() {
        return callApp;
    }

    /**
      * Setter method for property <tt>callApp</tt>.
      * 
      * @param callApp value to be assigned to property callApp
      */
    public void setCallApp(String callApp) {
        this.callApp = callApp;
    }

    /**
      * Getter method for property <tt>targetIP</tt>.
      * 
      * @return property value of targetIP
      */
    public String getTargetIP() {
        return targetIP;
    }

    /**
      * Setter method for property <tt>targetIP</tt>.
      * 
      * @param targetIP value to be assigned to property targetIP
      */
    public void setTargetIP(String targetIP) {
        this.targetIP = targetIP;
    }

    /**
      * Getter method for property <tt>targetApp</tt>.
      * 
      * @return property value of targetApp
      */
    public String getTargetApp() {
        return targetApp;
    }

    /**
      * Setter method for property <tt>targetApp</tt>.
      * 
      * @param targetApp value to be assigned to property targetApp
      */
    public void setTargetApp(String targetApp) {
        this.targetApp = targetApp;
    }

    /**
      * Getter method for property <tt>requestSize</tt>.
      * 
      * @return property value of requestSize
      */
    public long getRequestSize() {
        return requestSize;
    }

    /**
      * Setter method for property <tt>requestSize</tt>.
      * 
      * @param requestSize value to be assigned to property requestSize
      */
    public void setRequestSize(long requestSize) {
        this.requestSize = requestSize;
    }

    /**
      * Getter method for property <tt>responseSize</tt>.
      * 
      * @return property value of responseSize
      */
    public long getResponseSize() {
        return responseSize;
    }

    /**
      * Setter method for property <tt>responseSize</tt>.
      * 
      * @param responseSize value to be assigned to property responseSize
      */
    public void setResponseSize(long responseSize) {
        this.responseSize = responseSize;
    }
}