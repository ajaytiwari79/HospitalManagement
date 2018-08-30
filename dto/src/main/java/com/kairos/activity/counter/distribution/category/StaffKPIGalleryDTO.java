package com.kairos.activity.counter.distribution.category;

import com.kairos.activity.counter.KPIDTO;

import java.util.List;

public class StaffKPIGalleryDTO {
    private CategoryKPIMappingDTO category;
    private List<KPIDTO> kpis;

    public CategoryKPIMappingDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryKPIMappingDTO category) {
        this.category = category;
    }

    public List<KPIDTO> getKpis() {
        return kpis;
    }

    public void setKpis(List<KPIDTO> kpis) {
        this.kpis = kpis;
    }
}
