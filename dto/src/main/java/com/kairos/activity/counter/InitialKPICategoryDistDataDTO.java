package com.kairos.activity.counter;

import java.util.List;
import java.util.Map;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: JUL/05/2018
 */
public class InitialKPICategoryDistDataDTO {
    private List<KPICategoryDTO> categories;
    private CategoryKPIMappingDTO categoryKPIsmap;

    public InitialKPICategoryDistDataDTO(){}

    public InitialKPICategoryDistDataDTO(List categories, CategoryKPIMappingDTO categoryKPIsMap){
        this.categories = categories;
        this.categoryKPIsmap = categoryKPIsMap;
    }

    public List getCategories() {
        return categories;
    }

    public void setCategories(List categories) {
        this.categories = categories;
    }

    public CategoryKPIMappingDTO getCategoryKPIsmap() {
        return categoryKPIsmap;
    }

    public void setCategoryKPIsmap(CategoryKPIMappingDTO categoryKPIsmap) {
        this.categoryKPIsmap = categoryKPIsmap;
    }
}
