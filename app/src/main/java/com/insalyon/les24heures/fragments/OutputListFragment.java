package com.insalyon.les24heures.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.felipecsl.quickreturn.library.QuickReturnAttacher;
import com.felipecsl.quickreturn.library.widget.QuickReturnAdapter;
import com.felipecsl.quickreturn.library.widget.QuickReturnTargetView;
import com.insalyon.les24heures.MainActivity;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.adapter.ResourceAdapter;
import com.insalyon.les24heures.eventbus.CategoriesSelectedEvent;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.model.Resource;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by remi on 26/12/14.
 */
public class OutputListFragment extends OutputTypeFragment{
    private static final String TAG = OutputListFragment.class.getCanonicalName();

    View view;
    @InjectView(R.id.list_sort_alphabetical)
    View alphabeticalSort;
    @InjectView(R.id.list_sort_loc)
    View locSort;
    @InjectView(R.id.list_sort_time_loc)
    View timeLocSort;
    @InjectView(R.id.list_search_text)
    TextView searchText;
    //quickreturnbetter
    @InjectView(R.id.list_resource)
    ListView resourceListView;
//    QuickReturnListView resourceListView;
    @InjectView(R.id.empty_resource_list)
    View emptyResourceList;
    @InjectView(R.id.fab_goto_maps)
    FloatingActionButton fabGotoMaps;


    Boolean spinner = false; //TODO mettre en place un vrai spinner
    //coup de bluff pour contrecarrer le setText effectué par Android au rotate
    Boolean startingForText;

    ResourceAdapter resourceAdapter = null;

    //quickReturn
    //https://github.com/LarsWerkman/QuickReturnListView
    @InjectView(R.id.listView_header)
    View mQuickReturnView;
    private View mPlaceHolder;
    private View headerResourceList;
    private int mCachedVerticalScrollRange;
    private int mQuickReturnHeight;
    private static final int STATE_ONSCREEN = 0;
    private static final int STATE_OFFSCREEN = 1;
    private static final int STATE_RETURNING = 2;
    private int mState = STATE_ONSCREEN;
//    private int previousTranslationYList;
//    private int deltatRawY;
    private int mScrollY;
    private int mMinRawY = 0;
    private TranslateAnimation anim;

    //quickreturnBetter
    //https://github.com/felipecsl/QuickReturn
    private QuickReturnAttacher quickReturnAttacher;
    private View quickReturnTarget;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("fragment list onOptionsItemSelected",item.getTitle().toString());

        //mettre ca dans la mere, juste appeler des methodes presentes dans les filles pour afficher les fav et filrer par title
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.output_list_fragment, container, false);
        ButterKnife.inject(this, view);

        setHasOptionsMenu(true);

        //quickReturn
//        headerResourceList = inflater.inflate(R.layout.output_list_header, null);
//        mPlaceHolder = headerResourceList.findViewById(R.id.placeholder);

        //quickreturnBetter
        // the quick return target view to be hidden/displayed
        quickReturnTarget =  view.findViewById(R.id.listView_header);

        //create an ArrayAdaptar from the String Array
        resourceAdapter = new ResourceAdapter(this.getActivity().getApplicationContext(),
                R.layout.output_list_item, new ArrayList<>(resourcesList)); //no need of a pointer, ResourceAdapter takes care of its data via event and filter
        // Assign adapter to ListView
//        resourceListView.setAdapter(resourceAdapter);
        //quickreturnbetter
        // Wrap your adapter with QuickReturnAdapter
        resourceListView.setAdapter(new QuickReturnAdapter(resourceAdapter));

        //enables filtering for the contents of the given ListView
        resourceListView.setTextFilterEnabled(true);

        resourceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Resource resource = (Resource) parent.getItemAtPosition(position);
                //TODO details cf DSF
                Toast.makeText(getActivity().getApplicationContext(),
                        resource.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        //doesn't work anymore because of quicketurn listview
        resourceListView.setEmptyView(emptyResourceList);

        //quickreturn
//        resourceListView.addHeaderView(headerResourceList);







        return view;
    }

    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        public void onGlobalLayout() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            } else {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }

            // Add a quick return targetView to the attacher.
            // You can pass a position argument (POSITION_TOP or POSITION_BOTTOM).
            // You can also optionally pass the size of the target view, which will be used to
            // offset the list height, preventing it from hiding content behind the target view.
            quickReturnAttacher.addTargetView(quickReturnTarget, QuickReturnTargetView.POSITION_TOP, quickReturnTarget.getHeight());

            //c'est le quickreturn ou le fab qui se hide au scroll car les deux font un setOnScrollListener
            //mais je dois fork les deux projets...
            //http://stackoverflow.com/questions/25811458/how-to-bind-several-scroll-listener-on-a-listview
            //fabGotoMaps.attachToListView(resourceListView);
            //plus simple, ne fork que quickreturn ou utiliser celui de LarsWerkman et call fabGotoMaps.hide(); ou fabGotoMaps.show();



        }
    };




    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity)getActivity()).setTitle(R.string.drawer_outputtype_list);

    }

    @Override
    public void onResume() {
        super.onResume();
//        setQuickReturn();
//        if(!resourcesList.isEmpty())setTree();

        //quickreturnbetter
        // Attach a QuickReturnAttacher, which takes care of all of the hide/show functionality.
        quickReturnAttacher = QuickReturnAttacher.forView(resourceListView);

        view.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);


        if(!searchText.getText().toString().equals("")) resourceAdapter.getFilter().filter(searchText.getText().toString());


        //filter by text
        //have to here in order to prevent being fired by Android itself when it repopulate textView
        searchText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                resourceAdapter.getFilter().filter(s.toString());
            }
        });
    }


    /**     Fragment is alive       **/
    //filter by categories
    public void onEvent(CategoriesSelectedEvent event) {
        super.onEvent(event);
        Log.d(TAG+"onEvent(CategoryEvent)", event.getCategories().toString());
        resourceAdapter.getCategoryFilter().filter(
                (event.getCategories().size() != 0) ? event.getCategories().toString() : null
        );

    }

    public void onEvent(ResourcesUpdatedEvent event) {
        super.onEvent(event);
        updateListView();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(spinner){
                    spinner = false;
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.resources_found, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

//        setQuickReturn();
//        setTree();
    }

    @OnClick(R.id.fab_goto_maps)
    public void onClickFabGotoMaps(View v){
        ((MainActivity)getActivity()).selectMaps();
    }

    /**     Fragment is no more alive       **/
    @Override
    public void onPause() {
        super.onPause();
        resourceListView.setOnScrollListener(null);
    }


    /**     Fragment methods        **/
    //default filter
    private Boolean updateListView(){

        //TODO text search or categories filter priority stand here
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startingForText = true;
                searchText.setText("");
            }
        });

        //category is prior, text search is ignored and erased
        resourceAdapter.getCategoryFilter().filter(
                (categoriesSelected.size() != 0) ? categoriesSelected.toString() : null);

        return true;
    }

//    //quickreturn listview
//    private void setQuickReturn() {
//        resourceListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @SuppressLint("NewApi")
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem,
//                                 int visibleItemCount, int totalItemCount) {
//
//                mScrollY = 0;
//                int translationY = 0;
////                int translationYList = 0;
//
//                if (resourceListView.scrollYIsComputed()) {
//                    mScrollY = resourceListView.getComputedScrollY();
//                }
//
//                int rawY = mPlaceHolder.getTop()
//                        - Math.min(
//                        mCachedVerticalScrollRange
//                                - resourceListView.getHeight(), mScrollY);
//
//                switch (mState) {
//                    case STATE_OFFSCREEN:
//                        if (rawY <= mMinRawY) {
//                            mMinRawY = rawY;
//                        } else {
//                            mState = STATE_RETURNING;
//                        }
//                        translationY = rawY;
//
////                        Log.d("STATE_OFFSCREEN", "" + translationY);
////                        //liste en buté haute
////                        translationYList = 0;
//                        break;
//
//                    case STATE_ONSCREEN:
//                        if (rawY < -mQuickReturnHeight) {
//                            mState = STATE_OFFSCREEN;
//                            mMinRawY = rawY;
//                        }
//                        translationY = rawY;
//
////                        Log.d("STATE_ONSCREEN", "" + translationY);
////                        //list en train de remonter pour pousser le output_list_header
////                        translationYList = rawY + mQuickReturnHeight;
//                        break;
//
//                    case STATE_RETURNING:
//                        translationY = (rawY - mMinRawY) - mQuickReturnHeight;
//                        if (translationY > 0) {
//                            translationY = 0;
//                            mMinRawY = rawY - mQuickReturnHeight;
//                        }
//
//                        if (rawY > 0) {
//                            mState = STATE_ONSCREEN;
//                            translationY = rawY;
//                        }
//
//                        if (translationY < -mQuickReturnHeight) {
//                            mState = STATE_OFFSCREEN;
//                            mMinRawY = rawY;
//                        }
//
////                        Log.d("STATE_RETURNING", "" + translationY);
////                        if(translationY == 0){
////                            //liste en butée basse
////                            translationYList = mQuickReturnHeight;
////                        }else{
////                            //liste en train de descendre pour faire apparaitre les filtres
////                            //translationYList = rawY ;//- mQuickReturnHeight;
////                        }
//                        break;
//                }
//
//                /** this can be used if the build is below honeycomb **/
//                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
//                    anim = new TranslateAnimation(0, 0, translationY,
//                            translationY);
//                    anim.setFillAfter(true);
//                    anim.setDuration(0);
//                    mQuickReturnView.startAnimation(anim);
//                } else {
//                    mQuickReturnView.setTranslationY(translationY);
//                }
//
//            }
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//            }
//        });
//    }
//
//    //quickreturn listview
//    private void setTree() {
//        resourceListView.getViewTreeObserver().addOnGlobalLayoutListener(
//                new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        mQuickReturnHeight = mQuickReturnView.getHeight();
//                        resourceListView.computeScrollY();
//                        mCachedVerticalScrollRange = resourceListView.getListHeight();
//                    }
//                });
//    }

}
