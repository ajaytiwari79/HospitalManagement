package com.kairos.persistence.model.counter;

import com.kairos.dto.activity.counter.data.FilterCriteria;

import java.util.List;

public class ApplicableFilter {
    private List<FilterCriteria> criteriaList;
    private boolean modified;

    public ApplicableFilter() {
    }

    public ApplicableFilter(List<FilterCriteria> criteriaList, boolean modified) {
        this.criteriaList = criteriaList;
        this.modified = modified;
    }

    public List<FilterCriteria> getCriteriaList() {
        return criteriaList;
    }

    public void setCriteriaList(List<FilterCriteria> criteriaList) {
        this.criteriaList = criteriaList;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }
}
