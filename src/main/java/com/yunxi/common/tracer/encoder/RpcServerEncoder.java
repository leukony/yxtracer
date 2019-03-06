package com.yunxi.common.tracer.encoder;

import java.io.IOException;

import com.yunxi.common.lang.enums.CommonYN;
import com.yunxi.common.lang.util.DateUtils;
import com.yunxi.common.tracer.appender.TracerAppender;
import com.yunxi.common.tracer.context.RpcContext;
import com.yunxi.common.tracer.util.TracerBuilder;

/**
 * Rpc服务端服务格式编码转换
 * <p>非线程安全的</p>
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: RpcServerEncoder.java, v 0.1 2017年2月28日 下午5:03:46 leukony Exp $
 */
public class RpcServerEncoder implements TracerEncoder<RpcContext> {
    
    private TracerBuilder tb = new TracerBuilder();

    /** 
     * @see com.yunxi.common.tracer.encoder.TracerEncoder#encode(com.yunxi.common.tracer.context.TracerContext, com.yunxi.common.tracer.appender.TracerAppender)
     */
    @Override
    public void encode(RpcContext ctx, TracerAppender appender) throws IOException {
        tb.reset();
        
        tb.append(DateUtils.format(ctx.getFinishTime(), DateUtils.MILLS_FORMAT_PATTERN))
          .append(ctx.getCurrentApp())
          .append(ctx.getTraceId())
          .append(ctx.getRpcId())
          .append(ctx.getServiceName())
          .append(ctx.getMethodName())
          .append(ctx.getProtocol())
          .append(ctx.getRpcType())
          .append(ctx.getTargetApp())
          .append(ctx.getTargetIP())
          .append(ctx.getCallIP())
          .append(CommonYN.get(ctx.isSuccess()).name())
          .append(ctx.getResultCode())
          .append(ctx.getUsedTime())
          .appendEscape(ctx.getThreadName())
          .append(ctx.getRequestSize())
          .appendEnd(ctx.getResponseSize());

        appender.append(tb.toString());
    }
}