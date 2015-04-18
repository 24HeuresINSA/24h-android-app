package com.insalyon.les24heures.filter;

import com.insalyon.les24heures.adapter.ResourceAdapter;
import com.insalyon.les24heures.model.Resource;

import java.util.ArrayList;

/**
 * Created by remi on 20/01/15.
 */
public class ResourceSimpleAdapterCategoryFilter<T extends Resource> extends ResourceCategoryFilter<T> {
    ResourceAdapter resourceAdapter;


    public ResourceSimpleAdapterCategoryFilter(ArrayList<T> originalList, ArrayList<T> dayResourceList, ResourceAdapter resourceAdapter) {
        super(originalList, dayResourceList);
        this.resourceAdapter = resourceAdapter;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence constraint,
                                  FilterResults results) {

        resourceList.clear();
        resourceList.addAll((ArrayList<T>) results.values);
        resourceAdapter.notifyDataSetChanged();
        resourceAdapter.clear(); //will clear resourceAdapter.resourceList
        for (int i = 0, l = resourceList.size(); i < l; i++) {
            resourceAdapter.add(resourceList.get(i)); //will populate resourceAdapter.resourceList
        }
        resourceAdapter.notifyDataSetInvalidated(this);
    }
}
