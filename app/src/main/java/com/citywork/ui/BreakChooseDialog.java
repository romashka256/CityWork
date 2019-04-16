package com.citywork.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.citywork.R;
import com.citywork.ui.listeners.OnBreakValueSelected;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.Setter;

public class BreakChooseDialog extends DialogFragment {

    @BindView(R.id.break_choose_dialog_lv)
    ListView optionsList;
    @BindView(R.id.break_choose_dialog_title)
    TextView dialogTitle;

    private BreakType breakType;

    @Getter
    @Setter
    private int[] dataSet = new int[]{5, 10, 15, 20, 25};

    private int selected;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selected = getArguments().getInt("selected");
        breakType = BreakType.valueOf(getArguments().getString("breaktype"));

    }

    public static final String shortBreakTag = "SHORT";
    public static final String longBreakTag = "LONG";
    public static final String resulttag = "resulttag";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.break_choose_dialog, container, false);

        ButterKnife.bind(this, view);

        optionsList.setAdapter(new BreakDataSetAdapter(dataSet, getContext(), value -> {
            Intent intent = new Intent();
            intent.putExtra(resulttag, value);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        }, selected));

        switch (breakType) {
            case SHORT:
                dialogTitle.setText(getResources().getString(R.string.short_break));
                break;
            case LONG:
                dialogTitle.setText(getResources().getString(R.string.long_break));
                break;
        }

        return view;
    }


    public enum BreakType {
        SHORT, LONG;
    }
}
