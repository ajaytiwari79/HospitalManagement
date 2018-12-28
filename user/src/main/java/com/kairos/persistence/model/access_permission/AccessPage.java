package com.kairos.persistence.model.access_permission;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kairos.persistence.model.constants.RelationshipConstants.SUB_PAGE;


/**
 * Created by arvind on 24/10/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class AccessPage extends UserBaseEntity {

    @NotNull(message = "error.name.notnull")
    private String name;
    private boolean isModule;
    private boolean kpiEnabled;
    private String moduleId;
    private boolean active;
    //this value is "false" only in case of "moduleId" : "module_1"
    private Boolean editable;


    @Relationship(type = SUB_PAGE)
    List<AccessPage> subPages;

    private int sequence;

    public AccessPage(String name){
        this.name = name;
    }

    public AccessPage(String name, String moduleId) {
        this.name = name;
        this.moduleId = moduleId;
    }

    public AccessPage(String name, boolean isModule, String moduleId) {
        this.name = name;
        this.isModule = isModule;
        this.moduleId = moduleId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public AccessPage(){}

    public void setSubPages(List<AccessPage> subPages) {
        this.subPages = subPages;
    }

    public List<AccessPage> getSubPages() {
        return Optional.ofNullable(subPages).orElse(new ArrayList<>());
    }

    public void setModule(boolean module) {
        isModule = module;
    }

    public boolean isModule() {
        return isModule;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isKpiEnabled() {
        return kpiEnabled;
    }

    public void setKpiEnabled(boolean kpiEnabled) {
        this.kpiEnabled = kpiEnabled;
    }

    public Boolean isEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
