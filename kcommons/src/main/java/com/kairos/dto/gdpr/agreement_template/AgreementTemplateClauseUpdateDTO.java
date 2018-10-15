package com.kairos.dto.gdpr.agreement_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgreementTemplateClauseUpdateDTO {

    @NotEmpty
    private Set<BigInteger> agreementTemplateIds;
    @NotNull
    private BigInteger previousClauseId;
    @NotNull
    private BigInteger newClauseId;

    public Set<BigInteger> getAgreementTemplateIds() { return agreementTemplateIds; }

    public void setAgreementTemplateIds(Set<BigInteger> agreementTemplateIds) { this.agreementTemplateIds = agreementTemplateIds; }

    public BigInteger getPreviousClauseId() { return previousClauseId; }

    public void setPreviousClauseId(BigInteger previousClauseId) { this.previousClauseId = previousClauseId;}

    public BigInteger getNewClauseId() { return newClauseId; }

    public void setNewClauseId(BigInteger newClauseId) { this.newClauseId = newClauseId; }
}
