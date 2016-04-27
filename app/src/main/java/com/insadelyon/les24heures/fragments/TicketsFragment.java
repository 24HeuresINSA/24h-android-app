package com.insadelyon.les24heures.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.insadelyon.les24heures.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by remi on 19/04/15.
 */
public class TicketsFragment extends Fragment {

    @InjectView(R.id.billeterie_desc_1)
    TextView billet_desc;


    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.tickets_fragment, container, false);
        ButterKnife.inject(this,view);

        String text = "Pour <b><font color='#1b417a'>éviter l'attente</font></b> aux caisses les soirs du festival, et pour <b><font color='#1b417a'>être sûr d'avoir une place</font></b>, achetez votre billet sur Internet et <b><font color='#1b417a'>profitez de tarifs préférentiels</font></b>.";
        billet_desc.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);

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
