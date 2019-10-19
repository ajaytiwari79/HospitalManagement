package com.kairos.dto.activity.wta.basic_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.annotation.ValidateIgnoreCounter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by vipul on 21/12/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class WTADTO {
    private BigInteger id;
    private String name;
    private String description;
    private long expertiseId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long expiryDate;
    @NotEmpty(message = "message.wta-base-rule-template.null-list")
    @Valid
    @ValidateIgnoreCounter
    private List<WTABaseRuleTemplateDTO> ruleTemplates=new ArrayList<>();
    private Long organizationType;
    private Long organizationSubType;
    private List<BigInteger> tags;
    private LocalDate employmentEndDate;
    private List<Long> unitIds;


    public WTADTO(String name, String description, long expertiseId, LocalDate startDate, LocalDate endDate, @NotNull(message = "error.RuleTemplate.description.notnull") List<WTABaseRuleTemplateDTO> ruleTemplates, Long organizationType, Long organizationSubType) {
        this.name = name;
        this.description = description;
        this.expertiseId = expertiseId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.ruleTemplates = ruleTemplates;
        this.organizationType = organizationType;
        this.organizationSubType = organizationSubType;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("WTADTO{");
        sb.append("name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", expertiseId=").append(expertiseId);
        sb.append(", startDate=").append(startDate);
        sb.append(", endDate=").append(endDate);
        sb.append(", expiryDate=").append(expiryDate);
        sb.append(", ruleTemplates=").append(ruleTemplates);
        sb.append(", organizationType=").append(organizationType);
        sb.append(", organizationSubType=").append(organizationSubType);
        sb.append(", tags=").append(tags);
        sb.append('}');
        return sb.toString();
    }
}
