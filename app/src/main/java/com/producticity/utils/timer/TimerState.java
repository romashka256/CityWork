package com.producticity.utils.timer;

public class TimerState {

    //Pomodoro not started yet
    public static final int NOT_ONGOING = 1;
    //Pomodoro ongoing
    public static final int ONGOING = 2;
    //Pomodoro was canceled
    public static final int CANCELED = 3;
    //Work timer ended
    public static final int WORK_COMPLETED = 4;
    //User need to choose rest time and start rest timer
    public static final int REST = 5;
    //Rest timer ongoing
    public static final int REST_ONGOING = 6;
    //Rest timer was canceled
    public static final int REST_CANCELED = 7;
    //Rest completed. FULL
    public static final int COMPLETED = 8;

}