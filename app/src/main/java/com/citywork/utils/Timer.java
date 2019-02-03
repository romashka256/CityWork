package com.citywork.utils;

import io.reactivex.Flowable;

public interface Timer {
    Flowable<Long> startTimer(long time);
    void
    stopTimer();
}
