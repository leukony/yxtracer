package com.yunxi.common.tracer.util;

import static com.yunxi.common.tracer.constants.TracerConstants.COMMA;
import static com.yunxi.common.tracer.constants.TracerConstants.COMMA_ESCAPE;
import static com.yunxi.common.tracer.constants.TracerConstants.DEFAULT_BUFFER_SIZE;
import static com.yunxi.common.tracer.constants.TracerConstants.NEWLINE;
import static com.yunxi.common.tracer.constants.TracerConstants.EMPTY;

import java.util.Map;

/**
 * Tracer日志输出的字符串拼接工具
 * 
 * @author <a href="mailto:leukony@yeah.net">leukony</a>
 * @version $Id: TracerBuilder.java, v 0.1 2017年1月11日 下午6:13:38 leukony Exp $
 */
public class TracerBuilder {

    private static char     separator           = COMMA;
    private static String   separatorStr        = separator + "";
    private static String   spearatorEscape     = COMMA_ESCAPE;
    private StringBuilder   sb;

    public TracerBuilder() {
        this(DEFAULT_BUFFER_SIZE, COMMA);
    }

    public TracerBuilder(int size) {
        this(size, COMMA);
    }

    @SuppressWarnings("static-access")
    public TracerBuilder(int size, char separator) {
        this.separator = separator;
        sb = new StringBuilder(size);
    }

    /**
     * @param str
     * @return
     */
    public TracerBuilder append(String str) {
        sb.append(str == null ? EMPTY : str).append(separator);
        return this;
    }

    /**
     * @param str
     * @param separator
     * @return
     */
    public TracerBuilder append(String str, String separator) {
        sb.append(str == null ? EMPTY : str).append(separator);
        return this;
    }

    /**
     * @param str
     * @return
     */
    public TracerBuilder append(long str) {
        sb.append(str).append(separator);
        return this;
    }

    /**
     * @param str
     * @param separator
     * @return
     */
    public TracerBuilder append(long str, String separator) {
        sb.append(str).append(separator);
        return this;
    }

    /**
     * @param str
     * @param separator
     * @return
     */
    public TracerBuilder append(long str, char separator) {
        sb.append(str).append(separator);
        return this;
    }

    /**
     * @param str
     * @return
     */
    public TracerBuilder append(int str) {
        sb.append(str).append(separator);
        return this;
    }

    /**
     * @param str
     * @return
     */
    public TracerBuilder append(char str) {
        sb.append(str).append(separator);
        return this;
    }

    /**
     * @param map
     * @return
     */
    public TracerBuilder append(Map<String, String> map) {
        this.appendEscape(TracerUtils.mapToString(map));
        return this;
    }

    /**
     * @param str
     * @return
     */
    public TracerBuilder appendEnd(String str) {
        sb.append(str == null ? EMPTY : str).append(NEWLINE);
        return this;
    }

    /**
     * @param str
     * @return
     */
    public TracerBuilder appendEnd(long str) {
        sb.append(str).append(NEWLINE);
        return this;
    }

    /**
     * @param str
     * @return
     */
    public TracerBuilder appendEnd(int str) {
        sb.append(str).append(NEWLINE);
        return this;
    }

    /**
     * @param c
     * @return
     */
    public TracerBuilder appendEnd(char c) {
        sb.append(String.valueOf(c)).append(NEWLINE);
        return this;
    }

    /**
     * @param map
     * @return
     */
    public TracerBuilder appendEnd(Map<String, String> map) {
        this.appendEscapeEnd(TracerUtils.mapToString(map));
        return this;
    }

    /**
     * @param str
     * @return
     */
    public TracerBuilder appendRaw(String str) {
        sb.append(str == null ? EMPTY : str);
        return this;
    }

    /**
     * 将字符串中分隔符变成对应转义字符
     */
    public TracerBuilder appendEscape(String str) {
        str = (str == null) ? EMPTY : str;
        str = str.replace(separatorStr, spearatorEscape);
        return append(str);
    }

    /**
     * 将字符串中分隔符变成对应转义字符
     */
    public TracerBuilder appendEscapeRaw(String str) {
        str = (str == null) ? EMPTY : str;
        str = str.replace(separatorStr, spearatorEscape);
        return appendRaw(str);
    }

    /**
     * 将字符串中分隔符变成对应转义字符
     */
    public TracerBuilder appendEscapeEnd(String str) {
        str = (str == null) ? EMPTY : str;
        str = str.replace(separatorStr, spearatorEscape);
        return appendEnd(str);
    }

    /**
     * @return
     */
    public TracerBuilder reset() {
        sb.delete(0, sb.length());
        return this;
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        return sb.toString();
    }
}