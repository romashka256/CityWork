package com.citywork.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.citywork.R;
import com.citywork.viewmodels.SettingsViewModel;
import com.zcw.togglebutton.ToggleButton;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.settings_toolbar_back)
    ImageView backButtomIV;
    @BindView(R.id.settings_long_winnotif_block)
    RelativeLayout winnotifBlock;
    @BindView(R.id.settings_long_deleteafter24_block)
    RelativeLayout deleteAfter24Block;
    @BindView(R.id.settings_long_startendsound_block)
    RelativeLayout startendsoundBlock;
    @BindView(R.id.settings_long_notifbar_block)
    RelativeLayout notifbarBlock;
    @BindView(R.id.settings_short_break_block)
    LinearLayout shortBreakBlock;
    @BindView(R.id.settings_long_break_block)
    LinearLayout longBreakBlock;
    @BindView(R.id.settings_long_winnotif_switch)
    ToggleButton winnotifSwitch;
    @BindView(R.id.settings_long_deleteafter24_switch)
    ToggleButton deleteAfter24Switch;
    @BindView(R.id.settings_long_startendsound_switch)
    ToggleButton startendsoundSwitch;
    @BindView(R.id.settings_long_notifbar_switch)
    ToggleButton notifbarSwitch;
    @BindView(R.id.settings_short_break_tv)
    TextView shortBreakTV;
    @BindView(R.id.settings_long_break_tv)
    TextView longBreakTV;


    private MainActivity mainActivity;
    private SettingsViewModel settingsViewModel;


    public static final String TAG = "BreakChooseDialog";
    private int BREAK_REQUEST_CODE = 123;

    @Override
    public void onAttach(Context context) {
        mainActivity = (MainActivity) context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
        settingsViewModel.onCreate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, null);

        ButterKnife.bind(this, view);

        if (settingsViewModel.getIsWinNotifEnabled())
            winnotifSwitch.toggle();

        if (settingsViewModel.getIsDelete24HEnabled())
            deleteAfter24Switch.toggle();

        if (settingsViewModel.getIsNotifBarEnabled())
            notifbarSwitch.toggle();

        if (settingsViewModel.getIsStartEndSoundEnabled())
            startendsoundSwitch.toggle();

        winnotifSwitch.setOnToggleChanged(on -> {
            settingsViewModel.onWinOnNotifToggleChanged(on);
        });

        deleteAfter24Switch.setOnToggleChanged(on -> {
            settingsViewModel.on24HDeleteToggleChanged(on);
        });

        startendsoundSwitch.setOnToggleChanged(on -> {
            settingsViewModel.onStartEndSoundToggleChanged(on);
        });

        notifbarSwitch.setOnToggleChanged(on -> {
            settingsViewModel.onInNotifToggleChanged(on);
        });

        longBreakBlock.setOnClickListener(v -> {
            settingsViewModel.onLongBreakClicked();
            showBreakDialog();
        });

        shortBreakBlock.setOnClickListener(v -> {
            settingsViewModel.onShortBreakClicked();
            showBreakDialog();
        });

        winnotifBlock.setOnClickListener(this);
        deleteAfter24Block.setOnClickListener(this);
        startendsoundBlock.setOnClickListener(this);
        notifbarBlock.setOnClickListener(this);

        shortBreakTV.setText(settingsViewModel.getShortBreakValue() + " мин");
        longBreakTV.setText(settingsViewModel.getLongBreakValue() + " мин");

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BREAK_REQUEST_CODE) {
             settingsViewModel.onDialogFinished(data);
        }
    }

    public void showBreakDialog(){
        BreakChooseDialog dialog = new BreakChooseDialog();
        dialog.setTargetFragment(this, BREAK_REQUEST_CODE);

        dialog.show(mainActivity.getSupportFragmentManager(), TAG);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        backButtomIV.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        mainActivity.hideBottomNav();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_long_winnotif_block:
                winnotifSwitch.toggle();
                break;
            case R.id.settings_long_deleteafter24_block:
                deleteAfter24Switch.toggle();
                break;
            case R.id.settings_long_startendsound_block:
                startendsoundSwitch.toggle();
                break;
            case R.id.settings_long_notifbar_block:
                notifbarSwitch.toggle();
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        mainActivity.showBottomNav();
    }

    @Override
    public void onResume() {
        super.onResume();

        mainActivity.hideBottomNav();
    }
}
