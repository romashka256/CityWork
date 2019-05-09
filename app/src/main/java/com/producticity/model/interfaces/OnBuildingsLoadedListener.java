package com.producticity.model.interfaces;

import com.producticity.model.db.models.Building;

import java.util.List;

public interface OnBuildingsLoadedListener {
    void onBuildingsLoaded(List<Building> buildings);
}
