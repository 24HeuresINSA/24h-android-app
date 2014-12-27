package com.insalyon.les24heures.adapter;

import android.widget.Filter;

import com.insalyon.les24heures.model.Resource;

import java.util.ArrayList;

/**
 * Created by remi on 27/12/14.
 */
public class ResourceFilter extends Filter {

    private ArrayList<Resource> originalList;
    private ArrayList<Resource> resourceList;
    ResourceAdapter resourceAdapter;

    public ResourceFilter(ArrayList<Resource> originalList, ArrayList<Resource> resourceList, ResourceAdapter resourceAdapter) {
        this.originalList = originalList;
        this.resourceList = resourceList;
        this.resourceAdapter = resourceAdapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        constraint = constraint.toString().toLowerCase();
        FilterResults result = new FilterResults();
        if(constraint != null && constraint.toString().length() > 0)
        {
            ArrayList<Resource> filteredItems = new ArrayList<Resource>();

            for(int i = 0, l = originalList.size(); i < l; i++)
            {
                Resource resource = originalList.get(i);
                if(resource.getTitle().toString().toLowerCase().contains(constraint))
                    filteredItems.add(resource);
            }
            result.count = filteredItems.size();
            result.values = filteredItems;
        }
        else
        {
            synchronized(this)
            {
                result.values = originalList;
                result.count = originalList.size();
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence constraint,
                                  FilterResults results) {

        resourceList = (ArrayList<Resource>)results.values;
        resourceAdapter.notifyDataSetChanged();
        resourceAdapter.clear();
        for(int i = 0, l = resourceList.size(); i < l; i++)
            resourceAdapter.add(resourceList.get(i));
        resourceAdapter.notifyDataSetInvalidated();
    }
}
