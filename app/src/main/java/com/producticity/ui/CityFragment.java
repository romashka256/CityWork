package com.producticity.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.producticity.App;
import com.producticity.R;
import com.producticity.model.db.models.City;
import com.producticity.ui.customviews.LineChart;
import com.producticity.utils.commonutils.UIUtils;
import com.producticity.viewmodels.CityFragmentViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import timber.log.Timber;

public class CityFragment extends Fragment {

    @BindView(R.id.city_fragment_rv)
    RecyclerView recyclerView;
    @BindView(R.id.city_fragment_tab_rv)
    RecyclerView tabRV;
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
    private CityTabsAdapter cityTabsAdapter;

    private Context context;
    private UIUtils UIUtils;

    private int prevItem = 0;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate");

        UIUtils = App.getsAppComponent().getFontUtils();
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

        initStatTabs();

        setFonts();

        Point size = UIUtils.getScreenSize();

        SmoothScrollLinearLayManager cityListlinearLayoutManager = new SmoothScrollLinearLayManager(context, LinearLayoutManager.HORIZONTAL, true);
        SmoothScrollLinearLayManager tabCityListlinearLayoutManager = new SmoothScrollLinearLayManager(context, LinearLayoutManager.HORIZONTAL, true);
        recyclerView.setLayoutManager(cityListlinearLayoutManager);
        tabRV.setLayoutManager(tabCityListlinearLayoutManager);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int item1 = cityListlinearLayoutManager.findFirstVisibleItemPosition();
                int item2 = cityListlinearLayoutManager.findLastCompletelyVisibleItemPosition();
                int item3 = cityListlinearLayoutManager.findFirstCompletelyVisibleItemPosition();

                int scrollTo = 0;
                if (item2 != -1) {
                    scrollTo = item2;
                } else if (item3 != -1) {
                    scrollTo = item3;
                } else {
                    scrollTo = item1;
                }

                if (prevItem != scrollTo) {
                    tabRV.smoothScrollToPosition(scrollTo);

                    CityTabsAdapter.CityTabsAdapterVH viewHolder = (CityTabsAdapter.CityTabsAdapterVH) tabRV.findViewHolderForAdapterPosition(scrollTo);
                    if (viewHolder != null) {
                        viewHolder.textView.setTextColor(Color.BLACK);
                        viewHolder.textView.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.city_tab_bg_selected, null));
                    }

                    CityTabsAdapter.CityTabsAdapterVH prevViewHolder = (CityTabsAdapter.CityTabsAdapterVH) tabRV.findViewHolderForAdapterPosition(prevItem);
                    if (prevViewHolder != null) {
                        prevViewHolder.textView.setTextColor(Color.WHITE);
                        prevViewHolder.textView.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.city_tab_bg, null));
                    }

                    cityTabsAdapter.setSelected(scrollTo);
                    Timber.i("scrollTo : " + scrollTo);
                    prevItem = scrollTo;
                }

            }
        });


        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.city_tab_divider));

        tabRV.addItemDecoration(itemDecorator);

        cityFragmentViewModel.getCitiesLoaded().observe(this, cities -> {
            List<City> cityList = cities.subList(0, 14);
            cityTabsAdapter = new CityTabsAdapter(cityList, context);

            //TODO ITEMVIEW WIDTH

            int itemWidth = cityTabsAdapter.getItemWidth();
            int horPadding = size.x / 2 - 200 / 2;
            tabRV.setPadding(horPadding, 0, horPadding, 0);
            adapter = new CityAdapter(cityList, context);
            recyclerView.setAdapter(adapter);
            tabRV.setAdapter(cityTabsAdapter);
            cityFragmentViewModel.setCities(cities);
        });

        cityFragmentViewModel.getmCityPeopleCountChangeEvent().observe(this, count -> {
            mPeopleCountTV.setText(count + " человек");
        });

        mShareIV.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "https://play.google.com/apps/testing/com.producticity";
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

        cityFragmentViewModel.onDaySelected();
    }


    @Override
    public void onResume() {
        Timber.i("onResume");
        super.onResume();
    }

    private void hideTextBlock() {
        textStatBlock.setVisibility(View.GONE);
    }

    private void showTextBlock() {
        textStatBlock.setVisibility(View.VISIBLE);
    }

    private void setFonts() {
        pomoCountTV.setTypeface(UIUtils.getMedium());
        minCountTV.setTypeface(UIUtils.getMedium());
        pplCountTV.setTypeface(UIUtils.getMedium());
        pomoNameTV.setTypeface(UIUtils.getRegular());
        minNameTV.setTypeface(UIUtils.getRegular());
        pplNameTV.setTypeface(UIUtils.getRegular());
        pomoVerbTV.setTypeface(UIUtils.getBold());
        minVerbTV.setTypeface(UIUtils.getBold());
        pplVerbTV.setTypeface(UIUtils.getBold());
        mPeopleCountTV.setTypeface(UIUtils.getBold());
        mPeopleCountTextTV.setTypeface(UIUtils.getLight());
    }


    private void initStatTabs() {
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

    }
}
