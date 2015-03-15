package com.insalyon.les24heures.service.impl;

import com.insalyon.les24heures.DTO.CategoryDTO;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.service.CategoryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by remi on 30/12/14.
 */
public class CategoryServiceImpl implements CategoryService {

    private static CategoryServiceImpl categoryService;
    private static List<Category> categories = new ArrayList<>();


    public static CategoryServiceImpl getInstance() {
        if (categoryService == null) {
//            synchronized (categoryService) {
            categoryService = new CategoryServiceImpl();
//            }
        }
        return categoryService;
    }



    @Override
    public Category fromDTO(CategoryDTO categoryDTO) {
        return new Category(categoryDTO.get_id(),categoryDTO.getName(),categoryDTO.getIcon_name());
    }

    @Override
    public ArrayList<Category> fromDTO(ArrayList<CategoryDTO> categoryDTOs) {
        ArrayList<Category> categories = new ArrayList<>();

        for (CategoryDTO categoryDTO : categoryDTOs) {
            categories.add(this.fromDTO(categoryDTO));
        }

        return categories;
    }

    @Override
    public Category findById(ArrayList<Category> categories, Integer category) {
        for (Category cat : categories) {
            if(cat.get_id().equals(category))
                return cat;
        }
        return null;
    }


}
