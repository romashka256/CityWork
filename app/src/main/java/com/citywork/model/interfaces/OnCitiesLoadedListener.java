package com.citywork.model.interfaces;

import com.citywork.model.db.models.City;

import java.util.List;

public interface OnCitiesLoadedListener {
    void loadCiities(List<City> cityList);
}
