package com.insalyon.les24heures;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.insalyon.les24heures.adapter.CategoryAdapter;
import com.insalyon.les24heures.eventbus.CategoriesSelectedEvent;
import com.insalyon.les24heures.eventbus.CategoriesUpdatedEvent;
import com.insalyon.les24heures.eventbus.ManageDetailSlidingUpDrawer;
import com.insalyon.les24heures.eventbus.ResourceSelectedEvent;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.eventbus.SearchEvent;
import com.insalyon.les24heures.fragments.ArtistFragment;
import com.insalyon.les24heures.fragments.ContentFrameFragment;
import com.insalyon.les24heures.fragments.DetailFragment;
import com.insalyon.les24heures.fragments.OutputListFragment;
import com.insalyon.les24heures.fragments.OutputMapsFragment;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.model.NightResource;
import com.insalyon.les24heures.service.CategoryService;
import com.insalyon.les24heures.service.ResourceRetrofitService;
import com.insalyon.les24heures.service.ResourceService;
import com.insalyon.les24heures.service.impl.CategoryServiceImpl;
import com.insalyon.les24heures.service.impl.ResourceServiceImpl;
import com.insalyon.les24heures.utils.FilterAction;
import com.insalyon.les24heures.utils.OutputType;
import com.insalyon.les24heures.view.CustomDrawerLayout;
import com.insalyon.les24heures.view.DetailSlidingUpPanelLayout;
import com.insalyon.les24heures.view.DrawerArrowDrawable;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import retrofit.RestAdapter;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getCanonicalName();

    FragmentManager fragmentManager;
    EventBus eventBus;
    RestAdapter restAdapter;
    ResourceRetrofitService resourceRetrofitService;
    @InjectView(R.id.drawer_layout)
    CustomDrawerLayout drawerLayout;
    @InjectView(R.id.left_drawer)
    View drawerView;
    @InjectView(R.id.left_drawer_categories_list)
    ListView categoriesList;
    @InjectView(R.id.sliding_layout)
    DetailSlidingUpPanelLayout detailSlidingUpPanelLayoutLayout;
    DetailFragment detailFragment;
    private ResourceService resourceService;
    private CategoryService categoryService;
    private ArrayList<Category> categories;
    //need a list to store a category and favorites or not (it's a bad code resulting from the old impl where it could be possible to select several categories)
    private ArrayList<Category> categoriesSelected;
    private ArrayList<DayResource> dayResourceArrayList;
    private ArrayList<NightResource> nightResourceArrayList;

    private DrawerArrowDrawable drawerArrowDrawable;
    private DrawerLayout.SimpleDrawerListener drawerListener;
    private Menu globalMenu;


    private Boolean isFavoritesChecked = false;
    private String searchQuery;
    private Menu mMenu;
    private String slidingUpState;
    private RestAdapter restAdapterLocal;


    /**
     * Activity is being created       *
     */

    @Override
    //dans NavigationActivity sauf startRightOutput  
    protected void onCreate(Bundle savedInstanceState) {
        /*** init services ***/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this, this);
        eventBus = EventBus.getDefault();
        eventBus.register(this);
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.backend_url))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        restAdapterLocal = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.backend_url_local))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        resourceRetrofitService = restAdapter.create(ResourceRetrofitService.class);
//        resourceRetrofitService = restAdapterLocal.create(ResourceRetrofitService.class);
        fragmentManager = getFragmentManager();
        //dependency injection instead ?
        resourceService = ResourceServiceImpl.getInstance();
        categoryService = CategoryServiceImpl.getInstance();

        ///////////////////////// day, night need one
        detailSlidingUpPanelLayoutLayout.setActivity(this); //slidingPanel needs the activity to invalidateOptionMenu, manage appName and arrowDrawer
        detailSlidingUpPanelLayoutLayout.setParallaxHeader(findViewById(R.id.detail_paralax_header));

//

        detailFragment = (DetailFragment) fragmentManager.findFragmentById(R.id.sliding_layout_content_fragment);
        detailSlidingUpPanelLayoutLayout.setDetailFragment(detailFragment);


        ///////////////////////// all
        /*** recover data either from (by priority)
         *           savedInstanceState (rotate, restore from background)
         *           getIntent (start from another activity, another apps) TODO
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
            if (savedInstanceState.getParcelableArrayList("dayResourceArrayList") != null) {
                dayResourceArrayList = savedInstanceState.getParcelableArrayList("dayResourceArrayList");
            }
            if (savedInstanceState.getParcelableArrayList("nightResourceArrayList") != null) {
                nightResourceArrayList = savedInstanceState.getParcelableArrayList("nightResourceArrayList");
            }
            if (savedInstanceState.getString("searchQuery") != null) {
                SearchEvent searchEvent = new SearchEvent(savedInstanceState.getString("searchQuery").toString());
                eventBus.postSticky(searchEvent);
                searchQuery = savedInstanceState.getString("searchQuery").toString();
            }
            if (savedInstanceState.getBoolean("isFavoritesChecked")) {
                //globalMenu is null here
                isFavoritesChecked = savedInstanceState.getBoolean("isFavoritesChecked");
            }
            if (savedInstanceState.getString("slidingUpState") != null) {
                slidingUpState = savedInstanceState.getString("slidingUpState");
            }


        }

        if (dayResourceArrayList == null || nightResourceArrayList == null || categories == null) {
            dayResourceArrayList = new ArrayList<>();
            nightResourceArrayList = new ArrayList<>();
            categories = new ArrayList<>();
            resourceService.getResourcesAsyncFromBackend(resourceRetrofitService);
//            resourceService.getResourcesAsyncMock();
        }

        if (categoriesSelected == null) {
            categoriesSelected = new ArrayList<>();
        }


        /*** setup the navigation drawer ***/

        // set a custom shadow that overlays the main content when the drawer opens
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerLayout.setDrawerView(drawerView);
        // set up the drawer's list view with items and click listener
        categoriesList.setAdapter(new CategoryAdapter(this, R.layout.drawer_list_item_category, categories));
        categoriesList.setOnItemClickListener(new DrawerCategoriesClickListener());
        getActionBar().setHomeButtonEnabled(true);

        //arrow/sandwich
        final Resources resources = getResources();
        drawerArrowDrawable = new DrawerArrowDrawable(resources);
        drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.light_gray));
        getActionBar().setIcon(drawerArrowDrawable);

        drawerListener = new DrawerListener();
        drawerLayout.setDrawerListener(drawerListener);


        /////////////////////////day
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

    //day & night
    //dans NavigationActivity et demande et choisi l'impl en fonction du curent fragment
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
                drawerArrowDrawable.animateToArrow();
                drawerLayout.disabledDrawerSwipe();
                disableFavoritesFilter(favoritesItem);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                drawerArrowDrawable.animateToSandwich();
                drawerLayout.enabledDrawerSwipe();
                searchQuery = null;
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideKeyboard();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SearchEvent searchEvent = new SearchEvent(newText);
                eventBus.post(searchEvent);
                //TODO gros soucis, ce truc est fire quand sliding up s'ouvre et aussi au chandgement d'output...
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
                    hideKeyboard();
                }
        }

        //init favorites button depending on bundleSaveInstanceState
        disableFavoritesFilter(favoritesItem);

        return true;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

    }

    //day & night
    //dans NavigationActivity
    private void disableFavoritesFilter(MenuItem favoritesItem) {
        if (isFavoritesChecked) {
            toggleFavorites(favoritesItem);
        }
    }

    //can be day & night (just un parent commun a  detailSlidingUpPanelLayoutLayout
    @Override
    //dans NavigationActivity
    public boolean onOptionsItemSelected(MenuItem item) {

        //click on the appName or the appIcone
        if (item.getTitle().equals(getActionBar().getTitle())) {
            //search widget is active
            if (!((SearchView) globalMenu.findItem(R.id.menu_search).getActionView()).isIconified()) {
                drawerArrowDrawable.animateToSandwich();
                drawerLayout.enabledDrawerSwipe();
                SearchView searchView =
                        (SearchView) globalMenu.findItem(R.id.menu_search).getActionView();
                searchView.onActionViewCollapsed();
                searchQuery = null;
            }
            //detail is visible
            else if (detailSlidingUpPanelLayoutLayout.isPanelAnchored() || detailSlidingUpPanelLayoutLayout.isPanelExpanded()) {
                detailSlidingUpPanelLayoutLayout.collapsePanel();
            }
            //default
            else {
                drawerLayout.toggleDrawer();
            }
            return true;
        }

        switch (item.getItemId()) {
            case R.id.menu_favorites:

                toggleFavorites(item);

                return true;
            case R.id.menu_twitter:
                Toast toast = Toast.makeText(getApplicationContext(), "twitter clicked", Toast.LENGTH_SHORT);
                toast.show();
                return true;
            case R.id.menu_facebook:
                Toast toast2 = Toast.makeText(getApplicationContext(), "facebook clicked", Toast.LENGTH_SHORT);
                toast2.show();
                return true;
        }


        return false;

    }

    //day & night
    //dans NavigationActivity
    private void toggleFavorites(MenuItem item) {
        ArrayList<Category> list = new ArrayList<>();
        list.addAll(categoriesSelected);
        if (item.isChecked()) {
            item.setChecked(false);
            item.setIcon(R.drawable.ic_favorites_unchecked);
            isFavoritesChecked = false;
        } else {
            list.add((new Category("FAVORITES","ic_FAVORITES")));
            item.setChecked(true);
            item.setIcon(R.drawable.ic_favorites_checked);
            isFavoritesChecked = true;
        }
        CategoriesSelectedEvent event = new CategoriesSelectedEvent(list);
        event.setFilterAction(FilterAction.ADDED);
        eventBus.post(event);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    //day & night
    @Override
    //dans NavigationActivity
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenu = menu;
        customOnOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * invalidateOptionsMenu refire search from searchWidget, painful !
     */
    //day & night
    //dans NavigationActivity et demande et choisi l'impl en fonction du curent fragment
    public void customOnOptionsMenu() {
        boolean drawerOpen = drawerLayout.isDrawerVisible();//drawerLayout.isDrawerVisible(drawerView);
        Boolean displayGlobalItem = !drawerOpen && !detailSlidingUpPanelLayoutLayout.isAnchoredOrExpanded();
        mMenu.findItem(R.id.menu_search).setVisible(displayGlobalItem);
        mMenu.findItem(R.id.menu_favorites).setVisible(displayGlobalItem);
        mMenu.findItem(R.id.menu_facebook).setVisible(detailSlidingUpPanelLayoutLayout.isAnchoredOrExpanded());
        mMenu.findItem(R.id.menu_twitter).setVisible(detailSlidingUpPanelLayoutLayout.isAnchoredOrExpanded());
    }


    @Override
    public void onResume() {
        super.onResume();
        //onClickArtist(null);
    }

    /**
     * Activity is alive       *
     */

    //day
    public void onEvent(ResourcesUpdatedEvent event) {
        // super.onEvent(event);
        Log.d(TAG + "onEvent(ResourcesUpdatedEvent)", event.getDayResourceList().toString());
        dayResourceArrayList.clear();
        dayResourceArrayList.addAll(event.getDayResourceList());
        nightResourceArrayList.clear();
        nightResourceArrayList.addAll(event.getNightResourceList());
    }

    public void onEvent(CategoriesUpdatedEvent event) {
        Log.d("onEvent(ResourcesUpdatedEvent)", event.getCategories().toString());
        categories.clear();
        categories.addAll(event.getCategories());
    }

    //TODO move dans DetailSlidingUpPanelLayout
    public void onEvent(ManageDetailSlidingUpDrawer m) {

        //TODO demander a l'activity de masquer le clavier !


        switch (m.getState()) {
            case COLLAPSE:
                hideKeyboard();
                detailSlidingUpPanelLayoutLayout.collapsePanel();
                break;
            case EXPAND:
                hideKeyboard();
                detailSlidingUpPanelLayoutLayout.expandPanel();
                break;
            case HIDE:
                detailSlidingUpPanelLayoutLayout.hidePanel();
                break;
            case SHOW:
                hideKeyboard();
                if (m.getDayResource() == null) {
                    detailSlidingUpPanelLayoutLayout.showPanel();
                } else {
                    detailSlidingUpPanelLayoutLayout.showDetailPanel(m.getDayResource());
                }
                break;
            case ANCHORED:
                hideKeyboard();
                if (m.getDayResource() != null) {
                    detailFragment.notifyDataChanged(m.getDayResource());
                } else if (m.getNightResource() != null) {
                    detailFragment.notifyDataChanged(m.getNightResource());
                }
                detailSlidingUpPanelLayoutLayout.anchorPanel();
                break;
        }

    }

    //day
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

    //day
    //dans DayFragment
    public void selectMaps() {
        Fragment mapsFragment = new OutputMapsFragment();
        replaceContentFragment(mapsFragment);
    }

    //day
    //dans DayFragment
    public void selectList() {
        Fragment listFragment = new OutputListFragment();
        replaceContentFragment(listFragment);
    }

    //all
    @OnClick(R.id.navigation_drawer_artists)
    public void onClickArtist(View v) {
        Fragment artistFragment = new ArtistFragment();
        replaceContentFragment(artistFragment);
        drawerLayout.closeDrawer();

    }

    //all
    @Override
    //dans NavigationActivity
    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }

    //can be day & night (just un parent commun a  detailSlidingUpPanelLayoutLayout
    @Override
    //dans NavigationActivity et demande au currentFragment son slidingLayout
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen()) {
            drawerLayout.closeDrawer();
        } else if (detailSlidingUpPanelLayoutLayout.isAnchoredOrExpanded()) {
            detailSlidingUpPanelLayoutLayout.collapsePanel();
        } else if (!detailSlidingUpPanelLayoutLayout.isPanelHidden()) {
            detailSlidingUpPanelLayoutLayout.hideDetailPanel();
        } else {
            super.onBackPressed();
        }

    }

    /**
     * Activity is no more alive       *
     */

    //day,  night need one, pas forcement
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
        outState.putParcelableArrayList("categoriesSelected", categoriesSelected);
        //resources
        outState.putParcelableArrayList("dayResourceArrayList", dayResourceArrayList);
        outState.putParcelableArrayList("nightResourceArrayList", nightResourceArrayList);

        ///////////////////////////day and night
        //action bar menu
        SearchView searchView =
                (SearchView) globalMenu.findItem(R.id.menu_search).getActionView();
        if (!searchView.getQuery().toString().equals("")) {
            outState.putString("searchQuery", searchView.getQuery().toString());
        }
        outState.putBoolean("isFavoritesChecked", globalMenu.findItem(R.id.menu_favorites).isChecked());

        //sera simplifié avec la prochaine release
        //slidingUp
        String state = "";
        if (detailSlidingUpPanelLayoutLayout.isPanelHidden())
            state = "hidden";
        else if (detailSlidingUpPanelLayoutLayout.isPanelAnchored())
            state = "anchored";
        else if (detailSlidingUpPanelLayoutLayout.isPanelExpanded())
            state = "expanded";
        else
            state = "shown";
        outState.putString("slidingUpState", state);
    }


    /**
     * Activity methods      *
     */

    //day (peut etre night en a besoin d'un)
    //dans NavigationActivity et demande au currentFragment son slidingLayout
    private void replaceContentFragment(Fragment fragment) {
        Bundle bundleArgs = new Bundle();
        bundleArgs.putParcelableArrayList("categoriesSelected", categoriesSelected);
        searchQuery = (searchQuery == null) ? null : (searchQuery.equals("")) ? null : searchQuery;
        bundleArgs.putString("searchQuery", searchQuery);
        fragment.setArguments(bundleArgs);

        FragmentTransaction ft = fragmentManager.beginTransaction();

        if (fragment.getClass() == OutputListFragment.class || fragment.getClass() == OutputMapsFragment.class) {
            bundleArgs.putParcelableArrayList("resourcesList", dayResourceArrayList);

            if (fragment.getClass() == OutputListFragment.class)
                ft.setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_to_the_right);
            else
                ft.setCustomAnimations(R.animator.slide_in_from_right, R.animator.slide_out_to_the_left);

        } else if (fragment.getClass() == ArtistFragment.class) {
            bundleArgs.putParcelableArrayList("resourcesList", nightResourceArrayList);

        }

        ft.replace(R.id.content_frame, fragment).commit();

    }

    //day
    private void selectCategory(int position) {
        List<Category> categoriesSelected = new ArrayList<>();
        if (globalMenu.findItem(R.id.menu_favorites).isChecked()) {
            categoriesSelected.add(new Category("FAVORITES","ic_FAVORITES"));
        }


        CategoriesSelectedEvent categoriesSelectedEvent = new CategoriesSelectedEvent(categoriesSelected);

        // update selected item and title, then close the drawer
        if (categoriesList.isItemChecked(position)) {
            categoriesSelected.clear();
            if(categories.get(position).getIconeName() != "ic_ALLCATEGORY" ){
                categoriesSelected.add(categories.get(position));
            }
            categoriesSelectedEvent.setFilterAction(FilterAction.ADDED);
            eventBus.post(categoriesSelectedEvent);

        } else if (!categoriesList.isItemChecked(position)) {
            categoriesSelectedEvent.setFilterAction(FilterAction.REMOVED);
            eventBus.post(categoriesSelectedEvent);
        }

        Class<? extends Fragment> currentFragment = fragmentManager.findFragmentById(R.id.content_frame).getClass();

        if (!(currentFragment == OutputListFragment.class || currentFragment == OutputMapsFragment.class)) {
            selectMaps();
        }

        drawerLayout.closeDrawer();
    }


    public String getSlidingUpState() {
        return slidingUpState;
    }

    /**
     * Action bar methods
     */

    //all
    //dans NavigationActity
    public void restoreTitle() {
        //TODO faire comme pour les menu item
        String str;
        if (fragmentManager.findFragmentById(R.id.content_frame).getClass() == OutputMapsFragment.class) {
            str = (getResources().getString(R.string.drawer_outputtype_maps));
        } else {
            str = (getResources().getString(R.string.drawer_outputtype_list));
        }

        if (str != getActionBar().getTitle()) {
            setTitle(str);
        }
    }

    //dans NavigationActity
    public DrawerArrowDrawable getDrawerArrowDrawable() {
        return drawerArrowDrawable;
    }

    /**
     * Drawer methods and inner classes //TODO creer un fragment pour la navigation drawer et mettre ca dedans
     */

    //day
    private class DrawerCategoriesClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectCategory(position);
        }
    }

    //day + night (all need one)
    //dans NavigationActivity
    private class DrawerListener extends DrawerLayout.SimpleDrawerListener {
        private MenuItem itemFav;
        private MenuItem itemSearch;

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            drawerLayout.setIsDrawerOpen(true);
            getActionBar().setTitle(R.string.app_name);
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            // Sometimes slideOffset ends up so close to but not quite 1 or 0.
            if (slideOffset >= .995) {
                drawerArrowDrawable.setFlip(true);
            } else if (slideOffset <= .005) {
                drawerArrowDrawable.setFlip(false);
            }
            drawerArrowDrawable.setParameter(slideOffset);

            if (itemFav == null) {
                itemFav = globalMenu.findItem(R.id.menu_favorites);
                itemSearch = globalMenu.findItem(R.id.menu_search);
            }

            if (slideOffset > 0.95)
                return;
            itemFav.getIcon().setAlpha((int) (255 - slideOffset * 255));
            itemSearch.getActionView().setAlpha(1 - slideOffset);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
            drawerLayout.setIsDrawerOpen(false);
            getActionBar().setTitle(
                    ((ContentFrameFragment) getFragmentManager().findFragmentById(R.id.content_frame))
                            .getDisplayName());

        }
    }


}

