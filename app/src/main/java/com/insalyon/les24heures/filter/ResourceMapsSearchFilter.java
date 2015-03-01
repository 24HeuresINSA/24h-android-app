package com.insalyon.les24heures.filter;

import com.google.android.gms.maps.model.Marker;
import com.insalyon.les24heures.fragments.OutputMapsFragment;
import com.insalyon.les24heures.model.DayResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by remi on 20/01/15.
 */
public class ResourceMapsSearchFilter extends ResourceSearchFilter<DayResource> {

    OutputMapsFragment outputMapsFragment;
    Map<DayResource,Marker> resourceMarkerMap;


    public ResourceMapsSearchFilter(ArrayList<DayResource> originalList, ArrayList<DayResource> dayResourceList, OutputMapsFragment outputMapsFragment, HashMap<DayResource, Marker> resourceMarkerMap) {
        super(originalList, dayResourceList);
        this.outputMapsFragment = outputMapsFragment;
        this.resourceMarkerMap = resourceMarkerMap;

    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        resourceList.clear();
        resourceList.addAll((ArrayList<DayResource>) results.values);

        for (DayResource dayResource : originalList) {
            resourceMarkerMap.get(dayResource).setVisible(false);
        }

        for (DayResource dayResource : resourceList) {
            resourceMarkerMap.get(dayResource).setVisible(true);
        }


        outputMapsFragment.moveCamera();
    }
}
