package com.citywork.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.citywork.App;
import com.citywork.R;
import com.citywork.model.db.DataBaseHelper;
import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.City;
import com.citywork.utils.chart.ChartUtils;
import com.citywork.utils.CityUtils;
import com.citywork.utils.PomodoroManger;
import com.citywork.viewmodels.interfaces.ICityFragmentViewModel;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

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
    @Getter
    private MutableLiveData<Integer> mCityPeopleCountChangeEvent = new MutableLiveData<>();

    private ChartUtils chartUtils;

    @Getter
    private MutableLiveData<ArrayList<IBarDataSet>> chartBarSelectedEvent = new MutableLiveData<>();

    @Getter
    private MutableLiveData<ArrayList<IBarDataSet>> barModeStateChangedEvent = new MutableLiveData<>();

    private List<Building> buildingList;
    private CityUtils cityUtils;
    private PomodoroManger pomodoroManger;
    private Context context;

    private BarModeState barModeState;

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public CityFragmentViewModel() {
        this.barModeState = BarModeState.DAY;
        dataBaseHelper = App.getsAppComponent().getDataBaseHelper();
        pomodoroManger = App.getsAppComponent().getPomdoromManager();

        context = App.getsAppComponent().getApplicationContext();

        //TODO INJECT
        cityUtils = new CityUtils();
        chartUtils = new ChartUtils(context.getResources().getColor(R.color.barcolor), context.getResources().getColor(R.color.blue));
    }

    @Override
    public LiveData<Integer> getmCityPeopleCountChangeEvent() {
        return mCityPeopleCountChangeEvent;
    }

    @Override
    public void onCreate() {
        dataBaseHelper.loadCities(cityList -> {
            citiesCreatedEvent.postValue(cityUtils.getCityList(cityList));
            this.cities = cityList;
        });

        mCityPeopleCountChangeEvent.setValue(pomodoroManger.getCityPeopleCount());

    }

    @Override
    public LiveData<List<City>> getCitiesLoaded() {
        return citiesCreatedEvent;
    }

    @Override
    public City getTodayCity() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        Calendar cityDate = Calendar.getInstance();
        cityDate.setTime(cities.get(cities.size() - 1).getDate());
        // TODO CHANGE
        if (cityDate.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && calendar.get(Calendar.MONTH) == cityDate.get(Calendar.MONTH)) {
            return cities.get(cities.size() - 1);
        } else {
            return null;
        }
    }


    @Override
    public void onChartSelected(int value) {
        //      chartBarSelectedEvent.setValue(chartUtils.getDataForToday(getTodayCity()).first);
    }

    @Override
    public void onDaySelected() {
        this.barModeState = BarModeState.DAY;
        barModeStateChangedEvent.postValue(chartUtils.getDataForToday(getTodayCity()).first);
    }

    @Override
    public void onWeekSelected() {
        this.barModeState = BarModeState.WEEK;
    }

    @Override
    public void onMonthSelected() {
        this.barModeState = BarModeState.MONTH;
    }

    @Override
    public void onYearSelected() {
        this.barModeState = BarModeState.YEAR;
    }

    enum BarModeState {
        DAY, WEEK, MONTH, YEAR
    }
}
