package com.community.weare.utils;

public class ValidationPatterns {
    public static final String EMAIL_VALID_PATTERN="^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    public static final String USER_NAME_VALID_PATTERN="^[a-zA-Z]*$";
    public static final int PASSWORD_MIN_LEN=6;
    public static final int NAMES_MIN_LEN=2;
}
