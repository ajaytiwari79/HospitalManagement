package com.kairos.activity.counter;

import java.util.List;
import java.util.Map;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: JUL/05/2018
 */
public class InitialKPICategoryDistDataDTO {
    private List kpis;
    private List categories;
    private Map categoryKPIsmap;

    public InitialKPICategoryDistDataDTO(List kpis, List categories, Map categoryKPIsMap){
        this.kpis = kpis;
        this.categories = categories;
        this.categoryKPIsmap = categoryKPIsMap;
    }

    public List getKpis() {
        return kpis;
    }

    public void setKpis(List kpis) {
        this.kpis = kpis;
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
