package com.citywork.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.citywork.App;
import com.citywork.R;
import com.citywork.utils.ChartUtils;
import com.citywork.viewmodels.CityFragmentViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
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

    private CityFragmentViewModel cityFragmentViewModel;
    private CityAdapter adapter;

    private Context context;
    private ChartUtils chartUtils;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = App.getsAppComponent().getApplicationContext();
        //TODO INJECT
        chartUtils = new ChartUtils();

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

        //     barChart.setData(new BarData(chartUtils.getDataForToday(cityFragmentViewModel.getCities().get(cityFragmentViewModel.getCities().size() - 1))));
        BarData barData = new BarData(chartUtils.getDataForToday(getResources().getColor(R.color.barcolor), getResources().getColor(R.color.blue)));
        barData.setBarWidth(2f);
        barChart.setData(barData);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Timber.i("chart selected : " + e.getX());
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }
}
