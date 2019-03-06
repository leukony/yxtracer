package com.yunxi.common.tracer.appender;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.yunxi.common.tracer.daemon.TracerClear;

/**
 * 基于日志时间滚动的Tracer的日志打印
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: TimedRollingFileAppender.java, v 0.1 2017年1月10日 下午3:08:41 leukony Exp $
 */
public class TimedRollingFileAppender extends RollingFileAppender {

    public static final String DAILY_ROLLING_PATTERN  = "'.'yyyy-MM-dd";
    public static final String HOURLY_ROLLING_PATTERN = "'.'yyyy-MM-dd_HH";
    public static final int    DEFAULT_RESERVE_DAY    = 7;

    // The code assumes that the following constants are in a increasing
    // sequence.
    static final int           TOP_OF_TROUBLE         = -1;
    static final int           TOP_OF_SECONDS         = 0;
    static final int           TOP_OF_MINUTE          = 1;
    static final int           TOP_OF_HOUR            = 2;
    static final int           HALF_DAY               = 3;
    static final int           TOP_OF_DAY             = 4;
    static final int           TOP_OF_WEEK            = 5;
    static final int           TOP_OF_MONTH           = 6;

    /**
       The date pattern. By default, the pattern is set to
       "'.'yyyy-MM-dd" meaning daily rollover.
     */
    private String             datePattern            = DAILY_ROLLING_PATTERN;

    /**
       The log file will be renamed to the value of the
       scheduledFilename variable when the next interval is entered. For
       example, if the rollover period is one hour, the log file will be
       renamed to the value of "scheduledFilename" at the beginning of
       the next hour. 

       The precise time when a rollover occurs depends on logging
       activity. 
    */
    private String             scheduledFilename;

    /** The next time we estimate a rollover should occur. */
    private long               nextCheck              = System.currentTimeMillis() - 1;

    Date                       now                    = new Date();

    SimpleDateFormat           sdf;

    RollingCalendar            rc                     = new RollingCalendar();

    int                        checkPeriod            = TOP_OF_TROUBLE;

    int                        logReserve             = DEFAULT_RESERVE_DAY;

    // The gmtTimeZone is used only in computeCheckPeriod() method.
    static final TimeZone      gmtTimeZone            = TimeZone.getTimeZone("GMT");

    public TimedRollingFileAppender(String fileName) {
        this(fileName, DAILY_ROLLING_PATTERN, DEFAULT_RESERVE_DAY);
    }

    public TimedRollingFileAppender(String fileName, String datePattern, int logReserve) {
        this(fileName, datePattern, DEFAULT_BUFFER, logReserve);
    }

    public TimedRollingFileAppender(String fileName, String datePattern, int bufferSize,
                                    int logReserve) {
        super(fileName, bufferSize);
        this.datePattern = datePattern;
        this.logReserve = logReserve;
        activateOptions();
    }

    public void activateOptions() {
        if (datePattern != null && fileName != null) {
            now.setTime(System.currentTimeMillis());
            sdf = new SimpleDateFormat(datePattern);
            rc.setType(computeCheckPeriod());
            scheduledFilename = fileName + sdf.format(new Date(logFile.lastModified()));
            TracerClear.watch(this);
        } else {
            System.err.println("[Tracer] [参数错误：没有设置滚动的模式或者文件名为空]");
        }
    }

    /** 
     * @see com.yunxi.common.tracer.appender.RollingFileAppender#checkRollOver()
     */
    @Override
    protected boolean checkRollOver() {
        long n = System.currentTimeMillis();
        if (n >= nextCheck) {
            now.setTime(n);
            nextCheck = rc.getNextCheckMillis(now);
            return true;
        }
        return false;
    }

    /** 
     * @see com.yunxi.common.tracer.appender.RollingFileAppender#rollOver()
     */
    @Override
    protected void rollOver() {
        /* Compute filename, but only if datePattern is specified */
        if (datePattern == null) {
            System.err.println("[Tracer] [没有设置滚动的模式]");
            return;
        }

        String datedFilename = fileName + sdf.format(now);
        // It is too early to roll over because we are still within the
        // bounds of the current interval. Rollover will occur once the
        // next interval is reached.
        if (scheduledFilename.equals(datedFilename)) {
            return;
        }

        if (this.buffer != null) {
            try {
                this.buffer.close();
            } catch (IOException e) {
                if (e instanceof InterruptedIOException) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("[Tracer] [关闭输出流失败：" + e.getMessage() + "]");
            }
        }

        File target = new File(scheduledFilename);
        if (target.exists()) {
            target.delete();
        }

        if (logFile.renameTo(target)) {
            System.out.println("[Tracer] [成功将文件名：" + fileName + " -> " + scheduledFilename + "]");
        } else {
            System.err.println("[Tracer] [无法将文件名：" + fileName + " -> " + scheduledFilename + "]");
        }

        this.setFile();

        scheduledFilename = datedFilename;
    }

    /** 
     * @see com.yunxi.common.tracer.appender.TracerAppender#clear()
     */
    @Override
    public void clear() {
        try {
            File parentDirectory = logFile.getParentFile();

            if (parentDirectory == null || !parentDirectory.isDirectory()) {
                return;
            }

            final String baseName = logFile.getName();

            if (baseName == null || baseName.trim().length() == 0) {
                return;
            }

            File[] logFiles = parentDirectory.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name != null && name.startsWith(baseName);
                }
            });

            if (logFiles == null || logFiles.length == 0) {
                return;
            }

            for (File logFile : logFiles) {
                String logFileName = logFile.getName();

                int lastDot = logFileName.lastIndexOf(".");

                if (lastDot < 0) {
                    continue;
                }

                String logTime = logFileName.substring(lastDot);
                SimpleDateFormat dailyRollingSdf = new SimpleDateFormat(DAILY_ROLLING_PATTERN);
                SimpleDateFormat hourlyRollingSdf = new SimpleDateFormat(HOURLY_ROLLING_PATTERN);

                if (".log".equalsIgnoreCase(logTime)) {
                    continue;
                }

                Date date = null;
                try {
                    date = dailyRollingSdf.parse(logTime);
                } catch (ParseException e) {
                    try {
                        date = hourlyRollingSdf.parse(logTime);
                    } catch (ParseException pe) {
                        System.out.println("[Tracer] [无法解析此日志文件后缀：" + logFileName + "]");
                    }
                }

                if (date == null) {
                    continue;
                }

                Calendar now = Calendar.getInstance();
                now.add(Calendar.DATE, 0 - logReserve);
                now.set(Calendar.HOUR, 0);
                now.set(Calendar.MINUTE, 0);
                now.set(Calendar.SECOND, 0);
                now.set(Calendar.MILLISECOND, 0);

                Calendar compareCal = Calendar.getInstance();
                compareCal.clear();
                compareCal.set(Calendar.YEAR, now.get(Calendar.YEAR));
                compareCal.set(Calendar.MONTH, now.get(Calendar.MONTH));
                compareCal.set(Calendar.DATE, now.get(Calendar.DATE));

                Calendar logCal = Calendar.getInstance();
                logCal.setTime(date);

                if (!logCal.before(compareCal)) {
                    continue;
                }

                if (logFile.delete() && !logFile.exists()) {
                    System.out.println("[Tracer] [清理日志文件成功：" + logFileName + "]");
                } else {
                    System.err.println("[Tracer] [清理日志文件失败：" + logFileName + "]");
                }
            }
        } catch (Throwable e) {
            System.err.println("[Tracer] [清理日志文件异常：" + e.getMessage() + "]");
        }
    }

    // This method computes the roll over period by looping over the
    // periods, starting with the shortest, and stopping when the r0 is
    // different from from r1, where r0 is the epoch formatted according
    // the datePattern (supplied by the user) and r1 is the
    // epoch+nextMillis(i) formatted according to datePattern. All date
    // formatting is done in GMT and not local format because the test
    // logic is based on comparisons relative to 1970-01-01 00:00:00
    // GMT (the epoch).

    int computeCheckPeriod() {
        RollingCalendar rollingCalendar = new RollingCalendar(gmtTimeZone, Locale.getDefault());
        // set sate to 1970-01-01 00:00:00 GMT
        Date epoch = new Date(0);
        if (datePattern != null) {
            for (int i = TOP_OF_SECONDS; i <= TOP_OF_MONTH; i++) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
                simpleDateFormat.setTimeZone(gmtTimeZone); // do all date formatting in GMT
                String r0 = simpleDateFormat.format(epoch);
                rollingCalendar.setType(i);
                Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));
                String r1 = simpleDateFormat.format(next);
                //System.out.println("Type = "+i+", r0 = "+r0+", r1 = "+r1);
                if (r0 != null && r1 != null && !r0.equals(r1)) {
                    return i;
                }
            }
        }
        return TOP_OF_TROUBLE;
    }
}

/**
 *  RollingCalendar is a helper class to DailyRollingFileAppender.
 *  Given a periodicity type and the current time, it computes the
 *  start of the next interval.  
 * */
class RollingCalendar extends GregorianCalendar {

    private static final long serialVersionUID = -6610354456347143893L;

    int                       type             = TimedRollingFileAppender.TOP_OF_TROUBLE;

    RollingCalendar() {
        super();
    }

    RollingCalendar(TimeZone tz, Locale locale) {
        super(tz, locale);
    }

    void setType(int type) {
        this.type = type;
    }

    public long getNextCheckMillis(Date date) {
        return getNextCheckDate(date).getTime();
    }

    public Date getNextCheckDate(Date date) {
        this.setTime(date);

        switch (type) {
            case TimedRollingFileAppender.TOP_OF_SECONDS:
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.SECOND, 1);
                break;
            case TimedRollingFileAppender.TOP_OF_MINUTE:
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.MINUTE, 1);
                break;
            case TimedRollingFileAppender.TOP_OF_HOUR:
                this.set(Calendar.MINUTE, 0);
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.HOUR_OF_DAY, 1);
                break;
            case TimedRollingFileAppender.HALF_DAY:
                this.set(Calendar.MINUTE, 0);
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                int hour = get(Calendar.HOUR_OF_DAY);
                if (hour < 12) {
                    this.set(Calendar.HOUR_OF_DAY, 12);
                } else {
                    this.set(Calendar.HOUR_OF_DAY, 0);
                    this.add(Calendar.DAY_OF_MONTH, 1);
                }
                break;
            case TimedRollingFileAppender.TOP_OF_DAY:
                this.set(Calendar.HOUR_OF_DAY, 0);
                this.set(Calendar.MINUTE, 0);
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.DATE, 1);
                break;
            case TimedRollingFileAppender.TOP_OF_WEEK:
                this.set(Calendar.DAY_OF_WEEK, getFirstDayOfWeek());
                this.set(Calendar.HOUR_OF_DAY, 0);
                this.set(Calendar.MINUTE, 0);
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case TimedRollingFileAppender.TOP_OF_MONTH:
                this.set(Calendar.DATE, 1);
                this.set(Calendar.HOUR_OF_DAY, 0);
                this.set(Calendar.MINUTE, 0);
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.MONTH, 1);
                break;
            default:
                throw new IllegalStateException("Unknown Period type.");
        }

        return this.getTime();
    }
}