package com.producticity.model.interfaces;

import com.producticity.model.db.models.Pomodoro;

public interface OnPomodoroLoaded {
    void onLoaded(Pomodoro pomodoro);
}
