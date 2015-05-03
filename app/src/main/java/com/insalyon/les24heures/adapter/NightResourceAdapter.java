package com.insalyon.les24heures.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.insalyon.les24heures.R;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.model.NightResource;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by remi on 27/12/14.
 */
public class NightResourceAdapter extends ResourceAdapter<NightResource> {

    private final EventBus eventBus;
    private final int viewId;

    LayoutInflater vi;

    public NightResourceAdapter(Context context, int textViewResourceId,
                                ArrayList<NightResource> dayResources) {
        super(context, textViewResourceId, dayResources);
        this.viewId = textViewResourceId;

        this.vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //je voulais pas ca moi !
        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            convertView = vi.inflate(viewId, null);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.artist_grid_item_title_text);
            holder.favorites = (ImageButton) convertView.findViewById(R.id.artist_grid_item_favorite);
            holder.image = (ImageView) convertView.findViewById(R.id.artist_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final NightResource nightResource = resourceList.get(position);

        holder.favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nightResource.setIsFavorites(!nightResource.isFavorites());
                if (nightResource.isFavorites())
                    ((ImageButton) v).setImageResource(R.drawable.ic_action_favorite);
                else
                    ((ImageButton) v).setImageResource(R.drawable.ic_action_favorite_uncheck);
            }
        });

        holder.title.setText(nightResource.getTitle());

        //TODO complete with real data : loading to long with backend img
        switch(nightResource.getTitle()){
            case "Goldfish":
                holder.image.setImageResource(R.drawable.fish);
                break;
            case "John":
                holder.image.setImageResource(R.drawable.ecocup);
                break;
            default:
                holder.image.setImageResource(R.drawable.frites);
                break;
        }
        holder.title.setSelected(true);
        if (nightResource.isFavorites())
            holder.favorites.setImageResource(R.drawable.ic_action_favorite);
        else
            holder.favorites.setImageResource(R.drawable.ic_action_favorite_uncheck);

        return convertView;

    }

    public void onEvent(ResourcesUpdatedEvent event) {
        originalList.clear();
        originalList.addAll(event.getNightResourceList());
    }

    private class ViewHolder {
        ImageView image;
        TextView title;
        TextView schedule;
        ImageButton favorites;
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

}