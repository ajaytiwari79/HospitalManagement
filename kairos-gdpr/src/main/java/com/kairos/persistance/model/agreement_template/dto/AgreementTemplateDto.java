package com.kairos.persistance.model.agreement_template.dto;


import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

public class AgreementTemplateDto {

    @NotNullOrEmpty(message = "error.agreement.name.cannotbe.empty.or.null")
    private String name;

    @NotNullOrEmpty(message = "error.agreement.name.cannotbe.empty.or.null")
    private String description;

    private Long organisationTypeid;
    private Long orgServiceid;
    private BigInteger accountTypeId;
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

    public BigInteger getAccountTypeId() {
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
