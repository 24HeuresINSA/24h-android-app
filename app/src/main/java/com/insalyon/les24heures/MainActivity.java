package com.insalyon.les24heures;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.insalyon.les24heures.eventbus.CategoryEvent;
import com.insalyon.les24heures.fragments.OutputListFragment;
import com.insalyon.les24heures.fragments.OutputMapsFragment;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.utils.FilterAction;
import com.insalyon.les24heures.utils.OutputType;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();

    FragmentManager fragmentManager;
    EventBus eventBus;

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
    private List<Category> categories;
    private ArrayList<Category> categoriesSelected;
    private ArrayList<Resource> resourcesList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this, this);
        eventBus = EventBus.getDefault();


        fragmentManager= getFragmentManager();

        setSupportActionBar(toolbar);

        //viendra du backend
        categories = new ArrayList<>();
        navigationDrawerCategories = getResources().getStringArray(R.array.navigation_drawer_categories);
        for (String navigationDrawerCategory : navigationDrawerCategories) {
            categories.add(new Category(navigationDrawerCategory));
        }

        //viendra du backend
        resourcesList = new ArrayList<>();
        resourcesList.add(new Resource("se divertir", "les plaisirs c'est bien pour les calins et les chateau coconuts", null, new LatLng(45.783088762965, 4.8747852427139), categories.get(0)));
        resourcesList.add(new Resource("se cultiver", "la culture on s'en fout sauf Alexis et Jeaaane", null, new LatLng(45.783514302374, 4.8747852427139), categories.get(1)));
        resourcesList.add(new Resource("du sport", "du sport pour les pédales et éliminer l'apero parce qu'il ne faut pas déconner", null, new LatLng(45.784196093864, 4.8747852427139), categories.get(2)));
        resourcesList.add(new Resource("mes favoris", "mes favoris pour bien montrer que j'ai des gouts de merde", null, new LatLng(45.783827609484, 4.8747852427139), categories.get(3)));
        resourcesList.add(new Resource("lieux utiles", "où qu'on boit où qu'on pisse, où qu'on mange", null, new LatLng(45.784196093888, 4.8747852427139), categories.get(4)));

        //viendra du cache
        categoriesSelected = new ArrayList<>();

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
            if(savedInstanceState.getParcelableArrayList("categoriesSelected") != null){
                categoriesSelected = savedInstanceState.getParcelableArrayList("categoriesSelected");
                Log.i(TAG+" onCreate","categoriesSelected from savedInstanceState :"+categoriesSelected);
            }
        }else{
            //default get from manifest
            try {
                ApplicationInfo ai = getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
                Bundle bundle = ai.metaData;
                String defaultOutput = bundle.getString("outputType");
                if(defaultOutput.toLowerCase().equals(OutputType.MAPS.toString().toLowerCase())){
                    selectMaps(outputTypeMaps);
                } else  if(defaultOutput.toLowerCase().equals(OutputType.LIST.toString().toLowerCase())){
                    selectList(outputTypeList);
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
                selectMaps(outputTypeMaps);
            } catch (NullPointerException e) {
                Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
                selectMaps(outputTypeMaps);
            }

        }
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Output state
        if(outputTypeMaps.isSelected()){
            outState.putString("outputType", OutputType.MAPS.toString());  
        }else {
            outState.putString("outputType",OutputType.LIST.toString());  
        }

        //categories state
        ArrayList<Category> categoriesSelected = getCategoriesSelectedFromView();
        outState.putParcelableArrayList("categoriesSelected",categoriesSelected);
    }

    @OnClick(R.id.outputtype_maps)
    void selectMaps(View view) {
        if(outputTypeMaps.isSelected()) return;

        outputTypeMaps.setSelected(true);
        outputTypeList.setSelected(false);
        drawerLayout.closeDrawer(drawerView);

        Fragment mapsFragment = new OutputMapsFragment();
        replaceContentFragment(mapsFragment);
    }

    private void replaceContentFragment(Fragment fragment) {
        Bundle bundleArgs = new Bundle();
        bundleArgs.putParcelableArrayList("categoriesSelected", categoriesSelected);
        fragment.setArguments(bundleArgs);
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }


    @OnClick(R.id.outputtype_list)
    void selectList(View view) {
        if(outputTypeList.isSelected()) return;

        outputTypeList.setSelected(true);
        outputTypeMaps.setSelected(false);
        drawerLayout.closeDrawer(drawerView);

        Fragment listFragment = new OutputListFragment();
        replaceContentFragment(listFragment);
    }


    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectCategory(position);
        }
    }

    private void selectCategory(int position) {
        categoriesSelected = getCategoriesSelectedFromView();

        CategoryEvent categoryEvent = new CategoryEvent(categoriesSelected);

        // update selected item and title, then close the drawer
        if(categoriesList.isItemChecked(position)){
            Log.i(TAG+"selectCategory","categoy added :"+navigationDrawerCategories[position]);
            categoryEvent.setFilterAction(FilterAction.ADDED);
            eventBus.post(categoryEvent);

        } else if(!categoriesList.isItemChecked(position)){
            Log.i(TAG+"selectCategory","categoy removed :"+navigationDrawerCategories[position]);
            categoryEvent.setFilterAction(FilterAction.REMOVED);
            eventBus.post(categoryEvent);
        }

        drawerLayout.closeDrawer(drawerView);
    }

    private ArrayList<Category> getCategoriesSelectedFromView() {
        ArrayList<Category> categoriesSelected = new ArrayList<>();

        int len = categoriesList.getCount();
        SparseBooleanArray checked = categoriesList.getCheckedItemPositions();
        for (int i = 0; i < len; i++)
            if (checked.get(i)) {
                categoriesSelected.add(categories.get(i));
            }
        return categoriesSelected;
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


    public List<Category> getCategories() {
        return categories;
    }

    public ArrayList<Category> getCategoriesSelected() {
        return categoriesSelected;
    }

    @Override
    public String getPackageResourcePath() {
        return super.getPackageResourcePath();
    }

    public ArrayList<Resource> getResourcesList(){
        return resourcesList;
    }

    public void displayDrawer(){
        drawerLayout.openDrawer(drawerView);
    }

}