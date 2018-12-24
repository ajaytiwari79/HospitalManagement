package com.kairos.persistence.model.access_permission;

import com.kairos.enums.OrganizationCategory;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prabjot on 10/10/17.
 */
@QueryResult
public class AccessPageDTO {

    private Long id;
    @NotNull(message = "error.name.notnull")
    private String name;
    private boolean module;
    private Long parentTabId;
    private String moduleId;
    private Boolean active;
    private boolean accessibleForHub;
    private boolean accessibleForUnion;
    private boolean accessibleForOrganization;
    private List<OrganizationCategory> accessibleFor = new ArrayList<>();
    //this value is true only in case of "moduleId" : "module_1"
    private Boolean editable;
    private boolean hasSubTabs;


    public Long getParentTabId() {
        return parentTabId;
    }

    public void setParentTabId(Long parentTabId) {
        this.parentTabId = parentTabId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isModule() {
        return module;
    }

    public void setModule(boolean module) {
        this.module = module;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public boolean isAccessibleForHub() {
        return accessibleForHub;
    }

    public void setAccessibleForHub(boolean accessibleForHub) {
        this.accessibleForHub = accessibleForHub;
    }

    public boolean isAccessibleForUnion() {
        return accessibleForUnion;
    }

    public void setAccessibleForUnion(boolean accessibleForUnion) {
        this.accessibleForUnion = accessibleForUnion;
    }

    public boolean isAccessibleForOrganization() {
        return accessibleForOrganization;
    }

    public void setAccessibleForOrganization(boolean accessibleForOrganization) {
        this.accessibleForOrganization = accessibleForOrganization;
    }


    public List<OrganizationCategory> getAccessibleFor() {
        return accessibleFor;
    }

    public void setAccessibleFor(List<OrganizationCategory> accessibleFor) {
        this.accessibleFor = accessibleFor;
    }

    public Boolean isEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public boolean isHasSubTabs() {
        return hasSubTabs;
    }

    public void setHasSubTabs(boolean hasSubTabs) {
        this.hasSubTabs = hasSubTabs;
    }
}
