package com.kairos.persistence.model.user.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.constants.RelationshipConstants;
import com.kairos.persistence.model.country.feature.Feature;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static com.kairos.constants.UserMessagesConstants.ERROR_NAME_NOTNULL;
import static com.kairos.constants.UserMessagesConstants.ERROR_RESOURCE_ICON_NOTNULL;

/**
 * Created by Jasgeet on 18/9/17.
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Vehicle extends UserBaseEntity {
    @NotBlank(message = ERROR_NAME_NOTNULL)
    private String name;
    private String description;
    @NotBlank(message = ERROR_RESOURCE_ICON_NOTNULL)
    private String icon;
    private boolean enabled = true;

    @Relationship(type = RelationshipConstants.VEHICLE_HAS_FEATURE)
    private List<Feature> features;
}
