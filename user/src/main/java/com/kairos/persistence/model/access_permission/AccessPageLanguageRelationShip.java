package com.kairos.persistence.model.access_permission;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.system_setting.SystemLanguage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.constants.AppConstants.ACCESS_PAGE_HAS_LANGUAGE;
@NoArgsConstructor
@Getter
@Setter
@RelationshipEntity(type= ACCESS_PAGE_HAS_LANGUAGE)
public class AccessPageLanguageRelationShip extends UserBaseEntity {
    @StartNode
    private AccessPage accessPage;
    @EndNode
    private SystemLanguage systemLanguage;
    private String description;

    public AccessPageLanguageRelationShip(Long id,AccessPage accessPage, SystemLanguage systemLanguage, String description) {
        this.id=id;
        this.accessPage = accessPage;
        this.systemLanguage = systemLanguage;
        this.description = description;
    }
}
