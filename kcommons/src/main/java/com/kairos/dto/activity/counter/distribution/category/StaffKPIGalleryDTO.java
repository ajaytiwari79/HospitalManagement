package com.kairos.dto.activity.counter.distribution.category;

import com.kairos.dto.activity.counter.configuration.KPIDTO;

import java.util.List;

public class StaffKPIGalleryDTO {
    private List<CategoryKPIMappingDTO> category;
    private List<KPIDTO> kpis;

    public StaffKPIGalleryDTO() {
    }

    public StaffKPIGalleryDTO(List<CategoryKPIMappingDTO> category, List<KPIDTO> kpis) {
        this.category = category;
        this.kpis = kpis;
    }

    public List<CategoryKPIMappingDTO> getCategory() {
        return category;
    }

    public void setCategory(List<CategoryKPIMappingDTO> category) {
        this.category = category;
    }

    public List<KPIDTO> getKpis() {
        return kpis;
    }

    public void setKpis(List<KPIDTO> kpis) {
        this.kpis = kpis;
    }
}
