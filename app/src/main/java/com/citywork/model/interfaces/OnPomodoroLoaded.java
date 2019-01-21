package com.citywork.model.interfaces;

import com.citywork.model.db.models.Pomodoro;

public interface OnPomodoroLoaded {
    void onLoaded(Pomodoro pomodoro);
}
