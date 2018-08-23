package com.kairos.activity.counter.distribution.category;

import com.kairos.activity.counter.KPICategoryDTO;

import javax.validation.Valid;
import java.util.List;

public class KPICategoryUpdationDTO {
    private List<KPICategoryDTO> deletedCategories;
    @Valid
    private List<KPICategoryDTO> updatedCategories;

    public KPICategoryUpdationDTO(){

    }

    public KPICategoryUpdationDTO(List deletedCategories, List updatedCategories){
        this.deletedCategories = deletedCategories;
        this.updatedCategories = updatedCategories;
    }

    public List<KPICategoryDTO> getDeletedCategories() {
        return deletedCategories;
    }

    public void setDeletedCategories(List<KPICategoryDTO> deletedCategories) {
        this.deletedCategories = deletedCategories;
    }

    public List<KPICategoryDTO> getUpdatedCategories() {
        return updatedCategories;
    }

    public void setUpdatedCategories(List<KPICategoryDTO> updatedCategories) {
        this.updatedCategories = updatedCategories;
    }
}
