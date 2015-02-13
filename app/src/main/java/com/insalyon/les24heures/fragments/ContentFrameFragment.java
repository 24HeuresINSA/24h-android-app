package com.insalyon.les24heures.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Toast;

import com.insalyon.les24heures.MainActivity;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.eventbus.CategoriesSelectedEvent;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.eventbus.SearchEvent;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.model.Resource;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by remi on 11/02/15.
 */
public abstract class ContentFrameFragment<T extends Resource> extends Fragment {
    private static final String TAG = ContentFrameFragment.class.getCanonicalName();

    EventBus eventBus;

    //see spinner adapter
    Boolean spinner = false; //TODO mettre en place un vrai spinner

    View view;
    ArrayList<Category> categoriesSelected;
    String searchQuery;
    ArrayList<T> resourcesList;

    public String displayName;

    Filter searchFilter;
    Filter categoryFilter;


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

        setCategoryFilter();

    }

    public void onEvent(ResourcesUpdatedEvent event) {
        Log.d("onEvent(ResourcesUpdatedEvent)", event.getDayResourceList().toString());

        if (spinner) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.resources_found, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

            spinner = false;
        }

        setCategoryFilter();

    }

    public void onEvent(SearchEvent event) {
        Log.d("onEvent(SearchEvent)", event.getQuery().toString());
        if(searchFilter != null) {
            searchFilter.filter(event.getQuery().toString());
        }else
            Log.e(TAG,"search filter is null");
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

    /**
     * Fragment methods        *
     */
    protected Boolean setCategoryFilter() {
        if(categoryFilter != null) {
            categoryFilter.filter(
                    (categoriesSelected.size() != 0) ? categoriesSelected.toString() : null);
        }else
            Log.e(TAG,"category filter is null");

        return true;
    }

}
