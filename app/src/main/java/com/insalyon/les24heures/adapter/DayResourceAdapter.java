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
import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.utils.AlphabeticalReverseSortComparator;
import com.insalyon.les24heures.utils.AlphabeticalSortComparator;

import java.util.ArrayList;
import java.util.Collections;

import de.greenrobot.event.EventBus;

/**
 * Created by remi on 27/12/14.
 */
public class DayResourceAdapter extends ResourceAdapter<DayResource> {

    private final EventBus eventBus;
    private final int viewId;

    LayoutInflater vi;
    Location lastKnownPosition;

    public DayResourceAdapter(Context context, int textViewResourceId,
                              ArrayList<DayResource> dayResources, Location lastKnownPosition) {
        super(context, textViewResourceId, dayResources);
        this.lastKnownPosition = lastKnownPosition;
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
            holder.title = (TextView) convertView.findViewById(R.id.list_item_title_text);
            holder.distance = (TextView) convertView.findViewById(R.id.list_item_distance_text);
            holder.schedule = (TextView) convertView.findViewById(R.id.list_item_schedule_text);
            holder.favorites = (ImageButton) convertView.findViewById(R.id.list_item_favorite);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final DayResource dayResource = resourceList.get(position);

        holder.favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dayResource.setIsFavorites(!dayResource.isFavorites());
                if (dayResource.isFavorites())
                    ((ImageButton) v).setImageResource(R.drawable.ic_favorites_checked);
                else
                    ((ImageButton) v).setImageResource(R.drawable.ic_favorites_unchecked);
            }
        });

        holder.title.setText(dayResource.getTitle()+dayResource.getCategory());
        holder.title.setSelected(true);
        if (dayResource.isFavorites())
            holder.favorites.setImageResource(R.drawable.ic_favorites_checked);
        else
            holder.favorites.setImageResource(R.drawable.ic_favorites_unchecked);
        holder.schedule.setText(dayResource.printSchedules());
        Location loc = new Location("loc");
        loc.setLongitude(dayResource.getLoc().longitude);
        loc.setLatitude(dayResource.getLoc().latitude);
        if (lastKnownPosition != null) {
            Integer distance = Math.round(lastKnownPosition.distanceTo(loc));
            holder.distance.setText((distance < 1000) ? distance + "m" : distance / 1000 + "km");
        } else {
            holder.distance.setText(R.string.list_no_last_known_location);
        }


        return convertView;

    }

    public void onEvent(ResourcesUpdatedEvent event) {
        Log.d("onEvent(ResourcesUpdatedEvent)", event.getDayResourceList().toString());
        originalList.clear();
        originalList.addAll(event.getDayResourceList());
        notifyDataSetChanged();
    }

    public void sortAZ(){
        Collections.sort(resourceList, new AlphabeticalSortComparator());
        Collections.sort(originalList, new AlphabeticalSortComparator());
        this.notifyDataSetChanged();
    }

    public void sortZA(){
        Collections.sort(resourceList, new AlphabeticalReverseSortComparator());
        Collections.sort(originalList, new AlphabeticalReverseSortComparator());
        this.notifyDataSetChanged();
    }

    public void sortLoc(){
        //TODO sort loc
    }

    public void sortTimeLoc(){
        //TODO sort time loc
    }



    private class ViewHolder {
        TextView title;
        TextView distance;
        TextView schedule;
        ImageButton favorites;
    }


}