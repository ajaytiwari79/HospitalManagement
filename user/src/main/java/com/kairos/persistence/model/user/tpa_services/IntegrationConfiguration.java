package com.kairos.persistence.model.user.tpa_services;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;


/**
 * Created by prabjot on 17/1/17.
 */
@NodeEntity
@Getter
@Setter
public class IntegrationConfiguration extends UserBaseEntity {

    @NotBlank(message = "name can not be null")
    private String name;
    private String description;
    @NotBlank(message = "unique key can not be null")
    private String uniqueKey;
    private boolean isEnabled = true;
    @Relationship(type = BELONGS_TO)
    private Country country;
}
