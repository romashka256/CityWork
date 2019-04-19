package com.citywork.ui;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.citywork.utils.DialogUtils;

public class StopDialog extends Dialog {
    public StopDialog(@NonNull Context context) {
        super(context);
    }

    public StopDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public StopDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


}
