package com.producticity.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.producticity.Constants;

public class SharedPrefensecUtils {
    //SharedPreferences
    public final static String SHARED_PREFERENCES_NAME = "shared_preferences_file";

    public final static String SETTINGS_SHORT_BREAK = "settings_shortbreak";
    public final static String SETTINGS_LONG_BREAK = "settings_longbreak";
    public final static String SETTINGS_NOTIFBAR = "settings_notifbar";
    public final static String SETTINGS_STARTEND_TIMER = "settings_startend_timer";
    public final static String SETTINGS_WINNOTIF = "settings_winnotif";
    public final static String SETTINGS_24HDELETE = "settings_24hdelete";
    public final static String SETTINGS_PREFERED_TIMER = "prefered_timer";

    private Context context;
    private SharedPreferences sharedPreferences;

    public boolean isFirstRun() {
        boolean first = sharedPreferences.getBoolean("firstrun", true);
        sharedPreferences.edit().putBoolean("firstrun", false).apply();
        return first;
    }

    public SharedPrefensecUtils(Context context) {
        this.context = context;

        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public int getShortBreak() {
        return sharedPreferences.getInt(SETTINGS_SHORT_BREAK, Constants.DEFAULT_SHORT_BREAK_VALUE);
    }

    public void setShortBreak(int value) {
        sharedPreferences.edit().putInt(SETTINGS_SHORT_BREAK, value).apply();
    }

    public int getLongBreak() {
        return sharedPreferences.getInt(SETTINGS_LONG_BREAK, Constants.DEFAULT_LONG_BREAK_VALUE);
    }

    public void setLongBreak(int value) {
        sharedPreferences.edit().putInt(SETTINGS_LONG_BREAK, value).apply();
    }

    public boolean getInNotifBar() {
        return sharedPreferences.getBoolean(SETTINGS_NOTIFBAR, Constants.DEFAULT_IN_NOTIF_BAR);
    }

    public void setInNotifBar(boolean value) {
        sharedPreferences.edit().putBoolean(SETTINGS_NOTIFBAR, value).apply();
    }

    public boolean getStartEndSound() {
        return sharedPreferences.getBoolean(SETTINGS_STARTEND_TIMER, Constants.DEFAULT_START_END_SOUND);
    }

    public void setStartEndSound(boolean value) {
        sharedPreferences.edit().putBoolean(SETTINGS_STARTEND_TIMER, value).apply();
    }

    public boolean getWinNotif() {
        return sharedPreferences.getBoolean(SETTINGS_WINNOTIF, Constants.DEFAULT_WIN_NOTIF);
    }

    public void setWinNotif(boolean value) {
        sharedPreferences.edit().putBoolean(SETTINGS_WINNOTIF, value).apply();
    }

    public boolean get24hDelete() {
        return sharedPreferences.getBoolean(SETTINGS_24HDELETE, Constants.DEFAULT_24HDELETE);
    }

    public void set24hDelete(boolean value) {
        sharedPreferences.edit().putBoolean(SETTINGS_24HDELETE, value).apply();
    }

    public void setPrefredTimer(long inSeconds) {
        sharedPreferences.edit().putLong(SETTINGS_PREFERED_TIMER, inSeconds).apply();
    }

    public long getPrefredTimer() {
        return sharedPreferences.getLong(SETTINGS_PREFERED_TIMER, Constants.DEFAULT_MIN_TIMER_VALUE);
    }
}
