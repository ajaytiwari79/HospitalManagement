package com.kairos.persistence.model.user.expertise.response;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by vipul on 13/9/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
@QueryResult
public class ExpertiseDTO extends UserBaseEntity {
    private String name;
    private String description;

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
}
