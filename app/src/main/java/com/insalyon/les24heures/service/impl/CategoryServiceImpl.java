package com.insalyon.les24heures.service.impl;

import com.insalyon.les24heures.eventbus.CategoriesUpdatedEvent;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.service.CategoryService;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by remi on 30/12/14.
 */
public class CategoryServiceImpl implements CategoryService {

    private static CategoryServiceImpl categoryService;
    private static List<Category> categories = new ArrayList<>();
    private EventBus eventBus;


    private CategoryServiceImpl() {
        eventBus = EventBus.getDefault();

    }

    public static CategoryServiceImpl getInstance() {
        if (categoryService == null) {
//            synchronized (categoryService) {
            categoryService = new CategoryServiceImpl();
//            }
        }
        return categoryService;
    }

    public void onEvent(CategoriesUpdatedEvent categoriesUpdatedEvent) {
        this.setCategories(categoriesUpdatedEvent.getCategories());
    }


    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories.clear();
        this.categories.addAll(categories);
    }
}
