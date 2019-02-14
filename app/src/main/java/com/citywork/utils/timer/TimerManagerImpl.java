package com.citywork.utils.timer;

import android.annotation.SuppressLint;

import com.citywork.App;
import com.citywork.utils.SharedPrefensecUtils;

import io.reactivex.subjects.BehaviorSubject;
import lombok.Getter;
import lombok.Setter;

public class TimerManagerImpl implements TimerManager {

    private TimerBase timer;

    @Getter
    private long remainingTime;

    BehaviorSubject<Long> behaviorSubject;

    private TimerListener timerListener;
    private TimerStateListener timerStateListener;

    @Getter
    @Setter
    public SharedPrefensecUtils sharedPrefensecUtils;

    private long timerTime;


    public TimerManagerImpl() {
        //TODO INJECT
        sharedPrefensecUtils = new SharedPrefensecUtils(App.getsAppComponent().getApplicationContext());
        behaviorSubject = BehaviorSubject.create();

    }


    @Override
    public void setTimerStateListener(TimerStateListener timerListener) {
        timerStateListener = timerListener;
    }

    @Override
    public void setTimerListener(TimerListener timerListener) {
        this.timerListener = timerListener;
    }

    @SuppressLint("CheckResult")
    public void startTimer(long time) {
        timerStateListener.onStart();
        timer.setTimerListener(new TimerListener() {
            @Override
            public void onTimerTick(long time) {
                timerListener.onTimerTick(time);
            }

            @Override
            public void onTimerComplete() {
                timerListener.onTimerComplete();
            }

            @Override
            public void onTimerError() {
                timerListener.onTimerError();
            }
        });

        timer.startTimer(time);
    }

    @Override
    public boolean resumeTimer() {
        if(timer.resumeTimer()){

        }
    }

    @Override
    public void stopTimer() {
        timerStateListener.onStop();
        timer.stopTimer();
    }

    @Override
    public void pauseTimer() {
        timer.pauseTimer();
    }

    @Override
    public String getReminingTimeInString() {
        return timer.getReminingTimeInString();
    }
}
