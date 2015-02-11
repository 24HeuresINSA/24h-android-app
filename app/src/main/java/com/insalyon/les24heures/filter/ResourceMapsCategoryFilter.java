package com.insalyon.les24heures.filter;

import com.insalyon.les24heures.fragments.OutputMapsFragment;
import com.insalyon.les24heures.model.DayResource;

import java.util.ArrayList;

/**
 * Created by remi on 20/01/15.
 */
public class ResourceMapsCategoryFilter extends ResourceCategoryFilter {


    OutputMapsFragment outputMapsFragment;

    public ResourceMapsCategoryFilter(ArrayList<DayResource> originalList, ArrayList<DayResource> dayResourceList, OutputMapsFragment outputMapsFragment) {
        super(originalList, dayResourceList);
        this.outputMapsFragment = outputMapsFragment;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        dayResourceList.clear();
        dayResourceList.addAll((ArrayList<DayResource>) results.values);

        for (DayResource dayResource : originalList) {
            dayResource.getMarker().setVisible(false);
        }
        for (DayResource dayResource : dayResourceList) {
            dayResource.getMarker().setVisible(true);
        }
        outputMapsFragment.moveCamera();
    }
}
