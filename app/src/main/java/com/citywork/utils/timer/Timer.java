package com.citywork.utils.timer;

import io.reactivex.Flowable;

public interface Timer {
    Flowable<Long> startTimer(long time);
    void
    stopTimer();
}
