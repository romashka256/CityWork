package com.producticity.model.interfaces;

import com.producticity.model.db.models.City;

import java.util.List;

public interface OnCitiesLoadedListener {
    void loadCiities(List<City> cityList);
}
