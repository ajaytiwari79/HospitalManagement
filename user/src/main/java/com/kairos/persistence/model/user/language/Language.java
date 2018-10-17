package com.kairos.persistence.model.user.language;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by prabjot on 19/10/16.
 */
@NodeEntity
public class Language extends UserBaseEntity {

    @NotBlank(message = "error.Language.name.notEmpty")
    private String name;
    private String description;
    @Relationship(type =  BELONGS_TO)
    private Country country;

    public Language(String name) {
        this.name = StringUtils.trim(name);
    }

    private boolean inactive;
    private boolean isEnabled = true;
    private long readLevel;
    private long writeLevel;
    private long speakLevel;


    public long getReadLevel() {
        return readLevel;
    }

    public void setReadLevel(long readLevel) {
        this.readLevel = readLevel;
    }

    public long getWriteLevel() {
        return writeLevel;
    }

    public void setWriteLevel(long writeLevel) {
        this.writeLevel = writeLevel;
    }

    public long getSpeakLevel() {
        return speakLevel;
    }

    public void setSpeakLevel(long speakLevel) {
        this.speakLevel = speakLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = StringUtils.trim(description);
    }

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public Language(){}

    public void setName(String name) {
        this.name = StringUtils.trim(name);
    }

    public String getName() {
        return name;
    }

    public Language(String name, boolean inactive) {
        this.name = name;
        this.inactive = inactive;
    }

    public Map<String,Object> retrieveDetails(){
        Map<String,Object> data = new HashMap<>();
        data.put("id",this.id);
        data.put("name",this.name);
        data.put("description",this.description);
        return data;


    }

    public enum LanguageLevelDescription {
        BEGINEER(1), LEARNING(2), INTERMEDIATE(3), EXPERT(4), FLUENT(4);
        private int value;
        private LanguageLevelDescription(int value){
            this.value = value;
        }
    }
}
