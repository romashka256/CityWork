package com.producticity.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.producticity.SingleLiveEvent;
import com.producticity.model.db.models.Building;
import com.producticity.model.db.models.City;

import lombok.Getter;

public class SharedViewModel extends ViewModel {

    @Getter
    MutableLiveData<Building> buildingMutableLiveData = new SingleLiveEvent<>();

    @Getter
    MutableLiveData<City> cityMutableLiveData = new SingleLiveEvent<>();

    @Getter
    MutableLiveData<Integer> whatToShowLiveData = new SingleLiveEvent<>();

}
