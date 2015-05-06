package com.insalyon.les24heures.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.model.DayResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by remi on 19/04/15.
 */
public class FacilitiesFragment extends Fragment implements OnMapReadyCallback {
    public static final String PREFS_NAME = "dataFile";

    private ArrayList<DayResource> resources;

    private View view;

    MapView mapView;
    GoogleMap googleMap;
    CameraPosition initialCameraPosition;
    HashMap<DayResource, Marker> resourceMarkerMap;


    @InjectView(R.id.progress_wheel)
    View progressBar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.facilities_fragment, container, false);

        //impossible de faire passer les facilities dans le bundle de l'intent....
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        Gson gson = new Gson();
        String facilitiesArrayListStr = settings.getString("facilitiesList", "");
        resources = gson.fromJson(facilitiesArrayListStr, new TypeToken<List<DayResource>>() {
        }.getType());



        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(null);
        mapView.getMapAsync(this);

        googleMap = mapView.getMap();
        if (googleMap != null) {
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            addMarkers();
        }



        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelable("cameraPosition") != null) {
                initialCameraPosition = savedInstanceState.getParcelable("cameraPosition");
            }
        }

        resourceMarkerMap = new HashMap<>();

        if (initialCameraPosition == null) {
            SharedPreferences pref = getActivity().getPreferences(0);
            String lat = "45.74968239082803";
            String lng = "4.852847680449486";
            String zoom = "12";
            String tilt = "0";
            String bearing = "0";
            lat = pref.getString("lat", lat);
            lng = pref.getString("lng", lng);
            zoom = pref.getString("zoom", zoom);
            tilt = pref.getString("tilt", tilt);
            bearing = pref.getString("bearing", bearing);
            initialCameraPosition = new CameraPosition(
                    new LatLng(Double.valueOf(lat), Double.valueOf(lng)), Float.valueOf(zoom), Float.valueOf(tilt), Float.valueOf(bearing));
        }
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

        //if (initialCameraPosition != null) //{
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(initialCameraPosition));
        // } else
        //to prevent user to throw up, zoom on Lyon without animateCamera
        // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.74968239082803, 4.852847680449486), 12));

        //display data if already there when the fragment is created
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition arg0) {
                if (googleMap == null) return;
                try {
                    // Move camera
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(getBuilder().build(), 70));

                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    //no resources were added to the builder
                    //default if no builder - Lyon
                    //lg 4.852847680449486
                    //la 45.74968239082803
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.74968239082803, 4.852847680449486), 12));

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


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
        if (googleMap != null)
            googleMap.setMyLocationEnabled(true);

    }

    /**
     * Fragment is no more running *
     */
    @Override
    public void onPause() {
        super.onPause();
        if (googleMap == null) return;

        googleMap.setMyLocationEnabled(false);

        SharedPreferences settings = getActivity().getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("lat", String.valueOf(googleMap.getCameraPosition().target.latitude));
        editor.putString("lng", String.valueOf(googleMap.getCameraPosition().target.longitude));
        editor.putString("zoom", String.valueOf(googleMap.getCameraPosition().zoom));
        editor.putString("bearing", String.valueOf(googleMap.getCameraPosition().bearing));
        editor.putString("title", String.valueOf(googleMap.getCameraPosition().tilt));
        // Commit the edits!
        editor.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (googleMap != null)
            outState.putParcelable("cameraPosition", googleMap.getCameraPosition());
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    /**
     * Fragment methods *
     */
    private void addMarkers() {
        if (resources.isEmpty()) {
            return;
        }
        googleMap.clear();
        resourceMarkerMap.clear();
        for (DayResource facilities : resources) {
                Marker marker = googleMap.addMarker(
                        new MarkerOptions()
                                .title(facilities.getTitle())
                                .position(facilities.getLoc()));

            resourceMarkerMap.put(facilities, marker);


        }
    }

    private LatLngBounds.Builder getBuilder() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //include only resource selected by a one of the filter
        for (DayResource dayResource : resources) {
            builder.include(resourceMarkerMap.get(dayResource).getPosition());
        }
        return builder;
    }

}
