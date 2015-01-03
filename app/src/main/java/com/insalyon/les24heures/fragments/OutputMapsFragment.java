package com.insalyon.les24heures.fragments;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
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
import com.insalyon.les24heures.model.Resource;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by remi on 26/12/14.
 */
public class OutputMapsFragment extends OutputTypeFragment implements OnMapReadyCallback {
    private static final String TAG = OutputMapsFragment.class.getCanonicalName();


    View view;

    Boolean spinner; //TODO mettre en place un vrai spinner

    private ArrayList<Marker> markers;

    MapView mapView;
    GoogleMap globalMap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        markers = new ArrayList<>();

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

        globalMap = mapView.getMap();
        globalMap.getUiSettings().setZoomControlsEnabled(true);
       

        return view;
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        map.setMyLocationEnabled(true);

        updateMapsView();

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition arg0) {
                //to prevent user to throw up, zoom on Lyon without animateCamera
                globalMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.74968239082803, 4.852847680449486), 12));
                //then try to zoom on resources
                if(updateMapsView())moveCamera();
                // Remove listener to prevent position reset on camera move.
                map.setOnCameraChangeListener(null);
            }
        });

        // Other supported types include: MAP_TYPE_NORMAL,
        // MAP_TYPE_TERRAIN, MAP_TYPE_HYBRID and MAP_TYPE_NONE MAP_TYPE_SATELLITE
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private Boolean updateMapsView(){
        if(resourcesList.isEmpty()){
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.noResourcesFound, Toast.LENGTH_SHORT);
            toast.show();
            ((MainActivity) getActivity()).displayDrawer();
            //TODO display a spinner
            spinner = true;
            return false;
        }
        addMarkers();
        if(categoriesSelected.isEmpty()){
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.noCategoriesSelected, Toast.LENGTH_SHORT);
            toast.show();
            ((MainActivity) getActivity()).displayDrawer();
            return false;
        }
        if(!displayMarkersAccordingToSelectedCategories()){
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.noResourcesMatchSelectedCategories, Toast.LENGTH_SHORT);
            toast.show();
            ((MainActivity) getActivity()).displayDrawer();
            return false;
        }
        return true;
    }

    private void addMarkers() {
        for (Resource resource : resourcesList) {
            if(resource.getMarker() == null) {
                Marker marker = globalMap.addMarker(
                        new MarkerOptions()
                                .title(resource.getTitle()+" "+resource.getCategory().getName())
                                .snippet(resource.getDescription())
                                .position(resource.getLoc()));

                resource.setMarker(marker);
            }
        }
    }

    private LatLngBounds.Builder getBuilder() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //include only selected categories
        for (Resource resource : resourcesList) {
            if (categoriesSelected.indexOf(resource.getCategory()) != -1) {
                builder.include(resource.getMarker().getPosition());
            }
        }
        return builder;
    }



    private Boolean displayMarkersAccordingToSelectedCategories() {
        Boolean atLeastOneVisible = false;
        //include only selected categories
        for (Resource resource : resourcesList) {
            if (categoriesSelected.indexOf(resource.getCategory()) == -1) {
                resource.getMarker().setVisible(false);
            } else {
                resource.getMarker().setVisible(true);
                atLeastOneVisible = true;
            }
        }
        return atLeastOneVisible;
    }




    private void moveCamera() {
        try {
            // Move camera
            globalMap.animateCamera(CameraUpdateFactory.newLatLngBounds(getBuilder().build(), 70));
        } catch (IllegalStateException e) {
            Log.d("OutputMapsFragment.moveCamera","unexpected");
            e.printStackTrace();
            //no resources were added to the builder
            //default if no builder - Lyon
            //lg 4.852847680449486
            //la 45.74968239082803
            globalMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.74968239082803, 4.852847680449486), 12));
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.unexpected_move_camera_error, Toast.LENGTH_SHORT);
            toast.show();
            ((MainActivity) getActivity()).displayDrawer();
        }
    }



    private LatLngBounds.Builder getMapsBuilder() {
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        return builder;
    }

    public void onEvent(CategoriesSelectedEvent event) {
        super.onEvent(event);
        Log.d(TAG + "onEvent(CategoryEvent)", event.getCategories().toString());
        if(updateMapsView())moveCamera();
    }

    public void onEvent(ResourcesUpdatedEvent event) {
        super.onEvent(event);
        Log.d(TAG + "onEvent(CategoryEvent)", event.getResourceList().toString());

        if(spinner){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.resources_found, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

            spinner = false;
        }
        if(updateMapsView())moveCamera();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).setTitle(R.string.drawer_outputtype_maps);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onPause() {
        super.onPause();
        globalMap.setMyLocationEnabled(false);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
        globalMap.setMyLocationEnabled(true);

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



    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
