package com.citywork.utils.timer;

import android.annotation.SuppressLint;

import com.citywork.App;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.utils.Calculator;
import com.citywork.utils.SharedPrefensecUtils;

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

    private Pomodoro pomodoro;

    private Disposable disposable;

    private TimerStateListener timerStateListener;

    @Override
    public void setTimerListener(TimerStateListener timerListener) {
        timerStateListener = timerListener;
    }

    @Getter
    @Setter
    public SharedPrefensecUtils sharedPrefensecUtils;

    public TimerManagerImpl(Timer timer) {
        //TODO INJECT
        sharedPrefensecUtils = new SharedPrefensecUtils(App.getsAppComponent().getApplicationContext());
        behaviorSubject = BehaviorSubject.create();

        this.timer = timer;
    }


    @SuppressLint("CheckResult")
    @Override
    public BehaviorSubject<Long> startTimer(long time, Pomodoro pomodoro) {
        this.pomodoro = pomodoro;
        pomodoro.setTimerState(TimerState.ONGOING);
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
                            pomodoro.setTimerState(TimerState.NOT_ONGOING);
                        },
                        () -> {
                            behaviorSubject.onComplete();
                            pomodoro.setTimerState(TimerState.WORK_COMPLETED);
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
    public void stopTimer(Pomodoro pomodoro) {
        Timber.i("Stopping timer");
        timerStateListener.onStop();
        pomodoro.setTimerState(TimerState.NOT_ONGOING);
        timer.stopTimer();
        if (disposable != null && !disposable.isDisposed()) {
            Timber.i("Disposing");
            disposable.dispose();
        }
    }

    @Override
    public void pauseTimer() {
        timerStateListener.onStop();

//        if (disposable != null && !disposable.isDisposed()) {
//            Timber.i("Disposing");
//            disposable.dispose();
//        }
    }
}
