package com.insadelyon.les24heures;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mrengineer13.snackbar.SnackBar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.insadelyon.les24heures.adapter.CategoryAdapter;
import com.insadelyon.les24heures.androidService.FavoriteAlertingSchedulingService;
import com.insadelyon.les24heures.androidService.LiveUpdateGCMRegistrationService;
import com.insadelyon.les24heures.androidService.LiveUpdatesNotificationService;
import com.insadelyon.les24heures.eventbus.ApplicationVersionEvent;
import com.insadelyon.les24heures.eventbus.CategoriesUpdatedEvent;
import com.insadelyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insadelyon.les24heures.eventbus.RetrofitErrorEvent;
import com.insadelyon.les24heures.fragments.ConsoFragment;
import com.insadelyon.les24heures.fragments.FacilitiesFragment;
import com.insadelyon.les24heures.fragments.TclFragment;
import com.insadelyon.les24heures.fragments.TicketsFragment;
import com.insadelyon.les24heures.model.Category;
import com.insadelyon.les24heures.model.DayResource;
import com.insadelyon.les24heures.model.NightResource;
import com.insadelyon.les24heures.service.CategoryService;
import com.insadelyon.les24heures.service.DataBackendService;
import com.insadelyon.les24heures.service.ResourceService;
import com.insadelyon.les24heures.service.RetrofitService;
import com.insadelyon.les24heures.service.impl.ApplicationVersionServiceImpl;
import com.insadelyon.les24heures.service.impl.CategoryServiceImpl;
import com.insadelyon.les24heures.service.impl.DataBackendServiceImpl;
import com.insadelyon.les24heures.service.impl.ResourceServiceImpl;
import com.insadelyon.les24heures.utils.ApplicationVersionState;
import com.insadelyon.les24heures.utils.RetrofitErrorHandler;
import com.insadelyon.les24heures.view.CustomDrawerLayout;
import com.insadelyon.les24heures.view.DrawerArrowDrawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import de.greenrobot.event.EventBus;
import retrofit.RestAdapter;

/**
 * Created by remi on 19/04/15.
 */
public abstract class BaseActivity extends Activity implements SnackBar.OnMessageClickListener {
    public static final String PREFS_NAME = "dataFile";
    private static final String TAG = BaseActivity.class.getCanonicalName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

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
    @InjectView(R.id.navigation_drawer_tickets)
    View ticketsButton;
    @InjectView(R.id.navigation_drawer_tcl)
    View tclButton;
    @InjectView(R.id.navigation_drawer_facilities)
    View facilitiesButton;
    @InjectView(R.id.navigation_drawer_conso)
    View consoButton;


    DataBackendService dataBackendService;
    ResourceService resourceService;
    CategoryService categoryService;
    ArrayList<Category> categories;
    //need a list to store a category and favorites or not (it's a bad code resulting from the old impl where it could be possible to select several categories)
    ArrayList<Category> selectedCategories = new ArrayList<>();
    ArrayList<DayResource> dayResourceArrayList;
    ArrayList<DayResource> facilitiesArrayList;
    ArrayList<NightResource> nightResourceArrayList;
    String dataVersion;

    DrawerArrowDrawable drawerArrowDrawable;
    DrawerLayout.SimpleDrawerListener drawerListener;
    Menu globalMenu;


    Class nextActivity;
    Class nextStaticFragment;
    Integer positionCategorySelected;
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
        FavoriteAlertingSchedulingService.start(this);


        if (dayResourceArrayList == null || nightResourceArrayList == null || categories == null || facilitiesArrayList == null) {
            dayResourceArrayList = new ArrayList<>();
            facilitiesArrayList = new ArrayList<>();
            nightResourceArrayList = new ArrayList<>();
            categories = new ArrayList<>();
        }


        if (selectedCategories == null) {
            selectedCategories = new ArrayList<>();
        }

        if (checkPlayServices()) {
            LiveUpdateGCMRegistrationService.startRegisterAction(this);
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
            if (savedInstanceState.getParcelableArrayList("facilitiesArrayList") != null) {
                facilitiesArrayList = savedInstanceState.getParcelableArrayList("facilitiesArrayList");
            }
            if (savedInstanceState.getParcelableArrayList("nightResourceArrayList") != null) {
                nightResourceArrayList = savedInstanceState.getParcelableArrayList("nightResourceArrayList");
            }
        } else {

            //get from shared pref
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            Gson gson = new Gson();


            String categoriesListStr = settings.getString("categoriesList", "");
            String dayResourceArrayListStr = settings.getString("dayResourceList", "");
            String facilitiesArrayListStr = settings.getString("facilitiesList", "");
            String nightResourceArrayListStr = settings.getString("nightResourcesList", "");
            dataVersion = settings.getString("dataVersion", "");

            categories = gson.fromJson(categoriesListStr, new TypeToken<List<Category>>() {
            }.getType());
            dayResourceArrayList = gson.fromJson(dayResourceArrayListStr, new TypeToken<List<DayResource>>() {
            }.getType());
            facilitiesArrayList = gson.fromJson(facilitiesArrayListStr, new TypeToken<List<DayResource>>() {
            }.getType());
            nightResourceArrayList = gson.fromJson(nightResourceArrayListStr, new TypeToken<List<NightResource>>() {
            }.getType());


            //check if the app can download data
            settings = getSharedPreferences(getResources().getString(R.string.SHARED_PREF_APP_VERSION), 0);
            if (settings.getString("applicationVersionState", null) != null)
                manageApplicationVersionState(ApplicationVersionState.valueOf(
                                settings.getString("applicationVersionState", ApplicationVersionState.TODATE.toString()))
                );
            else
                Log.d(TAG, " retrieveData wait for authorization to download data");

        }


        if (dayResourceArrayList == null || nightResourceArrayList == null || categories == null || facilitiesArrayList == null) {
            dayResourceArrayList = new ArrayList<>();
            facilitiesArrayList = new ArrayList<>();
            nightResourceArrayList = new ArrayList<>();
            categories = new ArrayList<>();
        } else {
            ResourcesUpdatedEvent resourcesUpdatedEvent = new ResourcesUpdatedEvent(dayResourceArrayList, nightResourceArrayList, facilitiesArrayList,dataVersion);
            eventBus.post(resourcesUpdatedEvent);
            CategoriesUpdatedEvent categoriesUpdatedEvent = new CategoriesUpdatedEvent(categories, dataVersion);
            eventBus.post(categoriesUpdatedEvent);
        }


        if (selectedCategories == null) {
            selectedCategories = new ArrayList<>();
        }


    }


    private void setupNavigationDrawer() {
        // set a custom shadow that overlays the main content when the drawer opens
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerLayout.setDrawerView(drawerView);
        // set up the drawer's list view with items and click listener
        categoriesList.setAdapter(new CategoryAdapter(this, R.layout.drawer_list_item_category, categories, this));
        categoriesList.setOnItemClickListener(new DrawerCategoriesClickListener());
        getActionBar().setHomeButtonEnabled(true);

        //arrow/sandwich
        final Resources resources = getResources();
        drawerArrowDrawable = new DrawerArrowDrawable(resources);
        drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.light_gray));
        getActionBar().setIcon(drawerArrowDrawable);

        drawerListener = getDrawerListener();
        drawerLayout.setDrawerListener(drawerListener);

        if(drawerLayout.isDrawerVisible()){
            drawerLayout.closeDrawer();
        }
    }

    public DrawerListener getDrawerListener() {
        return new DrawerListener();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ButterKnife.inject(this);

        LiveUpdatesNotificationService.start(this);

        retrieveData(savedInstanceState);


        setupNavigationDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventBus.registerSticky(this);
        checkPlayServices();

    }

    /**
     * Activity is alive
     */

    public void onEvent(ResourcesUpdatedEvent event) {
        dayResourceArrayList.clear();
        dayResourceArrayList.addAll(event.getDayResourceList());
        facilitiesArrayList.clear();
        facilitiesArrayList.addAll(event.getFacilitiesList());
        nightResourceArrayList.clear();
        nightResourceArrayList.addAll(event.getNightResourceList());
        dataVersion = event.getDataVersion();

        writeResourceInSharedPref();
    }

    private void writeResourceInSharedPref() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        Gson gson = new Gson();
        String dayResourcesList = gson.toJson(dayResourceArrayList);
        String facilitiesList = gson.toJson(facilitiesArrayList);
        String nightResourcesList = gson.toJson(nightResourceArrayList);
        String categoriesResourceList = gson.toJson(categories);


        editor.putString("dayResourceList", dayResourcesList);
        editor.putString("facilitiesList", facilitiesList);
        editor.putString("categoriesList", categoriesResourceList);
        editor.putString("nightResourcesList", nightResourcesList);
        editor.putString("dataVersion", dataVersion);
        editor.commit();
    }

    public void onEvent(CategoriesUpdatedEvent event) {
        categories.clear();
        categories.addAll(event.getCategories());
        categories.remove(categoryService.getFacilitiesCategory());

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        Gson gson = new Gson();
        String categoriesList = gson.toJson(categories);


        editor.putString("categoriesList", categoriesList);
        editor.putString("dataVersion", event.getDataVersion());
        editor.commit();
    }


    public void onEvent(RetrofitErrorEvent event) {
        eventBus.removeStickyEvent(event);

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

    private Boolean longClicked = false;
    private int clicked = 0;
    @OnLongClick(R.id.navigation_drawer_header)
    public boolean onLongClick(View v){
        longClicked = true;
        clicked = 0;
        return true;
    }

    @OnClick(R.id.navigation_drawer_header)
    public void onClick(View v){
        if(longClicked){
            clicked++;
            if(clicked > 1){
                clicked = 0;
                longClicked = false;
                Intent intent = new Intent(this,HiddenActivity.class);
                startActivity(intent);
            }
        }
    }


    @OnClick(R.id.navigation_drawer_artists)
    public void onClickArtist(View v) {
        clearDrawerChoices();
        v.setActivated(true);
        setSelectedMenuItem(v, R.drawable.concert_bleu);
        drawerLayout.closeDrawer();
        nextActivity = NightActivity.class;

    }

    @OnClick(R.id.navigation_drawer_tickets)
    public void onClickTickets(View v) {
        clearDrawerChoices();
        v.setActivated(true);
        setSelectedMenuItem(v, R.drawable.billeterie_bleu);
        drawerLayout.closeDrawer();
        nextActivity = StaticDataActivity.class;
        nextStaticFragment = TicketsFragment.class;
    }

    @OnClick(R.id.navigation_drawer_conso)
    public void onClickConso(View v) {
        clearDrawerChoices();
        v.setActivated(true);
        setSelectedMenuItem(v, R.drawable.ic_beer_bleu);
        drawerLayout.closeDrawer();
        nextActivity = StaticDataActivity.class;
        nextStaticFragment = ConsoFragment.class;
    }

    @OnClick(R.id.navigation_drawer_tcl)
    public void onClickTcl(View v) {
        clearDrawerChoices();
        v.setActivated(true);
        setSelectedMenuItem(v, R.drawable.tcl_rouge);
        drawerLayout.closeDrawer();
        nextActivity = StaticDataActivity.class;
        nextStaticFragment = TclFragment.class;
    }

    @OnClick(R.id.navigation_drawer_facilities)
    public void onClickFacilities(View v) {
        clearDrawerChoices();
        v.setActivated(true);
        setSelectedMenuItem(v, R.drawable.drapeau_bleu);
        drawerLayout.closeDrawer();
        nextActivity = StaticDataActivity.class;
        nextStaticFragment = FacilitiesFragment.class;
    }

    public void setSelectedMenuItem(View v, int iconRes) {
        for (int i = 0; i < ((ViewGroup) v).getChildCount(); ++i) {
            View nextChild = ((ViewGroup) v).getChildAt(i);
            if (nextChild instanceof ImageView) {
                ImageView drawerSelectedIcon = (ImageView) nextChild;
                drawerSelectedIcon.setImageResource(iconRes);
            } else if (nextChild instanceof TextView) {
                TextView drawerSelectedText = (TextView) nextChild;
                drawerSelectedText.setTextColor(getResources().getColor(R.color.primary_day));
                drawerSelectedText.setTypeface(null, Typeface.BOLD);
            }
        }
    }

    public void setUnselectedMenuItem(View v, int iconRes) {
        for (int i = 0; i < ((ViewGroup) v).getChildCount(); ++i) {
            View nextChild = ((ViewGroup) v).getChildAt(i);
            if (nextChild instanceof ImageView) {
                ImageView drawerSelectedIcon = (ImageView) nextChild;
                drawerSelectedIcon.setImageResource(iconRes);
            } else if (nextChild instanceof TextView) {
                TextView drawerSelectedText = (TextView) nextChild;
                drawerSelectedText.setTextColor(getResources().getColor(R.color.drawer_label_default));
                drawerSelectedText.setTypeface(null, Typeface.NORMAL);
            }
        }
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
        setUnselectedMenuItem(artistButton, R.drawable.concert_gris);
        ticketsButton.setActivated(false);
        setUnselectedMenuItem(ticketsButton, R.drawable.billeterie_gris);
        tclButton.setActivated(false);
        setUnselectedMenuItem(tclButton, R.drawable.tcl_gris);
        consoButton.setActivated(false);
        setUnselectedMenuItem(consoButton, R.drawable.ic_beer);
        facilitiesButton.setActivated(false);
        setUnselectedMenuItem(facilitiesButton, R.drawable.drapeau_gris);
    }

    /**
     * Activity is no more alive
     */


    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);

        //save favorites
        writeResourceInSharedPref();

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //categories
        outState.putParcelableArrayList("categories", categories);
        //categories state
        outState.putParcelableArrayList("selectedCategories", selectedCategories);
        //resources
        outState.putParcelableArrayList("dayResourceList", dayResourceArrayList);
        outState.putParcelableArrayList("facilitiesList", facilitiesArrayList);
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
                builder.setPositiveButton(getResources().getString(R.string.update_app_label), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startPlayStoreIntent();
                    }
                });


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
                dataBackendService.getResourcesAsyncFromBackend(retrofitService, dataVersion, dayResourceArrayList, nightResourceArrayList);
                Log.d(TAG, "manageApplicationVersionState MINOR");
                builder = new AlertDialog.Builder(this);
                builder.setPositiveButton(getResources().getString(R.string.update_app_label), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startPlayStoreIntent();
                    }
                })
                        .setNegativeButton(getResources().getString(R.string.dealy_update_app_label), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.setMessage(R.string.apologize_dialog_messag_minor)
                        .setTitle(R.string.apologize_dialog_title);

                dialog = builder.create();
                dialog.show();

                break;
            case TODATE:
                dataBackendService.getResourcesAsyncFromBackend(retrofitService, dataVersion, dayResourceArrayList, nightResourceArrayList);
                Log.d(TAG, "manageApplicationVersionState TODATE");
                break;
        }


        //si popup sharedPref == major, (2.1) lance le timerTask pour la popup major changes
        //sinon si popup sharedPref == minor ou == toDate effectue le download des donnees et si popup sharedPref == minor affiche popup minor
    }

    private void startPlayStoreIntent() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
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

    public Integer getPositionCategorySelected() {
        return positionCategorySelected;
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
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
