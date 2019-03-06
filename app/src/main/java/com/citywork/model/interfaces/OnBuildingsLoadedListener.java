package com.citywork.model.interfaces;

import com.citywork.model.db.models.Building;

import java.util.List;

public interface OnBuildingsLoadedListener {
    void onBuildingsLoaded(List<Building> buildings);
}
