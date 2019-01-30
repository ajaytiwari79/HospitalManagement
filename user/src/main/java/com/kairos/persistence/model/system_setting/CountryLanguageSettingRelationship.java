package com.kairos.persistence.model.system_setting;

import com.kairos.constants.AppConstants;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.constants.AppConstants.HAS_SYSTEM_LANGUAGE;

@RelationshipEntity(type=HAS_SYSTEM_LANGUAGE)
public class CountryLanguageSettingRelationship extends UserBaseEntity {
    @StartNode
    private Country country;
    @EndNode
    private SystemLanguage systemLanguage;
    private boolean defaultLanguage;

    public CountryLanguageSettingRelationship() {

    }

    public CountryLanguageSettingRelationship(Country country, SystemLanguage systemLanguage, boolean defaultLanguage) {
        this.country = country;
        this.systemLanguage = systemLanguage;
        this.defaultLanguage = defaultLanguage;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public SystemLanguage getSystemLanguage() {
        return systemLanguage;
    }

    public void setSystemLanguage(SystemLanguage systemLanguage) {
        this.systemLanguage = systemLanguage;
    }



    public boolean isDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(boolean defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }
}
