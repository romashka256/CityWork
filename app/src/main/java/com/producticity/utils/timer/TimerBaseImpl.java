package com.producticity.utils.timer;

import com.producticity.utils.Calculator;

import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import lombok.Getter;
import timber.log.Timber;

public class TimerBaseImpl implements TimerBase {

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

    public TimerBaseImpl(Timer timer) {
        this.timer = timer;
    }

    @Override
    public BehaviorSubject<Long> startTimer(long time) {
        Timber.i("Creating new BehaviourSubject with initial time : %d", time);
        behaviorSubject = BehaviorSubject.createDefault(time);

        timerStateListener.onStart();
        disposable = timer.startTimer(time)
                .subscribe(ticktime -> {
                    //        Timber.i("ticktime %d", ticktime);
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
