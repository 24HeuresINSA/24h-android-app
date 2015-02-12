package com.insalyon.les24heures.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.insalyon.les24heures.R;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.eventbus.SearchEvent;
import com.insalyon.les24heures.model.NightResource;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by remi on 11/02/15.
 */
public class ArtistFragment extends ContentFrameFragment<NightResource>  {
    private static final String TAG = OutputMapsFragment.class.getCanonicalName();
    View view;

    @InjectView(R.id.artists_fragment_text)
    TextView text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayName = getActivity().getResources().getString(R.string.artist_fragment_name);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        
        view = inflater.inflate(R.layout.artists_fragment,container,false);
        ButterKnife.inject(this, view);

        text.setText(resourcesList.toString());

        return view;
    }

    /**
     * Fragment is alive      *
     */

    public void onEvent(ResourcesUpdatedEvent event) {
        Log.d("onEvent(ResourcesUpdatedEvent)", event.getNightResourceList().toString());
        resourcesList.clear();
        resourcesList.addAll(event.getNightResourceList());
        text.setText(resourcesList.toString());

    }

    public void onEvent(SearchEvent event) {
        Log.d("onEvent(SearchEvent)", event.getQuery().toString());
        //TODO
    }





}
