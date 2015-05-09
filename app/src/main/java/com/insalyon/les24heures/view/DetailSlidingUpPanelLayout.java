package com.insalyon.les24heures.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.insalyon.les24heures.BaseDynamicDataActivity;
import com.insalyon.les24heures.DayActivity;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.eventbus.ManageDetailSlidingUpDrawer;
import com.insalyon.les24heures.fragments.DetailFragment;
import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.model.NightResource;
import com.insalyon.les24heures.utils.DetailSlidingUpPanelLayoutNullActivity;
import com.insalyon.les24heures.utils.SlidingUpPannelState;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import de.greenrobot.event.EventBus;

/**
 * Created by remi on 04/02/15.
 */
//TODO minor pull an interface to get a SlidingUpPanelLayout with fragment inside and parallax header manage
public class DetailSlidingUpPanelLayout extends SlidingUpPanelLayout {
    private static final String TAG = DetailSlidingUpPanelLayout.class.getCanonicalName();

    EventBus eventBus;


    private DetailScrollView detailScrollView;
    private TextView nextSchedule;
    private ImageButton favoriteImageButton;
    private TextView detailSlidingTitle;
    private TextView detailSlidingDescription;
    private LinearLayout detailSlidingHeaderLabel;
    private RelativeLayout detailSlidingLayoutHeader;

    private View parallaxHeader;
    private DrawerArrowDrawable drawerArrowDrawable;


    private Integer wideHeight;
    private BaseDynamicDataActivity activity;
    private boolean isSetup = false;


    private float anchored;
    private int scrollingHeaderHeight;
    private Integer headerWidth;
    private Double finalXFavPosition;


    private PanelSlideListener panelSlideListener = new PanelSlideListener() {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            setFavoriteMovesParams();

            float newParallaxHeaderPos;
            float parallaxContentFrame = -self.getCurrentParalaxOffset();

            if (slideOffset <= 0) {//from visible to hidden and vice versa
                //parallax
                if (parallaxHeader.getVisibility() != INVISIBLE)
                    parallaxHeader.setVisibility(INVISIBLE);

            } else if (slideOffset < anchored) { //from visible to anchored and vice versa
                //parallax
                newParallaxHeaderPos = (wideHeight - scrollingHeaderHeight) * (1 - slideOffset / (anchored)) - 2;
                newParallaxHeaderPos = newParallaxHeaderPos + parallaxContentFrame;
                if (parallaxHeader.getVisibility() == INVISIBLE && !parallaxHeader.isSelected()) //parallaxHeader.selected is set to true by DetailFragment when no pictures are to be displayed
                    parallaxHeader.setVisibility(VISIBLE);
                parallaxHeader.setTranslationY(newParallaxHeaderPos);

                //arrowDrawable
                float param = 1 / anchored * slideOffset;
                if (param < 0) param = 0;
                else if (param > 1) param = 1;

                drawerArrowDrawable.setParameter(param);


                nextSchedule.setAlpha(1 - slideOffset / anchored);

                float a = (float) ((finalXFavPosition - headerWidth) / anchored);
                float b = headerWidth;
                float alpha = slideOffset * a + b;
                favoriteImageButton.setX(alpha);
                Log.v("DETAIL FAVORITES X", alpha + "");

                ViewGroup.LayoutParams params = detailSlidingHeaderLabel.getLayoutParams();
                params.width = (int) alpha;
                detailSlidingHeaderLabel.setLayoutParams(params);

            } else {      //from anchored to expand and vice versa
                drawerArrowDrawable.setParameter(1);
            }

            if (slideOffset == 0) {
                detailScrollView.setIsScrollEnable(true);
            }

        }

        @Override
        public void onPanelExpanded(View panel) {
            setFavoriteMovesParams();

            detailScrollView.setIsScrollEnable(true);
            activity.getActionBar().setTitle("");   //=> hide title in detail

            anchoredUIState();

            activity.customOnOptionsMenu();
        }


        @Override
        public void onPanelCollapsed(View panel) {
            setFavoriteMovesParams();

            detailScrollView.setIsScrollEnable(false);
            detailScrollView.fullScroll(ScrollView.FOCUS_UP);

            collapseUIState();

            activity.customOnOptionsMenu();
            activity.restoreTitle();
        }

        @Override
        public void onPanelAnchored(View panel) {
            setFavoriteMovesParams();

            detailScrollView.setIsScrollEnable(false);

            anchoredUIState();

            activity.getActionBar().setTitle("");   //=> hide title in detail
            activity.customOnOptionsMenu();

            //if anchored without touching header, need update
            detailFragment.updateHeavyData();
        }

        @Override
        public void onPanelHidden(View panel) {
            setFavoriteMovesParams();

            collapseUIState();
            activity.customOnOptionsMenu();

            eventBus.post(new ManageDetailSlidingUpDrawer(SlidingUpPannelState.HIDE, (NightResource) null));
        }

        private void anchoredUIState() {
            favoriteImageButton.setX(finalXFavPosition.floatValue());
        }

        private void collapseUIState() {
            favoriteImageButton.setX(self.getWidth());
            drawerArrowDrawable.setParameter(0);
        }

    };
    private DetailSlidingUpPanelLayout self;
    private DetailFragment detailFragment;


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
     *
     * @return
     */
    public Boolean setup() throws DetailSlidingUpPanelLayoutNullActivity {

        if (wideHeight == null)
            return false;
        if (activity == null) {
            throw new DetailSlidingUpPanelLayoutNullActivity();
        }

        self = this;

        findDetailView();
        eventBus = EventBus.getDefault(); //pas ouf, mettre dans le constructeur par défaut ?

        drawerArrowDrawable = activity.getDrawerArrowDrawable();

        //get params
        anchored = Float.parseFloat(getResources().getString(R.string.detail_anchored));

        scrollingHeaderHeight = (int) getResources().getDimension(R.dimen.detail_header_height);
        final int parallaxHeight = (int) ((wideHeight - scrollingHeaderHeight) * (1 - anchored));//407; //paralax height

        //set parallaxHeader
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) parallaxHeader.getLayoutParams();
        params.height = parallaxHeight + 2;
        parallaxHeader.setLayoutParams(params);
        parallaxHeader.setTranslationY(wideHeight);


        //setup
        this.setAnchorPoint(anchored);
        this.hidePanel();  //2.0.4 sera remplacé par mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN); à la prochaine release
//        this.setDragView(wholeSlidingLayout); //default but to be clear
        detailScrollView.setIsScrollEnable(false);

        this.setPanelSlideListener(panelSlideListener);

        isSetup = true;

        //setup previous sliding state
        String previousState = activity.getSlidingUpState();
        if (previousState != null) {
            switch (previousState) {
                case "hidden":
                    this.hidePanel();
                    break;
                case "anchored":
                    this.anchorPanel();
                    panelSlideListener.onPanelSlide(null, anchored - 0.001f); //pour provoquer le deplacement du paralax header
                    break;
                case "expanded":
                    this.expandPanel();
                    break;
                case "shown":
                    this.showPanel();
                    break;
            }
            detailFragment.notifyDataChanged((DayResource) null);
            //il faut le faire pour NightResource aussi, mais comment..., activity le sait...
        }

        if(detailFragment.getResource() != null) {
            this.anchorPanel();
        }

        return true;
    }

    private void setFavoriteMovesParams() {
        this.setOverlayed(false);
        //get params for favorite moves
        if (headerWidth == null) headerWidth = self.getWidth();
        if (finalXFavPosition == null) { //pour recuperer la position settée dans le layout
            finalXFavPosition = Double.valueOf(favoriteImageButton.getX());
        }
    }


    public void setDetailFragment(DetailFragment detailFragment) {
        this.detailFragment = detailFragment;
    }

    public void setParallaxHeader(View parallaxHeader) {
        this.parallaxHeader = parallaxHeader;
    }

    private void findDetailView() {
        detailScrollView = (DetailScrollView) detailFragment.getView().findViewById(R.id.detail_scrollView);
        nextSchedule = (TextView) detailFragment.getView().findViewById(R.id.detail_next_schedule);
        favoriteImageButton = (ImageButton) detailFragment.getView().findViewById(R.id.detail_favorites);
        detailSlidingTitle = (TextView) detailFragment.getView().findViewById(R.id.detail_sliding_title);
        detailSlidingDescription = (TextView) detailFragment.getView().findViewById(R.id.detail_description_text);
        detailSlidingHeaderLabel = (LinearLayout) detailFragment.getView().findViewById(R.id.detail_sliding_header_label);
        detailSlidingLayoutHeader = (RelativeLayout) detailFragment.getView().findViewById(R.id.detail_sliding_layout_header);
        detailSlidingLayoutHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean isMaps = false;
                if (activity.getClass().isAssignableFrom(DayActivity.class)
                        && ((DayActivity) activity).getmViewPager().getCurrentItem() == 0)
                    isMaps = true;

                if (self.isAnchoredOrExpanded()) {
                    if (isMaps)
                        self.collapsePanel();
                    else
                        self.hideDetailPanel();
                } else {
                    self.anchorPanel();
                }
            }
        });
    }


    public void showDetailPanel(DayResource res) {
        detailFragment.notifyDataChanged(res);
        this.showPanel();
    }

    public Boolean hideDetailPanel() {
        if (!this.isPanelHidden()) {
            this.hidePanel();
            return true;
        } else {
            return false;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //ce sont les dimensions en pixels sans la action bar
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        wideHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (!isSetup && detailFragment != null)
            try {
                setup();
            } catch (DetailSlidingUpPanelLayoutNullActivity detailSlidingUpPanelLayoutNullActivity) {
                detailSlidingUpPanelLayoutNullActivity.printStackTrace();
            }


    }


    public void setActivity(BaseDynamicDataActivity activity) {
        this.activity = activity;
    }


    public Boolean isAnchoredOrExpanded() {
        return this.isPanelExpanded() || this.isPanelAnchored();
    }


    public boolean isPanelAnchored() {
        return getPanelState() == PanelState.ANCHORED;
    }

    public boolean isPanelExpanded() {
        return getPanelState() == PanelState.EXPANDED;
    }

    public void hidePanel() {
        setPanelState(PanelState.HIDDEN);
    }

    public void collapsePanel() {
        setPanelState(PanelState.COLLAPSED);
    }

    public boolean isPanelHidden() {
        return getPanelState() == PanelState.HIDDEN;
    }

    public void anchorPanel() {
        setPanelState(PanelState.ANCHORED);
    }

    public void showPanel() {
        setPanelState(PanelState.COLLAPSED);
    }

    public void expandPanel() {
        setPanelState(PanelState.EXPANDED);
    }
}
