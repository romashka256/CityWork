package com.citywork.utils.timer;

import com.citywork.model.db.models.Pomodoro;

import io.reactivex.subjects.BehaviorSubject;

public interface TimerManager {
    BehaviorSubject<Long> startTimer(long time, Pomodoro pomodoro);

    BehaviorSubject<Long> getTimer();

    long getRemainingTime();

    void stopTimer(Pomodoro pomodoro);

    void pauseTimer();

    String getReminingTimeInString();

    void setTimerListener(TimerStateListener timerListener);
}
