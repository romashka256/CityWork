package com.citywork.viewmodels;

import android.arch.lifecycle.ViewModel;

import com.citywork.App;
import com.citywork.Constants;
import com.citywork.SingleLiveEvent;
import com.citywork.model.db.DBHelper;
import com.citywork.model.db.models.Pomodoro;
import com.citywork.model.db.models.Task;
import com.citywork.utils.CityManager;
import com.citywork.utils.SharedPrefensecUtils;
import com.citywork.utils.TaskValidator;
import com.citywork.viewmodels.interfaces.ITasksDialogViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class TasksDialogViewModel extends ViewModel implements ITasksDialogViewModel {

    private DBHelper dataBaseHelper;

    private SingleLiveEvent<List<Pomodoro>> newPomodorosEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> noTasksEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<List<Pomodoro>> updatePomodoroListEvent = new SingleLiveEvent<>();

    private List<Pomodoro> pomodoros;
    private TaskValidator taskValidator;
    private String currentTask;
    private CityManager cityManager;
    private SharedPrefensecUtils sharedPrefensecUtils;
    private CompositeDisposable disposables = new CompositeDisposable();

    private boolean isEmpty = true;

    @Override
    public void onCreate() {
        dataBaseHelper = App.getsAppComponent().getDataBaseHelper();
        cityManager = App.getsAppComponent().getPomdoromManager();
        sharedPrefensecUtils = App.getsAppComponent().getSharedPrefs();

        //TODO INJECT
        taskValidator = new TaskValidator();

        long timeTerm = 0;

        if (!sharedPrefensecUtils.get24hDelete()) {
            timeTerm = System.currentTimeMillis() - Constants.DEFAULT_TIME_AFTER_NOT_SHOW;
        }

        disposables.add(dataBaseHelper.getTasks(timeTerm)
                .doOnSuccess(list -> {
                    this.pomodoros = list;

                    isEmpty = createPomodoroList();

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    newPomodorosEvent.postValue(pomodoros);

                    if (isEmpty)
                        noTasksEvent.postValue(true);
                }, Timber::e));
    }

    private boolean createPomodoroList() {
        boolean added = false;

        List<Task> tasks = new ArrayList<>();

        for (int i = 0; i < pomodoros.size(); i++) {
            Pomodoro pom = pomodoros.get(i);
            if (pom.getId().equals(cityManager.getPomodoro().getId())) {
                pomodoros.set(i, cityManager.getPomodoro());
                added = true;
            }

            tasks.addAll(pom.getTasks() != null ? pom.getTasks() : new ArrayList<>());
        }
        if (!added) {
            pomodoros.add(cityManager.getPomodoro());
        }

        return tasks.isEmpty();
    }

    @Override
    public SingleLiveEvent<Boolean> getNoTasksEvent() {
        return noTasksEvent;
    }

    @Override
    public void onStop() {
        disposables.clear();
    }

    @Override
    public void onTextChanged(String s) {
        currentTask = s;
    }

    @Override
    public void onAddClicked() {
        if (taskValidator.isValid(currentTask)) {

            Task task = new Task(currentTask);
            cityManager.addTask(task);

            dataBaseHelper.saveBuilding(cityManager.getBuilding());

            newPomodorosEvent.postValue(pomodoros);
            noTasksEvent.postValue(false);

        }
    }

    @Override
    public void onDismiss() {

    }

    @Override
    public void onTaskClicked(Task task) {
        dataBaseHelper.saveTask(task);
    }

    @Override
    public SingleLiveEvent<List<Pomodoro>> getPomodoroLoadedEvent() {
        return newPomodorosEvent;
    }

    @Override
    public SingleLiveEvent<List<Pomodoro>> getUpdatePomodoroListEvent() {
        return updatePomodoroListEvent;
    }

}