package com.citywork.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BreakChooseDialog extends Dialog {
    public BreakChooseDialog(@NonNull Context context) {
        super(context);
    }

    public BreakChooseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public BreakChooseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, @Nullable Menu menu, int deviceId) {

    }
}
