package com.citywork.ui.tutorial;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import androidx.navigation.Navigation;

import com.citywork.R;

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
        Navigation.findNavController(view).navigate(R.id.action_firstTutorialFragment_to_secondTutorialFragment);
        ((TutorialActivity) getActivity()).setSelectedTab(1);
    }

    @Override
    public void onSkilClick(View view) {
        super.onSkilClick(view);
        getActivity().finish();
    }
}
