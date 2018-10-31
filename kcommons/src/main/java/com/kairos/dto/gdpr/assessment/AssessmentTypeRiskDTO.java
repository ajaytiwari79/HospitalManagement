package com.kairos.dto.gdpr.assessment;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssessmentTypeRiskDTO extends AssessmentDTO{

    @NotEmpty
    private Set<BigInteger> riskIds;

    @NotNull
    private QuestionnaireTemplateType riskAssociatedEntity;

    public QuestionnaireTemplateType getRiskAssociatedEntity() { return riskAssociatedEntity; }

    public void setRiskAssociatedEntity(QuestionnaireTemplateType riskAssociatedEntity) { this.riskAssociatedEntity = riskAssociatedEntity; }

    public Set<BigInteger> getRiskIds() { return riskIds; }

    public void setRiskIds(Set<BigInteger> riskIds) { this.riskIds = riskIds; }
}
