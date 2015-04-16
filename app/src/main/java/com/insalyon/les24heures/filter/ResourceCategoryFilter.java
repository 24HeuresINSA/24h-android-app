package com.insalyon.les24heures.filter;

import android.widget.Filter;

import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.service.ScheduleService;
import com.insalyon.les24heures.service.impl.ScheduleServiceImpl;
import com.insalyon.les24heures.utils.SpecificCategory;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by remi on 27/12/14.
 */
public abstract class ResourceCategoryFilter<T extends Resource> extends Filter {

    ArrayList<T> originalList;
    ArrayList<T> resourceList;

    ArrayList<String> selectedCategories;

    ScheduleService scheduleService = ScheduleServiceImpl.getInstance();

    public ResourceCategoryFilter(ArrayList<T> originalList, ArrayList<T> resourceList) {
        //we need pointer to inform the array adapter of what we are doing
        this.originalList = originalList;
        this.resourceList = resourceList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults result = new FilterResults();

        if (constraint != null) {
            selectedCategories =
                    new ArrayList<>(Arrays.asList(
                            ((String) constraint).substring(1, constraint.length() - 1).split(", "))
                    );

            if (selectedCategories.size() != 0) {
                ArrayList<Resource> filteredItems = new ArrayList<>();

                for (int i = 0, l = originalList.size(); i < l; i++) {
                    Resource dayResource = originalList.get(i);
                    //effective search pattern
                    if (isDisplayable(dayResource))
                        filteredItems.add(dayResource);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;

            } else {
                synchronized (this) {
                    result.values = originalList;
                    result.count = originalList.size();
                }
            }
        } else {
            synchronized (this) {
                result.values = new ArrayList<>(originalList);
                result.count = originalList.size();
            }
        }
        return result;
    }


    private Boolean isDisplayable(Resource dayResource) {
        if (selectedCategories.contains(SpecificCategory.FAVORITES.toString())) {
            if (selectedCategories.size() == 1)
                return dayResource.isFavorites();
            return dayResource.isFavorites() &&
                    isCategoryzedDisplayable(dayResource);
        }
        return isCategoryzedDisplayable(dayResource);
    }

    private boolean isCategoryzedDisplayable(Resource dayResource) {
        if (selectedCategories.contains(SpecificCategory.REMAINING.toString())) {
//            return !scheduleService.getNextSchedules(dayResource).isEmpty();
            return !((ScheduleServiceImpl)scheduleService).getNextSchedulesMocked(dayResource).isEmpty();
        }
        return (selectedCategories.contains(dayResource.getCategory().toString()));
    }

}
