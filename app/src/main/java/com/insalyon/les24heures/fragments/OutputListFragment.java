package com.insalyon.les24heures.fragments;

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
import android.widget.ListView;

import com.felipecsl.quickreturn.library.AbsListViewQuickReturnAttacher;
import com.felipecsl.quickreturn.library.QuickReturnAttacher;
import com.felipecsl.quickreturn.library.widget.QuickReturnAdapter;
import com.felipecsl.quickreturn.library.widget.QuickReturnTargetView;
import com.insalyon.les24heures.MainActivity;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.adapter.DayResourceAdapter;
import com.insalyon.les24heures.eventbus.CategoriesSelectedEvent;
import com.insalyon.les24heures.eventbus.ManageDetailSlidingUpDrawer;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.eventbus.SearchEvent;
import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.utils.SlidingUpPannelState;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by remi on 26/12/14.
 */
public class OutputListFragment extends OutputTypeFragment implements AbsListView.OnScrollListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final String TAG = OutputListFragment.class.getCanonicalName();

    View view;
    @InjectView(R.id.list_sort_alphabetical)
    View alphabeticalSort;
    @InjectView(R.id.list_sort_loc)
    View locSort;
    @InjectView(R.id.list_sort_time_loc)
    View timeLocSort;
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
    @InjectView(R.id.fab_goto_maps)
    FloatingActionButton fabGotoMaps;
    DayResourceAdapter dayResourceAdapter = null;
    private QuickReturnAttacher quickReturnAttacher;
    private Location lastKnownPosition;
    private int lastVisibleItem = 0;
    private int lastY = 0;
    private Boolean isScrollingUp = false;
    private Boolean isScrollingDown = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayName = getActivity().getResources().getString(R.string.drawer_outputtype_list);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.output_list_fragment, container, false);
        ButterKnife.inject(this, view);
        lastKnownPosition = new Location("lastKnownPosition");

        setHasOptionsMenu(true);

        //create an ArrayAdaptar from the String Array
        dayResourceAdapter = new DayResourceAdapter(this.getActivity().getApplicationContext(),
                R.layout.output_list_item, new ArrayList<>(resourcesList), lastKnownPosition); //no need of a pointer, ResourceAdapter takes care of its data via event and filter

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

        setCategoryFilter();

        lastKnownPosition.setLatitude(45.78401554);
        lastKnownPosition.setLongitude(4.8754406);

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

    @OnClick(R.id.fab_goto_maps)
    public void onClickFabGotoMaps(View v) {
        ((MainActivity) getActivity()).selectMaps();
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
        DayResource dayResource = (DayResource) parent.getItemAtPosition(position);

        ManageDetailSlidingUpDrawer manageDetailSlidingUpDrawer = new ManageDetailSlidingUpDrawer(SlidingUpPannelState.ANCHORED,
                dayResource);
        eventBus.post(manageDetailSlidingUpDrawer);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return true;
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
                fabGotoMaps.hide();
                eventBus.post(slidingUpEvent);

            }
            isScrollingDown = true;
            isScrollingUp = false;
        } else if (firstVisibleItem < lastVisibleItem) {
            //scroll up
            if (!isScrollingUp) {
                fabGotoMaps.show();
            }
            isScrollingUp = true;
            isScrollingDown = false;
        } else {
            if (top < lastY) {
                //scroll down
                if (!isScrollingDown) {
                    fabGotoMaps.hide();
                    eventBus.post(slidingUpEvent);
                }
                isScrollingDown = true;
                isScrollingUp = false;
            } else if (top > lastY) {
                //scroll up
                if (!isScrollingUp) {
                    fabGotoMaps.show();
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
    }


}
