package com.citywork.utils;

import com.citywork.model.db.models.City;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CityUtils {

    public List<City> getCityList(@NotNull List<City> cityList) {

        Calendar currentDate = Calendar.getInstance();
        Calendar cityDate = Calendar.getInstance();

        List<City> cityToReturn = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            for (City city : cityList) {
                cityDate.setTime(city.getDate());
                if (currentDate.get(Calendar.DAY_OF_YEAR) == cityDate.get(Calendar.DAY_OF_YEAR) && currentDate.get(Calendar.YEAR) == cityDate.get(Calendar.YEAR)) {
                    cityToReturn.add(city);
                } else {
                    cityToReturn.add(new City());
                }
            }

            currentDate.add(Calendar.DATE, -1);
        }

        return cityToReturn;
    }
}
