package com.citywork.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.citywork.App;
import com.citywork.R;
import com.citywork.utils.chart.ChartUtils;
import com.citywork.viewmodels.CityFragmentViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    @BindView(R.id.city_fragment_statistics_block_chart)
    BarChart barChart;
    @BindView(R.id.city_fragment_statistics_block_tablayout)
    TabLayout tabLayout;
    @BindView(R.id.city_fragment_statistics_block_textstat_lay_pomo)
    RelativeLayout pomoStat;
    @BindView(R.id.city_fragment_statistics_block_textstat_lay_min)
    RelativeLayout minStat;
    @BindView(R.id.city_fragment_statistics_block_textstat_lay_population)
    RelativeLayout pplStat;
    @BindView(R.id.city_fragment_statistics_block_textstat_lay)
    LinearLayout textStatBlock;

    TextView pomoCountTV;
    TextView minCountTV;
    TextView pplCountTV;

    private CityFragmentViewModel cityFragmentViewModel;
    private CityAdapter adapter;

    private Context context;
    private ChartUtils chartUtils;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = App.getsAppComponent().getApplicationContext();

        cityFragmentViewModel = ViewModelProviders.of(this).get(CityFragmentViewModel.class);

        cityFragmentViewModel.onCreate();

        cityFragmentViewModel.getCitiesLoaded();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.city_fragment, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pomoCountTV = pomoStat.findViewById(R.id.text_stat_item_number);
        minCountTV = minStat.findViewById(R.id.text_stat_item_number);
        pplCountTV = pplStat.findViewById(R.id.text_stat_item_number);

        TabLayout.Tab tab = tabLayout.newTab();
        tab.setText(getResources().getString(R.string.day));

        TabLayout.Tab tabw = tabLayout.newTab();
        tabw.setText(getResources().getString(R.string.week));

        TabLayout.Tab tabm = tabLayout.newTab();
        tabm.setText(getResources().getString(R.string.month));

        TabLayout.Tab taby = tabLayout.newTab();
        taby.setText(getResources().getString(R.string.year));

        tabLayout.addTab(tab);
        tabLayout.addTab(tabw);
        tabLayout.addTab(tabm);
        tabLayout.addTab(taby);

        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true));

        cityFragmentViewModel.getCitiesLoaded().observe(this, cities -> {
            adapter = new CityAdapter(cities, context);
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

        barChart.getDescription().setEnabled(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(false);
        barChart.setDrawGridBackground(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setScaleEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGridLineWidth(20);
        xAxis.setLabelCount(4, false);
        xAxis.setAxisLineWidth(0);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(50f);
        xAxis.setGranularityEnabled(true);

        xAxis.setDrawAxisLine(false);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.enableGridDashedLine(25, 25, 10);
        leftAxis.setSpaceBottom(0);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setTextColor(getResources().getColor(R.color.transparent));
        leftAxis.setSpaceTop(0);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        barChart.getLegend().setEnabled(false);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Timber.i("chart selected : " + e.getX());

                cityFragmentViewModel.onChartSelected((int) e.getX());
                showTextBlock();
            }

            @Override
            public void onNothingSelected() {
                hideTextBlock();
            }
        });

        cityFragmentViewModel.getBarModeStateChangedEvent().observe(this, list -> {
            if (list != null) {
                BarData barData = new BarData(list);
                barData.setBarWidth(2f);

                barChart.setData(barData);

                xAxis.setGranularity(50f);
                xAxis.setGranularityEnabled(true);

                barChart.invalidate();

            }
        });

        cityFragmentViewModel.getChartBarSelectedEvent().observe(this, list -> {

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
}
