package com.insalyon.les24heures.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.insalyon.les24heures.R;

import butterknife.InjectView;

/**
 * Created by remi on 19/04/15.
 */
public class TclFragment extends Fragment {

    @InjectView(R.id.info_tcl)
    TextView infoTCL;


    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.tcl_fragment, container, false);

        String text = "Pour rentrer en toute sécurité, utilisez les <b><font color='#1b417a'>transports en communs !</font></b> Les horaires des arrêts sont indiqués à titre d'information.";
        infoTCL.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);


        return view;
    }
}
