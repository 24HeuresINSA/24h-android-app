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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.insalyon.les24heures.MainActivity;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.eventbus.CategoriesSelectedEvent;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.eventbus.SearchEvent;
import com.insalyon.les24heures.filter.ResourceMapsCategoryFilter;
import com.insalyon.les24heures.filter.ResourceMapsSearchFilter;
import com.insalyon.les24heures.model.Resource;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by remi on 26/12/14.
 */
public class OutputMapsFragment extends OutputTypeFragment implements OnMapReadyCallback {
    private static final String TAG = OutputMapsFragment.class.getCanonicalName();
    View view;

    Boolean spinner = false; //TODO mettre en place un vrai spinner

    MapView mapView;
    GoogleMap googleMap;

    ResourceMapsCategoryFilter resourceMapsCategoryFilter;
    ResourceMapsSearchFilter resourceMapsSearchFilter;

    ArrayList<Resource> displayableResourcesLists;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayName = getActivity().getResources().getString(R.string.drawer_outputtype_maps);

        displayableResourcesLists = new ArrayList<>();
        displayableResourcesLists.addAll(resourcesList);
        resourceMapsCategoryFilter = new ResourceMapsCategoryFilter(resourcesList, displayableResourcesLists, this);
        resourceMapsSearchFilter = new ResourceMapsSearchFilter(resourcesList, displayableResourcesLists, this);

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

        //to prevent user to throw up, zoom on Lyon without animateCamera
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.74968239082803, 4.852847680449486), 12));

        //display data if already there when the fragment is created
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition arg0) {
                restoreFilterState();

                // Remove listener to prevent position reset on camera move.
                map.setOnCameraChangeListener(null);
            }
        });

        // Other supported types include: MAP_TYPE_NORMAL,
        // MAP_TYPE_TERRAIN, MAP_TYPE_HYBRID and MAP_TYPE_NONE MAP_TYPE_SATELLITE
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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
        Log.d(TAG + "onEvent(CategoryEvent)", event.getResourceList().toString());

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
        addMarkers();
        resourceMapsCategoryFilter.filter(
                (categoriesSelected.size() != 0) ? categoriesSelected.toString() : null
        );
    }

    public void onEvent(SearchEvent event) {
        super.onEvent(event);
        resourceMapsSearchFilter.filter(event.getQuery().toString());
    }

    @OnClick(R.id.fab_goto_list)
    public void onClickFabGotoList(View v) {
        ((MainActivity) getActivity()).selectList();
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
        for (Resource resource : resourcesList) {
            resource.setMarker(null);
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
        for (Resource resource : resourcesList) {
            if (resource.getMarker() == null) {
                Marker marker = googleMap.addMarker(
                        new MarkerOptions()
                                .title(resource.getTitle() + " " + resource.getCategory().getName())
                                .snippet(resource.getDescription())
                                .position(resource.getLoc()));

                resource.setMarker(marker);
            }
        }
    }

    private LatLngBounds.Builder getBuilder() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //include only resource selected by a one of the filter
        for (Resource resource : displayableResourcesLists) {
            builder.include(resource.getMarker().getPosition());
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
