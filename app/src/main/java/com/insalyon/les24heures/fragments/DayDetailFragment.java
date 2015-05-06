package com.insalyon.les24heures.fragments;

import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.eventbus.ResourceSelectedEvent;
import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.model.Schedule;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by remi on 23/04/15.
 */
public class DayDetailFragment extends DetailFragment implements OnMapReadyCallback {
    @InjectView(R.id.detail_mini_maps_holder)
    View miniMapsHolder;

    private GoogleMap googleMap;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.day_detail_fragment, container, false);
        ButterKnife.inject(this, view);

        FragmentManager fm;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fm = getFragmentManager();
        } else {
            fm = getChildFragmentManager();
        }
        MapFragment mapFragment = (MapFragment) fm
                .findFragmentById(R.id.detail_mini_maps);
        mapFragment.getMapAsync(this);
        googleMap = mapFragment.getMap();
        if (googleMap != null) {
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setAllGesturesEnabled(false);
        }


        super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }


    /**
     * Fragment is alive
     */
    public void onMapReady(final GoogleMap map) {
        if (resource != null && resource.getClass() == DayResource.class) {
            addMarkerAndMoveCam();
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.74968239082803, 4.852847680449486), 12));
        }

        // Other supported types include: MAP_TYPE_NORMAL,
        // MAP_TYPE_TERRAIN, MAP_TYPE_HYBRID and MAP_TYPE_NONE MAP_TYPE_SATELLITE
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public Boolean updateHeavyData() {
        if (super.updateHeavyData()) {

            //mini maps
            addMarkerAndMoveCam();

            slidingHeader.setBackground(this.getResources().getDrawable(R.color.primary_day));

            Schedule schedule;
            schedule = scheduleService.getNextSchedule(resource);
            if (schedule != null)
                nextSchedule.setText((schedule.getPrintableDay() + "  " +
                        schedule.getStart().getHours() + "h-" + schedule.getEnd().getHours() + "h").toUpperCase());
            else
                nextSchedule.setText(getResources().getString(R.string.no_more_schedule));

            return true;
        }
        return false;
    }


    private void addMarkerAndMoveCam() {
        if (googleMap == null) return;
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions()
//                                .title(resource.getTitle() + " " + resource.getCategory().getName())
//                                .snippet(resource.getDescription())
                .position(((DayResource) resource).getLoc()));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(((DayResource) resource).getLoc(), 15));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                eventBus.postSticky(new ResourceSelectedEvent((DayResource) resource));
            }
        });
    }
}
