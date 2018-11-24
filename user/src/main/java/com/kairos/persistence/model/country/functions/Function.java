package com.kairos.persistence.model.country.functions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by pavan on 13/3/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class Function extends UserBaseEntity {
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    @Relationship(type = HAS_UNION)
    private List<Organization> unions;

    @Relationship(type = HAS_ORGANIZATION_LEVEL)
    private List<Level> organizationLevels;

    @Relationship(type = BELONGS_TO)
    private Country country;
    private String icon;

    public Function() {
        //Default Constructor
    }

    public Function(Long id) {
        this.id = id;
    }

    public Function(String name, String description, LocalDate startDate, LocalDate endDate, List<Organization> unions, List<Level> organizationLevels, Country country, String icon) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.unions = unions;
        this.organizationLevels = organizationLevels;
        this.country = country;
        this.icon = icon;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<Organization> getUnions() {
        return unions;
    }

    public void setUnions(List<Organization> unions) {
        this.unions = unions;
    }

    public List<Level> getOrganizationLevels() {
        return organizationLevels;
    }

    public void setOrganizationLevels(List<Level> organizationLevels) {
        this.organizationLevels = organizationLevels;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
