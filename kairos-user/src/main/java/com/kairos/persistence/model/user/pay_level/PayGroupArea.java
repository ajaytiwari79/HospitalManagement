package com.kairos.persistence.model.user.pay_level;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.region.Municipality;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.math.BigDecimal;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_MUNICIPALITY;

/**
 * @Created by prabjot on 20/12/17.
 * @Modified by VIPUl for KP-2320 on 9-March-18
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
public class PayGroupArea extends UserBaseEntity {
    private String name;
    private String description;
    @Relationship(type = HAS_MUNICIPALITY)
    private Set<Municipality> municipality;
    private Long startDateMillis;
    private Long endDateMillis;

    public PayGroupArea() {
        //default constructor
    }

    public PayGroupArea(Long startDateMillis, Long endDateMillis) {
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
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

    public Set<Municipality> getMunicipality() {
        return municipality;
    }

    public void setMunicipality(Set<Municipality> municipality) {
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
}
