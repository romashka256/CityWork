package com.producticity.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Intent;

import com.producticity.App;
import com.producticity.ui.BreakChooseDialog;
import com.producticity.utils.SharedPrefensecUtils;

import lombok.Getter;

import static com.producticity.Constants.dataSet;

public class SettingsViewModel extends ViewModel {

    private SharedPrefensecUtils sharedPrefensecUtils;

    private BreakChooseDialog.BreakType curBreakType;

    @Getter
    private MutableLiveData<Integer> mShortBreakChangedEvent = new MutableLiveData<>();
    @Getter
    private MutableLiveData<Integer> mLongBreakChangedEvent = new MutableLiveData<>();

    public void onCreate() {
        sharedPrefensecUtils = App.getsAppComponent().getSharedPrefs();
    }

    public boolean getIsWinNotifEnabled() {
        return sharedPrefensecUtils.getWinNotif();
    }

    public boolean getIsDelete24HEnabled() {
        return sharedPrefensecUtils.get24hDelete();
    }

    public boolean getIsStartEndSoundEnabled() {
        return sharedPrefensecUtils.getStartEndSound();
    }

    public boolean getIsNotifBarEnabled() {
        return sharedPrefensecUtils.getInNotifBar();
    }

    public void onWinOnNotifToggleChanged(boolean enabled) {
        sharedPrefensecUtils.setWinNotif(enabled);
    }

    public void onInNotifToggleChanged(boolean enabled) {
        sharedPrefensecUtils.setInNotifBar(enabled);
    }

    public void onShortBreakClicked() {
        curBreakType = BreakChooseDialog.BreakType.SHORT;
    }

    public void onLongBreakClicked() {
        curBreakType = BreakChooseDialog.BreakType.LONG;
    }

    public void onStartEndSoundToggleChanged(boolean enabled) {
        sharedPrefensecUtils.setStartEndSound(enabled);
    }

    public void on24HDeleteToggleChanged(boolean enabled) {
        sharedPrefensecUtils.set24hDelete(enabled);
    }

    public int getShortBreakValue() {
        return sharedPrefensecUtils.getShortBreak() / 60;
    }

    public int getSelectedBreak() {
        int curValue = 0;

        if (curBreakType == BreakChooseDialog.BreakType.SHORT) {
            curValue = getShortBreakValue();
        } else if (curBreakType == BreakChooseDialog.BreakType.LONG) {
            curValue = getLongBreakValue();
        }

        for (int i = 0; i < dataSet.length; i++) {
            if (curValue == dataSet[i]) {
                return curValue;
            }
        }
        return -1;
    }

    public int getLongBreakValue() {
        return sharedPrefensecUtils.getLongBreak() / 60;
    }

    public void onDialogFinished(Intent intent) {
        int value = intent.getIntExtra(BreakChooseDialog.resulttag, 0);

        switch (curBreakType) {
            case LONG:
                setLongBreakValue(value);
                break;
            case SHORT:
                setShortBreakValue(value);
                break;
        }
    }

    public void setShortBreakValue(int value) {
        sharedPrefensecUtils.setShortBreak(value * 60);
        mShortBreakChangedEvent.postValue(value);
    }

    public void setLongBreakValue(int value) {
        sharedPrefensecUtils.setLongBreak(value * 60);
        mLongBreakChangedEvent.postValue(value);
    }
}
