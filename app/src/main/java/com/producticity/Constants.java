package com.producticity;

import lombok.Getter;

public class Constants {

    //Else
    public final static long TIME_TO_GO_NOT_SAVED = -1;

    public final static String TIMER_SUCCES = "timer_success";
    public final static String TIMER_FAILURE = "timer_failure";

    public final static int TIMER_NOT_INTENT_STOP = 123;
    public final static int TIMER_NOT_INTENT_TASKS = 124;
    public final static int TIMER_NOT_INTENT_SMALLREST = 125;
    public final static int TIMER_NOT_INTENT_BIGREST = 126;

    public final static String TIMER_NOT_INTENT_KEY = "intentType";

    public final static String DIALOG_SUCCESS_TAG = "dialog_success";
    public final static int DEFAULT_MIN_TIMER_VALUE = 600;

    public final static int DEFAULT_SHORT_BREAK_VALUE = 300;
    public final static int DEFAULT_LONG_BREAK_VALUE = 600;


    public final static int DEFAULT_MIN_TIME = 600;
    public final static int DEFAULT_MAX_TIME = 3600;


    public final static boolean DEFAULT_IN_NOTIF_BAR = true;
    public final static boolean DEFAULT_START_END_SOUND = false;
    public final static boolean DEFAULT_WIN_NOTIF = false;
    public final static boolean DEFAULT_24HDELETE = true;

    public final static int DEFAULT_PEOPLE_PER_30SEC = 3;

    public final static long DEFAULT_TIME_AFTER_NOT_SHOW = 86400000;

    @Getter
    public final static int[] dataSet = new int[]{5, 10, 15, 20, 25};

}
