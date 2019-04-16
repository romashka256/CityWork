package com.citywork.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.content.Intent;

import com.citywork.App;
import com.citywork.ui.BreakChooseDialog;
import com.citywork.utils.SharedPrefensecUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import lombok.Getter;
import lombok.Setter;

public class SettingsViewModel extends ViewModel {

    private SharedPrefensecUtils sharedPrefensecUtils;

    private BreakChooseDialog.BreakType curBreakType;

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

    public void onShortBreakClicked(){
        curBreakType = BreakChooseDialog.BreakType.SHORT;
    }

    public void onLongBreakClicked(){
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

    public int getLongBreakValue() {
        return sharedPrefensecUtils.getLongBreak() / 60;
    }

    public void onDialogFinished(Intent intent){
       int value = intent.getIntExtra(BreakChooseDialog.resulttag, 0);

        switch (curBreakType){
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
    }

    public void setLongBreakValue(int value) {
        sharedPrefensecUtils.setLongBreak(value * 60);
    }
}
