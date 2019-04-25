package com.kairos.wrapper;

import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.union.UnionQueryResult;

import java.util.List;

/**
 * Created by pavan on 14/3/18.
 */
public class OrganizationLevelAndUnionWrapper {
    private List<UnionQueryResult> unions;
    private List<Level> organizationLevel;

    public OrganizationLevelAndUnionWrapper() {
        //Default Constructor
    }

    public OrganizationLevelAndUnionWrapper(List<UnionQueryResult> unions, List<Level> organizationLevel) {
        this.unions = unions;
        this.organizationLevel = organizationLevel;
    }

    public List<UnionQueryResult> getUnions() {
        return unions;
    }

    public void setUnions(List<UnionQueryResult> unions) {
        this.unions = unions;
    }

    public List<Level> getOrganizationLevel() {
        return organizationLevel;
    }

    public void setOrganizationLevel(List<Level> organizationLevel) {
        this.organizationLevel = organizationLevel;
    }
}
