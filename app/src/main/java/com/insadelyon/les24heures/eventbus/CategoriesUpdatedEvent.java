package com.insadelyon.les24heures.eventbus;

import com.insadelyon.les24heures.model.Category;

import java.util.List;

/**
 * Created by remi on 27/12/14.
 */
public class CategoriesUpdatedEvent {
    List<Category> categories;
    String dataVersion;

    public String getDataVersion() {
        return dataVersion;
    }


    public CategoriesUpdatedEvent(List<Category> categories, String dataVersion) {
        this.categories = categories;
        this.dataVersion = dataVersion;
    }

    public CategoriesUpdatedEvent(List<Category> categories) {
        this.categories = categories;
    }

    public List<Category> getCategories() {
        return categories;
    }

}
