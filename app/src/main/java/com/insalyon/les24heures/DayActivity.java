package com.insalyon.les24heures;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.insalyon.les24heures.eventbus.CategoriesSelectedEvent;
import com.insalyon.les24heures.eventbus.ResourceSelectedEvent;
import com.insalyon.les24heures.fragments.DayListFragment;
import com.insalyon.les24heures.fragments.DayMapsFragment;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.utils.OutputType;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;


public class DayActivity extends BaseDynamicDataActivity {
    private static final String TAG = DayActivity.class.getCanonicalName();


    @InjectView(R.id.day_menu_tabs_list)
    View tabButtonList;
    @InjectView(R.id.day_menu_tabs_maps)
    View tabButtonMaps;


    /**
     * Activity is being created       *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*** init services ***/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_activity);

    }

    //here we do all the stuff requiring the view
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //override Base's DrawerCategoriesClickListener
        categoriesList.setOnItemClickListener(new DrawerCategoriesClickListener());

        startPreferredOutput(savedInstanceState);
    }

    //TODO is it still up ?
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
        if (fragmentManager.findFragmentById(R.id.day_output_holder).getClass() == DayMapsFragment.class) {
            //just focus on resource and highlith it
        } else {
            //open maps on resource
            selectMaps();
        }
    }

    @OnClick(R.id.day_menu_tabs_list)
    public void onClickListTab(View v){
        selectList();
    }
    @OnClick(R.id.day_menu_tabs_maps)
    public void onClickMapsTab(View v){
        selectMaps();
    }

    /**
     * Activity is no more alive
     */

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Output state
        if (fragmentManager.findFragmentById(R.id.day_output_holder).getClass() == DayMapsFragment.class) {
            outState.putString("outputType", OutputType.MAPS.toString());
        } else {
            outState.putString("outputType", OutputType.LIST.toString());
        }
    }

    private class DrawerCategoriesClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectCategory(position);
        }
    }


    /**
     * Activity's methods
     */

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

        if (fragment.getClass() == DayMapsFragment.class)
            ft.setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_to_the_right);
        else
            ft.setCustomAnimations(R.animator.slide_in_from_right, R.animator.slide_out_to_the_left);


        ft.replace(R.id.day_output_holder, fragment).commit();

    }

    private void selectCategory(int position) {
        drawerLayout.closeDrawer();

        List<Category> catSelected = new ArrayList<>();

        CategoriesSelectedEvent categoriesSelectedEvent = new CategoriesSelectedEvent(catSelected);

        // update selected item and title, then close the drawer
        if (categoriesList.isItemChecked(position)) {
            catSelected.clear();
            selectedCategories.clear();
            if(categories.get(position).getIconeName() != "ic_ALLCATEGORY" ){
                catSelected.add(categories.get(position));
                selectedCategories.add(categories.get(position));
            }
        }

        if (globalMenu.findItem(R.id.menu_favorites).isChecked()) {
            catSelected.add(new Category("FAVORITES","ic_FAVORITES"));
        }

        eventBus.post(categoriesSelectedEvent);

    }

}

