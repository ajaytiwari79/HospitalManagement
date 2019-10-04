package com.kairos.persistence.model.user.expertise.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by vipul on 13/9/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
@Getter
@Setter
public class ExpertiseDTO  {
    private String name;
    private String description;
}
