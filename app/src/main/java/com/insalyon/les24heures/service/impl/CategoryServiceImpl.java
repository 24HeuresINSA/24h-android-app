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
        return null;
    }

    @Override
    public ArrayList<Category> fromDTO(ArrayList<CategoryDTO> categoryDTOs) {
        return null;
    }
}
