package com.kairos.persistence.model.user.pay_group_area;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.user.region.Municipality;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by vipul on 12/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)

@QueryResult
public class PayGroupAreaQueryResult {
    private Long id;
    private Long payGroupAreaId;
    private String name;
    private String description;
    private Municipality municipality;
    private Long startDateMillis;
    private Long endDateMillis;
    private Long levelId;

    public PayGroupAreaQueryResult() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Municipality getMunicipality() {
        return municipality;
    }

    public void setMunicipality(Municipality municipality) {
        this.municipality = municipality;
    }

    public Long getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Long getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Long endDateMillis) {
        this.endDateMillis = endDateMillis;
    }

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }

    public Long getPayGroupAreaId() {
        return payGroupAreaId;
    }

    public void setPayGroupAreaId(Long payGroupAreaId) {
        this.payGroupAreaId = payGroupAreaId;
    }

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
