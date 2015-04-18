package com.insalyon.les24heures;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

            position = intent.getIntExtra("categoryPosition", categories.size() - 1);
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

        //todo #31
//        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset,
//                                       int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    mSlidingTabLayout.announceForAccessibility(
//                            getString(R.string.my_schedule_page_desc_a11y,
//                                    getDayName(position)));
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                enableDisableSwipeRefresh(state == ViewPager.SCROLL_STATE_IDLE);
//            }
//        });



        startPreferredOutput(savedInstanceState);

        animateSwitching = true;
    }

    private class OurViewPagerAdapter extends FragmentPagerAdapter {

        public OurViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag;
            if(position == 0){
                frag = new DayMapsFragment();
            }else {
                frag = new DayListFragment();
            }
            return frag;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return position+"";
        }
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
        //tODO #31
        //Output state
//        detailSlidingUpPanelLayoutLayout.collapsePanel();
//        if (fragmentManager.findFragmentById(R.id.day_output_holder).getClass() == DayMapsFragment.class) {
//            just focus on resource and highlith it
//        } else {
//            open maps on resource
//            selectMaps();
//        }
    }



    /**
     * Activity is no more alive
     */

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //TODO #31

//        Output state
//        if (fragmentManager.findFragmentById(R.id.day_output_holder).getClass() == DayMapsFragment.class) {
            outState.putString("outputType", OutputType.MAPS.toString());
//        } else {
//            outState.putString("outputType", OutputType.LIST.toString());
//        }
    }

    /**
     * Activity's methods
     */

    @Override
    public void restoreTitle() {
        //TODO #31
//        String str;
//        if (fragmentManager.findFragmentById(R.id.day_output_holder).getClass() == DayMapsFragment.class) {
//            str = (getResources().getString(R.string.day_maps_appname));
//        } else {
//            str = (getResources().getString(R.string.day_list_appname));
//        }
//
//        if (str != getActionBar().getTitle()) {
//            setTitle(str);
//        }
    }


    public void selectMaps() {
        Fragment mapsFragment = new DayMapsFragment();
        replaceContentFragment(mapsFragment);
    }

    public void selectList() {
        Fragment listFragment = new DayListFragment();
        replaceContentFragment(listFragment);
    }

    private void replaceContentFragment(Fragment fragment) {

        Bundle bundleArgs = new Bundle();

        bundleArgs.putParcelableArrayList("categoriesSelected", selectedCategories);
        searchQuery = (searchQuery == null) ? null : (searchQuery.equals("")) ? null : searchQuery;
        bundleArgs.putString("searchQuery", searchQuery);
        fragment.setArguments(bundleArgs);

        FragmentTransaction ft = fragmentManager.beginTransaction();

        bundleArgs.putParcelableArrayList("resourcesList", dayResourceArrayList);

        AnimatorSet set;

        if (fragment.getClass() == DayMapsFragment.class) {
            ft.setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_to_the_right);
            set = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(),
                    R.animator.slide_in_from_right);
        } else {
            ft.setCustomAnimations(R.animator.slide_in_from_right, R.animator.slide_out_to_the_left);
            set = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(),
                    R.animator.slide_out_to_the_right);
        }

//        ft.replace(R.id.day_output_holder, fragment).commit();
//
//        if (animateSwitching) {
//            set.setTarget(indicator);
//            set.start();
//        }


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

    private class DrawerCategoriesClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectCategory(position);
        }
    }

}

