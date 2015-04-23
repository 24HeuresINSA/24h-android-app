package com.insalyon.les24heures.fragments;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.felipecsl.quickreturn.library.AbsListViewQuickReturnAttacher;
import com.felipecsl.quickreturn.library.QuickReturnAttacher;
import com.felipecsl.quickreturn.library.widget.QuickReturnAdapter;
import com.felipecsl.quickreturn.library.widget.QuickReturnTargetView;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.adapter.DayResourceAdapter;
import com.insalyon.les24heures.eventbus.CategoriesSelectedEvent;
import com.insalyon.les24heures.eventbus.ListSetIsVisible;
import com.insalyon.les24heures.eventbus.ManageDetailSlidingUpDrawer;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.eventbus.SearchEvent;
import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.utils.SlidingUpPannelState;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by remi on 26/12/14.
 */
public class DayListFragment extends DayTypeFragment implements AbsListView.OnScrollListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final String TAG = DayListFragment.class.getCanonicalName();

    View view;
    @InjectView(R.id.list_sort_alphabetical)
    View alphabeticalSort;
    @InjectView(R.id.list_sort_loc)
    View locSort;
    @InjectView(R.id.list_resource)
    ListView resourceListView;
    @InjectView(R.id.listView_header)
    View quickReturnListHeader;
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        public void onGlobalLayout() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            } else {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
            // Add a quick return targetView to the attacher
            quickReturnAttacher.addTargetView(quickReturnListHeader, QuickReturnTargetView.POSITION_TOP, quickReturnListHeader.getHeight());
        }
    };
    @InjectView(R.id.progress_wheel)
    View progressBar;

    @InjectView(R.id.sort_AZ_text)
    ImageView sortAZText;

    DayResourceAdapter dayResourceAdapter = null;
    private QuickReturnAttacher quickReturnAttacher;
    private Location lastKnownPosition;
    private int lastVisibleItem = 0;
    private int lastY = 0;
    private Boolean isScrollingUp = false;
    private Boolean isScrollingDown = false;
    private int listPosition = 0;
    private int indexPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayName = getActivity().getResources().getString(R.string.day_list_appname);

        if(savedInstanceState != null){
            listPosition = savedInstanceState.getInt("position");
            indexPosition = savedInstanceState.getInt("indexPosition");
        }

        if (listPosition == 0 ) {
            SharedPreferences pref = getActivity().getPreferences(0);
            listPosition = pref.getInt("position",0);
            indexPosition = pref.getInt("indexPosition",0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.day_list_fragment, container, false);
        ButterKnife.inject(this, view);
        lastKnownPosition = new Location("lastKnownPosition");

        setHasOptionsMenu(true);

        //create an ArrayAdaptar from the String Array
        dayResourceAdapter = new DayResourceAdapter(this.getActivity().getApplicationContext(),
                R.layout.day_list_item, new ArrayList<>(resourcesList), lastKnownPosition); //no need of a pointer, ResourceAdapter takes care of its data via event and filter

        //get filters than are managed by ContentFrameFragment
        searchFilter = dayResourceAdapter.getFilter();
        categoryFilter = dayResourceAdapter.getCategoryFilter();

        // Wrap your adapter with QuickReturnAdapter
        resourceListView.setAdapter(new QuickReturnAdapter(dayResourceAdapter));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        quickReturnAttacher = QuickReturnAttacher.forView(resourceListView);
        final AbsListViewQuickReturnAttacher attacher = (AbsListViewQuickReturnAttacher) quickReturnAttacher;
        attacher.addOnScrollListener(this);
        attacher.setOnItemClickListener(this);
        attacher.setOnItemLongClickListener(this);

        view.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);

        //setCategoryFilter();

        lastKnownPosition.setLatitude(45.78401554);
        lastKnownPosition.setLongitude(4.8754406);

        restoreListPosition();
        alphabeticalSort.setSelected(true);
    }

    /**
     * Fragment is alive       *
     */
    public void onEvent(CategoriesSelectedEvent event) {
        super.onEvent(event);
    }

    public void onEvent(ResourcesUpdatedEvent event) {
        super.onEvent(event);
    }

    public void onEvent(SearchEvent event) {
        super.onEvent(event);
    }

    public void onEvent(ListSetIsVisible event){
        this.isVisible = event.isVisible();
        if(this.isVisible){
            this.catchUpCategorySelectedEvent();
        }

    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
        DayResource dayResource = dayResourceAdapter.getResources().get(position);

        ManageDetailSlidingUpDrawer manageDetailSlidingUpDrawer = new ManageDetailSlidingUpDrawer(SlidingUpPannelState.ANCHORED,
                dayResource);
        eventBus.post(manageDetailSlidingUpDrawer);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return true;
    }

   @OnClick(R.id.list_sort_alphabetical)
   public void onSortAlphabeticalClick(View v){
       if(!v.isSelected()){
           v.setSelected(true);
           sortAZText.setImageResource(R.drawable.za_sort);
           dayResourceAdapter.sortAZ();
       }else{
           v.setSelected(false);
           sortAZText.setImageResource(R.drawable.az_sort);
           dayResourceAdapter.sortZA();
       }

   }

    @OnClick(R.id.list_sort_loc)
    public void onSortLocClick(View v){
        if(!v.isSelected()){
            v.setSelected(true);
            dayResourceAdapter.sortLoc();
        }else{
            //nothing to do
            v.setSelected(false);
        }

    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int top = 0;
        if (view.getChildAt(0) != null) {
            top = view.getChildAt(0).getTop();
        }

        ManageDetailSlidingUpDrawer slidingUpEvent = new ManageDetailSlidingUpDrawer(SlidingUpPannelState.HIDE, (DayResource) null);

        if (firstVisibleItem > lastVisibleItem) {
            //scroll down
            if (!isScrollingDown) {
                eventBus.post(slidingUpEvent);

            }
            isScrollingDown = true;
            isScrollingUp = false;
        } else if (firstVisibleItem < lastVisibleItem) {
            //scroll up
            if (!isScrollingUp) {
            }
            isScrollingUp = true;
            isScrollingDown = false;
        } else {
            if (top < lastY) {
                //scroll down
                if (!isScrollingDown) {
                    eventBus.post(slidingUpEvent);
                }
                isScrollingDown = true;
                isScrollingUp = false;
            } else if (top > lastY) {
                //scroll up
                if (!isScrollingUp) {
                }
                isScrollingUp = true;
                isScrollingDown = false;
            }
        }

        lastVisibleItem = firstVisibleItem;
        lastY = top;

    }

    /**
     * Fragment is no more alive       *
     */
    @Override
    public void onPause() {
        super.onPause();
        resourceListView.setOnScrollListener(null);

        int index = resourceListView.getFirstVisiblePosition();
        View v = resourceListView.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - resourceListView.getPaddingTop());

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getActivity().getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("position", top);
        editor.putInt("indexPosition", index);
        // Commit the edits!
        editor.commit();
    }




    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save index and top position
        int index = resourceListView.getFirstVisiblePosition();
        View v = resourceListView.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - resourceListView.getPaddingTop());
        outState.putInt("position", top);
        outState.putInt("indexPosition", index);

        //sorted resources
        outState.putParcelableArrayList("resourcesList", dayResourceAdapter.getOriginalResources());

    }


    /**
     * Fragment methods
     */

    private void restoreListPosition(){
        resourceListView.setSelectionFromTop(indexPosition, listPosition);
    }


    protected void displayProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }



}
