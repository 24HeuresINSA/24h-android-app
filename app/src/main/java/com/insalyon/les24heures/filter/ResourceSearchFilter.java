package com.insalyon.les24heures.filter;

import android.widget.Filter;

import com.insalyon.les24heures.model.DayResource;

import java.util.ArrayList;

/**
 * Created by remi on 27/12/14.
 */
public abstract class ResourceSearchFilter extends Filter {

    ArrayList<DayResource> originalList;
    ArrayList<DayResource> dayResourceList;


    public ResourceSearchFilter(ArrayList<DayResource> originalList, ArrayList<DayResource> dayResourceList) {
        //we need pointer to inform the array adapter of what we are doing
        this.originalList = originalList;
        this.dayResourceList = dayResourceList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        FilterResults result = new FilterResults();
        if (constraint != null && constraint.toString().length() > 0) {
            constraint = constraint.toString().toLowerCase();
            ArrayList<DayResource> filteredItems = new ArrayList<DayResource>();

            for (int i = 0, l = originalList.size(); i < l; i++) {
                DayResource dayResource = originalList.get(i);
                //effective search pattern
                if (dayResource.getTitle().toString().toLowerCase().contains(constraint))
                    filteredItems.add(dayResource);
            }
            result.count = filteredItems.size();
            result.values = filteredItems;
        } else {
            synchronized (this) {
                result.values = new ArrayList<>(originalList);
                result.count = originalList.size();
            }
        }
        return result;
    }


}
