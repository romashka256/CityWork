package com.citywork.utils;

public class Calculator {
    public static String getMinutesAndSecondsFromSeconds(long allseconds) {
            long minutes = allseconds / 60;
            long seconds = allseconds % 60;
            return String.format("%02d:%02d", minutes, seconds);
    }

    public static long getRemainingTime(long stopTimerTime) {
        long current = System.currentTimeMillis();
        return (stopTimerTime - current) / 1000;
    }
}
