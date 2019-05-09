package com.producticity.ui.timerfragment.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.producticity.App;
import com.producticity.R;
import com.producticity.utils.commonutils.FontUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class StopDialog extends Dialog {


    @BindView(R.id.stop_dialog_cancel_btn)
    Button cancelBtn;
    @BindView(R.id.stop_dialog_stop_btn)
    Button stopBtn;
    @BindView(R.id.textView10)
    TextView mMessageTV;
    private View.OnClickListener onClickListener;

    private FontUtils fontUtils;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.stop_dialog);

        fontUtils = App.getsAppComponent().getFontUtils();

        ButterKnife.bind(this);

        stopBtn.setOnClickListener(v -> {
            onClickListener.onClick(v);
            dismiss();
        });

        cancelBtn.setTypeface(fontUtils.getMedium());
        stopBtn.setTypeface(fontUtils.getMedium());
        mMessageTV.setTypeface(fontUtils.getRegular());

        cancelBtn.setOnClickListener(v -> dismiss());

        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }




}
