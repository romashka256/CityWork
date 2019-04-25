package com.citywork.utils.timer;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import timber.log.Timber;

public class TimerImpl implements Timer {

    private boolean stopped;

    public TimerImpl() {
        stopped = false;
    }

    /**
     *  @param time in seconds
     **/
    @Override
    public Flowable<Long> startTimer(long time) {
        stopped = false;

        Timber.i("startTimer");
        return Flowable.interval(1, TimeUnit.SECONDS).take(time)
                .takeWhile(time1 -> !stopped || time1 >= 0);
    }

    @Override
    public void stopTimer() {
        stopped = true;
    }

}
