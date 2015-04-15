package com.insalyon.les24heures.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.insalyon.les24heures.JazzyViewPager.JazzyViewPager;
import com.insalyon.les24heures.JazzyViewPager.OutlineContainer;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.adapter.ScheduleAdapter;
import com.insalyon.les24heures.eventbus.ResourceSelectedEvent;
import com.insalyon.les24heures.eventbus.ResourceUpdatedEvent;
import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.model.NightResource;
import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.model.Schedule;
import com.insalyon.les24heures.service.impl.ResourceServiceImpl;
import com.insalyon.les24heures.view.DetailScrollView;
import com.insalyon.les24heures.view.viewpagerindicator.CirclePageIndicator;
import com.squareup.picasso.Picasso;

import java.net.URLDecoder;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by remi on 09/02/15.
 */
public class DetailFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = DayMapsFragment.class.getCanonicalName();
    private static ResourceServiceImpl resourceService = ResourceServiceImpl.getInstance();
    View view;
    @InjectView(R.id.detail_scrollView)
    DetailScrollView detailScrollView;
    @InjectView(R.id.detail_next_schedule)
    TextView nextSchedule;
    @InjectView(R.id.detail_favorites)
    ImageButton favoriteImageButton;
    @InjectView(R.id.detail_sliding_title)
    TextView detailSlidingTitle;
    @InjectView(R.id.detail_description_text)
    TextView detailSlidingDescription;
    @InjectView(R.id.detail_schedule_grid_layout)
    GridView schedulesGrid;
    @InjectView(R.id.detail_sliding_layout_header)
    View slidingHeader;
    @InjectView(R.id.detail_url)
    View detailUrlContainer;
    @InjectView(R.id.detail_url_facebook)
    TextView urlFacebook;
    @InjectView(R.id.detail_url_twitter)
    TextView urlTwitter;
    @InjectView(R.id.detail_url_web)
    TextView urlWeb;
    @InjectView(R.id.detail_mini_maps_holder)
    View miniMapsHolder;
    @InjectView(R.id.detail_carousel_layout)
    View carouselLayout;

    Resource resource;
    private ScheduleAdapter scheduleAdapter;
    private ArrayList<Schedule> schedules;
    private GoogleMap googleMap;

    private Boolean heavyDataUpdated = false;
    private EventBus eventBus;

    private JazzyViewPager mJazzy;
    private android.content.Context appContext;
    private MainAdapter picturePagerAdapter;
    private Picasso picasso;
    private ImageView parallaxImageHeader;
    private View parallaxHeader;
    private CirclePageIndicator mIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus = EventBus.getDefault();


        schedules = new ArrayList<>();

        appContext = getActivity().getApplicationContext();

        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelable("resource") != null) {
                resource = savedInstanceState.getParcelable("resource");
                schedules.addAll(resource.getSchedules());
            }
        }

        picasso = new Picasso.Builder(getActivity().getApplicationContext()).listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        }).build();

//        picasso.setLoggingEnabled(true);
//        picasso.setIndicatorsEnabled(true);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.detail_fragment, container, false);
        ButterKnife.inject(this, view);

        scheduleAdapter = new ScheduleAdapter(getActivity().getApplicationContext(),
                R.layout.schedule_grid_item, schedules);
        schedulesGrid.setAdapter(scheduleAdapter);

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
        if(googleMap != null) {
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setAllGesturesEnabled(false);
        }

        slidingHeader.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                updateHeavyData();
                return false;
            }
        });

        setupJazziness(JazzyViewPager.TransitionEffect.Standard);

        return view;
    }

    private void setupJazziness(JazzyViewPager.TransitionEffect effect) {
        mJazzy = (JazzyViewPager) view.findViewById(R.id.jazzy_pager);
        mJazzy.setTransitionEffect(effect);
        picturePagerAdapter = new MainAdapter();
        mJazzy.setAdapter(picturePagerAdapter);
        mJazzy.setPageMargin(30);
        mIndicator = (CirclePageIndicator)view.findViewById(R.id.indicator);
        mIndicator.setViewPager(mJazzy);

    }

    /**
     * Fragment is alive
     */
    @Override
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

    private void addMarkerAndMoveCam() {
        if(googleMap == null) return;
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

    @OnClick(R.id.detail_favorites)
    public void onClickFav(View v) {
        resource.setIsFavorites(!resource.isFavorites());
        if (resource.isFavorites()) {
            ((ImageButton) v).setImageResource(R.drawable.ic_action_favorite);
        }
        else {
            ((ImageButton) v).setImageResource(R.drawable.ic_action_favorite_uncheck);
        }

        eventBus.post(new ResourceUpdatedEvent());

    }

    public void updateHeavyData() {
        if (!heavyDataUpdated) {

            if (resource.getClass() == DayResource.class) {
                //mini maps
                addMarkerAndMoveCam();

                miniMapsHolder.setVisibility(View.VISIBLE);
                detailUrlContainer.setVisibility(View.GONE);
                slidingHeader.setBackground(this.getResources().getDrawable(R.color.primary_day));

            } else if (resource.getClass() == NightResource.class) {
                urlFacebook.setText(((NightResource) resource).getFacebookUrl());
                urlWeb.setText(((NightResource) resource).getSiteUrl());
                urlTwitter.setText(((NightResource) resource).getTwitterUrl());
                slidingHeader.setBackground(this.getResources().getDrawable(R.color.primary_night));

                miniMapsHolder.setVisibility(View.GONE);
                detailUrlContainer.setVisibility(View.VISIBLE);
            }


            //schedules
            schedules.clear();
            schedules.addAll(resource.getSchedules());
            scheduleAdapter.notifyDataSetChanged();

            //optionals  pictures
            if(resource.getPictures().size() == 0){
                carouselLayout.setVisibility(View.INVISIBLE);
            } else {
                mJazzy.setAdapter(picturePagerAdapter);
                carouselLayout.setVisibility(View.VISIBLE);
            }

            heavyDataUpdated = true;

        }
    }

    public void notifyDataChanged(final Resource res) {
        if (res != null)
            resource = res;
        if(resource == null)
            return;
        heavyDataUpdated = false;

        detailSlidingTitle.setText(resource.getTitle());
        detailSlidingTitle.setSelected(true);
        detailSlidingDescription.setText(resource.getDescription());

        Schedule schedule = resourceService.getNextSchedule(resource);
        nextSchedule.setText((schedule.getPrintableDay() + "\n" +
                schedule.getStart().getHours() + "h-" + schedule.getEnd().getHours() + "h").toUpperCase());

        if (resource.isFavorites()) {
            favoriteImageButton.setImageResource(R.drawable.ic_action_favorite_uncheck);
        }
        else {
            favoriteImageButton.setImageResource(R.drawable.ic_action_favorite);
        }


        if(resource.getMainPictureUrl() == null || resource.getMainPictureUrl() == ""){
            parallaxHeader.setVisibility(View.GONE);
            parallaxHeader.setSelected(true); //to prevent the slidingUp to display it
        }else {
            parallaxHeader.setVisibility(View.VISIBLE);
            parallaxHeader.setSelected(false); //to allow slidingUp to do its job
            picasso.load(URLDecoder.decode(resource.getMainPictureUrl()))
                    .placeholder(R.drawable.ic_waiting)
                    .error(R.drawable.ic_error)
                    .into(parallaxImageHeader);
        }


    }

    /**
     * Fragment is no more alive
     */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("resource", resource);
    }

    public void setParallaxImageHeader(ImageView parallaxImageHeader) {
        this.parallaxImageHeader = parallaxImageHeader;
    }


    public void setParallaxHeader(View parallaxHeader) {
        this.parallaxHeader = parallaxHeader;
    }



    private class MainAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView image = new ImageView(appContext);

            picasso.load(URLDecoder.decode(resource.getPictures().get(position)))
                    .placeholder(R.drawable.ic_waiting)
                    .error(R.drawable.ic_error)
                    .into(image);

            container.addView(image, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);



            mJazzy.setObjectForPosition(image, position);
            return image;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object obj) {
            container.removeView(mJazzy.findViewFromObject(position));
        }

        @Override
        public int getCount() {
            if(resource == null) return 0;
            return resource.getPictures().size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            if (view instanceof OutlineContainer) {
                return ((OutlineContainer) view).getChildAt(0) == obj;
            } else {
                return view == obj;
            }
        }
    }
}
