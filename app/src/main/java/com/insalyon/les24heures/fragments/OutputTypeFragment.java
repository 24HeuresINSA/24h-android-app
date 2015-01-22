package com.insalyon.les24heures.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.insalyon.les24heures.MainActivity;
import com.insalyon.les24heures.eventbus.CategoriesSelectedEvent;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.eventbus.SearchEvent;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.model.Resource;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by remi on 26/12/14.
 */
public class OutputTypeFragment extends Fragment {
    EventBus eventBus;

    View view;
    ArrayList<Category> categoriesSelected;
    String searchQuery;
    ArrayList<Resource> resourcesList;

    public String displayName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        eventBus = EventBus.getDefault();

        if (savedInstanceState != null) {
            //get from restore state (when it's Android which create the fragment)
            if (savedInstanceState.getParcelableArrayList("categoriesSelected") != null) {
                categoriesSelected = savedInstanceState.getParcelableArrayList("categoriesSelected");
            }
            if (savedInstanceState.getParcelableArrayList("resourcesList") != null) {
                resourcesList = savedInstanceState.getParcelableArrayList("resourcesList");
            }
            searchQuery = savedInstanceState.getString("searchQuery"); //we want null if there is no searchQuery
        } else if (getArguments() != null) {
            //get from arguments (when it's fragmentManager which create the fragment)
            if (getArguments().getParcelableArrayList("categoriesSelected") != null) {
                categoriesSelected = getArguments().getParcelableArrayList("categoriesSelected");
            }
            //get from arguments (when it's fragmentManager which create the fragment)
            if (getArguments().getParcelableArrayList("resourcesList") != null) {
                resourcesList = getArguments().getParcelableArrayList("resourcesList");
            }
            searchQuery = getArguments().getString("searchQuery"); //we want null if there is no searchQuery
        }
        if (resourcesList == null || categoriesSelected == null) {
            Log.e("OutputTypeFragment", "resourcesList or categoriesSelected are null. Are you sur you create the fragment with these parameters ?");
        }


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).setTitle(displayName);
    }

    @Override
    public void onResume() {
        super.onResume();
        eventBus.registerSticky(this);

    }

    /**
     * Fragment is alive      *
     */
    public void onEvent(CategoriesSelectedEvent event) {
        Log.d("onevent", event.getCategories().toString());
        categoriesSelected.clear();
        categoriesSelected.addAll((ArrayList<Category>) event.getCategories());
    }

    public void onEvent(ResourcesUpdatedEvent event) {
        Log.d("onEvent(ResourcesUpdatedEvent)", event.getResourceList().toString());
        resourcesList.clear();
        resourcesList.addAll(event.getResourceList());
    }

    public void onEvent(SearchEvent event) {
        Log.d("onEvent(SearchEvent)", event.getQuery().toString());
    }

    /**
     * Fragment is no more alive      *
     */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //categories state
        outState.putParcelableArrayList("categoriesSelected", categoriesSelected);
        //search state
        outState.putString("searchQuery", searchQuery);
        //resources
        outState.putParcelableArrayList("resourcesList", resourcesList);
    }

    @Override
    public void onPause() {
        super.onPause();
        eventBus.unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public String getDisplayName() {
        return displayName;
    }
}
