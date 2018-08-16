package com.kairos.persistence.model.country.default_data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.CIVILIAN_STATUS;


/**
 * Created by oodles on 5/1/17.
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CitizenStatus extends UserBaseEntity {
    @NotEmpty(message = "error.CitizenStatus.name.notEmpty") @NotNull(message = "error.CitizenStatus.name.notnull")
    String name;

    //@NotEmpty(message = "error.CitizenStatus.description.notEmpty") @NotNull(message = "error.CitizenStatus.description.notnull")
    String description;

    @Relationship(type = CIVILIAN_STATUS)
    Country country;

    private boolean isEnabled = true;


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
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public CitizenStatus() {
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
