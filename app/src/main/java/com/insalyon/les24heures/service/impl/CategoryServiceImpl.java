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
        return new Category(categoryDTO.get_id(),categoryDTO.getName(),categoryDTO.getIcon_name(),categoryDTO.getDisplay_name());
    }

    @Override
    public ArrayList<Category> fromDTO(ArrayList<CategoryDTO> categoryDTOs) {
        ArrayList<Category> categories = new ArrayList<>();

        for (CategoryDTO categoryDTO : categoryDTOs) {
            categories.add(this.fromDTO(categoryDTO));
        }
        //ALL_LABEL will be replaced by R.string.category_all_label by CategoryAdapter (sorry...)
        categories.add(new Category("id_all","ALL","ic_ALLCATEGORY","ALL_LABEL"));//WARNING ic-ALLCATEGORY is mandatory for the filter


        return categories;
    }

    @Override
    public Category findById(ArrayList<Category> categories, Integer id) {
        for (Category cat : categories) {
            if(cat.get_id().equals(id))
                return cat;
        }
        return null;
    }


}
