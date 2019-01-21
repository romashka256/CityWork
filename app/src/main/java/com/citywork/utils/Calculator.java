package com.citywork.utils;

public class Calculator {
    public static String getMinutesAndSecondsFromSeconds(long allseconds) {
        if (allseconds > 0) {
            long minutes = allseconds / 60;
            minutes = minutes <= 0 ? minutes : 0;
            long seconds = allseconds % 60;

            return minutes + ":" + seconds;
        } else {
            return "00:00";
        }
    }

    public static long getRemainingTime(long stopTimerTime) {
        long current = System.currentTimeMillis();
        return (stopTimerTime - current) / 1000;
    }
}
