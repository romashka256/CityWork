package com.citywork.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.citywork.R;
import com.citywork.utils.DialogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class StopDialog extends Dialog {

    private View.OnClickListener onClickListener;

    public StopDialog(@NonNull Context context, View.OnClickListener onClickListener) {
        super(context);
        this.onClickListener = onClickListener;
    }

    public StopDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public StopDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @BindView(R.id.stop_dialog_cancel_btn)
    Button cancelBtn;
    @BindView(R.id.stop_dialog_stop_btn)
    Button stopBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stop_dialog);

        ButterKnife.bind(this);

        stopBtn.setOnClickListener(v -> {
            onClickListener.onClick(v);
            dismiss();
        });

        cancelBtn.setOnClickListener(v -> dismiss());
    }
}
