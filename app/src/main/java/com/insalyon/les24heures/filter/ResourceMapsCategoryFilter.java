package com.insalyon.les24heures.filter;

import com.insalyon.les24heures.fragments.OutputMapsFragment;
import com.insalyon.les24heures.model.Resource;

import java.util.ArrayList;

/**
 * Created by remi on 20/01/15.
 */
public class ResourceMapsCategoryFilter extends ResourceCategoryFilter {


    OutputMapsFragment outputMapsFragment;

    public ResourceMapsCategoryFilter(ArrayList<Resource> originalList, ArrayList<Resource> resourceList, OutputMapsFragment outputMapsFragment) {
        super(originalList, resourceList);
        this.outputMapsFragment = outputMapsFragment;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        resourceList.clear();
        resourceList.addAll((ArrayList<Resource>) results.values);

        for (Resource resource : originalList) {
            resource.getMarker().setVisible(false);
        }
        for (Resource resource : resourceList) {
            resource.getMarker().setVisible(true);
        }
        outputMapsFragment.moveCamera();
    }
}
