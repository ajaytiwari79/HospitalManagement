package com.kairos.dto.activity.wta.basic_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.tags.TagDTO;
import com.kairos.dto.user.country.experties.ExpertiseResponseDTO;
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

/**
 * Created by vipul on 21/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class WTAResponseDTO {

    private List<WTABaseRuleTemplateDTO> ruleTemplates;

    private BigInteger parentId;
    private BigInteger organizationParentId;// wta id of parent organization and this must not be changable
    private LocalDate startDate;
    private LocalDate endDate;
    private Long expiryDate;
    private String name;
    private Long employmentId;
    private String description;
    private BigInteger id;
    private ExpertiseResponseDTO expertise;
    private OrganizationTypeDTO organizationType;
    private OrganizationTypeDTO organizationSubType;
    private WTAResponseDTO parentWTAResponse;
    private List<WTAResponseDTO> versions = new ArrayList<>();
    private List<TagDTO> tags;
    private Map<String, Object> unitInfo;

    public WTAResponseDTO(String name, BigInteger id,BigInteger parentId) {
        this.name = name;
        this.id = id;
        this.parentId = parentId;
    }

    public WTAResponseDTO(BigInteger id, LocalDate startDate, LocalDate endDate, @NotNull(message = "error.WorkingTimeAgreement.name.notnull") String name, String description) {
        this.id = id;
        this.startDate =startDate;
        this.endDate = endDate;
        this.name = name;
        this.description = description;


    }

    public WTAResponseDTO(BigInteger id, LocalDate startDate, LocalDate endDate, String name, String description, ExpertiseResponseDTO expertise, OrganizationTypeDTO organizationType, OrganizationTypeDTO organizationSubType, List<TagDTO> tags) {
        this.startDate = startDate;
        this.id = id;
        this.endDate = endDate;
        this.name = name;
        this.description = description;
        this.expertise = expertise;
        this.organizationType = organizationType;
        this.organizationSubType = organizationSubType;
        this.tags = tags;
    }

}