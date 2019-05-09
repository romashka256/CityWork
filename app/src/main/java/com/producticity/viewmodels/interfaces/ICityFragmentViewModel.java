package com.producticity.viewmodels.interfaces;

import android.arch.lifecycle.LiveData;

import com.producticity.model.db.models.City;

import java.util.List;

public interface ICityFragmentViewModel {
    void onCreate();

    LiveData<List<City>> getCitiesLoaded();

    LiveData<Integer> getmCityPeopleCountChangeEvent();

    void onChartSelected(int value);

    City getTodayCity();

    void onDaySelected();

    void onWeekSelected();

    void onMonthSelected();

    void onYearSelected();

}
