package com.kairos.dto.activity.counter.distribution.category;

import com.kairos.dto.activity.counter.KPICategoryDTO;

import java.util.List;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: JUL/05/2018
 */
public class InitialKPICategoryDistDataDTO {
    private List<KPICategoryDTO> categories;
    private List<CategoryKPIMappingDTO> categoryKPIsmap;

    public InitialKPICategoryDistDataDTO(){}

    public InitialKPICategoryDistDataDTO(List categories, List<CategoryKPIMappingDTO> categoryKPIsMap){
        this.categories = categories;
        this.categoryKPIsmap = categoryKPIsMap;
    }

    public List getCategories() {
        return categories;
    }

    public void setCategories(List categories) {
        this.categories = categories;
    }

    public List<CategoryKPIMappingDTO> getCategoryKPIsmap() {
        return categoryKPIsmap;
    }

    public void setCategoryKPIsmap(List<CategoryKPIMappingDTO> categoryKPIsmap) {
        this.categoryKPIsmap = categoryKPIsmap;
    }
}
