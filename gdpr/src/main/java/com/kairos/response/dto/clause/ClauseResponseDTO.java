package com.kairos.response.dto.clause;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;
import com.kairos.dto.gdpr.ServiceCategoryDTO;
import com.kairos.dto.gdpr.SubServiceCategoryDTO;
import com.kairos.dto.gdpr.master_data.AccountTypeVO;
import com.kairos.persistence.model.clause_tag.ClauseTag;
import com.kairos.response.dto.master_data.TemplateTypeResponseDTO;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
public class ClauseResponseDTO {

    @NotNull(message = "error.message.id.notnull")
    private Long id;

    @NotBlank(message = "error.message.title.notNull.orEmpty")
    private String title;

    @Valid
    @NotNull
    private List<ClauseTag> tags = new ArrayList<>();

    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;

    private List<TemplateTypeResponseDTO> templateTypes;

    private List<OrganizationTypeDTO> organizationTypes;

    private List<OrganizationSubTypeDTO> organizationSubTypes;

    private List<ServiceCategoryDTO> organizationServices;

    private List<SubServiceCategoryDTO> organizationSubServices;

    private List<AccountTypeVO> accountTypes;
}
