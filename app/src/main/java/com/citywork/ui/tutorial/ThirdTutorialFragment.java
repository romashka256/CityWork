package com.citywork.ui.tutorial;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.citywork.R;

public class ThirdTutorialFragment extends TutorialFragmentImpl {
    private String title;
    private String sunTitle;
    private int imageID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = getResources().getString(R.string.third_tutor_title);
        sunTitle = getResources().getString(R.string.third_subtitle_title);
        imageID = R.drawable.img3;

        mNextBtnTV.setText(getResources().getString(R.string.last_tutor_btn_text));
        mSkipTV.setVisibility(View.GONE);



        showData(title, sunTitle, imageID);
    }

    @Override
    public void onNextClick(View view) {
        super.onNextClick(view);
        getActivity().finish();
    }
}
