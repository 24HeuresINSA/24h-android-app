package com.insalyon.les24heures.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Toast;

import com.github.mrengineer13.snackbar.SnackBar;
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
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.eventbus.CategoriesSelectedEvent;
import com.insalyon.les24heures.eventbus.FilterUpdateEnded;
import com.insalyon.les24heures.eventbus.ManageDetailSlidingUpDrawer;
import com.insalyon.les24heures.eventbus.MapsSetIsVisible;
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
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by remi on 26/12/14.
 */
public class DayMapsFragment extends DayTypeFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final String TAG = DayMapsFragment.class.getCanonicalName();
    View view;


    MapView mapView;
    GoogleMap googleMap;
    @InjectView(R.id.progress_wheel)
    View progressBar;

//    ResourceMapsCategoryFilter resourceMapsCategoryFilter;
//    ResourceMapsSearchFilter resourceMapsSearchFilter;

    ArrayList<DayResource> displayableResourcesLists;
    HashMap<Marker, DayResource> markerResourceMap;
    HashMap<DayResource, Marker> resourceMarkerMap;

    DayResource selectedDayResource;
    CameraPosition initialCameraPosition;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayName = getActivity().getResources().getString(R.string.day_maps_appname);

        EventBus.getDefault().getStickyEvent(ResourceSelectedEvent.class);

        markerResourceMap = new HashMap<>();
        resourceMarkerMap = new HashMap<>();

        displayableResourcesLists = new ArrayList<>();
        displayableResourcesLists.addAll(resourcesList);
        categoryFilter = new ResourceMapsCategoryFilter(resourcesList, displayableResourcesLists, this,resourceMarkerMap);
        searchFilter = new ResourceMapsSearchFilter(resourcesList, displayableResourcesLists, this,resourceMarkerMap);


        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelable("cameraPosition") != null) {
                initialCameraPosition = savedInstanceState.getParcelable("cameraPosition");
            }
        }

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.day_maps_fragment, container, false);
        ButterKnife.inject(this, view);

        MapsInitializer.initialize(getActivity());

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(null);
        mapView.getMapAsync(this);

        googleMap = mapView.getMap();
        if(googleMap != null) {
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.setOnMarkerClickListener(this);
            addMarkers();
        }


        return view;
    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
        if(googleMap != null)
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

        //if (initialCameraPosition != null) //{
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(initialCameraPosition));
       // } else
            //to prevent user to throw up, zoom on Lyon without animateCamera
           // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.74968239082803, 4.852847680449486), 12));

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
    }

    public void onEvent(ResourcesUpdatedEvent event) {
        super.onEvent(event);
        if(googleMap != null) addMarkers();
    }

    public void onEvent(SearchEvent event) {
      super.onEvent(event);
    }


    public void onEvent(MapsSetIsVisible event){
        this.isVisible = event.isVisible();
        if(this.isVisible){
            this.catchUpCategorySelectedEvent();
        }
    }


    public void onEvent(ResourceSelectedEvent selectedEvent) {
        if(googleMap == null) return;
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedEvent.getDayResource().getLoc(), 17));
        onMarkerClick(resourceMarkerMap.get(selectedEvent.getDayResource()));
    }

    public void onEvent(ManageDetailSlidingUpDrawer event) {
        if (event.getState().equals(SlidingUpPannelState.HIDE)) {
            if(selectedDayResource != null)resourceMarkerMap.get(selectedDayResource).setIcon(BitmapDescriptorFactory.defaultMarker());
        }
    }


    @Override
    public boolean onMarkerClick(final Marker marker) {

        ManageDetailSlidingUpDrawer manageDetailSlidingUpDrawer = new ManageDetailSlidingUpDrawer(SlidingUpPannelState.SHOW, markerResourceMap.get(marker));
        eventBus.post(manageDetailSlidingUpDrawer);

        if (selectedDayResource != null) {
            //unset previously selected marker
            Marker previouslySelected = resourceMarkerMap.get(selectedDayResource);
            previouslySelected.setIcon(BitmapDescriptorFactory.fromResource(getMarkerDrawable(selectedDayResource)));
        }

        //set selected marker
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        selectedDayResource = markerResourceMap.get(marker);

        return false;
    }


    /**
     * Fragment is no more running *
     */
    @Override
    public void onPause() {
        super.onPause();
        if(googleMap == null) return;

        googleMap.setMyLocationEnabled(false);

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
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
        if(googleMap != null)
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
        if (resourcesList.isEmpty()) {
            return;
        }
        resourceMarkerMap.clear();
        for (DayResource dayResource : resourcesList) {
            if (resourceMarkerMap.get(dayResource) == null) {
                Float color = BitmapDescriptorFactory.HUE_RED;
                color = getCategoryHueColor(dayResource, color);


                int icon = getMarkerDrawable(dayResource);
                Marker marker = googleMap.addMarker(
                        new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(icon))
                                .position(dayResource.getLoc()));

                markerResourceMap.put(marker, dayResource);
                resourceMarkerMap.put(dayResource, marker);
            }
        }
    }

    private int getMarkerDrawable(DayResource dayResource) {
        int result =  getResources().getIdentifier("marker_"+dayResource.getCategory().getName(), "drawable", getActivity().getPackageName());
        if(result == 0) //TODO faire mieux ou pas du tout
            result = getResources().getIdentifier("ic_action_select_all","drawable",getActivity().getPackageName());
        return result;
    }

    private Float getCategoryHueColor(DayResource dayResource, Float color) {
        switch( dayResource.getCategory().getName()){
            case "divertissement" :
                color = BitmapDescriptorFactory.HUE_ORANGE;
                break;
            case "culturer":
                color = BitmapDescriptorFactory.HUE_BLUE;
                break;
            case "sportiver" :
                color = BitmapDescriptorFactory.HUE_YELLOW;
                break;
            case "prevention" :
                color = BitmapDescriptorFactory.HUE_GREEN;
                break;
        }
        return color;
    }

    private LatLngBounds.Builder getBuilder() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //include only resource selected by a one of the filter
        for (DayResource dayResource : displayableResourcesLists) {
            builder.include(resourceMarkerMap.get(dayResource).getPosition());
        }
        return builder;
    }

    public void moveCamera(Filter filter) {
        if(googleMap == null) return;
        try {
            // Move camera
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(getBuilder().build(), 70));

            eventBus.post(new FilterUpdateEnded(filter));
        } catch (IllegalStateException e) {
            e.printStackTrace();
            //no resources were added to the builder
            //default if no builder - Lyon
            //lg 4.852847680449486
            //la 45.74968239082803
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.74968239082803, 4.852847680449486), 12));
            String content = getResources().getString(R.string.unexpected_move_camera_error, Toast.LENGTH_SHORT);
            SnackBar.Builder snackBar = new SnackBar.Builder(this.getActivity())
                    .withMessage(content);
            snackBar.show();
        }
    }

    private void restoreFilterState() {
        //we need to restore a filter by text
        if (searchQuery != null) {
            searchFilter.filter(searchQuery.toString());
        }
        //we need to restore a filter by categories
        else if (!categoriesSelected.isEmpty()) {
            setCategoryFilter();
        }
        //else no filter needed
    }


    protected void displayProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected Boolean setCategoryFilter() {
        if(googleMap == null) return false;

            return super.setCategoryFilter();
    }
}
