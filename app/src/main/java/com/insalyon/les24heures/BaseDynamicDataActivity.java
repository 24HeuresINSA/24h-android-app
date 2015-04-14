package com.insalyon.les24heures;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.insalyon.les24heures.adapter.CategoryAdapter;
import com.insalyon.les24heures.eventbus.ApplicationVersionEvent;
import com.insalyon.les24heures.eventbus.CategoriesSelectedEvent;
import com.insalyon.les24heures.eventbus.CategoriesUpdatedEvent;
import com.insalyon.les24heures.eventbus.ManageDetailSlidingUpDrawer;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.eventbus.RetrofitErrorEvent;
import com.insalyon.les24heures.eventbus.SearchEvent;
import com.insalyon.les24heures.fragments.DetailFragment;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.model.NightResource;
import com.insalyon.les24heures.service.CategoryService;
import com.insalyon.les24heures.service.DataBackendService;
import com.insalyon.les24heures.service.ResourceService;
import com.insalyon.les24heures.service.RetrofitService;
import com.insalyon.les24heures.service.impl.CategoryServiceImpl;
import com.insalyon.les24heures.service.impl.DataBackendServiceImpl;
import com.insalyon.les24heures.service.impl.ResourceServiceImpl;
import com.insalyon.les24heures.utils.ApplicationVersionState;
import com.insalyon.les24heures.utils.FilterAction;
import com.insalyon.les24heures.view.CustomDrawerLayout;
import com.insalyon.les24heures.view.DetailSlidingUpPanelLayout;
import com.insalyon.les24heures.view.DrawerArrowDrawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import retrofit.RestAdapter;

/**
 * Created by remi on 12/03/15.
 */
public abstract class BaseDynamicDataActivity extends Activity {
    public static final String PREFS_NAME = "dataFile";
    private static final String TAG = BaseDynamicDataActivity.class.getCanonicalName();
    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;
    FragmentManager fragmentManager;
    EventBus eventBus;
    RestAdapter restAdapter;
    RetrofitService retrofitService;
    @InjectView(R.id.drawer_layout)
    CustomDrawerLayout drawerLayout;
    @InjectView(R.id.left_drawer)
    View drawerView;
    @InjectView(R.id.left_drawer_categories_list)
    ListView categoriesList;
    @InjectView(R.id.sliding_layout)
    DetailSlidingUpPanelLayout detailSlidingUpPanelLayoutLayout;
    @InjectView(R.id.navigation_drawer_artists)
    View artistButton;
    DetailFragment detailFragment;
    DataBackendService dataBackendService;
    ResourceService resourceService;
    CategoryService categoryService;
    ArrayList<Category> categories;
    //need a list to store a category and favorites or not (it's a bad code resulting from the old impl where it could be possible to select several categories)
    ArrayList<Category> selectedCategories = new ArrayList<>();
    ArrayList<DayResource> dayResourceArrayList;
    ArrayList<NightResource> nightResourceArrayList;
    String dataVersion;
    String applicationVersion;
    DrawerArrowDrawable drawerArrowDrawable;
    DrawerLayout.SimpleDrawerListener drawerListener;
    Menu globalMenu;
    Boolean isFavoritesChecked = false;
    String searchQuery;
    Menu mMenu;
    String slidingUpState;
    RestAdapter restAdapterLocal;
    Class nextActivity;
    private BaseDynamicDataActivity self = this;
    private int positionCategorySelected;

    /**
     * Activity is being created
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*** init services ***/
        super.onCreate(savedInstanceState);
        eventBus = EventBus.getDefault();
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.backend_url_mobile))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        restAdapterLocal = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.backend_url_local))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        retrofitService = restAdapter.create(RetrofitService.class);
//        resourceRetrofitService = restAdapterLocal.create(ResourceRetrofitService.class);
        fragmentManager = getFragmentManager();
        dataBackendService = DataBackendServiceImpl.getInstance();
        resourceService = ResourceServiceImpl.getInstance();
        categoryService = CategoryServiceImpl.getInstance();


        if (dayResourceArrayList == null || nightResourceArrayList == null || categories == null) {
            dayResourceArrayList = new ArrayList<>();
            nightResourceArrayList = new ArrayList<>();
            categories = new ArrayList<>();
        }

        if (dataVersion == null || applicationVersion == null) {
            dataVersion = getResources().getString(R.string.INSTALL_DATA_VERSION);
            applicationVersion = getResources().getString(R.string.INSTALL_APPLICATION_VERSION);
        }


        if (selectedCategories == null) {
            selectedCategories = new ArrayList<>();
        }


        //retrieveData(savedInstanceState);

    }

    private void retrieveData(Bundle savedInstanceState) {
        /*** recover data either from (by priority)
         *           savedInstanceState (rotate, restore from background)
         *           getIntent (start from another activity, another apps) => maybe it's more efficient to retrieve from intent than from localPref
         *           localStorage (start)
         *           backend (if needed)
         */
        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelableArrayList("categories") != null) {
                categories = savedInstanceState.getParcelableArrayList("categories");
            }
            if (savedInstanceState.getParcelableArrayList("categoriesSelected") != null) {
                selectedCategories = savedInstanceState.getParcelableArrayList("selectedCategories");
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

        //get from shared pref
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Gson gson = new Gson();


        String categoriesListStr = settings.getString("categoriesList", "");
        String dayResourceArrayListStr = settings.getString("dayResourceList", "");
        String nightResourceArrayListStr = settings.getString("nightResourcesList", "");
        dataVersion = settings.getString("dataVersion", "");
        applicationVersion = settings.getString("applicationVersion", "");

        categories = gson.fromJson(categoriesListStr, new TypeToken<List<Category>>() {
        }.getType());
        dayResourceArrayList = gson.fromJson(dayResourceArrayListStr, new TypeToken<List<DayResource>>() {
        }.getType());
        nightResourceArrayList = gson.fromJson(nightResourceArrayListStr, new TypeToken<List<NightResource>>() {
        }.getType());


        if (dayResourceArrayList == null || nightResourceArrayList == null || categories == null) {
            dayResourceArrayList = new ArrayList<>();
            nightResourceArrayList = new ArrayList<>();
            categories = new ArrayList<>();
        }

        if (dataVersion == null || applicationVersion == null) {
            dataVersion = getResources().getString(R.string.INSTALL_DATA_VERSION);
            applicationVersion = getResources().getString(R.string.INSTALL_APPLICATION_VERSION);
        }

        //TODO debug purpose only
        dataVersion = getResources().getString(R.string.INSTALL_DATA_VERSION);
        //dataBackendService.getResourcesAsyncFromBackend(retrofitService, dataVersion);
//      dataBackendService.getResourcesAsyncMock();

        //TODO check if the app can download data
        settings = getSharedPreferences(getResources().getString(R.string.SHARED_PREF_APP_VERSION), 0);
        if (settings.getString("applicationVersionState", null) != null)
            manageApplicationVersionState(ApplicationVersionState.valueOf(
                            settings.getString("applicationVersionState", ApplicationVersionState.TODATE.toString()))
            );
        else
            Log.d(TAG, " retrieveData wait for authorization to download data");


        if (selectedCategories == null) {
            selectedCategories = new ArrayList<>();
        }
    }

    private void setupDetailFragment() {
        detailSlidingUpPanelLayoutLayout.setActivity(this); //slidingPanel needs the activity to invalidateOptionMenu, manage appName and arrowDrawer
        detailSlidingUpPanelLayoutLayout.setParallaxHeader(findViewById(R.id.detail_paralax_header));

        detailFragment = (DetailFragment) fragmentManager.findFragmentById(R.id.sliding_layout_content_fragment);
        detailFragment.setParallaxImageHeader((ImageView) findViewById(R.id.detail_paralax_header_imageview));
        detailFragment.setParallaxHeader(findViewById(R.id.detail_paralax_header));
        detailSlidingUpPanelLayoutLayout.setDetailFragment(detailFragment);
    }

    private void setupNavigationDrawer() {
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ButterKnife.inject(this);


        retrieveData(savedInstanceState);

        setupNavigationDrawer();
        setupDetailFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventBus.registerSticky(this);

        if (detailSlidingUpPanelLayoutLayout != null) {
            detailSlidingUpPanelLayoutLayout.setAlpha(0);
            detailSlidingUpPanelLayoutLayout.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        } else {
            Log.d(TAG, "No view with ID sliding_layout to fade in.");
        }
    }

    /**
     * Activity is alive
     */

    public void onEvent(ResourcesUpdatedEvent event) {
        dayResourceArrayList.clear();
        dayResourceArrayList.addAll(event.getDayResourceList());
        nightResourceArrayList.clear();
        nightResourceArrayList.addAll(event.getNightResourceList());

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        Gson gson = new Gson();
        String dayResourcesList = gson.toJson(dayResourceArrayList);
        String nightResourcesList = gson.toJson(nightResourceArrayList);


        editor.putString("dayResourceList", dayResourcesList);
        editor.putString("nightResourcesList", nightResourcesList);
        editor.putString("dataVersion", event.getDataVersion());
        editor.commit();
    }

    public void onEvent(CategoriesUpdatedEvent event) {
        categories.clear();
        categories.addAll(event.getCategories());

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        Gson gson = new Gson();
        String categoriesList = gson.toJson(categories);


        editor.putString("categoriesList", categoriesList);
        editor.putString("dataVersion", event.getDataVersion());
        editor.commit();
    }

    @Override
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
     * invalidateOptionsMenu refire search from searchWidget, painful !
     */
    public void customOnOptionsMenu() {
        boolean drawerOpen = drawerLayout.isDrawerVisible();//drawerLayout.isDrawerVisible(drawerView);
        Boolean displayGlobalItem = !drawerOpen && !detailSlidingUpPanelLayoutLayout.isAnchoredOrExpanded();
        // boolean listVisible = is ???
        // boolean displaySortItem = displayGlobalItem && listVisible
        mMenu.findItem(R.id.menu_search).setVisible(displayGlobalItem);
        mMenu.findItem(R.id.menu_favorites).setVisible(displayGlobalItem);
        //   mMenu.findItem(R.id.menu_facebook).setVisible(detailSlidingUpPanelLayoutLayout.isAnchoredOrExpanded());
        // mMenu.findItem(R.id.menu_twitter).setVisible(detailSlidingUpPanelLayoutLayout.isAnchoredOrExpanded());
    }

    @OnClick(R.id.navigation_drawer_artists)
    public void onClickArtist(View v) {
        clearDrawerChoices();
        v.setActivated(true);
        drawerLayout.closeDrawer();
        nextActivity = NightActivity.class;

    }

    private void clearDrawerChoices() {
        clearDrawerChoices(true);
    }

    private void clearDrawerChoices(boolean clearList) {
        if (clearList) {
            categoriesList.clearChoices();
            categoriesList.requestLayout();
        }
        artistButton.setActivated(false);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenu = menu;
        customOnOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //click on the appName or the appIcone
        if (item.getTitle().equals(getActionBar().getTitle())) {
            //detail is visible
            if (detailSlidingUpPanelLayoutLayout.isPanelAnchored() || detailSlidingUpPanelLayoutLayout.isPanelExpanded()) {
                detailSlidingUpPanelLayoutLayout.collapsePanel();
            }
            //search widget is active
            else if (!((SearchView) globalMenu.findItem(R.id.menu_search).getActionView()).isIconified()) {
                drawerArrowDrawable.animateToSandwich();
                drawerLayout.enabledDrawerSwipe();
                SearchView searchView =
                        (SearchView) globalMenu.findItem(R.id.menu_search).getActionView();
                searchView.onActionViewCollapsed();
                searchQuery = null;
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
            //   case R.id.menu_twitter:
            //     Toast toast = Toast.makeText(getApplicationContext(), "twitter clicked", Toast.LENGTH_SHORT);
            //   toast.show();
            //          return true;
            //    case R.id.menu_facebook:
            //      Toast toast2 = Toast.makeText(getApplicationContext(), "facebook clicked", Toast.LENGTH_SHORT);
            //    toast2.show();
            //  return true;
        }


        return false;

    }


    /**
     * Activity is no more alive
     */

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //categories
        outState.putParcelableArrayList("categories", categories);
        //categories state
        outState.putParcelableArrayList("selectedCategories", selectedCategories);
        //resources
        outState.putParcelableArrayList("dayResourceArrayList", dayResourceArrayList);
        outState.putParcelableArrayList("nightResourceArrayList", nightResourceArrayList);

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
     * Activity methods
     */

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

    }

    public String getSlidingUpState() {
        return slidingUpState;
    }

    /**
     * Action bar methods
     */

    private void disableFavoritesFilter(MenuItem favoritesItem) {
        if (isFavoritesChecked) {
            toggleFavorites(favoritesItem);
        }
    }

    private void toggleFavorites(MenuItem item) {
        ArrayList<Category> list = new ArrayList<>();
        list.addAll(selectedCategories);
        if (item.isChecked()) {
            item.setChecked(false);
            item.setIcon(R.drawable.menu_favorite_unchecked);

            isFavoritesChecked = false;
        } else {
            list.add(categoryService.getFavoriteCategory());
            item.setChecked(true);
            item.setIcon(R.drawable.menu_favorite_checked);
            isFavoritesChecked = true;
        }
        CategoriesSelectedEvent event = new CategoriesSelectedEvent(list);
        event.setFilterAction(FilterAction.ADDED);
        eventBus.post(event);
    }

    public void restoreTitle() {
        String str = (getResources().getString(R.string.app_name));

        if (str != getActionBar().getTitle()) {
            setTitle(str);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }

    public DrawerArrowDrawable getDrawerArrowDrawable() {
        return drawerArrowDrawable;
    }

    /**
     * Navigation Drawer
     */

    public void onEvent(ManageDetailSlidingUpDrawer m) {

        switch (m.getState()) {
            case COLLAPSE:
                detailSlidingUpPanelLayoutLayout.collapsePanel();
                break;
            case EXPAND:
                detailSlidingUpPanelLayoutLayout.expandPanel();
                break;
            case HIDE:
                detailSlidingUpPanelLayoutLayout.hidePanel();
                break;
            case SHOW:
                if (m.getDayResource() == null) {
                    detailSlidingUpPanelLayoutLayout.showPanel();
                } else {
                    detailSlidingUpPanelLayoutLayout.showDetailPanel(m.getDayResource());
                }
                break;
            case ANCHORED:
                if (m.getDayResource() != null) {
                    detailFragment.notifyDataChanged(m.getDayResource());
                } else if (m.getNightResource() != null) {
                    detailFragment.notifyDataChanged(m.getNightResource());
                }
                detailSlidingUpPanelLayoutLayout.anchorPanel();
                break;
        }
    }

    public void onEvent(ApplicationVersionEvent event) {
        manageApplicationVersionState(event.getState());
    }

    public void onEvent(RetrofitErrorEvent event){

        String content = null;
        switch (event.getRetrofitError().getKind()){
            case NETWORK:
                content = getResources().getString(R.string.retrofit_network_error);
                break;
            case CONVERSION:
                content = getResources().getString(R.string.retrofit_internal_error);
                break;
            case HTTP:                 //TODO piwik
                content = getResources().getString(R.string.retrofit_server_error);
                break;
            case UNEXPECTED:
                content = getResources().getString(R.string.retrofit_unexpected_error);
                break;
        }

        if(content != null) {
            Toast toast = Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    private void manageApplicationVersionState(ApplicationVersionState state) {
        switch (state) {
            case MAJOR:
                Log.d(TAG, "manageApplicationVersionState MAJOR");

                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                self = this;
                builder.setMessage(R.string.apologize_dialog_messag_major)
                        .setTitle(R.string.apologize_dialog_title);
                dialog = builder.create();
                final AlertDialog finalDialog = dialog;
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        self.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finalDialog.show();

                            }
                        });
                    }
                };

                Timer timer = new Timer();
                timer.schedule(task, 0, 10000);

                break;
            case MINOR:
                dataBackendService.getResourcesAsyncFromBackend(retrofitService, dataVersion);
                Log.d(TAG, "manageApplicationVersionState MINOR");
                builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.apologize_dialog_messag_minor)
                        .setTitle(R.string.apologize_dialog_title);

                dialog = builder.create();
                dialog.show();

                break;
            case TODATE:
                dataBackendService.getResourcesAsyncFromBackend(retrofitService, dataVersion);
                Log.d(TAG, "manageApplicationVersionState TODATE");
                break;
        }


        //si popup sharedPref == major, (2.1) lance le timerTask pour la popup major changes
        //sinon si popup sharedPref == minor ou == toDate effectue le download des donnees et si popup sharedPref == minor affiche popup minor
    }

    protected void animateContentOut(float slideOffset) {
        detailSlidingUpPanelLayoutLayout.setAlpha(slideOffset);

    }

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

            if (nextActivity != null) {
                animateContentOut(slideOffset);
            }
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
            drawerLayout.setIsDrawerOpen(false);


            //switch activity if needed
            if (nextActivity != null) {
                Intent intent = new Intent(self, nextActivity);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                if (nextActivity.equals(DayActivity.class)) {
                    //TODO selectedCategories pourrait devenir inutile en fonction de comment on recuperer les categories coté DAyActivity
                    intent.putParcelableArrayListExtra("selectedCategories", new ArrayList<Category>(Arrays.asList(categories.get(positionCategorySelected))));
                    intent.putExtra("categoryPosition", positionCategorySelected);

                }

                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            } else
                restoreTitle();


        }
    }

    private class DrawerCategoriesClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            clearDrawerChoices(false);
            nextActivity = DayActivity.class;
            positionCategorySelected = position;
            drawerLayout.closeDrawer();
        }
    }


}
