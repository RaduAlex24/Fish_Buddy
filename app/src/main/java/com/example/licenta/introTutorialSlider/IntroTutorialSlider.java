package com.example.licenta.introTutorialSlider;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.licenta.R;
import com.github.appintro.AppIntro;
import com.github.appintro.AppIntro2;
import com.github.appintro.AppIntroFragment;

public class IntroTutorialSlider extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Folosire sageti
        setWizardMode(true);

        // Adaugare slideuri
        addSlide(AppIntroFragment.newInstance("Modul forum", "Instructiuni forum",
                R.drawable.b, ContextCompat.getColor(getApplicationContext(), R.color.purple_200)));


        addSlide(AppIntroFragment.newInstance("Modul asistent virtual", "Instructiuni asistent virtual",
                R.drawable.b, ContextCompat.getColor(getApplicationContext(), R.color.purple_200)));


        addSlide(AppIntroFragment.newInstance("Modul harti", "Instructiuni harti",
                R.drawable.b, ContextCompat.getColor(getApplicationContext(), R.color.purple_200)));

    }


    // Metode
    // Apasare done
    @Override
    protected void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }


    // Apasare skip
    @Override
    protected void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

}
