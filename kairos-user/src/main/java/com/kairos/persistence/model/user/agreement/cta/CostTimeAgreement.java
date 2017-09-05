package com.kairos.persistence.model.user.agreement.cta;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.expertise.Expertise;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by pawanmandhan on 26/7/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
public class CostTimeAgreement extends UserBaseEntity {

    @NotEmpty(message = "error.cta.name.notempty") @NotNull(message = "error.cta.name.notnull")
    private String name;

    private String description;

    @Relationship(type = HAS_EXPERTISE_IN)
    private Expertise expertise;

    @Relationship(type = HAS_SUB_TYPE)
    private List<OrganizationType> organizationTypeList;

    @Relationship(type = BELONGS_TO)
    private Country country;

    @Relationship(type = HAS_CTA)
    private CostTimeAgreement cta;

    private Long startDate;
    private Long endDate;
    private Long expiryDate;


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

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public List<OrganizationType> getOrganizationTypeList() {
        return organizationTypeList;
    }

    public void setOrganizationTypeList(List<OrganizationType> organizationTypeList) {
        this.organizationTypeList = organizationTypeList;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public CostTimeAgreement getCta() {
        return cta;
    }

    public void setCta(CostTimeAgreement cta) {
        this.cta = cta;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }
}
