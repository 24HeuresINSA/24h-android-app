package com.insalyon.les24heures;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.insalyon.les24heures.fragments.ListFragment;
import com.insalyon.les24heures.fragments.MapsFragment;
import com.insalyon.les24heures.utils.OutputType;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();

    FragmentManager fragmentManager;

    private ActionBarDrawerToggle actionBarDrawerToggle;

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.left_drawer)
    View drawerView;
    @InjectView(R.id.left_drawer_categories_list)
    ListView categoriesList;
    @InjectView(R.id.outputtype_maps)
    View outputTypeMaps;
    @InjectView(R.id.outputtype_list)
    View outputTypeList;


    private String[] navigationDrawerCategories; //viendra du backend




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this, this);

        fragmentManager= getFragmentManager();

        setSupportActionBar(toolbar);

        navigationDrawerCategories = getResources().getStringArray(R.array.navigation_drawer_categories); //

        // set a custom shadow that overlays the main content when the drawer opens
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // set up the drawer's list view with items and click listener
        categoriesList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item_category, navigationDrawerCategories));
        categoriesList.setOnItemClickListener(new DrawerItemClickListener());

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        if (savedInstanceState != null) {
            if(savedInstanceState.getString("outputType") != null ){ 
                switch (OutputType.valueOf(savedInstanceState.getString("outputType").toUpperCase())){
                    case MAPS:
                        selectMaps(outputTypeMaps);
                        break;
                    case LIST:
                        selectList(outputTypeList);
                        break;
                }
            }else{
                //default
                selectMaps(outputTypeMaps);
            }
        }else{
            //default
            selectMaps(outputTypeMaps);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outputTypeMaps.isSelected()){
            outState.putString("outputType", OutputType.MAPS.toString());  
        }else {
            outState.putString("outputType",OutputType.LIST.toString());  
        }
    }

    @OnClick(R.id.outputtype_maps)
    void selectMaps(View view) {
        setTitle(R.string.drawer_outputtype_maps);
        view.setSelected(true);
        outputTypeList.setSelected(false);
        drawerLayout.closeDrawer(drawerView);
        //TODO replace fragment
        Fragment mapsFragment = new MapsFragment();
        fragmentManager.beginTransaction().replace(R.id.content_frame, mapsFragment).commit();
    }


    @OnClick(R.id.outputtype_list)
    void selectList(View view) {
        setTitle(R.string.drawer_outputtype_list);
        view.setSelected(true);
        outputTypeMaps.setSelected(false);
        drawerLayout.closeDrawer(drawerView);
        //TODO replace fragment
        Fragment listFragment = new ListFragment();
        fragmentManager.beginTransaction().replace(R.id.content_frame, listFragment).commit();

    }


    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectCategory(position);
        }
    }

    private void selectCategory(int position) {
        ArrayList<String> categoriesString = new ArrayList<>();

        int len = categoriesList.getCount();
        SparseBooleanArray checked = categoriesList.getCheckedItemPositions();
        for (int i = 0; i < len; i++)
            if (checked.get(i)) {
                categoriesString.add(navigationDrawerCategories[i]);
            }

        //TODO pas besoin de remplacement de fragment, juste des events
        // update the main content by replacing fragments
//        Fragment fragment = new DummyFragment();
//        Bundle args = new Bundle();
//        args.putInt(DummyFragment.ARG_MENU_INDEX, position);
//        args.putStringArrayList("categories",categoriesString);
//        fragment.setArguments(args);
//
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        if(categoriesList.isItemChecked(position)){
            //TODO event categories
            Log.i(TAG+"selectCategory","categoy added :"+navigationDrawerCategories[position]);
        } else if(!categoriesList.isItemChecked(position)){
            //TODO event categories
            Log.i(TAG+"selectCategory","categoy removed :"+navigationDrawerCategories[position]);
        }

        drawerLayout.closeDrawer(drawerView);
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    //TODO bha, va disparaitre tout simplement
    public static class DummyFragment extends Fragment {
        public static final String ARG_MENU_INDEX = "index";

        public DummyFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.dummy_fragment, container, false);
            int index = getArguments().getInt(ARG_MENU_INDEX);
            List<String> categories = getArguments().getStringArrayList("categories");
            String text = String.format("Categories",categories);
            ((TextView) rootView.findViewById(R.id.textView)).setText(categories.toString());
            return rootView;
        }
    }
}