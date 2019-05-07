package com.citywork.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.citywork.R;
import com.citywork.ui.listeners.OnBreakValueSelected;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.Setter;

import static com.citywork.Constants.dataSet;

public class BreakChooseDialog extends DialogFragment {

    @BindView(R.id.break_choose_dialog_lv)
    ListView optionsList;
    @BindView(R.id.break_choose_dialog_title)
    TextView dialogTitle;

    private BreakType breakType;
    private int screenHeight;


    private int selected;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selected = getArguments().getInt(selectedTag);
        breakType = BreakType.valueOf(getArguments().getString(breakTypeTag));


    }

    public static final String shortBreakTag = "SHORT";
    public static final String longBreakTag = "LONG";
    public static final String resulttag = "resulttag";
    public static final String selectedTag = "selected";
    public static final String breakTypeTag = "breakType";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.break_choose_dialog, container, false);

        ButterKnife.bind(this, view);

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenHeight = size.y;

        optionsList.setAdapter(new BreakDataSetAdapter(dataSet, getContext(), value -> {
            Intent intent = new Intent();
            intent.putExtra(resulttag, value);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            dismiss();
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onViewCreated(view, savedInstanceState);

        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
        p.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
        float free = screenHeight - getView().getMeasuredHeight();
        float y = free * 0.05f;
        p.y = (int) y;

        getDialog().getWindow().setAttributes(p);
    }

    public enum BreakType {
        SHORT, LONG;
    }
}
