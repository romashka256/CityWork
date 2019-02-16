package com.citywork.utils.timer;

import com.citywork.model.db.models.Pomodoro;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class WorkTimer implements TimerBase {

    private TimerTransformator timerTransformator;
    private TimerListener timerListener;
    private Disposable disposable;

    public WorkTimer(TimerTransformator timerTransformator) {
        this.timerTransformator = timerTransformator;
    }

    @Override
    public BehaviorSubject<Long> createTimer(long time) {
        return timerTransformator.startTimer(time);
    }

    @Override
    public void setTimerListener(TimerListener timerListener) {
        this.timerListener = timerListener;

    }

    @Override
    public void startTimer(BehaviorSubject<Long> behaviorSubject, Pomodoro pomodoro) {
        disposable = behaviorSubject.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> {
                    timerListener.onTimerTick(t);
                }, e -> {
                    pomodoro.setTimerState(TimerState.NOT_ONGOING);
                    timerListener.onTimerError();
                }, () -> {
                    pomodoro.setTimerState(TimerState.WORK_COMPLETED);
                    timerListener.onTimerComplete();
                });
    }

    @Override
    public boolean resumeTimer(Pomodoro pomodoro) {
        if (timerTransformator.getTimer() == null) {
            return false;
        } else if (disposable.isDisposed()) {
            return false;
        } else {
            startTimer(timerTransformator.getTimer(), pomodoro);
            return true;
        }
    }

    @Override
    public void stopTimer() {
        timerTransformator.stopTimer();
    }

    @Override
    public void pauseTimer() {
        timerTransformator.pauseTimer();
    }

    @Override
    public long getRemainingTime() {
        return timerTransformator.getRemainingTime();
    }

    @Override
    public String getReminingTimeInString() {
        return timerTransformator.getReminingTimeInString();
    }
}
