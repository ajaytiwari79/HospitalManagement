package com.kairos.persistence.model.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.country.experties.ExpertiseResponseDTO;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.organization.OrganizationTypeDTO;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.templates.template_types.BreakWTATemplate;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.enums.wta.WTATemplateType.WTA_FOR_BREAKS_IN_SHIFT;

/**
 * @author pradeep
 * @date - 13/4/18
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class WTAQueryResultDTO {

    private BigInteger parentId;

    private BigInteger countryParentWTA;

    private BigInteger organizationParentId; // wta id of parent organization and this must not be changable
    private LocalDate startDate;
    private LocalDate endDate;
    private Long expiryDate;
    private String name;
    private String description;
    private Long employmentId;
    private BigInteger id;
    private ExpertiseResponseDTO expertise;
    private OrganizationDTO organization;
    private OrganizationTypeDTO organizationType;
    private OrganizationTypeDTO organizationSubType;
    private List<WTAQueryResultDTO> versions = new ArrayList<>();
    private List<TagDTO> tags = new ArrayList<>();
    private List<WTABaseRuleTemplate> ruleTemplates;
    private Map<String, TranslationInfo> translations;

    public List<WTABaseRuleTemplate> getRuleTemplates() {
        return Optional.ofNullable(ruleTemplates).orElse(new ArrayList<>());
    }

    public Set<WTATemplateType> getWTATemplateTypes(){
        return getRuleTemplates().stream().map(wtaBaseRuleTemplate -> wtaBaseRuleTemplate.getWtaTemplateType()).collect(Collectors.toSet());
    }
    public boolean isValidWorkTimeAgreement(LocalDate localDate){
        return (isNull(this.getEndDate()) && !this.getStartDate().isAfter(localDate)) || (isNotNull(this.getEndDate()) && !this.getStartDate().isAfter(localDate) && !this.getEndDate().isBefore(localDate));
    }

    public BreakWTATemplate getBreakRule(){
        return (BreakWTATemplate)
        this.getRuleTemplates().stream().filter(current->current.getWtaTemplateType().toString().equals(WTA_FOR_BREAKS_IN_SHIFT.toString())).findFirst().orElse(null);
    }

}
