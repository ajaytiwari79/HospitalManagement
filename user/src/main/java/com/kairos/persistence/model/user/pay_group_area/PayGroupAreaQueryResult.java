package com.kairos.persistence.model.user.pay_group_area;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.user.region.Municipality;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by vipul on 12/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)

@QueryResult
@Getter
@Setter
@NoArgsConstructor
public class PayGroupAreaQueryResult {
    private Long id;
    private Long payGroupAreaId;
    private String name;
    private String description;
    private Municipality municipality;
    private Long startDateMillis;
    private Long endDateMillis;
    private Long levelId;


    public PayGroupAreaQueryResult(PayGroupArea payGroupArea, PayGroupAreaMunicipalityRelationship relationship, Municipality municipality) {
        this.id = relationship.getId();
        this.payGroupAreaId = payGroupArea.getId();
        this.name = payGroupArea.getName();
        this.description = payGroupArea.getDescription();
        this.municipality = municipality.retrieveBasicDetails();
        this.startDateMillis = relationship.getStartDateMillis();
        this.endDateMillis = relationship.getEndDateMillis();
    }


}
