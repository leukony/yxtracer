package com.yunxi.common.tracer.context;

import com.yunxi.common.tracer.constants.TracerConstants;

/**
 * Http服务日志上下文
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: HttpContext.java, v 0.1 2017年1月9日 下午3:32:03 leukony Exp $
 */
public class HttpContext extends TracerContext<HttpContext> {

    /** 请求的Url */
    private String url;
    /** 请求的Method, 比如：GET、POST */
    private String method;
    /** 请求的IP */
    private String requestIp;
    /** 请求的大小 */
    private long   requestSize;
    /** 响应的大小 */
    private long   responseSize;
    
    /** 
     * @see com.yunxi.common.tracer.context.TracerContext#def()
     */
    @Override
    public HttpContext def() {
        HttpContext httpContext = new HttpContext();
        httpContext.setTraceId(getTraceId());
        httpContext.setRpcId(TracerConstants.RPC_ID_ROOT);
        return httpContext;
    }

    /** 
     * @see com.yunxi.common.tracer.context.TracerContext#clone()
     */
    @Override
    public HttpContext clone() {
        HttpContext httpContext = super.cloneTo(new HttpContext());
        httpContext.setUrl(getUrl());
        httpContext.setMethod(getMethod());
        httpContext.setRequestIp(getRequestIp());
        httpContext.setRequestSize(getRequestSize());
        httpContext.setResponseSize(getResponseSize());
        return httpContext;
    }

    /** 
     * @see com.yunxi.common.tracer.context.TracerContext#isSuccess()
     */
    @Override
    public boolean isSuccess() {
        // 1开头、2开头和302的结果码算是成功的，其他算是失败的
        String code = super.getResultCode();
        return code != null && code.length() > 0
               && (code.charAt(0) == '1' || code.charAt(0) == '2' || code.trim().equals("302"));
    }

    /**
      * Getter method for property <tt>url</tt>.
      * 
      * @return property value of url
      */
    public String getUrl() {
        return url;
    }

    /**
      * Setter method for property <tt>url</tt>.
      * 
      * @param url value to be assigned to property url
      */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
      * Getter method for property <tt>method</tt>.
      * 
      * @return property value of method
      */
    public String getMethod() {
        return method;
    }

    /**
      * Setter method for property <tt>method</tt>.
      * 
      * @param method value to be assigned to property method
      */
    public void setMethod(String method) {
        this.method = method;
    }
    
    /**
      * Getter method for property <tt>requestIp</tt>.
      * 
      * @return property value of requestIp
      */
    public String getRequestIp() {
        return requestIp;
    }
    
    /**
      * Setter method for property <tt>requestIp</tt>.
      * 
      * @param requestIp value to be assigned to property requestIp
      */
    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
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