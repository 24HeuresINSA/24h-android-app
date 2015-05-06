package com.insalyon.les24heures.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.model.DayResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by remi on 19/04/15.
 */
public class FacilitiesFragment extends Fragment {
    public static final String PREFS_NAME = "dataFile";

    private ArrayList<DayResource> resources;

    private View view;

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

        ((TextView)view.findViewById(R.id.textView2)).setText(resources.toString());


        return view;
    }
}
