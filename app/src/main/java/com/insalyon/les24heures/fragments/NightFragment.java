package com.insalyon.les24heures.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.insalyon.les24heures.NightActivity;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.utils.Day;
import com.insalyon.les24heures.view.SlidingTabLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by remi on 30/04/15.
 */
public class NightFragment extends ContentFrameFragment {


    @InjectView(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTabLayout;
    @InjectView(R.id.view_pager)
    ViewPager mViewPager;
    private OurViewPagerAdapter mViewPagerAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.night_tabs,container,false);
        ButterKnife.inject(this,view);

        mViewPagerAdapter = new OurViewPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);


        mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);

        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.tab_selected_strip));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ((NightActivity)getActivity()).restoreTitle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        return view;
    }



    private ArtistFragment getArtistFragment(Day day) {
        Bundle bundleArgs = new Bundle();
        bundleArgs.putParcelableArrayList("categoriesSelected", ((NightActivity)getActivity()).selectedCategories);
        bundleArgs.putParcelableArrayList("resourcesList", ((NightActivity)getActivity()).nightResourceArrayList);
        searchQuery = (searchQuery == null) ? null : (searchQuery.equals("")) ? null : searchQuery;
        bundleArgs.putString("searchQuery", searchQuery);
        bundleArgs.putSerializable("day",day);

        ArtistFragment fragment = new ArtistFragment();
        fragment.setArguments(bundleArgs);

        return fragment;
    }

    @Override
    protected void displayProgress() {
        //TODO
    }

    @Override
    protected void hideProgress() {
        //TODO
    }


    private class OurViewPagerAdapter extends FragmentPagerAdapter {

        public OurViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return getArtistFragment((position == 0)? Day.FRIDAY: Day.SATURDAY );
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) return getResources().getString(R.string.dayTabOne);
            return getResources().getString(R.string.dayTabTwo);
        }
    }
}
