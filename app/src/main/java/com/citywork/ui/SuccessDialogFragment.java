package com.citywork.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.*;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.citywork.App;
import com.citywork.R;

public class SuccessDialogFragment extends DialogFragment {

    private Context context;

    @BindView(R.id.success_dialog_close_iv)
    Button vCloseIV;
    @BindView(R.id.success_dialog_to_city_btn)
    Button vToCityBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = App.getsAppComponent().getApplicationContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.success_dialog, container, false);

        ButterKnife.bind(this, view);

        vCloseIV.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();

        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        window.setLayout((int) (size.x * 0.95), (int) (size.y * 0.9));
        window.setGravity(Gravity.CENTER);

        super.onResume();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }
}
