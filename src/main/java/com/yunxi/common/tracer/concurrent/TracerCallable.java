package com.yunxi.common.tracer.concurrent;

import java.util.concurrent.Callable;

import com.yunxi.common.tracer.TracerLocal;
import com.yunxi.common.tracer.context.TracerContext;
import com.yunxi.common.tracer.util.TracerUtils;

/**
 * Tracer多线程支持：Callable实现
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: TracerCallable.java, v 0.1 2017年1月11日 下午7:02:03 leukony Exp $
 */
public abstract class TracerCallable<T> implements Callable<T> {

    @SuppressWarnings("rawtypes")
    private TracerContext tracerContext = TracerUtils.cloneContext();

    public T call() throws Exception {
        TracerLocal.set(tracerContext);
        try {
            return doCall();
        } finally {
            TracerLocal.clear();
        }
    }

    public abstract T doCall() throws Exception;
}