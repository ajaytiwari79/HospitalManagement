package com.kairos.persistence.model.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotNull;

/**
 * Created by vipul on 13/9/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
public class OrganizationTypeDTO extends UserBaseEntity {
    private String name;
    private String description;

}
