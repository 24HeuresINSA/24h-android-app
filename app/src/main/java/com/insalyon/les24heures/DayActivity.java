package com.insalyon.les24heures;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.insalyon.les24heures.adapter.CategoryAdapter;
import com.insalyon.les24heures.eventbus.CategoriesSelectedEvent;
import com.insalyon.les24heures.eventbus.ListSetIsVisible;
import com.insalyon.les24heures.eventbus.MapsSetIsVisible;
import com.insalyon.les24heures.eventbus.ResourceSelectedEvent;
import com.insalyon.les24heures.fragments.DayListFragment;
import com.insalyon.les24heures.fragments.DayMapsFragment;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.utils.OutputType;
import com.insalyon.les24heures.utils.SpecificCategory;
import com.insalyon.les24heures.view.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;


public class DayActivity extends BaseDynamicDataActivity {
    private static final String TAG = DayActivity.class.getCanonicalName();


    @InjectView(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTabLayout;
    @InjectView(R.id.view_pager)
    ViewPager mViewPager;
    Boolean animateSwitching = false;
    private Integer position;
    private OurViewPagerAdapter mViewPagerAdapter;

    /**
     * Activity is being created       *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*** init services ***/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_activity);

        //get selectedCateogires
        Intent intent = getIntent();
        if (intent != null) {
            //TODO potentiellement inutile
            if (intent.getParcelableArrayListExtra("selectedCategories") != null)
                selectedCategories = intent.getParcelableArrayListExtra("selectedCategories");

            position = intent.getIntExtra("categoryPosition", 4); // a l'ouverture on montre toutes les categories = select_all
        }


    }

    //here we do all the stuff requiring the view
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //override Base's DrawerCategoriesClickListener
        categoriesList.setOnItemClickListener(new DrawerCategoriesClickListener());

        ((CategoryAdapter) categoriesList.getAdapter()).setSelectedCategoryInit(position);

        //TODO revoir ca en fonction de la maniere dont on recupere les categories
        if (!selectedCategories.isEmpty()) {
            Category temp = selectedCategories.get(0);
            selectedCategories.clear();
            if (!temp.get_id().equals(SpecificCategory.ALL)) {
                selectedCategories.add(temp);
            }
        } else
            selectedCategories.clear();


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
                dayTypeFragmentSetIsVisible(position);
                restoreTitle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        startPreferredOutput(savedInstanceState);

        animateSwitching = true;
        restoreTitle();
    }

    private void dayTypeFragmentSetIsVisible(int position) {
        eventBus.postSticky(new ListSetIsVisible(position != 0));
        eventBus.postSticky(new MapsSetIsVisible(position == 0));
    }

    private void startPreferredOutput(Bundle savedInstanceState) {
        /*** start the right ouptut : Maps or List ***/
        if (savedInstanceState == null) {
            //default start : get from manifest
            try {
                ApplicationInfo ai = getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
                Bundle bundle = ai.metaData;
                String defaultOutput = bundle.getString("outputType");
                if (defaultOutput.toLowerCase().equals(OutputType.MAPS.toString().toLowerCase())) {
                    selectMaps();

                } else if (defaultOutput.toLowerCase().equals(OutputType.LIST.toString().toLowerCase())) {
                    selectList();

                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
                selectMaps();
            } catch (NullPointerException e) {
                Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
                selectMaps();
            }
        }
    }

    /**
     * Activity is alive
     */

    public void onEvent(ResourceSelectedEvent resourceSelected) {
        //Output state
        detailSlidingUpPanelLayoutLayout.collapsePanel();
        if (isMapsSelected()) {
//            just focus on resource and highlith it
        } else {
//            open maps on resource
            selectMaps();

        }
    }

    private boolean isMapsSelected() {
        return mViewPager.getCurrentItem() == 0;
    }

    /**
     * Activity is no more alive
     */

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

//        Output state
        if (isMapsSelected()) {
            outState.putString("outputType", OutputType.MAPS.toString());
        } else {
            outState.putString("outputType", OutputType.LIST.toString());
        }
    }

    /**
     * Activity's methods
     */

    @Override
    public void restoreTitle() {
        String str;
        if (isMapsSelected()) {
            str = (getResources().getString(R.string.day_maps_appname));
        } else {
            str = (getResources().getString(R.string.day_list_appname));
        }

        if (str != getActionBar().getTitle()) {
            setTitle(str);
        }
    }

    public void selectMaps() {
        mViewPager.setCurrentItem(0, animateSwitching);
        dayTypeFragmentSetIsVisible(0);

    }

    public void selectList() {
        mViewPager.setCurrentItem(0, animateSwitching);
        dayTypeFragmentSetIsVisible(1);

    }

    private List<Category> selectCategory(int position) {
        drawerLayout.closeDrawer();

        List<Category> catSelected = new ArrayList<>();

        CategoriesSelectedEvent categoriesSelectedEvent = new CategoriesSelectedEvent(catSelected);

        // update selected item and title, then close the drawer
        if (categoriesList.isItemChecked(position)) {
            catSelected.clear();
            selectedCategories.clear();
            if (categories.get(position).get_id() != SpecificCategory.ALL.toString()) {
                catSelected.add(categories.get(position));
                selectedCategories.add(categories.get(position));
            }
        }

        if (globalMenu.findItem(R.id.menu_favorites).isChecked()) {
            catSelected.add(categoryService.getFavoriteCategory());
        }

        eventBus.post(categoriesSelectedEvent);

        return categoriesSelectedEvent.getCategories();
    }

    private class OurViewPagerAdapter extends FragmentPagerAdapter {

        public OurViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                DayMapsFragment fragment = new DayMapsFragment();

                Bundle bundleArgs = new Bundle();
                bundleArgs.putParcelableArrayList("categoriesSelected", selectedCategories);
                fragment.setArguments(bundleArgs);

                fragment.setIsVisible(true);
                return fragment;
            }
            DayListFragment fragment = new DayListFragment();

            Bundle bundleArgs = new Bundle();
            bundleArgs.putParcelableArrayList("categoriesSelected", selectedCategories);
            fragment.setArguments(bundleArgs);

            fragment.setIsVisible(false);
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) return getResources().getString(R.string.dayTabmap);
            return getResources().getString(R.string.dayTablist);
        }
    }

    private class DrawerCategoriesClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectCategory(position);
        }
    }

}

