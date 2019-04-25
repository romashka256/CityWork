package com.citywork.viewmodels;

import android.arch.lifecycle.ViewModel;

import com.citywork.App;
import com.citywork.Constants;
import com.citywork.SingleLiveEvent;
import com.citywork.model.db.DBHelper;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.model.db.models.Task;
import com.citywork.utils.PomodoroManger;
import com.citywork.utils.SharedPrefensecUtils;
import com.citywork.utils.TaskValidator;
import com.citywork.viewmodels.interfaces.ITasksDialogViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TasksDialogViewModel extends ViewModel implements ITasksDialogViewModel {

    private DBHelper dataBaseHelper;
    private SingleLiveEvent<List<Pomodoro>> newPomodorosEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> noTasksEvent = new SingleLiveEvent<>();
    private List<Pomodoro> pomodoros;
    private TaskValidator taskValidator;
    private String currentTask;
    private PomodoroManger pomodoroManger;
    private SharedPrefensecUtils sharedPrefensecUtils;

    @Override
    public void onCreate() {
        dataBaseHelper = App.getsAppComponent().getDataBaseHelper();
        pomodoroManger = App.getsAppComponent().getPomdoromManager();
        sharedPrefensecUtils = App.getsAppComponent().getSharedPrefs();

        //TODO INJECT
        taskValidator = new TaskValidator();

        currentTask = "";

        long timeTerm = 0;

        if (!sharedPrefensecUtils.get24hDelete()) {
            timeTerm = System.currentTimeMillis() - Constants.DEFAULT_TIME_AFTER_NOT_SHOW;
        }

        dataBaseHelper.getTasks(timeTerm
                , pomodoros -> {
                    this.pomodoros = pomodoros;

                    boolean added = false;
                    List<Task> tasks = new ArrayList<>();
                    for (int i = 0; i < pomodoros.size(); i++) {
                        Pomodoro pom = pomodoros.get(i);
                        if (pom.getId().equals(pomodoroManger.getPomodoro().getId())) {
                            pomodoros.remove(i);
                            pomodoros.add(i, pomodoroManger.getPomodoro());
                            added = true;
                        }
                        tasks.addAll(pom.getTasks());
                    }

                    if (!added) {
                        pomodoros.add(pomodoroManger.getPomodoro());
                    }

                    newPomodorosEvent.postValue(pomodoros);

                    if (tasks.isEmpty())
                        noTasksEvent.postValue(true);
                });
    }


    @Override
    public SingleLiveEvent<Boolean> getNoTasksEvent() {
        return noTasksEvent;
    }

    @Override
    public void onPositionChanged() {

    }

    @Override
    public void onChecked() {

    }

    @Override
    public SingleLiveEvent<List<Pomodoro>> getPomodoroLoadedEvent() {
        return newPomodorosEvent;
    }

    @Override
    public void addTask(String text) {

    }

    @Override
    public void onTextChanged(String s) {
        currentTask = s;
    }

    @Override
    public void onAddClicked() {
        if (taskValidator.isValid(currentTask)) {
            Task task = new Task(currentTask);
            pomodoroManger.getPomodoro().getTasks().add(task);
            dataBaseHelper.savePomodoro(pomodoroManger.getPomodoro());
            newPomodorosEvent.postValue(pomodoros);
        }
    }

    @Override
    public void onDismiss() {

    }

    @Override
    public void onTaskClicked(Task task) {
        dataBaseHelper.saveTask(task);
    }
}