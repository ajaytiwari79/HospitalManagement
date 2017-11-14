package com.kairos.persistence.model.user.expertise;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.tag.Tag;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_TAG;


/**
 * Created by prabjot on 28/10/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
public class Expertise extends UserBaseEntity {

    @NotEmpty(message = "error.Expertise.name.notEmpty") @NotNull(message = "error.Expertise.name.notnull")
    private String name;

    @NotEmpty(message = "error.Expertise.description.notEmpty") @NotNull(message = "error.Expertise.description.notnull")
    private String description;

    private boolean isEnabled = true;

    @Relationship(type = BELONGS_TO)
    Country country;

    @Relationship(type = HAS_TAG)
    private List<Tag> tags = new ArrayList<>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Expertise(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Expertise(){}


    public String getName() {
        return name;
    }

    public Map<String, Object> retrieveDetails() {
        Map<String, Object> map = new HashMap();
        map.put("id",this.id);
        map.put("name",this.name);
        map.put("description",this.description);
        map.put("country",this.country.getName());
        map.put("lastModificationDate",this.getLastModificationDate());
        map.put("creationDate",this.getCreationDate());
        return map;
    }
}
