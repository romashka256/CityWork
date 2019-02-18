package com.citywork.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.citywork.Constants;

public class SharedPrefensecUtils {
    //SharedPreferences
    public final static String SHARED_PREFERENCES_NAME = "shared_preferences_file";
    public final static String TIMETOGO_SHAREDPREFS = "timetogo_sharedprefs";
    public final static String START_TIME_IN_MILLIS = "start_time_in_millis";
    public final static String STOP_TIME_IN_MILLIS = "stop_time_in_millis";
    public final static String TIMER_STATE = "timer_state";

    private Context context;
    private SharedPreferences sharedPreferences;

    public SharedPrefensecUtils(Context context) {
        this.context = context;

        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    //Check if service was killed. Restore timetogo. Timer cant reach -1 by himself
    public long getTimeToGo() {
        return sharedPreferences.getLong(TIMETOGO_SHAREDPREFS, Constants.TIME_TO_GO_NOT_SAVED);
    }

    //Saving timer data (start time in millis and stop time in millis)
    public void saveTimerData(long startTimeInMillis, long stopTimeInMillis) {
        sharedPreferences
                .edit()
                .putLong(START_TIME_IN_MILLIS, startTimeInMillis)
                .putLong(STOP_TIME_IN_MILLIS, stopTimeInMillis)
                .apply();
    }
}
