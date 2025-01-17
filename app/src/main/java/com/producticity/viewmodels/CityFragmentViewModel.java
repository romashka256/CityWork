package com.producticity.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.producticity.App;
import com.producticity.model.db.DataBaseHelper;
import com.producticity.model.db.models.Building;
import com.producticity.model.db.models.City;
import com.producticity.utils.Calculator;
import com.producticity.utils.CityManager;
import com.producticity.utils.chart.BarModeState;
import com.producticity.utils.chart.ChartBar;
import com.producticity.utils.CityUtils;
import com.producticity.utils.chart.CustomChartUtils;
import com.producticity.utils.chart.StatusticUtils;
import com.producticity.viewmodels.interfaces.ICityFragmentViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class CityFragmentViewModel extends ViewModel implements ICityFragmentViewModel {

    private DataBaseHelper dataBaseHelper;
    @Getter
    private List<City> cities;

    private MutableLiveData<List<City>> citiesCreatedEvent = new MutableLiveData<>();

    private MutableLiveData<Integer> mCityPeopleCountChangeEvent = new MutableLiveData<>();

    @Getter
    private MutableLiveData<List<Building>> chartBarSelectedEvent = new MutableLiveData<>();
    @Getter
    private MutableLiveData<List<ChartBar>> barModeStateChangedEvent = new MutableLiveData<>();

    private List<Building> buildingList;
    private CityUtils cityUtils;
    private CityManager cityManager;
    private CustomChartUtils customChartUtils;
    private Context context;
    private StatusticUtils statusticUtils;

    @Getter
    @Setter
    private List<String> curLabels;

    @Getter
    private BarModeState barModeState;

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public CityFragmentViewModel() {
        this.barModeState = BarModeState.DAY;
        dataBaseHelper = App.getsAppComponent().getDataBaseHelper();
        cityManager = App.getsAppComponent().getPomdoromManager();
        statusticUtils = App.getsAppComponent().getStatisticsUtils();
        context = App.getsAppComponent().getApplicationContext();

        //TODO INJECT
        customChartUtils = new CustomChartUtils();
        cityUtils = new CityUtils();
    }

    @Override
    public LiveData<Integer> getmCityPeopleCountChangeEvent() {
        return mCityPeopleCountChangeEvent;
    }

    @Override
    public void onCreate() {
        citiesCreatedEvent.postValue(statusticUtils.getCities());

        mCityPeopleCountChangeEvent.setValue(cityManager.getCityPeopleCount());
    }

    @Override
    public LiveData<List<City>> getCitiesLoaded() {
        return citiesCreatedEvent;
    }

    @Override
    public City getTodayCity() {
        if (cities != null && !cities.isEmpty() && cities.get(0) != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(System.currentTimeMillis()));
            Calendar cityDate = Calendar.getInstance();
            cityDate.setTime(cities.get(0).getDate());

            if (cityDate.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && calendar.get(Calendar.MONTH) == cityDate.get(Calendar.MONTH)) {
                return cities.get(0);
            } else {
                return new City();
            }
        } else {
            return new City();
        }
    }


    @Override
    public void onChartSelected(int value) {
        chartBarSelectedEvent.setValue(statusticUtils.getStatisticData().get(barModeState).second.get(value));
    }

    @Override
    public void onDaySelected() {
        curLabels = statusticUtils.getDayLabels();
        this.barModeState = BarModeState.DAY;
        List<ChartBar> list = statusticUtils.getStatisticData().get(barModeState).first;

        barModeStateChangedEvent.postValue(customChartUtils.createBars(list));
    }

    @Override
    public void onWeekSelected() {
        this.barModeState = BarModeState.WEEK;
        curLabels = statusticUtils.getLongTermLabels(barModeState);
        List<ChartBar> list = statusticUtils.getStatisticData().get(barModeState).first;

        barModeStateChangedEvent.postValue(customChartUtils.createBars(list));
    }

    @Override
    public void onMonthSelected() {
        this.barModeState = BarModeState.MONTH;

        curLabels =  statusticUtils.getLongTermLabels(barModeState);
        List<ChartBar> list = statusticUtils.getStatisticData().get(barModeState).first;

        barModeStateChangedEvent.postValue(customChartUtils.createBars(list));
    }

    public List<Integer> calculateStat(List<Building> buildings) {
        List<Integer> integers = new ArrayList<>();

        int minutes = 0;
        int pomo = 0;
        int people = 0;

        for (Building building : buildings) {
            minutes += Calculator.getTime(building.getPomodoro().getStarttime(), building.getPomodoro().getStoptime()) / 60;
            pomo++;

            people += building.getPeople_count();
        }

        integers.add(minutes);
        integers.add(pomo);
        integers.add(people);

        return integers;
    }

    @Override
    public void onYearSelected() {
        this.barModeState = BarModeState.YEAR;

        curLabels =  statusticUtils.getLongTermLabels(barModeState);
        List<ChartBar> list = statusticUtils.getStatisticData().get(barModeState).first;

        barModeStateChangedEvent.postValue(customChartUtils.createBars(list));
    }
}
