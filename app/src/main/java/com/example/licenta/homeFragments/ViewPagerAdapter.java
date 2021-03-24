package com.example.licenta.homeFragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private static final int NUM_INTEMS = 2;

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new VirtualAssistantFragment();
            case 1: return new ForumFragment();
            default: return  new VirtualAssistantFragment();
        }
    }

    @Override
    public int getCount() {
        return NUM_INTEMS;
    }
}

