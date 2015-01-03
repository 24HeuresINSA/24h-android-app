package com.insalyon.les24heures.adapter;

import android.widget.Filter;

import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.utils.FilterAction;

import java.util.ArrayList;

/**
 * Created by remi on 03/01/15.
 */
public abstract class ResourceListFilter extends Filter{

    ArrayList<Resource> originalList;
    ArrayList<Resource> resourceList;
    ResourceAdapter resourceAdapter;

    public ResourceListFilter(ArrayList<Resource> originalList, ArrayList<Resource> resourceList, ResourceAdapter resourceAdapter) {
        //we need pointer to inform the array adapter of what we are doing
        this.originalList = originalList;
        this.resourceList = resourceList;
        this.resourceAdapter = resourceAdapter;
    }



    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence constraint,
                                  FilterResults results) {

        resourceList.clear();
        resourceList.addAll((ArrayList<Resource>)results.values);
        resourceAdapter.notifyDataSetChanged();
        resourceAdapter.clear(); //will clear resourceAdapter.resourceList
        for(int i = 0, l = resourceList.size(); i < l; i++) {
            resourceAdapter.add(resourceList.get(i)); //will populate resourceAdapter.resourceList
        }
        resourceAdapter.notifyDataSetInvalidated();
    }
}
