package com.insalyon.les24heures.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.insalyon.les24heures.MainActivity;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.eventbus.CategoriesSelectedEvent;
import com.insalyon.les24heures.eventbus.ManageDetailSlidingUpDrawer;
import com.insalyon.les24heures.eventbus.ResourceSelectedEvent;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.eventbus.SearchEvent;
import com.insalyon.les24heures.filter.ResourceMapsCategoryFilter;
import com.insalyon.les24heures.filter.ResourceMapsSearchFilter;
import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.utils.SlidingUpPannelState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by remi on 26/12/14.
 */
public class OutputMapsFragment extends OutputTypeFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final String TAG = OutputMapsFragment.class.getCanonicalName();
    View view;


    MapView mapView;
    GoogleMap googleMap;

    ResourceMapsCategoryFilter resourceMapsCategoryFilter;
    ResourceMapsSearchFilter resourceMapsSearchFilter;

    ArrayList<DayResource> displayableResourcesLists;
    HashMap<Marker, DayResource> markerResourceMap;

    DayResource selectedDayResource;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayName = getActivity().getResources().getString(R.string.drawer_outputtype_maps);

        EventBus.getDefault().getStickyEvent(ResourceSelectedEvent.class);

        displayableResourcesLists = new ArrayList<>();
        displayableResourcesLists.addAll(resourcesList);
        resourceMapsCategoryFilter = new ResourceMapsCategoryFilter(resourcesList, displayableResourcesLists, this);
        resourceMapsSearchFilter = new ResourceMapsSearchFilter(resourcesList, displayableResourcesLists, this);
        markerResourceMap = new HashMap<>();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.output_maps_fragment, container, false);
        ButterKnife.inject(this, view);

        MapsInitializer.initialize(getActivity());

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(null);
        mapView.getMapAsync(this);

        googleMap = mapView.getMap();
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setOnMarkerClickListener(this);
        addMarkers();


        return view;
    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
        googleMap.setMyLocationEnabled(true);

    }

    /**
     * Fragment is running *
     */
    @Override
    public void onMapReady(final GoogleMap map) {
        map.setMyLocationEnabled(true);

        // Other supported types include: MAP_TYPE_NORMAL,
        // MAP_TYPE_TERRAIN, MAP_TYPE_HYBRID and MAP_TYPE_NONE MAP_TYPE_SATELLITE
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //to prevent user to throw up, zoom on Lyon without animateCamera
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.74968239082803, 4.852847680449486), 12));

        //display data if already there when the fragment is created
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition arg0) {
                if (selectedDayResource != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedDayResource.getLoc(), 17));
                    for (Map.Entry<Marker, DayResource> entry : markerResourceMap.entrySet()) {
                        if (entry.getValue() == selectedDayResource) {
                            entry.getKey().setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                            break;
                        }
                    }
                } else {
                    restoreFilterState();
                }

                // Remove listener to prevent position reset on camera move.
                map.setOnCameraChangeListener(null);
            }
        });

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void onEvent(CategoriesSelectedEvent event) {
        super.onEvent(event);
        Log.d(TAG + "onEvent(CategoryEvent)", event.getCategories().toString());
        resourceMapsCategoryFilter.filter(
                (event.getCategories().size() != 0) ? event.getCategories().toString() : null
        );

    }

    public void onEvent(ResourcesUpdatedEvent event) {
        super.onEvent(event);
        Log.d(TAG + "onEvent(CategoryEvent)", event.getDayResourceList().toString());


        addMarkers();
        resourceMapsCategoryFilter.filter(
                (categoriesSelected.size() != 0) ? categoriesSelected.toString() : null
        );
    }

    public void onEvent(SearchEvent event) {
        super.onEvent(event);
        resourceMapsSearchFilter.filter(event.getQuery().toString());
    }

    public void onEvent(ResourceSelectedEvent selectedEvent) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedEvent.getDayResource().getLoc(), 17));
//        EventBus.getDefault().removeStickyEvent(selectedEvent);
        selectedDayResource = selectedEvent.getDayResource();
    }

    @OnClick(R.id.fab_goto_list)
    public void onClickFabGotoList(View v) {
        ((MainActivity) getActivity()).selectList();
    }


    @Override
    public boolean onMarkerClick(final Marker marker) {

        ManageDetailSlidingUpDrawer manageDetailSlidingUpDrawer = new ManageDetailSlidingUpDrawer(SlidingUpPannelState.SHOW, markerResourceMap.get(marker));
        eventBus.post(manageDetailSlidingUpDrawer);

        if(selectedDayResource != null){
            for (Map.Entry<Marker, DayResource> entry : markerResourceMap.entrySet()) {
                if(entry.getValue() == selectedDayResource){
                    entry.getKey().setIcon(BitmapDescriptorFactory.defaultMarker());
                    break;
                }
            }
        }

       marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));


        selectedDayResource = markerResourceMap.get(marker);


        return false;
    }


    /**
     * Fragment is no more running *
     */
    @Override
    public void onPause() {
        super.onPause();
        googleMap.setMyLocationEnabled(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        for (DayResource dayResource : resourcesList) {
            dayResource.setMarker(null);
            //TODO en attendant de trouver mieux
        }
    }


    /**
     * Fragment methods *
     */
    private void addMarkers() {
        if (resourcesList.isEmpty()) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.noResourcesFound, Toast.LENGTH_SHORT);
            toast.show();
            //TODO display a spinner
            spinner = true;
            return;
        }
        for (DayResource dayResource : resourcesList) {
            if (dayResource.getMarker() == null) {
                Marker marker = googleMap.addMarker(
                        new MarkerOptions()
//                                .title(resource.getTitle() + " " + resource.getCategory().getName())
//                                .snippet(resource.getDescription())
                                .position(dayResource.getLoc()));

                markerResourceMap.put(marker, dayResource);
                dayResource.setMarker(marker); //TODO a supprimer
            }
        }
    }

    private LatLngBounds.Builder getBuilder() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //include only resource selected by a one of the filter
        for (DayResource dayResource : displayableResourcesLists) {
            builder.include(dayResource.getMarker().getPosition());
        }
        return builder;
    }

    public void moveCamera() {
        try {
            // Move camera
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(getBuilder().build(), 70));
        } catch (IllegalStateException e) {
            Log.d("OutputMapsFragment.moveCamera", "unexpected");
            e.printStackTrace();
            //no resources were added to the builder
            //default if no builder - Lyon
            //lg 4.852847680449486
            //la 45.74968239082803
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.74968239082803, 4.852847680449486), 12));
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.unexpected_move_camera_error, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void restoreFilterState() {
        //we need to restore a filter by text
        if (searchQuery != null) {
            resourceMapsSearchFilter.filter(searchQuery.toString());
        }
        //we need to restore a filter by categories
        else if (!categoriesSelected.isEmpty()) {
            resourceMapsCategoryFilter.filter(
                    (categoriesSelected.size() != 0) ? categoriesSelected.toString() : null
            );
        }
        //else no filter needed
    }

}
