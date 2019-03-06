package com.citywork.viewmodels.interfaces;

import android.arch.lifecycle.LiveData;
import android.util.Pair;

import com.citywork.model.db.models.Building;

import java.util.Date;
import java.util.List;

public interface ICityFragmentViewModel {
    void onCreate();

    LiveData<List<Pair<Date, List<Building>>>> getCitiesLoaded();
}
