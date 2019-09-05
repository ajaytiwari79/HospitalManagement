package com.kairos.response.dto.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;
import com.kairos.dto.gdpr.ServiceCategoryDTO;
import com.kairos.dto.gdpr.SubServiceCategoryDTO;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class MasterProcessingActivityResponseDTO {

    @NotNull(message = "error.message.id.notnull")
    private Long id;
    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String name;
    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;
    private List<OrganizationTypeDTO> organizationTypes;
    private List<OrganizationSubTypeDTO> organizationSubTypes;
    private List<ServiceCategoryDTO> organizationServices;
    private List<SubServiceCategoryDTO> organizationSubServices;
    private List<MasterProcessingActivityResponseDTO> subProcessingActivities=new ArrayList<>();
    private LocalDate suggestedDate;
    private SuggestedDataStatus suggestedDataStatus;
    private Boolean hasSubProcessingActivity;

    public MasterProcessingActivityResponseDTO(@NotNull Long id, @NotBlank String name, @NotBlank String description, LocalDate suggestedDate, SuggestedDataStatus suggestedDataStatus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.suggestedDate = suggestedDate;
        this.suggestedDataStatus = suggestedDataStatus;
    }

}
