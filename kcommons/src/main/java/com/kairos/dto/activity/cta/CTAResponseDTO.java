package com.kairos.dto.activity.cta;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.country.experties.ExpertiseResponseDTO;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.organization.OrganizationTypeDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;

/**
 * Created by pavan on 16/4/18.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CTAResponseDTO {
    @NotNull
    private BigInteger id;
    private BigInteger parentId;
    private BigInteger organizationParentId;// cta id of parent organization and this must not be changable
    private String name;
    private String description;
    private ExpertiseResponseDTO expertise;
    private OrganizationTypeDTO organizationType;
    private OrganizationTypeDTO organizationSubType;

    private List<CTARuleTemplateDTO> ruleTemplates = new ArrayList<>();
    private LocalDate startDate;
    private LocalDate endDate;
    private OrganizationDTO organization;
    // Added for version of CTA
    private List<CTAResponseDTO> versions = new ArrayList<>();
    private Map<String, Object> unitInfo;
    private Long employmentId;
    private Boolean disabled;
    private List<TagDTO> tags;
    private Long countryId;
    private Long unitId;
    private Map<String, TranslationInfo> translations;

    public CTAResponseDTO(String name, BigInteger id,BigInteger parentId) {
        this.name = name;
        this.id = id;
        this.parentId = parentId;
    }

    public CTAResponseDTO(@NotNull BigInteger id, String name, ExpertiseResponseDTO expertise, List<CTARuleTemplateDTO> ruleTemplates, LocalDate startDate, LocalDate endDate, Boolean disabled, Long employmentId, String description) {
        this.id = id;
        this.name = name;
        this.expertise = expertise;
        this.ruleTemplates = ruleTemplates;
        this.startDate = startDate;
        this.endDate = endDate;
        this.disabled = disabled;
        this.employmentId = employmentId;
        this.description=description;
    }

    public String getName() {
        return TranslationUtil.getName(translations,name);
    }

    public String getDescription() {
        return  TranslationUtil.getDescription(translations,description);
    }

    public boolean isValidCostTimeAgreement(LocalDate localDate){
        return (isNull(this.getEndDate()) && !this.getStartDate().isAfter(localDate)) || (isNotNull(this.getEndDate()) && !this.getStartDate().isAfter(localDate) && !this.getEndDate().isBefore(localDate));
    }
}
