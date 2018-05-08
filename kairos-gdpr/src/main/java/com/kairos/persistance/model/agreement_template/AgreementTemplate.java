package com.kairos.persistance.model.agreement_template;


import com.kairos.persistance.country.Country;
import com.kairos.persistance.model.clause.AccountType;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.persistance.model.organization.OrganizationService;
import com.kairos.persistance.model.organization.OrganizationType;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@Document(collection = "agreement_template")
public class AgreementTemplate extends MongoBaseEntity {

    @NotNull(message = "error.agreement.name.cannotbe.null")
    @NotEmpty(message = "error.agreement.name.cannotbe.empty")
    String name;

    String description;
    OrganizationType organisationType;
    OrganizationService orgService;
    AccountType accountType;
    List<BigInteger> clauses;

    Country country;

    Boolean isDefault=true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrganizationType getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(OrganizationType organisationType) {
        this.organisationType = organisationType;
    }

    public OrganizationService getOrgService() {
        return orgService;
    }

    public void setOrgService(OrganizationService orgService) {
        this.orgService = orgService;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public List<BigInteger> getClauses() {
        return clauses;
    }

    public void setClauses(List<BigInteger> clauses) {
        this.clauses = clauses;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {

        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }
}
