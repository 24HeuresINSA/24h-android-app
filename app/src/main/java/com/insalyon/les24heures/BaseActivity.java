package com.insalyon.les24heures;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.mrengineer13.snackbar.SnackBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.insalyon.les24heures.adapter.CategoryAdapter;
import com.insalyon.les24heures.androidService.LiveUpdateService;
import com.insalyon.les24heures.eventbus.ApplicationVersionEvent;
import com.insalyon.les24heures.eventbus.CategoriesUpdatedEvent;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.eventbus.RetrofitErrorEvent;
import com.insalyon.les24heures.fragments.FacilitiesFragment;
import com.insalyon.les24heures.fragments.ParamsFragment;
import com.insalyon.les24heures.fragments.TclFragment;
import com.insalyon.les24heures.fragments.TicketsFragment;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.model.NightResource;
import com.insalyon.les24heures.service.CategoryService;
import com.insalyon.les24heures.service.DataBackendService;
import com.insalyon.les24heures.service.ResourceService;
import com.insalyon.les24heures.service.RetrofitService;
import com.insalyon.les24heures.service.impl.ApplicationVersionServiceImpl;
import com.insalyon.les24heures.service.impl.CategoryServiceImpl;
import com.insalyon.les24heures.service.impl.DataBackendServiceImpl;
import com.insalyon.les24heures.service.impl.ResourceServiceImpl;
import com.insalyon.les24heures.utils.ApplicationVersionState;
import com.insalyon.les24heures.utils.RetrofitErrorHandler;
import com.insalyon.les24heures.view.CustomDrawerLayout;
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
 * Created by remi on 19/04/15.
 */
public abstract class BaseActivity extends Activity implements SnackBar.OnMessageClickListener {
    public static final String PREFS_NAME = "dataFile";
    private static final String TAG = BaseActivity.class.getCanonicalName();

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
    @InjectView(R.id.navigation_drawer_artists)
    View artistButton;
    @InjectView(R.id.navigation_drawer_tcl)
    View ticketsButton;
    @InjectView(R.id.navigation_drawer_tickets)
    View tclButton;
    @InjectView(R.id.navigation_drawer_facilities)
    View facilitiesButton;
    @InjectView(R.id.navigation_drawer_params)
    View paramsButton;


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


    Class nextActivity;
    Class nextStaticFragment;
    int positionCategorySelected;
    private BaseActivity self = this;

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
                .setErrorHandler(new RetrofitErrorHandler())
                .build();

        retrofitService = restAdapter.create(RetrofitService.class);
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


    }

    public void retrieveData(Bundle savedInstanceState) {
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

        //check if the app can download data
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

        drawerListener = getDrawerListener();
        drawerLayout.setDrawerListener(drawerListener);
    }

    public DrawerListener getDrawerListener() {
        return new DrawerListener();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ButterKnife.inject(this);

        retrieveData(savedInstanceState);


        setupNavigationDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventBus.registerSticky(this);

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


    public void onEvent(RetrofitErrorEvent event) {

        String content = null;
        Boolean withAction = true;
        switch (event.getRetrofitError().getKind()) {
            case NETWORK:
                content = getResources().getString(R.string.retrofit_network_error);
                withAction = false;
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


        if (content != null) {
            SnackBar.Builder snackBar = new SnackBar.Builder(this)
                    .withOnClickListener(this)
                    .withMessage(content)
                    .withTextColorId(R.color.sb__button_text_color_green)
                    .withDuration(SnackBar.LONG_SNACK);

            if (withAction)
                snackBar.withActionMessageId(R.string.retrofit_error_snackbar_action_label);

            snackBar.show();

        }
    }


    @OnClick(R.id.navigation_drawer_artists)
    public void onClickArtist(View v) {
        clearDrawerChoices();
        v.setActivated(true);
        drawerLayout.closeDrawer();
        nextActivity = NightActivity.class;

    }

    @OnClick(R.id.navigation_drawer_tickets)
    public void onClickTickets(View v) {
        clearDrawerChoices();
        v.setActivated(true);
        drawerLayout.closeDrawer();
        nextActivity = StaticDataActivity.class;
        nextStaticFragment = TicketsFragment.class;
    }

    @OnClick(R.id.navigation_drawer_tcl)
    public void onClickTcl(View v) {
        clearDrawerChoices();
        v.setActivated(true);
        drawerLayout.closeDrawer();
        nextActivity = StaticDataActivity.class;
        nextStaticFragment = TclFragment.class;
    }

    @OnClick(R.id.navigation_drawer_facilities)
    public void onClickFacilities(View v) {
        clearDrawerChoices();
        v.setActivated(true);
        drawerLayout.closeDrawer();
        nextActivity = StaticDataActivity.class;
        nextStaticFragment = FacilitiesFragment.class;
    }

    @OnClick(R.id.navigation_drawer_params)
    public void onClickParams(View v) {
        clearDrawerChoices();
        v.setActivated(true);
        drawerLayout.closeDrawer();
        nextActivity = StaticDataActivity.class;
        nextStaticFragment = ParamsFragment.class;
    }


    void clearDrawerChoices() {
        clearDrawerChoices(true);
    }

    void clearDrawerChoices(boolean clearList) {
        if (clearList) {
            categoriesList.clearChoices();
            categoriesList.requestLayout();
        }
        artistButton.setActivated(false);
        ticketsButton.setActivated(false);
        tclButton.setActivated(false);
        facilitiesButton.setActivated(false);
        paramsButton.setActivated(false);
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


    }

    @Override
    public void onMessageClick(Parcelable parcelable) {
        ApplicationVersionServiceImpl.getInstance().checkApplicationVersion(getApplicationContext());
    }


    /**
     * Action bar methods
     */


    public void manageApplicationVersionState(ApplicationVersionState state) {
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

                Intent checkForUpdates = new Intent(this, LiveUpdateService.class);
                startService(checkForUpdates);

                Log.d(TAG, "manageApplicationVersionState TODATE");
                break;
        }


        //si popup sharedPref == major, (2.1) lance le timerTask pour la popup major changes
        //sinon si popup sharedPref == minor ou == toDate effectue le download des donnees et si popup sharedPref == minor affiche popup minor
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //click on the appName or the appIcone
        if (item.getTitle().equals(getActionBar().getTitle())) {
            drawerLayout.toggleDrawer();
            return true;
        }

        return false;

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

    /**
     * Navigation Drawer
     */


    public DrawerArrowDrawable getDrawerArrowDrawable() {
        return drawerArrowDrawable;
    }

    public void onEvent(ApplicationVersionEvent event) {
        manageApplicationVersionState(event.getState());
    }

    public class DrawerListener extends DrawerLayout.SimpleDrawerListener {

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


            if (slideOffset > 0.95)
                return;

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
                } else if (nextActivity.equals(StaticDataActivity.class)) {

                    if (this.getClass().equals(StaticDataActivity.class)) {
                        ((StaticDataActivity) self).startFragment(nextStaticFragment);
                        return;
                    } else
                        intent.putExtra("nextStaticFragment", nextStaticFragment.getCanonicalName());
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
