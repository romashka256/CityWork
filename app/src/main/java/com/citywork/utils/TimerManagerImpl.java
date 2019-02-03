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

    private TimerStateListener timerStateListener;

    @Override
    public void setTimerListener(TimerStateListener timerListener) {
        timerStateListener = timerListener;
    }

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
        Timber.i("Creating new BehaviourSubject with initial time : %d", time);
        behaviorSubject = BehaviorSubject.createDefault(time);
        timerStateListener.onStart();
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
        Timber.i("Stopping timer");
        timerState = TimerState.NOT_ONGOING;
        timerStateListener.onStop();
        sharedPrefensecUtils.saveTimerState(TimerState.NOT_ONGOING);
        timer.stopTimer();
        if (disposable != null && !disposable.isDisposed()) {
            Timber.i("Disposing");
            disposable.dispose();
        }
    }


    @Override
    public void pauseTimer() {
        timerStateListener.onStop();
        timer.stopTimer();

        if (disposable != null && !disposable.isDisposed()) {
            Timber.i("Disposing");
            disposable.dispose();
        }
    }

    @Override
    public TimerState getState() {
        return timerState;
    }
}
