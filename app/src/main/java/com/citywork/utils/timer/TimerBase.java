package com.citywork.utils.timer;

import com.citywork.model.db.models.Pomodoro;

import io.reactivex.subjects.BehaviorSubject;

public interface TimerBase {

    void setTimerListener(TimerListener timerListener);

    BehaviorSubject<Long> createTimer(long time);
    void startTimer(BehaviorSubject<Long> behaviorSubject, Pomodoro pomodoro);

    void stopTimer();

    void pauseTimer();

    boolean resumeTimer(Pomodoro pomodoro);

    long getRemainingTime();

    String getReminingTimeInString();

}
