package com.kairos.activity.counter;

import java.util.List;
import java.util.Map;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: JUL/05/2018
 */
public class InitialKPICategoryDistDataDTO {
    private List categories;
    private Map categoryKPIsmap;

    public InitialKPICategoryDistDataDTO(List categories, Map categoryKPIsMap){
        this.categories = categories;
        this.categoryKPIsmap = categoryKPIsMap;
    }

    public List getCategories() {
        return categories;
    }

    public void setCategories(List categories) {
        this.categories = categories;
    }

    public Map getCategoryKPIsmap() {
        return categoryKPIsmap;
    }

    public void setCategoryKPIsmap(Map categoryKPIsmap) {
        this.categoryKPIsmap = categoryKPIsmap;
    }
}
