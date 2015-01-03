package com.insalyon.les24heures.eventbus;

import com.insalyon.les24heures.model.Category;

import java.util.List;

/**
 * Created by remi on 27/12/14.
 */
public class CategoriesUpdatedEvent {
    List<Category> categories;

    public CategoriesUpdatedEvent(List<Category> categories) {
        this.categories = categories;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
