package com.kairos.persistence.model.organization.union;

import com.kairos.persistence.model.query_wrapper.OrganizationCreationData;

import java.util.List;
import java.util.Map;

/**
 * Created by vipul on 13/2/18.
 */
public class UnionQueryWrapper {
    private OrganizationCreationData globalData;
    private  List<Map<String, Object>> unions;

    public UnionQueryWrapper() {
        //Default Constructor
    }

    public OrganizationCreationData getGlobalData() {
        return globalData;
    }

    public void setGlobalData(OrganizationCreationData globalData) {
        this.globalData = globalData;
    }

    public List<Map<String, Object>> getUnions() {
        return unions;
    }

    public void setUnions(List<Map<String, Object>> unions) {
        this.unions = unions;
    }
}
