package com.veltus.covidnewstracking.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.veltus.covidnewstracking.Fragments.CovidCasesFragment;
import com.veltus.covidnewstracking.Fragments.NewsFeedFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {


    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return new NewsFeedFragment();
            case 1:
                return new CovidCasesFragment();
        }
        return null;

    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
