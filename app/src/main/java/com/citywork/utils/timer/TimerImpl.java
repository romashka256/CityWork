package com.citywork.utils.timer;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import timber.log.Timber;

public class TimerImpl implements Timer {

    private boolean stopped;

    public TimerImpl() {
        stopped = false;
    }

    @Override
    public Flowable<Long> startTimer(long time) {
        stopped = false;

        Timber.i("startTimer");
        return Flowable.interval(100, TimeUnit.MILLISECONDS).take(time)
                .takeWhile(unused -> !stopped);
    }

    @Override
    public void stopTimer() {
        stopped = true;
    }
}
