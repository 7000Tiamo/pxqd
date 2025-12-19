package com.training.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 时间工具类
 * 统一处理时区问题，使用中国时区（UTC+8）
 */
public class DateTimeUtil {

    /**
     * 中国时区
     */
    private static final ZoneId CHINA_ZONE = ZoneId.of("Asia/Shanghai");

    /**
     * 获取中国时区的当前时间
     * @return 当前时间的 LocalDateTime（中国时区）
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(CHINA_ZONE);
    }
}

