package com.citywork.utils.timer;

import android.annotation.SuppressLint;

import com.citywork.App;
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

        this.timer = timer;
    }


    @SuppressLint("CheckResult")
    @Override
    public BehaviorSubject<Long> startTimer(long time) {
        Timber.i("Creating new BehaviourSubject with initial time : %d", time);
        behaviorSubject = BehaviorSubject.createDefault(time);
        timerStateListener.onStart();
        disposable = timer.startTimer(time)
                .subscribe(ticktime -> {
                            Timber.i("ticktime %d", ticktime);
                            remainingTime = time - ticktime;

                            behaviorSubject.onNext(remainingTime);
                        }, e -> {
                            Timber.e(e);
                            behaviorSubject.onError(e);
                        },
                        () -> {
                            behaviorSubject.onComplete();
                            Timber.i("onComplete");
                        });
        return behaviorSubject;
    }

    public BehaviorSubject<Long> getTimer() {
        timerStateListener.onStart();
        return behaviorSubject;
    }

    @Override
    public String getReminingTimeInString() {
        return Calculator.getMinutesAndSecondsFromSeconds(remainingTime);
    }

    @Override
    public void stopTimer() {
        Timber.i("Stopping timer");
        timerStateListener.onStop();
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
