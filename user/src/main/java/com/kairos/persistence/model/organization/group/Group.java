package com.kairos.persistence.model.organization.group;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.filter.FilterSelection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created By G.P.Ranjan on 19/11/19
 **/
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class Group extends UserBaseEntity {
    private String name;
    private String description;
    @Relationship(type = "HAS_FILTERS")
    private List<FilterSelection> filtersData = new ArrayList<>();
    private List<Long> excludedStaffs = new ArrayList<>();

    public Group(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
