package com.insalyon.les24heures.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.insalyon.les24heures.MainActivity;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.eventbus.SearchEvent;
import com.insalyon.les24heures.model.NightResource;

import java.util.ArrayList;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by remi on 11/02/15.
 */
public class ArtistFragment extends Fragment  {
    private static final String TAG = OutputMapsFragment.class.getCanonicalName();
    View view;
    private String displayName;
    private EventBus eventBus;
    private ArrayList<NightResource> resourcesList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus = EventBus.getDefault();
        displayName = getActivity().getResources().getString(R.string.drawer_outputtype_list);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).setTitle(displayName);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        
        view = inflater.inflate(R.layout.artists_fragment,container,false);
        ButterKnife.inject(this, view);
        
        
        return view;
    }

    /**
     * Fragment is alive      *
     */

    public void onEvent(ResourcesUpdatedEvent event) {
        Log.d("onEvent(ResourcesUpdatedEvent)", event.getNightResourceList().toString());
        resourcesList.clear();
        resourcesList.addAll(event.getNightResourceList());
    }

    public void onEvent(SearchEvent event) {
        Log.d("onEvent(SearchEvent)", event.getQuery().toString());
        //TODO
    }



    public String getDisplayName() {
        return displayName;
    }
}
