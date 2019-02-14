package com.citywork.utils.timer;

import io.reactivex.subjects.BehaviorSubject;

public interface TimerTransformator {
    BehaviorSubject<Long> startTimer(long time);

    BehaviorSubject<Long> getTimer();

    boolean isDisposed();

    long getRemainingTime();

    void stopTimer();

    void pauseTimer();

    String getReminingTimeInString();

    void setTimerListener(TimerStateListener timerListener);
}
