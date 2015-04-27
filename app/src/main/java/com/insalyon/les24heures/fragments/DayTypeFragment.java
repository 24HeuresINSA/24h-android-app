package com.insalyon.les24heures.fragments;

import android.os.Bundle;
import android.util.Log;

import com.insalyon.les24heures.eventbus.CategoriesSelectedEvent;
import com.insalyon.les24heures.eventbus.FilterUpdateEnded;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.eventbus.SearchEvent;
import com.insalyon.les24heures.filter.ResourceSearchFilter;
import com.insalyon.les24heures.model.DayResource;

import java.util.ArrayList;

/**
 * Created by remi on 26/12/14.
 */
public abstract class DayTypeFragment extends ContentFrameFragment<DayResource> {
    private static final String TAG = ContentFrameFragment.class.getCanonicalName();

    public Boolean isVisible;
    public int cpt = 0;
    CategoriesSelectedEvent categoriesSelectedEventPending;
    SearchEvent searchEventPending;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (resourcesList == null) {
            Log.e("OutputTypeFragment", "resourcesList or categoriesSelected are null. Are you sur you create the fragment with these parameters ?");
            //TODO #31
            resourcesList = new ArrayList<>();
        }

        if (categoriesSelected == null) {
            Log.e("OutputTypeFragment", "resourcesList or categoriesSelected are null. Are you sur you create the fragment with these parameters ?");
            //TODO #31
            resourcesList = new ArrayList<>();
        }

        if(savedInstanceState != null)
        isVisible = savedInstanceState.getBoolean("isVisible", true);

    }

    /**
     * Fragment is alive      *
     */

    public void onEvent(ResourcesUpdatedEvent event) {
        super.onEvent(event);
        resourcesList.clear();
        resourcesList.addAll(event.getDayResourceList());
    }

    public void onEvent(CategoriesSelectedEvent event) {
        if (isVisible)
            super.onEvent(event);

        categoriesSelectedEventPending = event;
    }


    public void onEvent(SearchEvent event) {
        if (isVisible)
            super.onEvent(event);
        searchEventPending = event;

    }


    public void onEvent(FilterUpdateEnded event) {
        // if A.class extends B.class, and B.class extends C.class
//        C.class.isAssignableFrom(A.class); // evaluates to true

        //le search filter est effectué tout le temps
        if (ResourceSearchFilter.class.isAssignableFrom(event.getFilter().getClass())) {
            if (searchEventPending != null) {
                super.onEvent(searchEventPending);
                searchEventPending = null;
            }
        }

    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }


    public void catchUpCategorySelectedEvent() {
        if (categoriesSelectedEventPending != null) {
            super.onEvent(categoriesSelectedEventPending);
            categoriesSelectedEventPending = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("isVisible", isVisible);
    }
}
