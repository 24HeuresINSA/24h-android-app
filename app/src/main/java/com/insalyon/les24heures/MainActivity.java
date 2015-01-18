package com.insalyon.les24heures;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.insalyon.les24heures.eventbus.CategoriesSelectedEvent;
import com.insalyon.les24heures.eventbus.CategoriesUpdatedEvent;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.fragments.OutputListFragment;
import com.insalyon.les24heures.fragments.OutputMapsFragment;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.service.CategoryService;
import com.insalyon.les24heures.service.ResourceRetrofitService;
import com.insalyon.les24heures.service.ResourceService;
import com.insalyon.les24heures.service.impl.CategoryServiceImpl;
import com.insalyon.les24heures.service.impl.ResourceServiceImpl;
import com.insalyon.les24heures.utils.CustomSearchView;
import com.insalyon.les24heures.utils.DrawerArrowDrawable;
import com.insalyon.les24heures.utils.FilterAction;
import com.insalyon.les24heures.utils.OutputType;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import retrofit.RestAdapter;


public class MainActivity extends Activity  {
    private static final String TAG = MainActivity.class.getCanonicalName();

    FragmentManager fragmentManager;
    EventBus eventBus;
    RestAdapter restAdapter;
    ResourceRetrofitService resourceRetrofitService;

//    private ActionBarDrawerToggle actionBarDrawerToggle;

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.left_drawer)
    View drawerView;
    @InjectView(R.id.left_drawer_categories_list)
    ListView categoriesList;
//    @InjectView(R.id.outputtype_maps)
//    View outputTypeMaps;
//    @InjectView(R.id.outputtype_list)
//    View outputTypeList;
//    @InjectView(R.id.search)
//    SearchView search;

    private DrawerArrowDrawable drawerArrowDrawable;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout.SimpleDrawerListener drawerListener;

    //fichtre
    //cest parce que le isDrawerOpen ne fonctionne pas bien lorsque le drawer est en mouvement
    private Boolean isDrawerOpen = false;



    Menu globalMenu;
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        globalMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_favorites, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final CustomSearchView searchView =
                (CustomSearchView) menu.findItem(R.id.search).getActionView();

        ((SearchView) globalMenu.findItem(R.id.search).getActionView()).isIconified();

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setArrow();
                disabledDrawerSwipe();

            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                setSandwich();
                enabledDrawerSwipe();
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("search","onQueryTextSubmit "+query);
                //TODO hide soft keyboard
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("search","onQueryTextChange "+newText);
                //tODO do filter
                return false;
            }
        });






        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //if item is the 'app name'
        if(item.getTitle().equals(getActionBar().getTitle())){
            //return from search view widget
            if(!((SearchView) globalMenu.findItem(R.id.search).getActionView()).isIconified()) {
                setSandwich();
                enabledDrawerSwipe();
                SearchView searchView =
                        (SearchView) globalMenu.findItem(R.id.search).getActionView();
                searchView.onActionViewCollapsed();
            }else {
                toggleDrawer();
            }
            return true;
        }
        return false;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    //TODO utiliser pour masquer les boutons lorsque le drawer apparait
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        invalidateOptionsMenu();
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerVisible(drawerView);
        menu.findItem(R.id.search).setVisible(!isDrawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }







    private String[] navigationDrawerCategories; //viendra du backend, a supprimer du manifest
    private ArrayList<Category> categories;
    private ArrayList<Category> categoriesSelected;
    private ArrayList<Resource> resourcesList;

    //depency injection ?
    private ResourceService resourceService;
    private CategoryService categoryService;

    private float offset;
    private boolean flipped;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*** init services ***/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this, this);
        eventBus = EventBus.getDefault();
        restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://cetaitmieuxavant.24heures.org")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        resourceRetrofitService = restAdapter.create(ResourceRetrofitService.class);
        fragmentManager = getFragmentManager();
        //dependency injection instead ?
        resourceService = ResourceServiceImpl.getInstance();
        categoryService = CategoryServiceImpl.getInstance();



        /*** recover data either from (by priority)
         *           savedInstanceState (rotate, restore from background)
         *           getIntent (start from another activity, another apps) TODO
         *           sharedPreferences (start,rotate, restore from bg) TODO
         *           localStorage (start) TODO
         *           backend (if needed TODO)
         */
        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelableArrayList("categories") != null) {
                categories = savedInstanceState.getParcelableArrayList("categories");
            }
            if (savedInstanceState.getParcelableArrayList("categoriesSelected") != null) {
                categoriesSelected = savedInstanceState.getParcelableArrayList("categoriesSelected");
            }
            if (savedInstanceState.getParcelableArrayList("resourcesList") != null) {
                resourcesList = savedInstanceState.getParcelableArrayList("resourcesList");
            }
        }

        navigationDrawerCategories = getResources().getStringArray(R.array.navigation_drawer_categories); //TODO que veut en parametre ArrayAdapter ?
        if (categories == null) {
            //viendra du backend
            categories = new ArrayList<>();
            for (String navigationDrawerCategory : navigationDrawerCategories) {
                categories.add(new Category(navigationDrawerCategory));
            }
            eventBus.post(new CategoriesUpdatedEvent(categories));
            //TODO comprendre pourquoi est ce que je dois faire ca, meme si au final la réponse ne servira pas pour ce cas precis
            categoryService.setCategories(categories);
        }

        if (resourcesList == null) {
            resourcesList = new ArrayList<>();
           resourceService.getResourcesAsyncFromBackend(resourceRetrofitService);
//            resourceService.getResourcesAsyncMock();
        }

        if(categoriesSelected == null){
            categoriesSelected = new ArrayList<>();
        }


        /*** setup the navigation drawer ***/
//        setSupportActionBar(toolbar);

        // set a custom shadow that overlays the main content when the drawer opens
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // set up the drawer's list view with items and click listener
        categoriesList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item_category, navigationDrawerCategories));
        categoriesList.setOnItemClickListener(new DrawerItemClickListener());

//        actionBarDrawerToggle.onDrawerOpened();
//        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);

//        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        // enable ActionBar app icon to behave as action to toggle nav drawer
//        getSupportActionBar().
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);


        //arrow
        final Resources resources = getResources();
        drawerArrowDrawable = new DrawerArrowDrawable(resources);
        drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.light_gray));
        getActionBar().
                setIcon(drawerArrowDrawable);

        drawerListener = new DrawerLayout.SimpleDrawerListener() {
            @Override public void onDrawerSlide(View drawerView, float slideOffset) {
                offset = slideOffset;

                // Sometimes slideOffset ends up so close to but not quite 1 or 0.
                if (slideOffset >= .995) {
                    flipped = true;
                    drawerArrowDrawable.setFlip(flipped);
                } else if (slideOffset <= .005) {
                    flipped = false;
                    drawerArrowDrawable.setFlip(flipped);
                }

                drawerArrowDrawable.setParameter(offset);

                Log.d("onDrawerSlide",flipped+" "+offset);
            }
        };

        drawerLayout.setDrawerListener(drawerListener);





//        search.seton





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
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
//        actionBarDrawerToggle.syncState();
    }


    /**         Activity is alive       **/

    public void onEvent(ResourcesUpdatedEvent event) {
        // super.onEvent(event);
        Log.d(TAG + "onEvent(ResourcesUpdatedEvent)", event.getResourceList().toString());
        resourcesList.clear();
        resourcesList.addAll(event.getResourceList());
    }

//    @OnClick(R.id.outputtype_maps)
    public void selectMaps() {
//        if (outputTypeMaps.isSelected()) return;

//        outputTypeMaps.setSelected(true);
//        outputTypeList.setSelected(false);
//        drawerLayout.closeDrawer(drawerView);

        Fragment mapsFragment = new OutputMapsFragment();
        replaceContentFragment(mapsFragment);
    }

//    @OnClick(R.id.outputtype_list)
    public void selectList() {
//        if (outputTypeList.isSelected()) return;

//        outputTypeList.setSelected(true);
//        outputTypeMaps.setSelected(false);
//        drawerLayout.closeDrawer(drawerView);

        Fragment listFragment = new OutputListFragment();
        replaceContentFragment(listFragment);
    }

    @Override
    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
//        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**         Activity is no more alive       **/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Output state
//        if (outputTypeMaps.isSelected()) {
        if (fragmentManager.findFragmentById(R.id.content_frame).getClass() == OutputMapsFragment.class) {
            outState.putString("outputType", OutputType.MAPS.toString());
        } else {
            outState.putString("outputType", OutputType.LIST.toString());
        }

        //categories
        outState.putParcelableArrayList("categories", categories);
        //categories state
        ArrayList<Category> categoriesSelected = getCategoriesSelectedFromView();
        outState.putParcelableArrayList("categoriesSelected", categoriesSelected);
        //resources
        outState.putParcelableArrayList("resourcesList", resourcesList);
    }


    /**         Activity methods      **/

    private void replaceContentFragment(Fragment fragment) {
        Bundle bundleArgs = new Bundle();
        bundleArgs.putParcelableArrayList("categoriesSelected", categoriesSelected);
        bundleArgs.putParcelableArrayList("resourcesList", resourcesList);
        fragment.setArguments(bundleArgs);

        FragmentTransaction ft = fragmentManager.beginTransaction();

        if(fragment.getClass() == OutputListFragment.class)
            ft.setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_to_the_right);
        else
            ft.setCustomAnimations(R.animator.slide_in_from_right, R.animator.slide_out_to_the_left);

        ft.replace(R.id.content_frame, fragment).commit();
    }

//    @Override
//    public boolean onQueryTextSubmit(String s) {
//        Log.d("caca",s);
//        return false;
//    }
//
//    @Override
//    public boolean onQueryTextChange(String s) {
//        Log.d("popo",s);
//        return false;
//    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectCategory(position);
        }
    }

    private void selectCategory(int position) {
        categoriesSelected = getCategoriesSelectedFromView();

        CategoriesSelectedEvent categoriesSelectedEvent = new CategoriesSelectedEvent(categoriesSelected);

        // update selected item and title, then close the drawer
        if (categoriesList.isItemChecked(position)) {
            Log.i(TAG + "selectCategory", "categoy added :" + navigationDrawerCategories[position]);
            categoriesSelectedEvent.setFilterAction(FilterAction.ADDED);
            eventBus.post(categoriesSelectedEvent);

        } else if (!categoriesList.isItemChecked(position)) {
            Log.i(TAG + "selectCategory", "categoy removed :" + navigationDrawerCategories[position]);
            categoriesSelectedEvent.setFilterAction(FilterAction.REMOVED);
            eventBus.post(categoriesSelectedEvent);
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


    private void toggleDrawer(){
        if(drawerLayout.isDrawerOpen(drawerView)){
            closeDrawer();
        }else{
            openDrawer();
        }

    }
    public void openDrawer() {

        drawerLayout.openDrawer(drawerView);
        isDrawerOpen = true;
        invalidateOptionsMenu();

    }

    public void closeDrawer() {

        drawerLayout.closeDrawer(drawerView);
        isDrawerOpen = false;
        invalidateOptionsMenu();

    }

    public void setArrow(){
        ValueAnimator animation = ValueAnimator.ofFloat(0f, 1f);
        animation.setDuration(500);
        animation.start();
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                drawerArrowDrawable.setParameter((Float) animation.getAnimatedValue());
            }
        });



        drawerArrowDrawable.setFlip(true);




    }


    private void disabledDrawerSwipe(){
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void enabledDrawerSwipe(){
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public void setSandwich(){
//          drawerArrowDrawable.setFlip(false);

        ValueAnimator animation = ValueAnimator.ofFloat(1f, 0f);
        animation.setDuration(500);
        animation.start();
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.d("setArrow",animation.getAnimatedValue().toString());
                drawerArrowDrawable.setParameter((Float) animation.getAnimatedValue());

            }
        });


        drawerArrowDrawable.setParameter(0);

    }




}

