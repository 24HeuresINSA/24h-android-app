package com.insalyon.les24heures.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.insalyon.les24heures.MainActivity;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.eventbus.CategoryEvent;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.utils.OutputType;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by remi on 26/12/14.
 */
public class OutputTypeFragment extends Fragment{
    EventBus eventBus;

    View view;
     ArrayList<Category> categoriesSelected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        eventBus = EventBus.getDefault();
        eventBus.register(this);

        if(savedInstanceState != null){
            if(savedInstanceState.getParcelableArrayList("categoriesSelected") != null){
                categoriesSelected = savedInstanceState.getParcelableArrayList("categoriesSelected");
            }else {
                categoriesSelected = new ArrayList<>();
            }
        }else if(getArguments() != null){
            //get from arguments
            if(getArguments().getParcelableArrayList("categoriesSelected") != null){
                categoriesSelected = getArguments().getParcelableArrayList("categoriesSelected");
            }else {
                categoriesSelected = new ArrayList<>();
            }

        } else {
            categoriesSelected = new ArrayList<>();
        }



    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //categories state
        outState.putParcelableArrayList("categoriesSelected",categoriesSelected);
    }

    public void onEvent(CategoryEvent event) {
        Log.d("onevent", event.getCategories().toString());
        categoriesSelected = (ArrayList<Category>) event.getCategories();
    }



    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
