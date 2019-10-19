package com.kairos.persistence.model.country.functions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.LocalDate;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by pavan on 13/3/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Function extends UserBaseEntity {
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    @Relationship(type = HAS_UNION)
    private List<Unit> unions;

    @Relationship(type = HAS_ORGANIZATION_LEVEL)
    private List<Level> organizationLevels;

    @Relationship(type = BELONGS_TO)
    private Country country;
    private String icon;
    private int code;
}
