package com.kairos.persistence.model.country.experties;

import com.kairos.dto.user.organization.union.SectorDTO;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.union.UnionQueryResult;

import java.util.List;
import java.util.Map;

/**
 * Created by vipul on 27/3/18.
 */

public class UnionServiceWrapper {
    private List<UnionQueryResult> unions;
    private List<Map<String,Object>> services;
    private List<Level> organizationLevels;
    private List<SectorDTO> sectors;

    public UnionServiceWrapper() {
        //Default Constructor
    }

    public List<Map<String, Object>> getServices() {
        return services;
    }

    public void setServices(List<Map<String, Object>> services) {
        this.services = services;
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



    public List<SectorDTO> getSectors() {
        return sectors;
    }

    public void setSectors(List<SectorDTO> sectors) {
        this.sectors = sectors;
    }
}
