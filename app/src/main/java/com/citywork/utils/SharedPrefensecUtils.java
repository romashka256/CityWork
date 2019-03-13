package com.citywork.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.citywork.Constants;

public class SharedPrefensecUtils {
    //SharedPreferences
    public final static String SHARED_PREFERENCES_NAME = "shared_preferences_file";

    private Context context;
    private SharedPreferences sharedPreferences;

    public SharedPrefensecUtils(Context context) {
        this.context = context;

        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}
