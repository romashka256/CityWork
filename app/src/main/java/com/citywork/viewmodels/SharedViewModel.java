package com.citywork.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import com.citywork.model.db.models.Building;
import com.citywork.model.db.models.Pomodoro;
import lombok.Getter;

public class SharedViewModel extends ViewModel {

    @Getter
    MutableLiveData<Building> buildingMutableLiveData = new MutableLiveData<>();

}