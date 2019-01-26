package com.citywork.utils;

import com.citywork.Constants;

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

    public static int calculatePeopleCount(long time) {
        return (int) ((time / 30) * Constants.DEFAULT_PEOPLE_PER_30SEC);
    }

    public static long getTime(long startTime, long stopTime){
        return (stopTime - startTime) / 1000;
    }
}
