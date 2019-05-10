package com.producticity.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.producticity.App;
import com.producticity.R;
import com.producticity.utils.commonutils.FontUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
