package com.citywork.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.citywork.App;
import com.citywork.Constants;
import com.citywork.SingleLiveEvent;
import com.citywork.model.db.DBHelper;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.model.db.models.Task;
import com.citywork.utils.PomodoroManger;
import com.citywork.utils.TaskValidator;
import com.citywork.viewmodels.interfaces.ITasksDialogViewModel;

import java.util.List;

public class TasksDialogViewModel extends ViewModel implements ITasksDialogViewModel {

    private DBHelper dataBaseHelper;
    private SingleLiveEvent<List<Pomodoro>> newPomodorosEvent = new SingleLiveEvent<>();
    private List<Pomodoro> pomodoros;
    private TaskValidator taskValidator;
    private String currentTask;
    private PomodoroManger pomodoroManger;

    @Override
    public void onCreate() {
        dataBaseHelper = App.getsAppComponent().getDataBaseHelper();
        pomodoroManger = App.getsAppComponent().getPomdoromManager();

        //TODO INJECT
        taskValidator = new TaskValidator();

        currentTask = "";

        dataBaseHelper.getTasks(System.currentTimeMillis() - Constants.DEFAULT_TIME_AFTER_NOT_SHOW
                , pomodoros -> {
                    this.pomodoros = pomodoros;
                    newPomodorosEvent.postValue(pomodoros);
                });
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
            //pomodoros.get(pomodoros.size() - 1).getTasks().add(task);
            //newPomodorosEvent.postValue(pomodoros);
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