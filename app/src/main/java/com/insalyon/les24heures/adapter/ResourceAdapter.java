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
import com.insalyon.les24heures.utils.AlphabeticalReverseSortComparator;
import com.insalyon.les24heures.utils.AlphabeticalSortComparator;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by remi on 13/02/15.
 */
public abstract class ResourceAdapter<T extends Resource> extends ArrayAdapter<T> {
    ArrayList<T> originalList;
    ArrayList<T> resourceList;
    private ResourceSearchFilter resourceSearchFilter;
    private ResourceCategoryFilter resourceCategoryFilter;


    public ResourceAdapter(Context context, int textViewResourceId,
                           ArrayList<T> resources) {
        super(context, textViewResourceId, resources);
        this.resourceList = new ArrayList<>();
        this.resourceList.addAll(resources);
        this.originalList = new ArrayList<>();
        this.originalList.addAll(resources);
    }


    //il faut que ce soit ResourceAdapter qui cree les filters car ils ont besoin des pointeurs utilisés par ResourceAdapter qui
    //ne sont pas les memes que dans le fragment car le fragment doit conserver une copie intact des resources (car il manage lui
    //meme la sauvegarde des resources en BundleSavedInstanceState)

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

    public void sortAZ(){
        Collections.sort(resourceList, new AlphabeticalSortComparator());
        Collections.sort(originalList, new AlphabeticalSortComparator());
        this.notifyDataSetChanged();
    }

    public void sortZA(){
        Collections.sort(resourceList, new AlphabeticalReverseSortComparator());
        Collections.sort(originalList, new AlphabeticalReverseSortComparator());
        this.notifyDataSetChanged();
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
