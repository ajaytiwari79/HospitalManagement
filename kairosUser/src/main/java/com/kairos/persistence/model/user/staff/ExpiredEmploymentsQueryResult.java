package com.kairos.persistence.model.user.staff;

import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.access_permission.AccessGroup;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by yatharth on 4/4/18.
 */
@QueryResult
public class ExpiredEmploymentsQueryResult {

    private Employment employment;
    private List<AccessGroup> accessGroups;
    private List<Organization> organizations;
    private List<UnitPermission> unitPermissions;


    public List<UnitPermission> getUnitPermissions() {
        return unitPermissions;
    }

    public void setUnitPermissions(List<UnitPermission> unitPermissions) {
        this.unitPermissions = unitPermissions;
    }


    public Employment getEmployment() {
        return employment;
    }

    public void setEmployment(Employment employment) {
        this.employment = employment;
    }

    public List<AccessGroup> getAccessGroups() {
        return accessGroups;
    }

    public void setAccessGroups(List<AccessGroup> accessGroups) {
        this.accessGroups = accessGroups;
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<Organization> organizations) {
        this.organizations = organizations;
    }

}
