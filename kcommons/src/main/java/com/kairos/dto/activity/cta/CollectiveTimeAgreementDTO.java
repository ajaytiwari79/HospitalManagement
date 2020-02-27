package com.kairos.dto.activity.cta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.tags.TagDTO;
import com.kairos.dto.user.country.experties.ExpertiseResponseDTO;
import com.kairos.dto.user.organization.OrganizationTypeDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kairos.constants.CommonMessageConstants.MESSAGE_WTA_BASE_RULE_TEMPLATE_NULL_LIST;

/**
 * @author pradeep
 * @date - 30/7/18
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class CollectiveTimeAgreementDTO {
    private BigInteger id;
    @NotNull(message = "error.cta.expertise.notNull")
    private String name;
    private String description;
    @NotNull(message = "error.cta.parentExpertise.notNull")
    private ExpertiseResponseDTO expertise;
    //@NotNull(message = "error.cta.organizationType.notNull")
    private OrganizationTypeDTO organizationType;
    //@NotNull(message = "error.cta.organizationSubType.notNull")
    private OrganizationTypeDTO organizationSubType;
    @NotEmpty(message = MESSAGE_WTA_BASE_RULE_TEMPLATE_NULL_LIST)
    private List<CTARuleTemplateDTO> ruleTemplates = new ArrayList<>();
    @NotNull(message = "error.cta.startDate.notNull")
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Long> unitIds;
    private List<TagDTO> tags =new ArrayList<>();


    public void setRuleTemplates(List<CTARuleTemplateDTO> ruleTemplates) {
        this.ruleTemplates = Optional.ofNullable(ruleTemplates).orElse(new ArrayList<>());
    }

    @Override
    public String toString() {
        return "CollectiveTimeAgreementDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", expertise=" + expertise +
                ", organizationType=" + organizationType +
                ", organizationSubType=" + organizationSubType +
                ", ruleTemplates=" + ruleTemplates +
                '}';
    }
}

