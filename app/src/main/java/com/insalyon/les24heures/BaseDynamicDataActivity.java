package com.insalyon.les24heures;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.ShareActionProvider;

import com.insalyon.les24heures.eventbus.CategoriesSelectedEvent;
import com.insalyon.les24heures.eventbus.ManageDetailSlidingUpDrawer;
import com.insalyon.les24heures.eventbus.SearchEvent;
import com.insalyon.les24heures.fragments.DetailFragment;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.model.NightResource;
import com.insalyon.les24heures.socialSharing.OnShareTargetSelectedListener;
import com.insalyon.les24heures.socialSharing.ShareIntentFactory;
import com.insalyon.les24heures.utils.FilterAction;
import com.insalyon.les24heures.view.DetailSlidingUpPanelLayout;

import java.util.ArrayList;

import butterknife.InjectView;
import retrofit.RestAdapter;

/**
 * Created by remi on 12/03/15.
 */
public abstract class BaseDynamicDataActivity extends BaseActivity {
    public static final String PREFS_NAME = "dataFile";
    private static final String TAG = BaseDynamicDataActivity.class.getCanonicalName();
    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;
    //    FragmentManager fragmentManager;
//    EventBus eventBus;
//    RestAdapter restAdapter;
//    RetrofitService retrofitService;
//    @InjectView(R.id.drawer_layout)
//    CustomDrawerLayout drawerLayout;
//    @InjectView(R.id.left_drawer)
//    View drawerView;
//    @InjectView(R.id.left_drawer_categories_list)
//    ListView categoriesList;
    @InjectView(R.id.sliding_layout)
    DetailSlidingUpPanelLayout detailSlidingUpPanelLayoutLayout;
    //    @InjectView(R.id.navigation_drawer_artists)
//    View artistButton;
    DetailFragment detailFragment;
    //    DataBackendService dataBackendService;
//    ResourceService resourceService;
//    CategoryService categoryService;
//    ArrayList<Category> categories;
//    //need a list to store a category and favorites or not (it's a bad code resulting from the old impl where it could be possible to select several categories)
//    ArrayList<Category> selectedCategories = new ArrayList<>();
//    ArrayList<DayResource> dayResourceArrayList;
//    ArrayList<NightResource> nightResourceArrayList;
//    String dataVersion;
//    String applicationVersion;
//    DrawerArrowDrawable drawerArrowDrawable;
//    DrawerLayout.SimpleDrawerListener drawerListener;
//    Menu globalMenu;
    Boolean isFavoritesChecked = false;
    String searchQuery;
    Menu mMenu;
    String slidingUpState;
    RestAdapter restAdapterLocal;
    //    Class nextActivity;
    private BaseDynamicDataActivity self = this;
    private ShareActionProvider mShareActionProvider;
//    private int positionCategorySelected;

    /**
     * Activity is being created
     */

    public void retrieveData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
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

        super.retrieveData(savedInstanceState);


    }


    private void setupDetailFragment() {
        detailSlidingUpPanelLayoutLayout.setActivity(this); //slidingPanel needs the activity to invalidateOptionMenu, manage appName and arrowDrawer
        detailSlidingUpPanelLayoutLayout.setParallaxHeader(findViewById(R.id.detail_paralax_header));

        detailFragment = (DetailFragment) fragmentManager.findFragmentById(R.id.sliding_layout_content_fragment);
        detailFragment.setParallaxImageHeader((ImageView) findViewById(R.id.detail_paralax_header_imageview));
        detailFragment.setParallaxHeader(findViewById(R.id.detail_paralax_header));
        detailSlidingUpPanelLayoutLayout.setDetailFragment(detailFragment);
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

        MenuItem item = menu.findItem(R.id.menu_item_share);


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

                if (!searchView.isIconified()) {
                    SearchEvent searchEvent = new SearchEvent(newText);
                    eventBus.post(searchEvent);
                    searchQuery = newText;
                }

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

        setupDetailFragment();

    }

    @Override
    protected void onResume() {
        super.onResume();

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
                    setupResourceSharingIntent(m.getDayResource());
                }
                break;
            case ANCHORED:
                if (m.getDayResource() != null) {
                    setupResourceSharingIntent(m.getDayResource());
                    detailFragment.notifyDataChanged(m.getDayResource());
                } else if (m.getNightResource() != null) {
                    detailFragment.notifyDataChanged(m.getNightResource());
                    setupResourceSharingIntent(m.getNightResource());
                }
                detailSlidingUpPanelLayoutLayout.anchorPanel();
                break;
        }
    }


    private void setupResourceSharingIntent(DayResource resource) {
        setShareIntent(ShareIntentFactory.getResourceSharingIntent(this, resource));
    }

    private void setupResourceSharingIntent(NightResource resource) {
        setShareIntent(ShareIntentFactory.getResourceSharingIntent(this, resource));
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen()) {
            drawerLayout.closeDrawer();
        } else if (detailSlidingUpPanelLayoutLayout.isAnchoredOrExpanded()) {
            if (DayActivity.class.isAssignableFrom(this.getClass()) &&
                    ((DayActivity) this).getmViewPager().getCurrentItem() == 1)//if list is active
                detailSlidingUpPanelLayoutLayout.hidePanel(); //TODO le rendu est moche
            else
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
        if(drawerLayout == null) return; //on Lollipop, onPrepareOptionsMenu is called before onPostCreate which find the view (via ButterKnife). All view are null in thise case
        boolean drawerOpen = drawerLayout.isDrawerVisible();
        Boolean displayGlobalItem = !drawerOpen && !detailSlidingUpPanelLayoutLayout.isAnchoredOrExpanded();


        mMenu.findItem(R.id.menu_search).setVisible(displayGlobalItem);
        mMenu.findItem(R.id.menu_favorites).setVisible(displayGlobalItem);
        mMenu.findItem(R.id.menu_item_share).setVisible(!displayGlobalItem);


        MenuItem item = mMenu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        mShareActionProvider.setOnShareTargetSelectedListener(new OnShareTargetSelectedListener());

    }


    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        //trick to change the searchView icon, see if it works on every device
        MenuItem searchViewMenuItem = menu.findItem(R.id.menu_search);
        SearchView mSearchView = (SearchView) searchViewMenuItem.getActionView();
        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView v = (ImageView) mSearchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_action_search);

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
                if (DayActivity.class.isAssignableFrom(this.getClass()) &&
                        ((DayActivity) this).getmViewPager().getCurrentItem() == 1)//if list is active
                    detailSlidingUpPanelLayoutLayout.hidePanel(); //TODO le rendu est moche
                else
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


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
            item.setIcon(R.drawable.ic_action_favorite_uncheck);

            isFavoritesChecked = false;
        } else {
            list.add(categoryService.getFavoriteCategory());
            item.setChecked(true);
            item.setIcon(R.drawable.ic_action_favorite);
            isFavoritesChecked = true;
        }
        CategoriesSelectedEvent event = new CategoriesSelectedEvent(list);
        event.setFilterAction(FilterAction.ADDED);
        eventBus.post(event);
    }

    protected void animateContentOut(float slideOffset) {
        detailSlidingUpPanelLayoutLayout.setAlpha(slideOffset);

    }

    public DrawerListener getDrawerListener() {
        return new DrawerListener();
    }

    private class DrawerListener extends BaseActivity.DrawerListener {//DrawerLayout.SimpleDrawerListener {
        private MenuItem itemFav;
        private MenuItem itemSearch;

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            itemFav.setEnabled(false);
            itemSearch.setEnabled(false);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
            itemFav.setEnabled(true);
            itemSearch.setEnabled(true);
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            super.onDrawerSlide(drawerView, slideOffset);

            if (globalMenu != null && itemFav == null) {
                itemFav = globalMenu.findItem(R.id.menu_favorites);
                itemSearch = globalMenu.findItem(R.id.menu_search);
            }

            if (slideOffset > 0.95)
                return;
            if (itemFav != null && itemSearch != null) {
                itemFav.getIcon().setAlpha((int) (255 - slideOffset * 255));
                itemSearch.getActionView().setAlpha(1 - slideOffset);
            }

            if (nextActivity != null) {
                animateContentOut(slideOffset);
            }
        }


    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
        invalidateOptionsMenu();
    }


}
