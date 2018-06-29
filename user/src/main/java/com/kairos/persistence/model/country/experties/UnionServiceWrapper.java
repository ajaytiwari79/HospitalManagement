package com.kairos.persistence.model.country.experties;

import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.union.UnionQueryResult;

import java.util.List;

/**
 * Created by vipul on 27/3/18.
 */

public class UnionServiceWrapper {
    private List<UnionQueryResult> unions;
    private List<Object> services;
    List<Level> organizationLevels;

    public UnionServiceWrapper() {
    }

    public List<Level> getOrganizationLevels() {
        return organizationLevels;
    }

    public void setOrganizationLevels(List<Level> organizationLevels) {
        this.organizationLevels = organizationLevels;
    }

    public List<UnionQueryResult> getUnions() {
        return unions;
    }

    public void setUnions(List<UnionQueryResult> unions) {
        this.unions = unions;
    }

    public List<Object> getServices() {
        return services;
    }

    public void setServices(List<Object> services) {
        this.services = services;
    }
}
