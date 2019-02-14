package com.citywork.utils.timer;

import io.reactivex.subjects.BehaviorSubject;

public interface TimerBase {

    void setTimerListener(TimerListener timerListener);

    BehaviorSubject<Long> createTimer(long time);
    void startTimer(BehaviorSubject<Long> behaviorSubject);

    void stopTimer();

    void pauseTimer();

    boolean resumeTimer();

    long getRemainingTime();

    String getReminingTimeInString();

}
