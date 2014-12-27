package com.insalyon.les24heures.adapter;

import android.widget.Filter;

import com.insalyon.les24heures.model.Resource;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by remi on 27/12/14.
 */
public class ResourceCategoryFilter extends Filter {

    private ArrayList<Resource> originalList;
    private ArrayList<Resource> resourceList;
    ResourceAdapter resourceAdapter;

    public ResourceCategoryFilter(ArrayList<Resource> originalList, ArrayList<Resource> resourceList, ResourceAdapter resourceAdapter) {
        this.originalList = originalList;
        this.resourceList = resourceList;
        this.resourceAdapter = resourceAdapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults result = new FilterResults();


        if (constraint != null) {
            ArrayList<String> selectedCategories =
                    new ArrayList<>(Arrays.asList(
                            ((String) constraint).substring(1, constraint.length() - 1).split(", "))
                    );

            //TODO faire ca proprement
            if (selectedCategories.size() != 0 || selectedCategories.get(0) != "") {
                ArrayList<Resource> filteredItems = new ArrayList<Resource>();

                for (int i = 0, l = originalList.size(); i < l; i++) {
                    Resource resource = originalList.get(i);
                    //effective search pattern
                    if (selectedCategories.contains(resource.getCategory().toString()))
                        filteredItems.add(resource);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;

            } else {
                synchronized (this) {
                    result.values = originalList;
                    result.count = originalList.size();
                }
            }
        } else {
            synchronized (this) {
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
        resourceList.clear();
        resourceList.addAll((ArrayList<Resource>)results.values);
        resourceAdapter.notifyDataSetChanged();
        resourceAdapter.clear();
        for (int i = 0, l = resourceList.size(); i < l; i++)
            resourceAdapter.add(resourceList.get(i));
        resourceAdapter.notifyDataSetInvalidated();
    }
}
