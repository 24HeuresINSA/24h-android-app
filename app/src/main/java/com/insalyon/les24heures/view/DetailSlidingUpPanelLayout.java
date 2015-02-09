package com.insalyon.les24heures.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.insalyon.les24heures.MainActivity;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.model.Schedule;
import com.insalyon.les24heures.service.impl.ResourceServiceImpl;
import com.insalyon.les24heures.utils.DetailSlidingUpPanelLayoutNullActivity;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by remi on 04/02/15.
 */
public class DetailSlidingUpPanelLayout extends SlidingUpPanelLayout{
    private static final String TAG = DetailSlidingUpPanelLayout.class.getCanonicalName();

    private static ResourceServiceImpl resourceService = ResourceServiceImpl.getInstance();

    Resource resource;

    private DetailScrollView detailScrollView;
    private TextView nextSchedule;
    private ImageButton favoriteImageButton;
    private TextView detailSlidingTitle;
    private TextView detailSlidingDescription;
    private View paralaxHeader;
    private DrawerArrowDrawable drawerArrowDrawable;


    private Integer wideHeight;
    private MainActivity activity;
    private boolean isSetup = false;


    private float anchored;
    private int scrollingHeaderHeight;
    private DetailSlidingUpPanelLayout self;

    private PanelSlideListener panelSlideListener = new PanelSlideListener() {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            Log.i(TAG, "onPanelSlide, offset " + slideOffset);

            float newParallaxHeaderPos;
            float parallaxContentFrame = -self.getCurrentParalaxOffset();

            if (slideOffset <= 0) {//from visible to hidden and vice versa
                //parallax
                newParallaxHeaderPos = (wideHeight - scrollingHeaderHeight) * (1 - slideOffset / (anchored));
                newParallaxHeaderPos = newParallaxHeaderPos + parallaxContentFrame;
                paralaxHeader.setTranslationY(newParallaxHeaderPos);
                //TODO on peut juste se contenter de cacher la main pic a cette etape au lieu de la bouger

            } else if (slideOffset < anchored) { //from visible to anchored and vice versa
                //parallax
                newParallaxHeaderPos = (wideHeight - scrollingHeaderHeight) * (1 - slideOffset / (anchored));
                newParallaxHeaderPos = newParallaxHeaderPos + parallaxContentFrame;
                paralaxHeader.setTranslationY(newParallaxHeaderPos);

                //arrowDrawable
                float param = 1 / anchored * slideOffset;
                if (param < 0) param = 0;
                else if (param > 1) param = 1;

                drawerArrowDrawable.setParameter(param);

//                    activity.getActionBar().setTitle("");
            } else {      //from anchored to expand and vice versa
                drawerArrowDrawable.setParameter(1);
//                // TODO ou pas ?
//                    newParallaxHeaderPos = parallaxHeight*slideOffset/(1-anchored);
//                  newParallaxHeaderPos = (height * (1 - slideOffset));
//                    newParallaxHeaderPos = newParallaxHeaderPos + parallaxContentFrame;
//                    paralaxHeader.setTranslationY(newParallaxHeaderPos);
            }

            if (slideOffset == 0) {
                detailScrollView.setIsScrollEnable(true);
            }

        }

        @Override
        public void onPanelExpanded(View panel) {
            Log.i(TAG, "onPanelExpanded");
            detailScrollView.setIsScrollEnable(true);
            nextSchedule.setVisibility(View.GONE);
            favoriteImageButton.setVisibility(View.VISIBLE);
//                this.setDragView(slidingDetailHeader);

            activity.invalidateOptionsMenu();
//                activity.getActionBar().setTitle(resource.getTitle());   => hide title in detail

        }

        @Override
        public void onPanelCollapsed(View panel) {
            Log.i(TAG, "onPanelCollapsed");
            nextSchedule.setVisibility(View.VISIBLE);
            favoriteImageButton.setVisibility(View.GONE);
//                this.setDragView(wholeSlidingLayout);
            detailScrollView.setIsScrollEnable(false);
            detailScrollView.fullScroll(ScrollView.FOCUS_UP);

            activity.invalidateOptionsMenu();
            activity.restoreTitle();
        }

        @Override
        public void onPanelAnchored(View panel) {
            Log.i(TAG, "onPanelAnchored");
            nextSchedule.setVisibility(View.GONE);
            favoriteImageButton.setVisibility(View.VISIBLE);
            detailScrollView.setIsScrollEnable(false);

            activity.invalidateOptionsMenu();
        }

        @Override
        public void onPanelHidden(View panel) {
            Log.i(TAG, "onPanelHidden");
        }
    };


    public DetailSlidingUpPanelLayout(Context context) {
        super(context);
    }

    public DetailSlidingUpPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetailSlidingUpPanelLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    /**
     * if return false it's because the height of the view isn't yet ready, the setup will be done once the view is rendered and
     * the view height is processed
     * @return
     */
    public Boolean setup() throws DetailSlidingUpPanelLayoutNullActivity {
        if(wideHeight == null)
            return false;
        if(activity == null){
            throw new DetailSlidingUpPanelLayoutNullActivity();
        }

        findDetailView(activity);

        drawerArrowDrawable = activity.getDrawerArrowDrawable();

        //get params
        anchored = Float.parseFloat(getResources().getString(R.string.detail_anchored));
        scrollingHeaderHeight = (int) getResources().getDimension(R.dimen.detail_header_height);
        final int parallaxHeight = (int) ((wideHeight - scrollingHeaderHeight) * (1-anchored));//407; //paralax height

        //set parallaxHeader
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) paralaxHeader.getLayoutParams();
        params.height = parallaxHeight;
        paralaxHeader.setLayoutParams(params);
        paralaxHeader.setTranslationY(wideHeight);

        self = this;

        //setup
        this.setAnchorPoint(anchored);
        this.hidePanel();  //2.0.4 sera remplacé par mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN); à la prochaine release
//        this.setDragView(wholeSlidingLayout); //default but to be clear
        detailScrollView.setIsScrollEnable(false);

//        panelSlideListener =
        this.setPanelSlideListener(panelSlideListener);

        isSetup = true;
        return true;
    }



    private void findDetailView(Activity activity) {
        detailScrollView = (DetailScrollView) activity.findViewById(R.id.detail_scrollView);
        nextSchedule = (TextView) activity.findViewById(R.id.detail_next_schedule);
        favoriteImageButton = (ImageButton) activity.findViewById(R.id.detail_favorites);
        detailSlidingTitle = (TextView) activity.findViewById(R.id.detail_sliding_title);
        detailSlidingDescription = (TextView) activity.findViewById(R.id.detail_desciption_text);
        paralaxHeader = activity.findViewById(R.id.detail_paralax_header);

        favoriteImageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                resource.setIsFavorites(!resource.isFavorites());
                if(resource.isFavorites())
                    ((ImageButton) v).setImageResource(R.drawable.ic_favorites_checked);
                else
                    ((ImageButton) v).setImageResource(R.drawable.ic_favorites_unchecked);

                //TODO notify dataset changed (surtout List)
            }
        });
    }



    public void showDetailPannel(Resource res){
        resource = res;

        detailSlidingTitle.setText(resource.getTitle());
        detailSlidingDescription.setText(resource.getDescription());

        Schedule schedule = resourceService.getNextSchedule(resource);
        nextSchedule.setText(schedule.getPrintableDay()+"\n"+
                schedule.getStart().getHours()+"h-"+schedule.getEnd().getHours()+"h");

        if(resource.isFavorites())
            favoriteImageButton.setImageResource(R.drawable.ic_favorites_checked);
        else
            favoriteImageButton.setImageResource(R.drawable.ic_favorites_unchecked);


        //TODO mettre à jour le pannel

        //mini maps

        //schedules

        //optionals  pictures


        this.showPanel();

    }

    public Boolean hideDetailPanel(){
        if(!this.isPanelHidden()) {
            this.hidePanel();
            return true;
        }else{
            return false;
        }

    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec){
        //ce sont les dimensions en pixels sans la action bar
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        wideHeight = MeasureSpec.getSize(heightMeasureSpec);

        if(!isSetup)
            try {
                setup();
            } catch (DetailSlidingUpPanelLayoutNullActivity detailSlidingUpPanelLayoutNullActivity) {
                detailSlidingUpPanelLayoutNullActivity.printStackTrace();
            }
    }


    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }


    public Boolean isAnchoredOrExpanded(){
        return this.isPanelExpanded() || this.isPanelAnchored();
    }


}
