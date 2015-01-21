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
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.insalyon.les24heures.eventbus.CategoriesSelectedEvent;
import com.insalyon.les24heures.eventbus.CategoriesUpdatedEvent;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.eventbus.SearchEvent;
import com.insalyon.les24heures.fragments.OutputListFragment;
import com.insalyon.les24heures.fragments.OutputMapsFragment;
import com.insalyon.les24heures.fragments.OutputTypeFragment;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.service.CategoryService;
import com.insalyon.les24heures.service.ResourceRetrofitService;
import com.insalyon.les24heures.service.ResourceService;
import com.insalyon.les24heures.service.impl.CategoryServiceImpl;
import com.insalyon.les24heures.service.impl.ResourceServiceImpl;
import com.insalyon.les24heures.utils.DrawerArrowDrawable;
import com.insalyon.les24heures.utils.FilterAction;
import com.insalyon.les24heures.utils.OutputType;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import retrofit.RestAdapter;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getCanonicalName();

    FragmentManager fragmentManager;
    EventBus eventBus;
    RestAdapter restAdapter;
    ResourceRetrofitService resourceRetrofitService;


    private ResourceService resourceService;
    private CategoryService categoryService;

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.left_drawer)
    View drawerView;
    @InjectView(R.id.left_drawer_categories_list)
    ListView categoriesList;


    private String[] navigationDrawerCategories; //viendra du backend, a supprimer du manifest
    private ArrayList<Category> categories;
    private ArrayList<Category> categoriesSelected;
    private ArrayList<Resource> resourcesList;

    private DrawerArrowDrawable drawerArrowDrawable;
    private DrawerLayout.SimpleDrawerListener drawerListener;
    private Menu globalMenu;


    //isDrawerOpen only switches when the drawer is fully opened
    private Boolean isDrawerOpen = false;
    private Boolean isFavoritesChecked = false;
    private String searchQuery;
    private float offset;
    private boolean flipped;

    /**
     * Activity is being created       *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*** init services ***/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this, this);
        eventBus = EventBus.getDefault();
        eventBus.register(this);
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
            if (savedInstanceState.getString("searchQuery") != null) {
                SearchEvent searchEvent = new SearchEvent(savedInstanceState.getString("searchQuery").toString());
                eventBus.postSticky(searchEvent);
                searchQuery = savedInstanceState.getString("searchQuery").toString();
            }
            if (savedInstanceState.getBoolean("isFavoritesChecked")) {
                //TODO globalMenu is null here
                isFavoritesChecked = savedInstanceState.getBoolean("isFavoritesChecked");
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

        if (categoriesSelected == null) {
            categoriesSelected = new ArrayList<>();
        }


        /*** setup the navigation drawer ***/

        // set a custom shadow that overlays the main content when the drawer opens
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        categoriesList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item_category, navigationDrawerCategories));
        categoriesList.setOnItemClickListener(new DrawerItemClickListener());
        getActionBar().setHomeButtonEnabled(true);

        //arrow/sandwich
        final Resources resources = getResources();
        drawerArrowDrawable = new DrawerArrowDrawable(resources);
        drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.light_gray));
        getActionBar().setIcon(drawerArrowDrawable);

        drawerListener = new DrawerListener();
        drawerLayout.setDrawerListener(drawerListener);


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

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        globalMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_favorites, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        final MenuItem favoritesItem = menu.findItem(R.id.menu_favorites);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("pouet", "open search view");
            }
        });

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setArrow();
                disabledDrawerSwipe();
                disableFavoritesFilter(favoritesItem);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                setSandwich();
                enabledDrawerSwipe();
                searchQuery = null;
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SearchEvent searchEvent = new SearchEvent(newText);
                eventBus.post(searchEvent);
                searchQuery = newText;
                return false;
            }
        });

        //init searchView widget depending on bundleSaveInstanceState
        if (searchQuery != null) {
            if (!searchQuery.equals("")) //je craque, tant pis pour la rotation...
                if (!searchView.getQuery().equals(searchQuery)) {
                    searchView.setIconified(false);
                    searchView.setQuery(searchQuery.toString(), true);
                    //TODO it doesn't work, the soft keyboard is displayed anyway...
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
        }

        //init favorites button depending on bundleSaveInstanceState
        disableFavoritesFilter(favoritesItem);

        return true;
    }

    private void disableFavoritesFilter(MenuItem favoritesItem) {
        if (isFavoritesChecked) {
            toggleFavorites(favoritesItem);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //click on the appName or the appIcone
        if (item.getTitle().equals(getActionBar().getTitle())) {
            if (!((SearchView) globalMenu.findItem(R.id.menu_search).getActionView()).isIconified()) {
                setSandwich();
                enabledDrawerSwipe();
                SearchView searchView =
                        (SearchView) globalMenu.findItem(R.id.menu_search).getActionView();
                searchView.onActionViewCollapsed();
                searchQuery = null;
            } else {
                toggleDrawer();
            }
            return true;
        }

        switch (item.getItemId()) {
            case R.id.menu_favorites:

                toggleFavorites(item);

                return true;
        }


        return false;

    }

    private void toggleFavorites(MenuItem item) {
        ArrayList<Category> list = new ArrayList<>();
        list.addAll(getCategoriesSelectedFromDrawer());
        if (item.isChecked()) {
            item.setChecked(false);
            item.setIcon(R.drawable.ic_favorites_unchecked);
            isFavoritesChecked = false;
        } else {
            list.add((new Category("favorites")));
            item.setChecked(true);
            item.setIcon(R.drawable.ic_favorites_checked);
            isFavoritesChecked = true;
        }
        CategoriesSelectedEvent event = new CategoriesSelectedEvent(list);
        eventBus.post(event);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = drawerLayout.isDrawerVisible(drawerView);


        hideOptionsMenu(menu, drawerOpen);

        return super.onPrepareOptionsMenu(menu);
    }

    private void hideOptionsMenu(Menu menu, boolean drawerOpen) {
        menu.findItem(R.id.menu_search).setVisible(!drawerOpen);
        menu.findItem(R.id.menu_favorites).setVisible(!drawerOpen);
    }

    /**
     * Activity is alive       *
     */

    public void onEvent(ResourcesUpdatedEvent event) {
        // super.onEvent(event);
        Log.d(TAG + "onEvent(ResourcesUpdatedEvent)", event.getResourceList().toString());
        resourcesList.clear();
        resourcesList.addAll(event.getResourceList());
    }

    public void selectMaps() {
        Fragment mapsFragment = new OutputMapsFragment();
        replaceContentFragment(mapsFragment);
    }

    public void selectList() {
        Fragment listFragment = new OutputListFragment();
        replaceContentFragment(listFragment);
    }

    @Override
    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }


    /**
     * Activity is no more alive       *
     */

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Output state
        if (fragmentManager.findFragmentById(R.id.content_frame).getClass() == OutputMapsFragment.class) {
            outState.putString("outputType", OutputType.MAPS.toString());
        } else {
            outState.putString("outputType", OutputType.LIST.toString());
        }

        //categories
        outState.putParcelableArrayList("categories", categories);
        //categories state
        ArrayList<Category> categoriesSelected = getCategoriesSelectedFromDrawer();
        outState.putParcelableArrayList("categoriesSelected", categoriesSelected);
        //resources
        outState.putParcelableArrayList("resourcesList", resourcesList);

        //action bar menu
        SearchView searchView =
                (SearchView) globalMenu.findItem(R.id.menu_search).getActionView();
        if (!searchView.getQuery().toString().equals("")) {
            outState.putString("searchQuery", searchView.getQuery().toString());
        }
        outState.putBoolean("isFavoritesChecked", globalMenu.findItem(R.id.menu_favorites).isChecked());
    }


    /**
     * Activity methods      *
     */

    private void replaceContentFragment(Fragment fragment) {
        Bundle bundleArgs = new Bundle();
        bundleArgs.putParcelableArrayList("categoriesSelected", categoriesSelected);
        bundleArgs.putParcelableArrayList("resourcesList", resourcesList);
        searchQuery = (searchQuery == null) ? null : (searchQuery.equals("")) ? null : searchQuery;
        bundleArgs.putString("searchQuery", searchQuery);
        fragment.setArguments(bundleArgs);

        FragmentTransaction ft = fragmentManager.beginTransaction();

        if (fragment.getClass() == OutputListFragment.class)
            ft.setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_to_the_right);
        else
            ft.setCustomAnimations(R.animator.slide_in_from_right, R.animator.slide_out_to_the_left);

        ft.replace(R.id.content_frame, fragment).commit();
    }

    /**
     * Drawer methods and inner classes
     */

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectCategory(position);
        }
    }

    private class DrawerListener extends DrawerLayout.SimpleDrawerListener {
        private MenuItem itemFav;
        private MenuItem itemSearch;

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            isDrawerOpen = true;
            getActionBar().setTitle(R.string.app_name);
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
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

            if (itemFav == null) {
                itemFav = globalMenu.findItem(R.id.menu_favorites);
                itemSearch = globalMenu.findItem(R.id.menu_search);
            }

            if (offset > 0.95)
                return;
            itemFav.getIcon().setAlpha((int) (255 - slideOffset * 255));
            itemSearch.getActionView().setAlpha(1 - slideOffset);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
            isDrawerOpen = false;
            getActionBar().setTitle(
                    ((OutputTypeFragment) getFragmentManager().findFragmentById(R.id.content_frame))
                            .getDisplayName());

        }
    }

    private void selectCategory(int position) {
        categoriesSelected = getCategoriesSelectedFromDrawer();
        if (globalMenu.findItem(R.id.menu_favorites).isChecked()) {
            categoriesSelected.add(new Category("favorites"));
        }

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

    private ArrayList<Category> getCategoriesSelectedFromDrawer() {
        ArrayList<Category> categoriesSelected = new ArrayList<>();

        int len = categoriesList.getCount();
        SparseBooleanArray checked = categoriesList.getCheckedItemPositions();
        for (int i = 0; i < len; i++)
            if (checked.get(i)) {
                categoriesSelected.add(categories.get(i));
            }
        return categoriesSelected;
    }

    private void toggleDrawer() {
        if (drawerLayout.isDrawerOpen(drawerView)) {
            closeDrawer();
        } else {
            openDrawer();
        }

    }

    public void openDrawer() {
        drawerLayout.openDrawer(drawerView);


    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(drawerView);


    }

    public void setArrow() {
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

    private void disabledDrawerSwipe() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void enabledDrawerSwipe() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public void setSandwich() {
        ValueAnimator animation = ValueAnimator.ofFloat(1f, 0f);
        animation.setDuration(500);
        animation.start();
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                drawerArrowDrawable.setParameter((Float) animation.getAnimatedValue());
            }
        });
        drawerArrowDrawable.setParameter(0);
    }


}

