package com.citywork.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Pair;

import com.citywork.App;
import com.citywork.model.db.DataBaseHelper;
import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.City;
import com.citywork.model.interfaces.OnCitiesLoadedListener;
import com.citywork.utils.CityUtils;
import com.citywork.utils.PomodoroManger;
import com.citywork.viewmodels.interfaces.ICityFragmentViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class CityFragmentViewModel extends ViewModel implements ICityFragmentViewModel {

    private DataBaseHelper dataBaseHelper;
    @Setter
    @Getter
    private List<City> cities;

    private MutableLiveData<List<City>> citiesCreatedEvent = new MutableLiveData<>();
    @Getter
    private MutableLiveData<Integer> mCityPeopleCountChangeEvent = new MutableLiveData<>();

    private List<Building> buildingList;
    private CityUtils cityUtils;
    private PomodoroManger pomodoroManger;

    public CityFragmentViewModel() {
        dataBaseHelper = App.getsAppComponent().getDataBaseHelper();
        pomodoroManger = App.getsAppComponent().getPomdoromManager();

        //TODO INJECT
        cityUtils = new CityUtils();
    }

    @Override
    public LiveData<Integer> getmCityPeopleCountChangeEvent() {
        return mCityPeopleCountChangeEvent;
    }

    @Override
    public void onCreate() {
        dataBaseHelper.loadCities(cityList -> {
            this.cities = cityList;
            citiesCreatedEvent.postValue(cityUtils.getCityList(cityList));
        });

        mCityPeopleCountChangeEvent.setValue(pomodoroManger.getCityPeopleCount());

//        dataBaseHelper.loadAllCompletedBuildings(buildings -> {
//            this.c = buildings;
//
//            citiesCreatedEvent.postValue(sortBuildings(buildings));
//        });
    }

    private List<Pair<Date, List<Building>>> sortBuildings(List<Building> buildings) {
        List<Pair<Date, List<Building>>> sortedPairs = new ArrayList<>();
        Date date, prevdate, current;
        Pair<Date, List<Building>> pair;
        Calendar prevCalendar = null;
        List<Building> listForPair = new ArrayList<>();
        Calendar currentCalendar = Calendar.getInstance();
        current = new Date(System.currentTimeMillis());
        currentCalendar.setTime(current);
        for (int i = 0; i < buildings.size(); i++) {
            Building building = buildings.get(i);

            date = new Date(building.getPomodoro().getStoptime());

            if (i == buildings.size() - 1) {
                listForPair.add(building);
                pair = new Pair<>(date, listForPair);
                sortedPairs.add(pair);
                break;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            if (prevCalendar != null && prevCalendar.get(Calendar.DAY_OF_MONTH) != calendar.get(Calendar.DAY_OF_MONTH)) {
                pair = new Pair<>(date, listForPair);
                sortedPairs.add(pair);
                listForPair = new ArrayList<>();
            }

            prevdate = new Date(building.getPomodoro().getStoptime());

            prevCalendar = Calendar.getInstance();
            prevCalendar.setTime(prevdate);

            listForPair.add(building);
        }
        return sortedPairs;
    }

    @Override
    public LiveData<List<City>> getCitiesLoaded() {
        return citiesCreatedEvent;
    }
}
