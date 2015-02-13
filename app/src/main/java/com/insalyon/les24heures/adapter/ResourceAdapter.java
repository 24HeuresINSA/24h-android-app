package com.insalyon.les24heures.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.filter.ResourceCategoryFilter;
import com.insalyon.les24heures.filter.ResourceSearchFilter;
import com.insalyon.les24heures.filter.ResourceSimpleAdapterCategoryFilter;
import com.insalyon.les24heures.filter.ResourceSimpleAdapterSearchFilter;
import com.insalyon.les24heures.model.Resource;

import java.util.ArrayList;

/**
 * Created by remi on 13/02/15.
 */
public abstract class ResourceAdapter<T extends Resource> extends ArrayAdapter<T> {
    private ResourceSearchFilter resourceSearchFilter;
    private ResourceCategoryFilter resourceCategoryFilter;

    ArrayList<T> originalList;
    ArrayList<T> resourceList;


    public ResourceAdapter(Context context, int textViewResourceId,
                              ArrayList<T> resources) {
        super(context, textViewResourceId, resources);
        this.resourceList = new ArrayList<>();
        this.resourceList.addAll(resources);
        this.originalList = new ArrayList<>();
        this.originalList.addAll(resources);
//        this.lastKnownPosition = lastKnownPosition;

//        this.vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//        je voulais pas ca moi !
//        eventBus = EventBus.getDefault();
//        eventBus.register(this);
    }






    @Override
    public Filter getFilter() {
        if (resourceSearchFilter == null) {
            resourceSearchFilter = new ResourceSimpleAdapterSearchFilter<T>(originalList, resourceList, this);
        }
        return resourceSearchFilter;
    }


    public Filter getCategoryFilter() {
        if (resourceCategoryFilter == null) {
            resourceCategoryFilter = new ResourceSimpleAdapterCategoryFilter<T>(originalList, resourceList, this);
        }
        return resourceCategoryFilter;
    }

    @Deprecated
    //il ne faut pas l'utiliser comme avec un arrayAdapter habituel, les filter s'occupe de notify if needed
    @Override
    public final void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }


    public abstract void onEvent(ResourcesUpdatedEvent event);
    public abstract View getView(final int position, View convertView, ViewGroup parent);

}
