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
import com.insalyon.les24heures.eventbus.CategoryEvent;
import com.insalyon.les24heures.model.Resource;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by remi on 26/12/14.
 */
public class OutputMapsFragment extends OutputTypeFragment implements OnMapReadyCallback {
    private static final String TAG = OutputMapsFragment.class.getCanonicalName();


    View view;


    private ArrayList<Resource> resourcesList;
    private ArrayList<Marker> markers;

    MapView mapView;

    //TODO faire proprement
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

        view = inflater.inflate(R.layout.maps_fragment, container, false);
        ButterKnife.inject(this, view);

        MapsInitializer.initialize(getActivity());

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(null);
//        mapView.setBuiltInZoomControls(true);
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        map.setMyLocationEnabled(true);

        //TODO faire ca proprement
        globalMap = map;

        //TODO je ne sais pas si s'il vaut mieux passer par getActivity que de passer par un bundle
        resourcesList = ((MainActivity) getActivity()).getResourcesList();

        //add markers to the map
        for (Resource resource : resourcesList) {
            Marker marker = map.addMarker(
                    new MarkerOptions()
                            .title(resource.getTitle())
                            .snippet(resource.getDescription())
                            .position(resource.getLoc()));

            resource.setMarker(marker);
        }

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition arg0) {
//            //to prevent user to throw up
            globalMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.74968239082803,4.852847680449486), 12));
                moveCameraAndDisplayResourceAccordingToSelectedCategories();
                // Remove listener to prevent position reset on camera move.
                map.setOnCameraChangeListener(null);
            }
        });



        // Other supported types include: MAP_TYPE_NORMAL,
        // MAP_TYPE_TERRAIN, MAP_TYPE_HYBRID and MAP_TYPE_NONE MAP_TYPE_SATELLITE
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void moveCameraAndDisplayResourceAccordingToSelectedCategories() {
        try{
            // Move camera
            globalMap.animateCamera(CameraUpdateFactory.newLatLngBounds(getBuilderAndDisplayResourceAcccordingToSelectedCategories().build(), 70));
        }catch (IllegalStateException e){
            //no resources were added to the builder
            //default if no builder - Lyon
            //lg 4.852847680449486
            //la 45.74968239082803
            globalMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.74968239082803,4.852847680449486), 12));
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.noCategoriesSelectedText, Toast.LENGTH_SHORT);
            toast.show();
            ((MainActivity)getActivity()).displayDrawer();
        }
    }

    //TODO si probleme de perf au changement de categories, il est possible de faire un getBuilderAndDisplayResourceAcccordingToSelectedCategories intelligent en utilisant plus precisement les events
    private LatLngBounds.Builder getBuilderAndDisplayResourceAcccordingToSelectedCategories() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //include only selected categories
        for (Resource resource : resourcesList) {
            if(categoriesSelected.indexOf(resource.getCategory()) == -1){
                resource.getMarker().setVisible(false);
            }else {
                resource.getMarker().setVisible(true);
                builder.include(resource.getMarker().getPosition());
            }
        }

       return builder;

    }

    private LatLngBounds.Builder getMapsBuilder() {
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        return builder;
    }

    public void onEvent(CategoryEvent event) {
        super.onEvent(event);
        Log.d(TAG+"onEvent(CategoryEvent)", event.getCategories().toString());
        moveCameraAndDisplayResourceAccordingToSelectedCategories();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).setTitle(R.string.drawer_outputtype_maps);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putParcelableArrayList("resourcesList",resourcesList);
    }


    @Override
    public void onPause() {
        super.onPause();
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
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
