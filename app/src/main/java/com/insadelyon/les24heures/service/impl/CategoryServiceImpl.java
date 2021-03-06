package com.insadelyon.les24heures.service.impl;

import com.insadelyon.les24heures.DTO.CategoryDTO;
import com.insadelyon.les24heures.model.Category;
import com.insadelyon.les24heures.service.CategoryService;
import com.insadelyon.les24heures.utils.SpecificCategory;

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
        categories.add(this.getAllCategory());
        categories.add(this.getRemainingCategory());


        return categories;
    }

    @Override
    public Category findById(ArrayList<Category> categories, String id) {
        for (Category cat : categories) {
            if(cat.get_id().equals(id))
                return cat;
        }
        return null;
    }

    @Override
    public Category getFavoriteCategory() {
        return new Category(SpecificCategory.FAVORITES.toString(),SpecificCategory.FAVORITES.toString(),SpecificCategory.FAVORITES.toString(),SpecificCategory.FAVORITES.toString());
    }

    @Override
    public Category getAllCategory() {
        //ALL_LABEL will be replaced by R.string.category_all_label by CategoryAdapter (sorry...)
        return new Category(SpecificCategory.ALL.toString(),SpecificCategory.ALL.toString(),"ic_all_categories","ALL_LABEL");
    }

    @Override
    public Category getRemainingCategory(){
        //REMAINING_LABEL will be replaced by R.string.category_all_label by CategoryAdapter (sorry...)
        return new Category(SpecificCategory.REMAINING.toString(),SpecificCategory.REMAINING.toString(),"ic_remaining_categories","REMAINING_LABEL");
    }

    @Override
    public Category getFacilitiesCategory(){
        return new Category(SpecificCategory.FACILITIES.toString(),SpecificCategory.FACILITIES.toString().toLowerCase(),"ic_facilities_categories","FACILITIES_LABEL");
    }


}
