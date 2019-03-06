package com.yunxi.common.tracer.util;

import static com.yunxi.common.tracer.constants.TracerConstants.NEWLINE;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.yunxi.common.lang.util.DateUtils;
import com.yunxi.common.tracer.appender.TimedRollingFileAppender;
import com.yunxi.common.tracer.appender.TracerAppender;
import com.yunxi.common.tracer.tracer.Tracer;

/**
 * Tracer自身的Log处理
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: TracerSelfLog.java, v 0.1 2017年2月16日 上午11:00:26 leukony Exp $
 */
public class TracerSelfLog {

    private static final String   ERROR_PREFIX = "[ERROR] ";
    private static final String   WARN_PREFIX  = "[WARN]  ";
    private static final String   INFO_PREFIX  = "[INFO]  ";

    private static final String   SELF_LOG     = "tracer-self.log";

    private static TracerAppender tracerAppender;

    static {
        String selfPath = Tracer.TRACR_LOGGING_ROOT + File.separator + SELF_LOG;
        tracerAppender = new TimedRollingFileAppender(selfPath,
            TimedRollingFileAppender.DAILY_ROLLING_PATTERN,
            TimedRollingFileAppender.DEFAULT_RESERVE_DAY);
    }

    public static void info(String log) {
        doLog(log, INFO_PREFIX);
    }

    public static void infoWithTraceId(String log) {
        doLog(log, INFO_PREFIX + "[" + TracerUtils.getTraceId() + "]");
    }

    public static void warn(String log) {
        doLog(log, WARN_PREFIX);
    }

    public static void error(String log) {
        doLog(log, ERROR_PREFIX);
    }

    public static void error(String log, Throwable e) {
        try {
            StringWriter sw = new StringWriter(4096);
            PrintWriter pw = new PrintWriter(sw, false);

            pw.append(timestamp());
            pw.append(ERROR_PREFIX);
            pw.append(log);
            pw.append(NEWLINE);

            e.printStackTrace(pw);

            pw.println();
            pw.flush();

            tracerAppender.append(sw.toString());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void errorWithTraceId(String log) {
        doLog(log, ERROR_PREFIX + "[" + TracerUtils.getTraceId() + "]");
    }

    public static void errorWithTraceId(String log, Throwable e) {
        try {
            StringWriter sw = new StringWriter(4096);
            PrintWriter pw = new PrintWriter(sw, false);

            pw.append(timestamp());
            pw.append(ERROR_PREFIX);
            pw.append("[");
            pw.append(TracerUtils.getTraceId());
            pw.append("]");
            pw.append(log);
            pw.append(NEWLINE);

            e.printStackTrace(pw);

            pw.println();
            pw.flush();

            tracerAppender.append(sw.toString());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void doLog(String log, String prefix) {
        try {
            StringBuilder sb = new StringBuilder();

            sb.append(timestamp());
            sb.append(prefix);
            sb.append(log);
            sb.append(NEWLINE);

            tracerAppender.append(sb.toString());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static String timestamp() {
        return DateUtils.format(DateUtils.currentMills(), DateUtils.MILLS_FORMAT_PATTERN);
    }
}