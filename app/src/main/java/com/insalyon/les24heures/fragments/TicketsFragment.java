package com.insalyon.les24heures.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.insalyon.les24heures.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by remi on 19/04/15.
 */
public class TicketsFragment extends Fragment {


    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.tickets_fragment, container, false);

        ButterKnife.inject(this,view);

        return view;
    }

    @OnClick(R.id.ticket_button)
    public void onButtonClick(View v){
        String url = getResources().getString(R.string.ticket_url);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
