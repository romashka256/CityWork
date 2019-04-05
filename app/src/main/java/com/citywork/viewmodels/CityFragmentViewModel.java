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
import com.citywork.ui.customviews.LineChart;
import com.citywork.utils.chart.ChartBar;
import com.citywork.utils.chart.ChartUtils;
import com.citywork.utils.CityUtils;
import com.citywork.utils.PomodoroManger;
import com.citywork.utils.chart.CustomChartUtils;
import com.citywork.viewmodels.interfaces.ICityFragmentViewModel;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
    private MutableLiveData<List<ChartBar>> barModeStateChangedEvent = new MutableLiveData<>();

    private List<Building> buildingList;
    private CityUtils cityUtils;
    private PomodoroManger pomodoroManger;
    private CustomChartUtils customChartUtils;
    private Context context;

    @Getter
    @Setter
    private List<String> curLabels;

    @Getter
    private LineChart.BarModeState barModeState;

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public CityFragmentViewModel() {
        this.barModeState = LineChart.BarModeState.DAY;
        dataBaseHelper = App.getsAppComponent().getDataBaseHelper();
        pomodoroManger = App.getsAppComponent().getPomdoromManager();

        context = App.getsAppComponent().getApplicationContext();

        //TODO INJECT
        customChartUtils = new CustomChartUtils();
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
            this.cities = cityList;
            citiesCreatedEvent.postValue(cityUtils.getCityList(cityList));
            Collections.reverse(cityList);
        });

        mCityPeopleCountChangeEvent.setValue(pomodoroManger.getCityPeopleCount());

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
        //      chartBarSelectedEvent.setValue(chartUtils.getDataForToday(getTodayCity()).first);
    }

    @Override
    public void onDaySelected() {
        curLabels = customChartUtils.getDaily();
        this.barModeState = LineChart.BarModeState.DAY;
        List<ChartBar> list = chartUtils.getDataForToday(getTodayCity()).first;
        List<Integer> integers = getValuesForChart(list);

        barModeStateChangedEvent.postValue(customChartUtils.createBars(integers));
    }

    @Override
    public void onWeekSelected() {
        this.barModeState = LineChart.BarModeState.WEEK;
        curLabels = customChartUtils.getWeekly();
        List<ChartBar> list = chartUtils.getDataForWeek(cities).first;
        List<Integer> integers = getValuesForChart(list);

        barModeStateChangedEvent.postValue(customChartUtils.createBars(integers));
    }

    @Override
    public void onMonthSelected() {
        this.barModeState = LineChart.BarModeState.MONTH;
    }

    @Override
    public void onYearSelected() {
        this.barModeState = LineChart.BarModeState.YEAR;
    }

    private List<Integer> getValuesForChart(List<ChartBar> list) {
        List<Integer> integers = new ArrayList<>();
        integers.clear();
        for (ChartBar chartBar : list) {
            integers.add(new Random().nextInt(100));
        }
        return integers;
    }
}
