package com.citywork.ui.tutorial;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.LinearLayout;

import com.citywork.R;
import com.citywork.ui.MainActivity;
import com.citywork.utils.SharedPrefensecUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TutorialActivity extends AppCompatActivity {

    @BindView(R.id.tutorial_activity_tablayout)
    TabLayout tabLayout;

    private SharedPrefensecUtils sharedPrefensecUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_activity);

        sharedPrefensecUtils = new SharedPrefensecUtils(this);

//        if (!sharedPrefensecUtils.isFirstRun()) {
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
//            startActivity(intent);
//            finish();
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        }

        ButterKnife.bind(this);

        tabLayout.setEnabled(false);

        tabLayout.addTab(tabLayout.newTab(), true);

        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        tabLayout.clearOnTabSelectedListeners();

        LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener((v, event) -> true);
        }
    }

    public void setSelectedTab(int position) {
        tabLayout.getTabAt(position).select();
    }

}
