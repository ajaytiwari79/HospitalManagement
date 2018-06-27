package com.kairos.user.access_permission;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by prabjot on 9/10/17.
 */
@NodeEntity
public class AccessPageCustomId extends UserBaseEntity {

    public AccessPageCustomId() {
        //default constructor
    }

    private String moduleId;
    private String tabId;

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }
}
