package com.insalyon.les24heures.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.insalyon.les24heures.R;
import com.insalyon.les24heures.model.Resource;

import java.util.ArrayList;

/**
 * Created by remi on 27/12/14.
 */
public class ResourceAdapter extends ArrayAdapter<Resource> {

    private ArrayList<Resource> originalList;
    private ArrayList<Resource> resourceList;
    private ResourceSearchFilter resourceSearchFilter;
    private ResourceCategoryFilter resourceCategoryFilter;
    LayoutInflater vi;

    public ResourceAdapter(Context context, int textViewResourceId,
                           ArrayList<Resource> resources) {
        super(context, textViewResourceId, resources);
        this.resourceList = new ArrayList<Resource>();
        this.resourceList.addAll(resources);
        this.originalList = new ArrayList<Resource>();
        this.originalList.addAll(resources);
        this.vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public Filter getFilter() {
        if (resourceSearchFilter == null) {
            resourceSearchFilter = new ResourceSearchFilter(originalList,resourceList,this);
        }
        return resourceSearchFilter;
    }


    public Filter getCategoryFilter() {
        if (resourceCategoryFilter == null) {
            resourceCategoryFilter = new ResourceCategoryFilter(originalList,resourceList,this);
        }
        return resourceCategoryFilter;
    }


    private class ViewHolder {
        TextView title;
//        TextView description;
        TextView distance;
        TextView schedule;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));
        if (convertView == null) {


            convertView = vi.inflate(R.layout.list_item, null);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.list_item_title_text);
            holder.distance = (TextView) convertView.findViewById(R.id.list_item_distance_text);
            holder.schedule = (TextView) convertView.findViewById(R.id.list_item_schedule_text);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Resource resource = resourceList.get(position);
        holder.title.setText(resource.getTitle());
//        holder.description.setText(resource.getDescription());
        holder.distance.setText("TODO");
        holder.schedule.setText("TODO");

        return convertView;

    }
}