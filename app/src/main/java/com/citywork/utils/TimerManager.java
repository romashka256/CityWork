package com.citywork.utils;

import io.reactivex.subjects.BehaviorSubject;

public interface TimerManager {
    BehaviorSubject<Long> startTimer(long time);

    BehaviorSubject<Long> getTimer();

    long getRemainingTime();

    void stopTimer();

    String getReminingTimeInString();

    TimerState getState();

























}
