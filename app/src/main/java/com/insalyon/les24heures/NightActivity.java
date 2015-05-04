package com.insalyon.les24heures;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.insalyon.les24heures.fragments.ArtistFragment;
import com.insalyon.les24heures.service.impl.ResourceServiceImpl;
import com.insalyon.les24heures.utils.Day;
import com.insalyon.les24heures.view.SlidingTabLayout;

import butterknife.InjectView;

/**
 * Created by remi on 12/03/15.
 */
public class NightActivity extends BaseDynamicDataActivity {

    @InjectView(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTabLayout;
    @InjectView(R.id.view_pager)
    ViewPager mViewPager;
    private OurViewPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.night_activity);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

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
                restoreTitle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        clearDrawerChoices();
        artistButton.setActivated(true);
        setSelectedMenuItem(artistButton, R.drawable.concert_bleu);
        drawerLayout.closeDrawer();
        restoreTitle();
    }


    private ArtistFragment getArtistFragment(Day day) {
        Bundle bundleArgs = new Bundle();
        bundleArgs.putParcelableArrayList("categoriesSelected", selectedCategories);
        bundleArgs.putParcelableArrayList("resourcesList", ResourceServiceImpl.getInstance().filterByDay(nightResourceArrayList,day));
        searchQuery = (searchQuery == null) ? null : (searchQuery.equals("")) ? null : searchQuery;
        bundleArgs.putString("searchQuery", searchQuery);
        bundleArgs.putSerializable("day",day);

        ArtistFragment fragment = new ArtistFragment();
        fragment.setArguments(bundleArgs);

        return fragment;
    }

    @Override
    public void restoreTitle() {
        String str = (getResources().getString(R.string.artist_fragment_appname));

        if (str != getActionBar().getTitle()) {
            setTitle(str);
        }
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
