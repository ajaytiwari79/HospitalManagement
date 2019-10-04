package com.kairos.persistence.model.access_permission;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.system_setting.SystemLanguage;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.constants.AppConstants.ACCESS_PAGE_HAS_LANGUAGE;

@RelationshipEntity(type= ACCESS_PAGE_HAS_LANGUAGE)
public class AccessPageLanguageRelationShip extends UserBaseEntity {
    @StartNode
    private AccessPage accessPage;
    @EndNode
    private SystemLanguage systemLanguage;
    private String description;

    public AccessPageLanguageRelationShip() {
        //Default Constructor
    }

    public AccessPageLanguageRelationShip(Long id,AccessPage accessPage, SystemLanguage systemLanguage, String description) {
        this.id=id;
        this.accessPage = accessPage;
        this.systemLanguage = systemLanguage;
        this.description = description;
    }

    public AccessPage getAccessPage() {
        return accessPage;
    }

    public void setAccessPage(AccessPage accessPage) {
        this.accessPage = accessPage;
    }

    public SystemLanguage getSystemLanguage() {
        return systemLanguage;
    }

    public void setSystemLanguage(SystemLanguage systemLanguage) {
        this.systemLanguage = systemLanguage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
