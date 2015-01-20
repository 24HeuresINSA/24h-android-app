package com.insalyon.les24heures.filter;

import com.insalyon.les24heures.model.Resource;

import java.util.ArrayList;

/**
 * Created by remi on 27/12/14.
 */
public abstract class ResourceSearchFilter extends ResourceFilter {

    public ResourceSearchFilter(ArrayList<Resource> originalList, ArrayList<Resource> resourceList) {
        super(originalList, resourceList);
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
