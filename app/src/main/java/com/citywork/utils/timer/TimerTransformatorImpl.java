package com.citywork.utils.timer;

import com.citywork.utils.Calculator;

import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import lombok.Getter;
import timber.log.Timber;

public class TimerTransformatorImpl implements TimerTransformator {

    private Timer timer;
    @Getter
    private long remainingTime;

    BehaviorSubject<Long> behaviorSubject;

    @Getter
    private Disposable disposable;

    private TimerStateListener timerStateListener;

    private long timerTime;


    public TimerTransformatorImpl(Timer timer) {
        //TODO INJECT
        behaviorSubject = BehaviorSubject.create();

        this.timer = timer;
    }

    @Override
    public BehaviorSubject<Long> startTimer(long time) {
        timerTime = time;
        Timber.i("Creating new BehaviourSubject with initial time : %d", time);
        behaviorSubject = BehaviorSubject.createDefault(time);
        disposable = timer.startTimer(time)
                .subscribe(ticktime -> {
                            Timber.i("ticktime %d", ticktime);
                            Timber.i("time %d", time);
                            remainingTime = time - ticktime;

                            behaviorSubject.onNext(remainingTime);
                        }, e -> {
                            Timber.e(e);
                            behaviorSubject.onError(e);
                            //sharedPrefensecUtils.saveTimerState(TimerState.NOT_ONGOING);
                            Timber.i(e);
                        },
                        () -> {
                            behaviorSubject.onComplete();
                            //sharedPrefensecUtils.saveTimerState(TimerState.COMPLETED);
                            Timber.i("onComplete");
                        });
        return behaviorSubject;
    }

    @Override
    public BehaviorSubject<Long> getTimer() {
        return behaviorSubject;
    }


    @Override
    public void stopTimer() {
        Timber.i("Stopping timer");
        //timerState = TimerState.NOT_ONGOING;

        //sharedPrefensecUtils.saveTimerState(TimerState.NOT_ONGOING);
        timer.stopTimer();
        if (disposable != null && !disposable.isDisposed()) {
            Timber.i("Disposing");
            disposable.dispose();
        }
    }

    @Override
    public boolean isDisposed() {
        if (disposable != null) {
            return disposable.isDisposed();
        } else {
            return true;
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
    public String getReminingTimeInString() {
        return Calculator.getMinutesAndSecondsFromSeconds(remainingTime);
    }

    @Override
    public void setTimerListener(TimerStateListener timerListener) {
        this.timerStateListener = timerListener;
    }
}
