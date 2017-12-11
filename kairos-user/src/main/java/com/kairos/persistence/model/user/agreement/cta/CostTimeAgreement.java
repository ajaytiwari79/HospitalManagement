package com.kairos.persistence.model.user.agreement.cta;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.expertise.Expertise;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


@NodeEntity
public class CostTimeAgreement extends UserBaseEntity {

    @NotEmpty(message = "error.parent.name.notempty") @NotNull(message = "error.parent.name.notnull")
    private String name;
    private String description;
    @Relationship(type = HAS_EXPERTISE_IN)
    private Expertise expertise;
    @Relationship(type = HAS_SUB_TYPE)
    private List<OrganizationType> organizationTypeList=new ArrayList<>();
    @Relationship(type = BELONGS_TO)
    private Country country;
    @Relationship(type = HAS_CTA_PARENT)
    private CostTimeAgreement parent;
    @DateLong
    private Date startDate;
    @DateLong
    private Date endDate;
    @DateLong
    private Date expiryDate;
    private boolean disabled;


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

    public CostTimeAgreement getParent() {
        return parent;
    }

    public void setParent(CostTimeAgreement parent) {
        this.parent = parent;
    }
    public boolean hasParent(){
        return true;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
