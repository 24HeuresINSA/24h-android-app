package com.insalyon.les24heures.eventbus;

import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.utils.FilterAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by remi on 26/12/14.
 */
public class CategoryEvent {
    List<Category> categories;
    FilterAction filterAction;

    @Override
    public String toString() {
        return categories.toString();
    }

    public CategoryEvent(List<Category> categories) {
        this.categories = categories;
    }


    public CategoryEvent(List<Category> categories, FilterAction filterAction) {
        this.categories = categories;
        this.filterAction = filterAction;
    }


    public FilterAction getFilterAction() {
        return filterAction;
    }

    public void setFilterAction(FilterAction filterAction) {
        this.filterAction = filterAction;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
