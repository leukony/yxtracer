package com.yunxi.common.tracer.encoder;

import java.io.IOException;

import com.yunxi.common.lang.enums.CommonYN;
import com.yunxi.common.lang.util.DateUtils;
import com.yunxi.common.tracer.appender.TracerAppender;
import com.yunxi.common.tracer.context.HttpContext;
import com.yunxi.common.tracer.util.TracerBuilder;

/**
 * Http服务端服务格式编码转换
 * <p>非线程安全的</p>
 *  
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: HttpServerEncoder.java, v 0.1 2017年1月11日 下午6:12:21 leukony Exp $
 */
public class HttpServerEncoder implements TracerEncoder<HttpContext> {
    
    private TracerBuilder tb = new TracerBuilder();

    /** 
     * @see com.yunxi.common.tracer.encoder.TracerEncoder#encode(com.yunxi.common.tracer.context.TracerContext, com.yunxi.common.tracer.appender.TracerAppender)
     */
    @Override
    public void encode(HttpContext ctx, TracerAppender appender) throws IOException {
        tb.reset();
        
        tb.append(DateUtils.format(ctx.getFinishTime(), DateUtils.MILLS_FORMAT_PATTERN))
          .append(ctx.getCurrentApp())
          .append(ctx.getTraceId())
          .append(ctx.getRpcId())
          .appendEscape(ctx.getThreadName())
          .append(CommonYN.get(ctx.isSuccess()).name())
          .append(ctx.getUsedTime())
          .append(ctx.getRequestIp())
          .appendEscape(ctx.getUrl())
          .append(ctx.getMethod())
          .append(ctx.getResultCode())
          .append(ctx.getRequestSize())
          .appendEnd(ctx.getResponseSize());

        appender.append(tb.toString());
    }
}