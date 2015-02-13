package com.insalyon.les24heures.eventbus;

import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.utils.FilterAction;

import java.util.List;

/**
 * Created by remi on 26/12/14.
 */
public class CategoriesSelectedEvent {
    List<Category> categories;
    FilterAction filterAction;

    public CategoriesSelectedEvent(List<Category> categories) {
        this.categories = categories;
    }

    public CategoriesSelectedEvent(List<Category> categories, FilterAction filterAction) {
        this.categories = categories;
        this.filterAction = filterAction;
    }

    @Override
    public String toString() {
        return categories.toString();
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

}
