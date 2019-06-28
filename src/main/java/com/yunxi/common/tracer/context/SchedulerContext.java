package com.yunxi.common.tracer.context;

import com.yunxi.common.lang.enums.RpcCode;
import com.yunxi.common.tracer.constants.TracerConstants;

/**
 * 定时任务调度日志上下文
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: SchedulerContext.java, v 0.1 2019年6月28日 上午10:20:18 leukony Exp $
 */
public class SchedulerContext extends TracerContext<SchedulerContext> {

    /** 定时任务调度动作 */
    private String taskAction;
    /** 定时任务名称 */
    private String taskName;
    /** 定时任务子任务业务唯一标识 */
    private String businessKey;

    /** 
     * @see com.yunxi.common.tracer.context.TracerContext#def()
     */
    @Override
    public SchedulerContext def() {
        SchedulerContext schedulerContext = new SchedulerContext();
        schedulerContext.setTraceId(getTraceId());
        schedulerContext.setRpcId(TracerConstants.RPC_ID_ROOT);
        return schedulerContext;
    }

    /** 
     * @see com.yunxi.common.tracer.context.TracerContext#clone()
     */
    @Override
    public SchedulerContext clone() {
        SchedulerContext schedulerContext = super.cloneTo(new SchedulerContext());
        schedulerContext.setTaskAction(getTaskAction());
        schedulerContext.setTaskName(getTaskName());
        schedulerContext.setBusinessKey(getBusinessKey());
        return schedulerContext;
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
      * Getter method for property <tt>taskAction</tt>.
      * 
      * @return property value of taskAction
      */
    public String getTaskAction() {
        return taskAction;
    }

    /**
      * Setter method for property <tt>taskAction</tt>.
      * 
      * @param taskAction value to be assigned to property taskAction
      */
    public void setTaskAction(String taskAction) {
        this.taskAction = taskAction;
    }

    /**
      * Getter method for property <tt>taskName</tt>.
      * 
      * @return property value of taskName
      */
    public String getTaskName() {
        return taskName;
    }

    /**
      * Setter method for property <tt>taskName</tt>.
      * 
      * @param taskName value to be assigned to property taskName
      */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
      * Getter method for property <tt>businessKey</tt>.
      * 
      * @return property value of businessKey
      */
    public String getBusinessKey() {
        return businessKey;
    }

    /**
      * Setter method for property <tt>businessKey</tt>.
      * 
      * @param businessKey value to be assigned to property businessKey
      */
    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }
}