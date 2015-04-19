package com.insalyon.les24heures.service;

import com.insalyon.les24heures.DTO.CategoryDTO;
import com.insalyon.les24heures.model.Category;

import java.util.ArrayList;

/**
 * Created by remi on 30/12/14.
 */
public interface CategoryService {
    public Category fromDTO(CategoryDTO categoryDTO);

    public ArrayList<Category> fromDTO(ArrayList<CategoryDTO> categoryDTOs);

    Category findById(ArrayList<Category> categories, String category);

    //specific category for filter
    public Category getFavoriteCategory();
    public Category getAllCategory();
    public Category getRemainingCategory();
}
