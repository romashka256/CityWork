package com.citywork.utils.timer;

import io.reactivex.subjects.BehaviorSubject;

public interface TimerManager {
    BehaviorSubject<Long> startTimer(long time);

    BehaviorSubject<Long> getTimer();

    long getRemainingTime();

    void stopTimer();

    void pauseTimer();

    String getReminingTimeInString();

    void setTimerListener(TimerStateListener timerListener);
}
