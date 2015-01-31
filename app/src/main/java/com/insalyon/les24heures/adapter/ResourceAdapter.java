package com.insalyon.les24heures.adapter;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.insalyon.les24heures.R;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.filter.ResourceCategoryFilter;
import com.insalyon.les24heures.filter.ResourceListCategoryFilter;
import com.insalyon.les24heures.filter.ResourceListSearchFilter;
import com.insalyon.les24heures.filter.ResourceSearchFilter;
import com.insalyon.les24heures.model.Resource;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by remi on 27/12/14.
 */
public class ResourceAdapter extends ArrayAdapter<Resource>  {

    private final EventBus eventBus;
    private ArrayList<Resource> originalList;
    private ArrayList<Resource> resourceList;
    private ResourceSearchFilter resourceSearchFilter;
    private ResourceCategoryFilter resourceCategoryFilter;
    LayoutInflater vi;
    Location lastKnownPosition;

    public ResourceAdapter(Context context, int textViewResourceId,
                           ArrayList<Resource> resources, Location lastKnownPosition) {
        super(context, textViewResourceId, resources);
        this.resourceList = new ArrayList<>();
        this.resourceList.addAll(resources);
        this.originalList = new ArrayList<>();
        this.originalList.addAll(resources);
        this.lastKnownPosition = lastKnownPosition;

        this.vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //je voulais pas ca moi !
        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

    @Override
    public Filter getFilter() {
        if (resourceSearchFilter == null) {
            resourceSearchFilter = new ResourceListSearchFilter(originalList, resourceList, this);
        }
        return resourceSearchFilter;
    }


    public Filter getCategoryFilter() {
        if (resourceCategoryFilter == null) {
            resourceCategoryFilter = new ResourceListCategoryFilter(originalList, resourceList, this);
        }
        return resourceCategoryFilter;
    }


    private class ViewHolder {
        TextView title;
        TextView distance;
        TextView schedule;
        ImageButton favorites;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            convertView = vi.inflate(R.layout.output_list_item, null);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.list_item_title_text);
            holder.distance = (TextView) convertView.findViewById(R.id.list_item_distance_text);
            holder.schedule = (TextView) convertView.findViewById(R.id.list_item_schedule_text);
            holder.favorites = (ImageButton) convertView.findViewById(R.id.list_item_favorite);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Resource resource = resourceList.get(position);

        holder.favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resource.setIsFavorites(!resource.isFavorites());
                if(resource.isFavorites())
                    ((ImageButton) v).setImageResource(R.drawable.ic_favorites_checked);
                else
                    ((ImageButton) v).setImageResource(R.drawable.ic_favorites_unchecked);
            }
        });

        holder.title.setText(resource.getTitle());
        holder.title.setSelected(true);
        if(resource.isFavorites())
            holder.favorites.setImageResource(R.drawable.ic_favorites_checked);
        else
            holder.favorites.setImageResource(R.drawable.ic_favorites_unchecked);
        holder.schedule.setText(resource.printSchedules());
        Location loc = new Location("loc");
        loc.setLongitude(resource.getLoc().longitude);
        loc.setLatitude(resource.getLoc().latitude);
        if(lastKnownPosition != null){
            Integer distance = Math.round(lastKnownPosition.distanceTo(loc));
            holder.distance.setText((distance < 1000) ? distance+"m":distance/1000+"km");
        } else {
            holder.distance.setText(R.string.list_no_last_known_location);
        }



        return convertView;

    }




    public void onEvent(ResourcesUpdatedEvent event) {
        Log.d("onEvent(ResourcesUpdatedEvent)", event.getResourceList().toString());
        originalList.clear();
        originalList.addAll(event.getResourceList());
    }

    @Deprecated
    //il ne faut pas l'utiliser comme avec un arrayAdapter habituel, les filter s'occupe de notify if needed
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}