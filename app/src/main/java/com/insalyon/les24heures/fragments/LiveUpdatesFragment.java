package com.insalyon.les24heures.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.insalyon.les24heures.R;
import com.insalyon.les24heures.adapter.LiveUpdateAdapter;
import com.insalyon.les24heures.androidService.LiveUpdateService;
import com.insalyon.les24heures.eventbus.LiveUpdatesReceivedEvent;
import com.insalyon.les24heures.model.LiveUpdate;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;


public class LiveUpdatesFragment extends Fragment {


    private View view;

    @InjectView(R.id.list_live_updates)
    ListView liveUpdatesListView;
    private List<LiveUpdate> liveUpdates;
    private EventBus eventBus;
    private LiveUpdateAdapter liveUpdateAdapter;
    private String displayName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayName = getActivity().getResources().getString(R.string.live_updates_fragment_appname);

        liveUpdates = new ArrayList<LiveUpdate>();
        liveUpdateAdapter = new LiveUpdateAdapter(getActivity(), liveUpdates);

        eventBus = EventBus.getDefault();
    }

    @Override
    public void onResume() {
        super.onResume();
        eventBus.registerSticky(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        eventBus.unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        prepareView(inflater, container);
        requestRefreshOfLiveUpdates();


        return view;
    }

    private void prepareView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.live_updates_fragment, container, false);
        ButterKnife.inject(this, view);
        liveUpdatesListView.setAdapter(liveUpdateAdapter);
    }

    private void requestRefreshOfLiveUpdates() {
        LiveUpdateService.start(getActivity());
    }

    public void onEvent(LiveUpdatesReceivedEvent event) {
        liveUpdates.clear();
        liveUpdates.addAll(event.getLiveUpdates());
        liveUpdateAdapter.notifyDataSetChanged();
    }

    public String getDisplayName() {
        return displayName;
    }
}
