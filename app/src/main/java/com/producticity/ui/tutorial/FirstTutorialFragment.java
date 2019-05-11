package com.producticity.ui.tutorial;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.producticity.R;

public class FirstTutorialFragment extends TutorialFragmentImpl {

    private String title;
    private String sunTitle;
    private int imageID;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title = getResources().getString(R.string.first_tutor_title);
        sunTitle = getResources().getString(R.string.first_subtitle_title);
        imageID = R.drawable.img1;

        showData(title, sunTitle, imageID);
    }

    @Override
    public void onNextClick(View view) {
        super.onNextClick(view);
        getTutorialActivity().viewPager.setCurrentItem(1,true);
    }

    @Override
    public void onSkilClick(View view) {
        super.onSkilClick(view);
        getActivity().finish();
    }
}
