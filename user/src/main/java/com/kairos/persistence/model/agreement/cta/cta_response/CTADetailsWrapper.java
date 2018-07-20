package com.kairos.persistence.model.agreement.cta.cta_response;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.expertise.Expertise;

public class CTADetailsWrapper {
    private Boolean all;
    private Country country;
    private Expertise expertise;
    private OrganizationType organizationType;
    private OrganizationType organizationSubType;

    public Boolean getAll() {
        return all;
    }

    public void setAll(Boolean all) {
        this.all = all;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(OrganizationType organizationType) {
        this.organizationType = organizationType;
    }

    public OrganizationType getOrganizationSubType() {
        return organizationSubType;
    }

    public void setOrganizationSubType(OrganizationType organizationSubType) {
        this.organizationSubType = organizationSubType;
    }
}
