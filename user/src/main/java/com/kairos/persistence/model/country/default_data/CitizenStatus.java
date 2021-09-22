package com.kairos.persistence.model.country.default_data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import org.apache.commons.lang.StringUtils;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

import static com.kairos.constants.UserMessagesConstants.ERROR_CITIZENSTATUS_NAME_NOTEMPTY;
import static com.kairos.persistence.model.constants.RelationshipConstants.CIVILIAN_STATUS;


/**
 * Created by oodles on 5/1/17.
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CitizenStatus extends UserBaseEntity {

    private static final long serialVersionUID = 644786606504139799L;
    @NotBlank(message = ERROR_CITIZENSTATUS_NAME_NOTEMPTY)
    String name;
    String description;
    @Relationship(type = CIVILIAN_STATUS)
    Country country;
    private boolean isEnabled = true;

    public CitizenStatus() {
    }

    public CitizenStatus(@NotBlank(message = ERROR_CITIZENSTATUS_NAME_NOTEMPTY) String name, String description) {
        this.name = name;
        this.description = description;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtils.trim(name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = StringUtils.trim(description);
    }


    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }



    public Map<String,Object> retrieveDetails(){
        Map<String,Object> data = new HashMap<>();
        data.put("id",this.id);
        data.put("name",this.name);
        data.put("description",this.description);
        data.put("lastModificationDate",this.getLastModificationDate());
        data.put("creationDate",this.getCreationDate());
        return data;


    }

    @Override
    public String toString() {
        return "CitizenStatus{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", country=" + country +
                ", isEnabled=" + isEnabled +
                ", id=" + super.getId() +
                '}';
    }
}
