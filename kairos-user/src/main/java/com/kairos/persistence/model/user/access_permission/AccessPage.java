package com.kairos.persistence.model.user.access_permission;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.SUB_PAGE;


/**
 * Created by arvind on 24/10/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class AccessPage extends UserBaseEntity {

    private String name;
    private boolean isModule;
    private String moduleId;

    @Relationship(type = SUB_PAGE)
    List<AccessPage> subPages;

    public AccessPage(String name){
        this.name = name;
    }

    public AccessPage(String name, String moduleId) {
        this.name = name;
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
        return subPages;
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
}
