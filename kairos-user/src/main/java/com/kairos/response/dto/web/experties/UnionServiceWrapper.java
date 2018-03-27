package com.kairos.response.dto.web.experties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kairos.controller.organization_service.OrganizationServiceController;
import com.kairos.persistence.model.organization.union.UnionQueryResult;
import jdk.nashorn.internal.ir.annotations.Ignore;

import java.util.List;

/**
 * Created by vipul on 27/3/18.
 */

public class UnionServiceWrapper {
    private List<UnionQueryResult> unions;
    private List<Object> services;

    public UnionServiceWrapper() {
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
