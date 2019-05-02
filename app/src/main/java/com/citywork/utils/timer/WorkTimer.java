package com.citywork.utils.timer;

import android.util.Pair;

import com.citywork.model.db.models.Task;
import com.citywork.ui.listeners.OnTaskClickListener;
import com.citywork.utils.Calculator;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import lombok.Getter;

public class WorkTimer extends TimerBaseImpl {

    @Getter
    private TimerType timerType = TimerType.WORK;

    public WorkTimer(Timer timer) {
        super(timer);
    }

    private Disposable disposable;

    @Override
    public BehaviorSubject<Long> startTimer(long startTime) {
        return super.startTimer(startTime);

    }
}
