package com.insalyon.les24heures.filter;

import android.widget.Filter;

import com.insalyon.les24heures.model.DayResource;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by remi on 27/12/14.
 */
public abstract class ResourceCategoryFilter extends Filter {

    ArrayList<DayResource> originalList;
    ArrayList<DayResource> dayResourceList;

    ArrayList<String> selectedCategories;

    public ResourceCategoryFilter(ArrayList<DayResource> originalList, ArrayList<DayResource> dayResourceList) {
        //we need pointer to inform the array adapter of what we are doing
        this.originalList = originalList;
        this.dayResourceList = dayResourceList;
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
                ArrayList<DayResource> filteredItems = new ArrayList<DayResource>();

                for (int i = 0, l = originalList.size(); i < l; i++) {
                    DayResource dayResource = originalList.get(i);
                    //effective search pattern
                    if (isDisplayable(dayResource))
                        filteredItems.add(dayResource);
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


    private Boolean isDisplayable(DayResource dayResource) {
        if (selectedCategories.contains("favorites")) {
            if (selectedCategories.size() == 1)
                return dayResource.isFavorites();
            return dayResource.isFavorites() &&
                    (selectedCategories.contains(dayResource.getCategory().toString()));
        }
        return selectedCategories.contains(dayResource.getCategory().toString());
    }

}
