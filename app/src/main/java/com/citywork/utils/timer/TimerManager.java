package com.citywork.utils.timer;

public interface TimerManager {
    void startTimer(long time);

    boolean resumeTimer();

    long getRemainingTime();

    void stopTimer();

    void pauseTimer();

    String getReminingTimeInString();

    void setTimerStateListener(TimerStateListener timerListener);
    void setTimerListener(TimerListener timerListener);



}
