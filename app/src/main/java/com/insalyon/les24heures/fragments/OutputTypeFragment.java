package com.insalyon.les24heures.fragments;

import android.os.Bundle;
import android.util.Log;

import com.insalyon.les24heures.eventbus.CategoriesSelectedEvent;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.eventbus.SearchEvent;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.model.DayResource;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by remi on 26/12/14.
 */
public class OutputTypeFragment extends ContentFrameFragment<DayResource>   {
    private static final String TAG = ContentFrameFragment.class.getCanonicalName();

    EventBus eventBus;

    ArrayList<Category> categoriesSelected;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        eventBus = EventBus.getDefault();

        if (savedInstanceState != null) {
            //get from restore state (when it's Android which create the fragment)
//            if (savedInstanceState.getParcelableArrayList("categoriesSelected") != null) {
//                categoriesSelected = savedInstanceState.getParcelableArrayList("categoriesSelected");
//            }
//            if (savedInstanceState.getParcelableArrayList("resourcesList") != null) {
//                resourcesList = savedInstanceState.getParcelableArrayList("resourcesList");
//            }
//            searchQuery = savedInstanceState.getString("searchQuery"); //we want null if there is no searchQuery
        } else if (getArguments() != null) {
            //get from arguments (when it's fragmentManager which create the fragment)
//            if (getArguments().getParcelableArrayList("categoriesSelected") != null) {
//                categoriesSelected = getArguments().getParcelableArrayList("categoriesSelected");
//            }
            //get from arguments (when it's fragmentManager which create the fragment)
//            if (getArguments().getParcelableArrayList("resourcesList") != null) {
//                resourcesList = getArguments().getParcelableArrayList("resourcesList");
//            }
//            searchQuery = getArguments().getString("searchQuery"); //we want null if there is no searchQuery
        }
        if (resourcesList == null || categoriesSelected == null) {
            Log.e("OutputTypeFragment", "resourcesList or categoriesSelected are null. Are you sur you create the fragment with these parameters ?");
        }


    }

//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        view = super.onCreateView(inflater, container, savedInstanceState);
//        return view;
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        ((MainActivity) getActivity()).setTitle(displayName);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        eventBus.registerSticky(this);
//
//    }

    /**
     * Fragment is alive      *
     */
    public void onEvent(CategoriesSelectedEvent event) {
        super.onEvent(event);
        Log.d(TAG+" onevent", event.getCategories().toString());
//        categoriesSelected.clear();
//        categoriesSelected.addAll(event.getCategories());
    }

    public void onEvent(ResourcesUpdatedEvent event) {
        super.onEvent(event);
        Log.d(TAG+"onEvent(ResourcesUpdatedEvent)", event.getDayResourceList().toString());
        resourcesList.clear();
        resourcesList.addAll(event.getDayResourceList());
    }

    public void onEvent(SearchEvent event) {
        super.onEvent(event);
        Log.d(TAG+"onEvent(SearchEvent)", event.getQuery().toString());
    }

    /**
     * Fragment is no more alive      *
     */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //categories state
//        outState.putParcelableArrayList("categoriesSelected", categoriesSelected);
        //search state
//        outState.putString("searchQuery", searchQuery);
        //resources
//        outState.putParcelableArrayList("resourcesList", resourcesList);
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        eventBus.unregister(this);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
//
//
//    public String getDisplayName() {
//        return displayName;
//    }
}
