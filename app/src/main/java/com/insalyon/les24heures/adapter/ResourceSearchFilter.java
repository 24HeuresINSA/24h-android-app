package com.insalyon.les24heures.adapter;

import android.widget.Filter;

import com.insalyon.les24heures.model.Resource;

import java.util.ArrayList;

/**
 * Created by remi on 27/12/14.
 */
public class ResourceSearchFilter extends ResourceListFilter {


    public ResourceSearchFilter(ArrayList<Resource> originalList, ArrayList<Resource> resourceList, ResourceAdapter resourceAdapter) {
        super(originalList, resourceList, resourceAdapter);
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        FilterResults result = new FilterResults();
        if(constraint != null && constraint.toString().length() > 0)
        {
            constraint = constraint.toString().toLowerCase();
            ArrayList<Resource> filteredItems = new ArrayList<Resource>();

            for(int i = 0, l = originalList.size(); i < l; i++)
            {
                Resource resource = originalList.get(i);
                //effective search pattern
                if(resource.getTitle().toString().toLowerCase().contains(constraint))
                    filteredItems.add(resource);
            }
            result.count = filteredItems.size();
            result.values = filteredItems;
        }
        else
        {
            synchronized(this)
            {
                result.values = new ArrayList<>(originalList);
                result.count = originalList.size();
            }
        }
        return result;
    }


}
