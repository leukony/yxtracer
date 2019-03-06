package com.yunxi.common.tracer.daemon;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.yunxi.common.tracer.appender.TracerAppender;
import com.yunxi.common.tracer.context.TracerContext;
import com.yunxi.common.tracer.encoder.TracerEncoder;
import com.yunxi.common.tracer.util.TracerSelfLog;

/**
 * 用来做异步的日志打印
 * <p>包含了一个日志队列，打印的日志回先放到这个队列中。</p>
 * <p>包含了一个异步线程，从队列中取出日志对象，并根据日志对象的类型找到对应的实际的Encoder和TracerAppender，做实际的日志打印动作。<p>
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: TracerWriter.java, v 0.1 2017年1月11日 下午3:24:12 leukony Exp $
 */
@SuppressWarnings("rawtypes")
public class TracerWriter {

    /**
     * 默认的消费者唤醒阈值，这个值需要让消费者能较持续的有事情做，这个值设置过小，会导致生产者频繁唤起消费者； 设置过大，可能导致生产者速度过快导致队列满丢日志的问题。
     */
    private static final int               DEFAULT_THRESHOLD = 512;

    private final int                      queueSize;
    private final int                      indexMask;
    private final int                      threshold;
    private final TracerContext[]          ctxs;
    private final ReentrantLock            lock;
    private final Condition                notEmpty;

    /** 下一个写的位置，一直递增 */
    private AtomicLong                     putIndex;
    /** 下一个读的位置，一直递增 */
    private AtomicLong                     takeIndex;
    /** 最近丢弃的日志条数 */
    private AtomicLong                     discardCount;
    /** 是否正在运行 */
    private AtomicBoolean                  isRunning;

    /** 日志类型到实际的 Appender的映射 */
    private Map<Character, TracerAppender> appenderMap;
    /** 日志类型到对应的 Encoder的映射 */
    private Map<Character, TracerEncoder>  encoderMap;

    public TracerWriter(int queueSize) {
        // queueSize 取大于或等于 value的2的 n次方数
        queueSize = 1 << (32 - Integer.numberOfLeadingZeros(queueSize - 1));

        this.queueSize = queueSize;
        this.ctxs = new TracerContext[queueSize];
        this.indexMask = queueSize - 1;
        this.threshold = queueSize >= DEFAULT_THRESHOLD ? DEFAULT_THRESHOLD : queueSize;

        this.putIndex = new AtomicLong(0L);
        this.takeIndex = new AtomicLong(0L);
        this.discardCount = new AtomicLong(0L);

        this.isRunning = new AtomicBoolean(false);

        this.lock = new ReentrantLock(false);
        this.notEmpty = lock.newCondition();

        this.appenderMap = new HashMap<Character, TracerAppender>();
        this.encoderMap = new HashMap<Character, TracerEncoder>();
    }

    /**
     * 启动追加日志
     */
    public void start(String workerName) {
        Thread worker = new Thread(new AsyncRunnable());
        worker.setName("Tracer-Writer-" + workerName);
        worker.setDaemon(true);
        worker.start();
    }

    /**
     * 添加Appender
     * @param type
     * @param appender
     * @param encoder
     */
    public void addAppender(Character type, TracerAppender appender, TracerEncoder encoder) {
        this.appenderMap.put(type, appender);
        this.encoderMap.put(type, encoder);
    }

    /**
     * 队列满时直接丢弃日志，不阻塞业务线程
     */
    public boolean append(TracerContext ctx) {
        final long qZize = queueSize;
        while (true) {
            final long put = putIndex.get();
            final long useSize = put - takeIndex.get();
            if (useSize >= qZize) {
                discardCount.incrementAndGet();
                return false;
            }
            if (putIndex.compareAndSet(put, put + 1)) {
                ctxs[(int) put & indexMask] = ctx;
                // 在队列的日志数超过阈值，消费者不在运行，且获得锁，才唤醒消费者
                // 这个做法能保证只有必要时才立即通知消费者，减少上下文切换的开销
                if (useSize >= threshold && !isRunning.get() && lock.tryLock()) {
                    try {
                        notEmpty.signal();
                    } catch (Exception e) {
                        TracerSelfLog.error("唤醒异步打印日志线程失败", e);
                    } finally {
                        lock.unlock();
                    }
                }
                return true;
            }
        }
    }

    class AsyncRunnable implements Runnable {

        @SuppressWarnings("unchecked")
        public void run() {
            final TracerWriter parent = TracerWriter.this;
            final int indexMask = parent.indexMask;
            final int queueSize = parent.queueSize;
            final TracerContext[] ctxs = parent.ctxs;
            final ReentrantLock lock = parent.lock;
            final Condition notEmpty = parent.notEmpty;
            final AtomicLong putIndex = parent.putIndex;
            final AtomicLong takeIndex = parent.takeIndex;
            final AtomicLong discardCount = parent.discardCount;
            final AtomicBoolean isRunning = parent.isRunning;
            final Map<Character, TracerAppender> appenderMap = parent.appenderMap;
            final Map<Character, TracerEncoder> encoderMap = parent.encoderMap;

            // 输出丢弃的日志数
            final long outputSpan = TimeUnit.MINUTES.toMillis(1);
            long lastOutputTime = System.currentTimeMillis();

            while (true) {
                try {
                    isRunning.set(true);

                    long take = takeIndex.get();
                    long useSize = putIndex.get() - take;
                    if (useSize > 0) {
                        do {
                            final int idx = (int) take & indexMask;
                            TracerContext ctx = ctxs[idx];
                            // 从生产者增加 putIndex，到生产者把日志对象放入队列之间，有可能存在间隙
                            while (ctx == null) {
                                Thread.yield();
                                ctx = ctxs[idx];
                            }
                            ctxs[idx] = null;
                            // 单消费者，无需CAS
                            takeIndex.set(++take);
                            --useSize;

                            TracerAppender appender = appenderMap.get(ctx.getTracerType());
                            TracerEncoder encoder = encoderMap.get(ctx.getTracerType());
                            encoder.encode(ctx, appender);
                        } while (useSize > 0);

                        // 输出丢掉的日志
                        long discardNum = discardCount.get();
                        long now = System.currentTimeMillis();
                        if ((discardNum > 0) && ((now - lastOutputTime) > outputSpan)) {
                            discardNum = discardCount.get();
                            discardCount.lazySet(0);
                            lastOutputTime = now;
                            TracerSelfLog.warn("[" + Thread.currentThread().getName() + "] [丢失数量="
                                               + discardNum + ", 队列大小=" + queueSize + "]");
                        }
                    } else {
                        for (TracerAppender appender : appenderMap.values()) {
                            appender.flush();
                        }
                        if (lock.tryLock()) {
                            try {
                                isRunning.set(false);
                                notEmpty.await(1, TimeUnit.SECONDS);
                            } finally {
                                lock.unlock();
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    TracerSelfLog.warn("[" + Thread.currentThread().getName() + "] [线程被中断]");
                    break;
                } catch (Exception e) {
                    TracerSelfLog.error("[" + Thread.currentThread().getName() + "] [异步打印日志异常]", e);
                }
            }
            isRunning.set(false);
        }
    }
}