package com.citywork.ui.tutorial;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import androidx.navigation.Navigation;
import androidx.navigation.NavigatorProvider;

import com.citywork.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TutorialActivity extends AppCompatActivity {

    @BindView(R.id.tutorial_activity_tablayout)
    TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        }

        ButterKnife.bind(this);

        tabLayout.setEnabled(false);

        tabLayout.addTab(tabLayout.newTab(), true);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
    }

    public void setSelectedTab(int position) {
        tabLayout.getTabAt(position).select();
    }

}
