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
import com.insalyon.les24heures.fragments.OutputListFragment;
import com.insalyon.les24heures.fragments.OutputMapsFragment;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.utils.OutputType;

import java.util.ArrayList;
import java.util.List;


public class DayActivity extends BaseDynamicDataActivity {
    private static final String TAG = DayActivity.class.getCanonicalName();



    /**
     * Activity is being created       *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*** init services ***/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_activity);


    }

    private void startPreferedOutput(Bundle savedInstanceState) {
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

    //here we do all the stuff requiring the view
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //override Base's DrawerCategoriesClickListener
        categoriesList.setOnItemClickListener(new DrawerCategoriesClickListener());

        startPreferedOutput(savedInstanceState);


    }

    public void onEvent(ResourceSelectedEvent resourceSelected) {
        //Output state
        detailSlidingUpPanelLayoutLayout.collapsePanel();
        if (fragmentManager.findFragmentById(R.id.content_frame).getClass() == OutputMapsFragment.class) {
            //just focus on resource and highlith it
        } else {
            //open maps on resource
            selectMaps();
        }
    }


    public void selectMaps() {
        Fragment mapsFragment = new OutputMapsFragment();
        replaceContentFragment(mapsFragment);
    }

    public void selectList() {
        Fragment listFragment = new OutputListFragment();
        replaceContentFragment(listFragment);
    }

    private void replaceContentFragment(Fragment fragment) {

        //TODO remplacer le fragment ou juste faire une animation pour afficher/masquer au besoin les fragments tout le temps vivant ?

        Bundle bundleArgs = new Bundle();
        bundleArgs.putParcelableArrayList("categoriesSelected", selectedCategories);
        searchQuery = (searchQuery == null) ? null : (searchQuery.equals("")) ? null : searchQuery;
        bundleArgs.putString("searchQuery", searchQuery);
        fragment.setArguments(bundleArgs);

        FragmentTransaction ft = fragmentManager.beginTransaction();

        bundleArgs.putParcelableArrayList("resourcesList", dayResourceArrayList);

        if (fragment.getClass() == OutputListFragment.class)
            ft.setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_to_the_right);
        else
            ft.setCustomAnimations(R.animator.slide_in_from_right, R.animator.slide_out_to_the_left);


        ft.replace(R.id.day_output_holder, fragment).commit();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //TODO a adapter en fonction de la maniere dont on switch de list a maps

        //Output state
        if (fragmentManager.findFragmentById(R.id.content_frame).getClass() == OutputMapsFragment.class) {
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

    //day
    private void selectCategory(int position) {
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


        Class<? extends Fragment> currentFragment = fragmentManager.findFragmentById(R.id.content_frame).getClass();

        if (!(currentFragment == OutputListFragment.class || currentFragment == OutputMapsFragment.class)) {
            selectMaps();
        }

        drawerLayout.closeDrawer();
    }





}

