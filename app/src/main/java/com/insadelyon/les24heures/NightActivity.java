package com.insadelyon.les24heures;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.insadelyon.les24heures.fragments.ArtistFragment;
import com.insadelyon.les24heures.service.impl.ResourceServiceImpl;
import com.insadelyon.les24heures.utils.Day;
import com.insadelyon.les24heures.view.SlidingTabLayout;

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
        bundleArgs.putParcelableArrayList("resourcesList", ResourceServiceImpl.getInstance().filterByDay(nightResourceArrayList, day));
        searchQuery = (searchQuery == null) ? null : (searchQuery.equals("")) ? null : searchQuery;
        bundleArgs.putString("searchQuery", searchQuery);
        bundleArgs.putSerializable("day", day);

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
            Day day;
            switch (position) {
                case 0:
                    day = Day.FRIDAY;
                    break;
                case 1:
                    day = Day.SATURDAY;
                    break;
                case 2:
                    day = Day.SUNDAY;
                    break;
                default:
                    day = null;
                    break;

            }

            return getArtistFragment(day);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.dayTabOne);
                case 1:
                    return getResources().getString(R.string.dayTabTwo);
                case 2:
                    return getResources().getString(R.string.dayTabThree);
                default:
                    return getResources().getString(R.string.dayNoTab);
            }
        }
    }


}
