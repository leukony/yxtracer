package com.yunxi.common.tracer.encoder;

import java.io.IOException;

import com.yunxi.common.lang.enums.CommonYN;
import com.yunxi.common.lang.util.DateUtils;
import com.yunxi.common.tracer.appender.TracerAppender;
import com.yunxi.common.tracer.context.SchedulerContext;
import com.yunxi.common.tracer.util.TracerBuilder;

/**
 * 定时任务调度发送端格式编码转换
 * <p>非线程安全的</p>
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: SchedulerSendEncoder.java, v 0.1 2019年6月28日 上午10:46:46 leukony Exp $
 */
public class SchedulerSendEncoder implements TracerEncoder<SchedulerContext> {
    
    private TracerBuilder tb = new TracerBuilder();

    /** 
     * @see com.yunxi.common.tracer.encoder.TracerEncoder#encode(com.yunxi.common.tracer.context.TracerContext, com.yunxi.common.tracer.appender.TracerAppender)
     */
    @Override
    public void encode(SchedulerContext ctx, TracerAppender appender) throws IOException {
        tb.reset();
        
        tb.append(DateUtils.format(ctx.getFinishTime(), DateUtils.MILLS_FORMAT_PATTERN))
          .append(ctx.getCurrentApp())
          .append(ctx.getTraceId())
          .append(ctx.getRpcId())
          .appendEscape(ctx.getThreadName())
          .append(CommonYN.get(ctx.isSuccess()).name())
          .append(ctx.getUsedTime())
          .append(ctx.getTaskName());
       
        if (ctx.getBusinessKey() != null) {
            tb.append(ctx.getTaskAction())
              .appendEscapeEnd(ctx.getBusinessKey());
        } else {
            tb.appendEnd(ctx.getTaskAction());  
        }

        appender.append(tb.toString());
    }
}