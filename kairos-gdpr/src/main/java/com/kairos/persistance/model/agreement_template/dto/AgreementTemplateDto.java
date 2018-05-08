package com.kairos.persistance.model.agreement_template.dto;

import com.kairos.persistance.model.clause.AccountType;
import com.kairos.persistance.model.organization.OrganizationService;
import com.kairos.persistance.model.organization.OrganizationType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

public class AgreementTemplateDto {

    @NotNull(message = "error.agreement.name.cannotbe.null")
    @NotEmpty(message = "error.agreement.name.cannotbe.empty")
    private String name;
    @NotNull(message = "error.agreement.description.cannotbe.null")
    @NotEmpty(message = "error.agreement.description.cannotbe.empty")
    private String description;

    private Long organisationTypeid;
    private Long orgServiceid;
    private Long accountTypeId;
    private List<BigInteger> clauseIds;
    private  Long countryId;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getOrganisationTypeid() {
        return organisationTypeid;
    }

    public Long getOrgServiceid() {
        return orgServiceid;
    }

    public Long getAccountTypeId() {
        return accountTypeId;
    }

    public List<BigInteger> getClauseIds() {
        return clauseIds;
    }

    public Long getCountryId() {
        return countryId;
    }

    public  AgreementTemplateDto()
    {}
}
