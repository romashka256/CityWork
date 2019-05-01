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

        boolean found = false;

        for (int i = 0; i < 364; i++) {
            found = false;
            for (City city : cityList) {
                  cityDate.setTime(city.getDate());
                if (currentDate.get(Calendar.DAY_OF_YEAR) == cityDate.get(Calendar.DAY_OF_YEAR) && currentDate.get(Calendar.YEAR) == cityDate.get(Calendar.YEAR)) {
                    cityToReturn.add(city);
                    found = true;
                    break;
                }
            }

            if (!found)
                cityToReturn.add(new City());

            currentDate.add(Calendar.DATE, -1);
        }

        return cityToReturn;
    }
}
