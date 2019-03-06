package com.citywork.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Pair;

import com.citywork.App;
import com.citywork.model.db.DataBaseHelper;
import com.citywork.model.db.models.Building;
import com.citywork.model.interfaces.OnBuildingsLoadedListener;
import com.citywork.utils.timer.TimerState;
import com.citywork.viewmodels.interfaces.ICityFragmentViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CityFragmentViewModel extends ViewModel implements ICityFragmentViewModel {

    private DataBaseHelper dataBaseHelper;
    private List<Building> buildings;

    private MutableLiveData<List<Pair<Date, List<Building>>>> citiesCreatedEvent = new MutableLiveData<>();

    public CityFragmentViewModel() {
        dataBaseHelper = App.getsAppComponent().getDataBaseHelper();

    }

    @Override
    public void onCreate() {
        dataBaseHelper.loadAllCompletedBuildings(buildings -> {
            this.buildings = buildings;

            citiesCreatedEvent.postValue(sortBuildings(buildings));
        });
    }

    private List<Pair<Date, List<Building>>> sortBuildings(List<Building> buildings) {
        List<Pair<Date, List<Building>>> sortedPairs = new ArrayList<>();
//        Date date, prevdate;
        Pair<Date, List<Building>> pair;
//        Calendar calendar = Calendar.getInstance();
        Calendar prevCalendar = Calendar.getInstance();
        List<Building> listForPair = new ArrayList<>();
        for (Building building : buildings) {

            Date date = new Date(building.getPomodoro().getStoptime());
            Date prevdate = new Date(building.getPomodoro().getStoptime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            if (prevCalendar.get(Calendar.DAY_OF_YEAR) != calendar.get(Calendar.DAY_OF_YEAR) || building.getPomodoro().getTimerState() == TimerState.COMPLETED) {
                listForPair = new ArrayList<>();
                prevCalendar.setTime(prevdate);
                pair = new Pair<>(date, listForPair);
                sortedPairs.add(pair);
            }
            listForPair.add(building);
        }
        return sortedPairs;
    }

    @Override
    public LiveData<List<Pair<Date, List<Building>>>> getCitiesLoaded() {
        return citiesCreatedEvent;
    }
}
