package com.insalyon.les24heures.adapter;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.insalyon.les24heures.R;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.model.NightResource;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by remi on 27/12/14.
 */
public class NightResourceAdapter extends ResourceAdapter<NightResource> {

    private final EventBus eventBus;
    private final int viewId;

    LayoutInflater vi;
    Location lastKnownPosition;

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
            holder.schedule = (TextView) convertView.findViewById(R.id.artist_grid_item_schedule_text);
            holder.favorites = (ImageButton) convertView.findViewById(R.id.artist_grid_item_favorite);
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
                    ((ImageButton) v).setImageResource(R.drawable.ic_favorites_checked);
                else
                    ((ImageButton) v).setImageResource(R.drawable.ic_favorites_unchecked);
            }
        });

        holder.title.setText(nightResource.getTitle());
        holder.title.setSelected(true);
        if (nightResource.isFavorites())
            holder.favorites.setImageResource(R.drawable.ic_favorites_checked);
        else
            holder.favorites.setImageResource(R.drawable.ic_favorites_unchecked);
        holder.schedule.setText(nightResource.printSchedules());


        return convertView;

    }

    public void onEvent(ResourcesUpdatedEvent event) {
        originalList.clear();
        originalList.addAll(event.getNightResourceList());
    }

    private class ViewHolder {
        TextView title;
        TextView schedule;
        ImageButton favorites;
    }


}