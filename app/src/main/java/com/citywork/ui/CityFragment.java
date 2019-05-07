package com.citywork.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.citywork.App;
import com.citywork.R;
import com.citywork.ui.customviews.LineChart;
import com.citywork.utils.commonutils.FontUtils;
import com.citywork.viewmodels.CityFragmentViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import timber.log.Timber;

public class CityFragment extends Fragment {

    @BindView(R.id.city_fragment_rv)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar_share_iv)
    ImageView mShareIV;
    @BindView(R.id.toolbar_settings_iv)
    ImageView mSettings;
    @BindView(R.id.toolbar_city_people_count)
    TextView mPeopleCountTV;
    @BindView(R.id.toolbar_city_people_count_text)
    TextView mPeopleCountTextTV;
    @BindView(R.id.city_fragment_statistics_block_chart)
    LineChart barChart;
    @BindView(R.id.city_fragment_statistics_block_tablayout)
    TabLayout tabLayout;
    @BindView(R.id.city_fragment_statistics_block_textstat_lay_pomo)
    ConstraintLayout pomoStat;
    @BindView(R.id.city_fragment_statistics_block_textstat_lay_min)
    ConstraintLayout minStat;
    @BindView(R.id.city_fragment_statistics_block_textstat_lay_population)
    ConstraintLayout pplStat;
    @BindView(R.id.city_fragment_statistics_block_textstat_lay)
    LinearLayout textStatBlock;

    @Getter
    public final static int fragmnetIndex = 1;

    TextView pomoCountTV;
    TextView minCountTV;
    TextView pplCountTV;

    TextView pomoNameTV;
    TextView minNameTV;
    TextView pplNameTV;

    TextView pomoVerbTV;
    TextView minVerbTV;
    TextView pplVerbTV;

    private CityFragmentViewModel cityFragmentViewModel;
    private CityAdapter adapter;

    private Context context;
    private FontUtils fontUtils;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate");

        fontUtils = App.getsAppComponent().getFontUtils();
        context = App.getsAppComponent().getApplicationContext();

        cityFragmentViewModel = ViewModelProviders.of(this).get(CityFragmentViewModel.class);

        cityFragmentViewModel.onCreate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.i("onCreateView");
        View view = inflater.inflate(R.layout.city_fragment, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Timber.i("onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        pomoCountTV = pomoStat.findViewById(R.id.text_stat_item_number);
        minCountTV = minStat.findViewById(R.id.text_stat_item_number);
        pplCountTV = pplStat.findViewById(R.id.text_stat_item_number);

        pomoNameTV = pomoStat.findViewById(R.id.text_stat_item_name);
        minNameTV = minStat.findViewById(R.id.text_stat_item_name);
        pplNameTV = pplStat.findViewById(R.id.text_stat_item_name);

        pomoVerbTV = pomoStat.findViewById(R.id.text_stat_item_verb);
        minVerbTV = minStat.findViewById(R.id.text_stat_item_verb);
        pplVerbTV = pplStat.findViewById(R.id.text_stat_item_verb);

        pomoNameTV.setText(getResources().getString(R.string.pomo));
        minNameTV.setText(getResources().getString(R.string.minute_short));
        pplNameTV.setText(getResources().getString(R.string.people_short));

        pomoVerbTV.setText(getResources().getString(R.string.pomo_verb));
        minVerbTV.setText(getResources().getString(R.string.minute_verb));
        pplVerbTV.setText(getResources().getString(R.string.people_verb));

        TabLayout.Tab tab = tabLayout.newTab();
        tab.setText(getResources().getString(R.string.day));
        tab.select();

        TabLayout.Tab tabw = tabLayout.newTab();
        tabw.setText(getResources().getString(R.string.week));

        TabLayout.Tab tabm = tabLayout.newTab();
        tabm.setText(getResources().getString(R.string.month));

        TabLayout.Tab taby = tabLayout.newTab();
        taby.setText(getResources().getString(R.string.year));

        tabLayout.addTab(tab, true);
        tabLayout.addTab(tabw);
        tabLayout.addTab(tabm);
        tabLayout.addTab(taby);

        setFonts();

        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true));

        cityFragmentViewModel.getCitiesLoaded().observe(this, cities -> {
            adapter = new CityAdapter(cities.subList(0, 14), context);
            recyclerView.setAdapter(adapter);
            cityFragmentViewModel.setCities(cities);
        });

        cityFragmentViewModel.getmCityPeopleCountChangeEvent().observe(this, count -> {
            mPeopleCountTV.setText(count + " человек");
        });

        mShareIV.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Share";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });

        mSettings.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_cityFragment_to_settingsFragment));

        barChart.setOnBarClickListener(barIndex -> {
            showTextBlock();
            cityFragmentViewModel.onChartSelected(barIndex);
        });

        cityFragmentViewModel.getBarModeStateChangedEvent().observe(this, list -> {
            if (list != null) {
                barChart.setValues(list, cityFragmentViewModel.getCurLabels());
            }
        });

        cityFragmentViewModel.getChartBarSelectedEvent().observe(this, list -> {
            List<Integer> integers = cityFragmentViewModel.calculateStat(list);

            minCountTV.setText(integers.get(0) + "");
            pplCountTV.setText(integers.get(2) + "");
            pomoCountTV.setText(integers.get(1) + "");
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        cityFragmentViewModel.onDaySelected();
                        break;
                    case 1:
                        cityFragmentViewModel.onWeekSelected();
                        break;
                    case 2:
                        cityFragmentViewModel.onMonthSelected();
                        break;
                    case 3:
                        cityFragmentViewModel.onYearSelected();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        cityFragmentViewModel.onDaySelected();
    }

    private void hideTextBlock() {
        textStatBlock.setVisibility(View.GONE);
    }

    private void showTextBlock() {
        textStatBlock.setVisibility(View.VISIBLE);
    }

    private void setFonts() {
        pomoCountTV.setTypeface(fontUtils.getMedium());
        minCountTV.setTypeface(fontUtils.getMedium());
        pplCountTV.setTypeface(fontUtils.getMedium());
        pomoNameTV.setTypeface(fontUtils.getRegular());
        minNameTV.setTypeface(fontUtils.getRegular());
        pplNameTV.setTypeface(fontUtils.getRegular());
        pomoVerbTV.setTypeface(fontUtils.getBold());
        minVerbTV.setTypeface(fontUtils.getBold());
        pplVerbTV.setTypeface(fontUtils.getBold());
        mPeopleCountTV.setTypeface(fontUtils.getBold());
        mPeopleCountTextTV.setTypeface(fontUtils.getLight());
    }

    @Override
    public void onResume() {
        Timber.i("onResume");
        super.onResume();
    }
}
