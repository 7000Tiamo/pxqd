package com.training.common.api;

/**
 * 统一错误文案常量
 */
public final class ErrorMessages {
    private ErrorMessages() {}

    public static final String TRAINING_NOT_FOUND = "培训不存在";
    public static final String USER_NOT_FOUND = "用户不存在";
    public static final String TRAINING_NOT_NEED_SIGNUP = "该培训不需要报名";
    public static final String TRAINING_ALREADY_SIGNEDUP = "您已报名该培训";
    public static final String TRAINING_FULL = "报名人数已满";
    public static final String ENROLLMENT_NOT_FOUND = "未找到报名记录";
    public static final String TRAINING_NOT_SIGNEDUP = "您未报名该培训";
    public static final String TRAINING_ALREADY_CHECKIN = "您已签到";
    public static final String TRAINING_NEED_GPS = "需要开启定位权限";
    public static final String TRAINING_NOT_NEED_CHECKOUT = "该培训不需要签退";
    public static final String TRAINING_NEED_CHECKIN_FIRST = "请先完成签到";
    public static final String TRAINING_ALREADY_CHECKOUT = "您已签退";
    public static final String TRAINING_TIME_NOT_SET = "培训时间未设置";
}


