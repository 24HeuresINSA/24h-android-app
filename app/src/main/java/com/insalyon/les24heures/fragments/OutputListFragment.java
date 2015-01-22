package com.insalyon.les24heures.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.felipecsl.quickreturn.library.AbsListViewQuickReturnAttacher;
import com.felipecsl.quickreturn.library.QuickReturnAttacher;
import com.felipecsl.quickreturn.library.widget.QuickReturnAdapter;
import com.felipecsl.quickreturn.library.widget.QuickReturnTargetView;
import com.insalyon.les24heures.MainActivity;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.adapter.ResourceAdapter;
import com.insalyon.les24heures.eventbus.CategoriesSelectedEvent;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.eventbus.SearchEvent;
import com.insalyon.les24heures.model.Resource;
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
    @InjectView(R.id.fab_goto_maps)
    FloatingActionButton fabGotoMaps;


    //see spinner adapter
    Boolean spinner = false; //TODO mettre en place un vrai spinner

    ResourceAdapter resourceAdapter = null;
    private QuickReturnAttacher quickReturnAttacher;

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

        setHasOptionsMenu(true);

        //create an ArrayAdaptar from the String Array
        resourceAdapter = new ResourceAdapter(this.getActivity().getApplicationContext(),
                R.layout.output_list_item, new ArrayList<>(resourcesList)); //no need of a pointer, ResourceAdapter takes care of its data via event and filter

        // Wrap your adapter with QuickReturnAdapter
        resourceListView.setAdapter(new QuickReturnAdapter(resourceAdapter));

        return view;
    }


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

    }


    /**
     * Fragment is alive       *
     */
    public void onEvent(CategoriesSelectedEvent event) {
        super.onEvent(event);
        Log.d(TAG + "onEvent(CategoryEvent)", event.getCategories().toString());
        resourceAdapter.getCategoryFilter().filter(
                (event.getCategories().size() != 0) ? event.getCategories().toString() : null
        );

    }

    public void onEvent(ResourcesUpdatedEvent event) {
        super.onEvent(event);
        setCategoryFilter();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (spinner) {
                    spinner = false;
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.resources_found, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    public void onEvent(SearchEvent event) {
        super.onEvent(event);
        resourceAdapter.getFilter().filter(event.getQuery().toString());
    }

    @OnClick(R.id.fab_goto_maps)
    public void onClickFabGotoMaps(View v) {
        ((MainActivity) getActivity()).selectMaps();
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
        Resource resource = (Resource) parent.getItemAtPosition(position);
        //TODO details : cf DSF
        Toast.makeText(getActivity().getApplicationContext(),
                resource.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return true;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    private int lastVisibleItem = 0;
    private int lastY = 0;
    private Boolean isScrollingUp = false;
    private Boolean isScrollingDown = false;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int top = 0;
        if (view.getChildAt(0) != null) {
            top = view.getChildAt(0).getTop();
        }

        if (firstVisibleItem > lastVisibleItem) {
            //scroll down
            if (!isScrollingDown) {
                fabGotoMaps.hide();
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


    /**
     * Fragment methods        *
     */
    private Boolean setCategoryFilter() {
        resourceAdapter.getCategoryFilter().filter(
                (categoriesSelected.size() != 0) ? categoriesSelected.toString() : null);

        return true;
    }


}
