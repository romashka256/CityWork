package com.citywork.utils;

import android.annotation.SuppressLint;
import com.citywork.App;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

public class TimerManagerImpl implements TimerManager {

    private Timer timer;
    @Getter
    private long remainingTime;

    BehaviorSubject<Long> behaviorSubject;

    private Disposable disposable;

    @Getter
    @Setter
    public SharedPrefensecUtils sharedPrefensecUtils;

    private long timerTime;
    private TimerState timerState;

    public TimerManagerImpl(Timer timer) {
        //TODO INJECT
        sharedPrefensecUtils = new SharedPrefensecUtils(App.getsAppComponent().getApplicationContext());
        behaviorSubject = BehaviorSubject.create();

        this.timer = timer;
    }

    @SuppressLint("CheckResult")
    @Override
    public BehaviorSubject<Long> startTimer(long time) {
        timerState = TimerState.ONGOING;
        sharedPrefensecUtils.saveTimerState(TimerState.ONGOING);
        timerTime = time;
        if(behaviorSubject.hasComplete()){
            behaviorSubject = BehaviorSubject.create();
        }
        disposable = timer.startTimer(time)
                .subscribe(ticktime -> {
                            Timber.i("ticktime %d", ticktime);
                            Timber.i("time %d", time);
                            remainingTime = time - ticktime;

                            behaviorSubject.onNext(remainingTime);
                        }, e -> {
                            Timber.e(e);
                            behaviorSubject.onError(e);
                            sharedPrefensecUtils.saveTimerState(TimerState.NOT_ONGOING);
                        },
                        () -> {
                            behaviorSubject.onComplete();
                            sharedPrefensecUtils.saveTimerState(TimerState.COMPLETED);
                            Timber.i("onComplete");
                        });
        return behaviorSubject;
    }

    public BehaviorSubject<Long> getTimer() {
        return behaviorSubject;
    }

    @Override
    public String getReminingTimeInString() {
        return Calculator.getMinutesAndSecondsFromSeconds(remainingTime);
    }

    @Override
    public void stopTimer() {
        timerState = TimerState.NOT_ONGOING;
        sharedPrefensecUtils.saveTimerState(TimerState.NOT_ONGOING);
        timer.stopTimer();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public TimerState getState() {
        return timerState;
    }
}
