package com.insalyon.les24heures.fragments;

import android.os.Bundle;
import android.util.Log;

import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.model.DayResource;

import java.util.ArrayList;

/**
 * Created by remi on 26/12/14.
 */
public abstract class DayTypeFragment extends ContentFrameFragment<DayResource> {
    private static final String TAG = ContentFrameFragment.class.getCanonicalName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (resourcesList == null || categoriesSelected == null) {
            Log.e("OutputTypeFragment", "resourcesList or categoriesSelected are null. Are you sur you create the fragment with these parameters ?");
            //TODO #31
            resourcesList = new ArrayList<>();
            categoriesSelected = new ArrayList<>();
        }
    }

    /**
     * Fragment is alive      *
     */

    public void onEvent(ResourcesUpdatedEvent event) {
        super.onEvent(event);
        resourcesList.clear();
        resourcesList.addAll(event.getDayResourceList());
    }


}
