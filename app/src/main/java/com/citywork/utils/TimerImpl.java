package com.citywork.utils;

import io.reactivex.Flowable;
import timber.log.Timber;

import java.util.concurrent.TimeUnit;

public class TimerImpl implements Timer {

    private boolean stopped;

    public TimerImpl() {
        stopped = false;
    }

    @Override
    public Flowable<Long> startTimer(long time) {
        stopped = false;

        Timber.i("startTimer");
        return Flowable.interval(1, TimeUnit.SECONDS).take(time)
                .takeWhile(unused -> !stopped);
    }

    @Override
    public void stopTimer() {
        stopped = true;
    }
}
