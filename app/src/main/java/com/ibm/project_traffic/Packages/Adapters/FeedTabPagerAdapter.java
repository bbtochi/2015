package com.ibm.project_traffic.Packages.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ibm.project_traffic.Packages.UI.TrendingTrafficFeedFragment;
import com.ibm.project_traffic.Packages.UI.RouteFeedFragment;

/**
 * Created by Larry on 11/22/2014.
 */
public class FeedTabPagerAdapter extends FragmentPagerAdapter {

    CharSequence titles[];
    int NumbOfTabs;

    public FeedTabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public FeedTabPagerAdapter(FragmentManager fm,CharSequence mTitles[], int numberOfTabs) {
        super(fm);

        this.titles = mTitles;
        this.NumbOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return new RouteFeedFragment();
            case 1:
                return new TrendingTrafficFeedFragment();
        }
        return null;
    }



    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}
