package com.insalyon.les24heures.filter;

import android.widget.Filter;

import com.insalyon.les24heures.model.Resource;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by remi on 27/12/14.
 */
public abstract class ResourceCategoryFilter extends Filter {

    ArrayList<Resource> originalList;
    ArrayList<Resource> resourceList;

    ArrayList<String> selectedCategories;

    public ResourceCategoryFilter(ArrayList<Resource> originalList, ArrayList<Resource> resourceList) {
        //we need pointer to inform the array adapter of what we are doing
        this.originalList = originalList;
        this.resourceList = resourceList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults result = new FilterResults();

        if (constraint != null) {
            selectedCategories =
                    new ArrayList<>(Arrays.asList(
                            ((String) constraint).substring(1, constraint.length() - 1).split(", "))
                    );

            if (selectedCategories.size() != 0) {
                ArrayList<Resource> filteredItems = new ArrayList<Resource>();

                for (int i = 0, l = originalList.size(); i < l; i++) {
                    Resource resource = originalList.get(i);
                    //effective search pattern
                    if (isDisplayable(resource))
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
                result.values = new ArrayList<>(originalList);
                result.count = originalList.size();
            }
        }
        return result;
    }


    private Boolean isDisplayable(Resource resource) {
        if (selectedCategories.contains("favorites")) {
            if (selectedCategories.size() == 1)
                return resource.isFavorites();
            return resource.isFavorites() &&
                    (selectedCategories.contains(resource.getCategory().toString()));
        }
        return selectedCategories.contains(resource.getCategory().toString());
    }

}
