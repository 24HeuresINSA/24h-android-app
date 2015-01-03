package com.insalyon.les24heures.adapter;

import android.widget.Filter;

import com.insalyon.les24heures.model.Resource;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by remi on 27/12/14.
 */
public class ResourceCategoryFilter extends ResourceListFilter {


    public ResourceCategoryFilter(ArrayList<Resource> originalList, ArrayList<Resource> resourceList, ResourceAdapter resourceAdapter) {
        super(originalList, resourceList, resourceAdapter);
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults result = new FilterResults();


        if (constraint != null) {
            ArrayList<String> selectedCategories =
                    new ArrayList<>(Arrays.asList(
                            ((String) constraint).substring(1, constraint.length() - 1).split(", "))
                    );

            //TODO faire ca proprement (le get(0) != "")
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
                result.values =  new ArrayList<>(originalList);
                result.count = originalList.size();
            }
        }

        return result;
    }

}
