package com.kairos.dto.gdpr.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.*;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class MasterProcessingActivityDTO {

    private Long id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private  String name;

    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;


    @Valid
    @NotEmpty(message = "error.message.organizationType.not.Selected")
    private Set<OrganizationTypeDTO> organizationTypes =new HashSet<>();

    @Valid
    @NotEmpty(message = "error.message.organizationSubType.not.Selected")
    private Set<OrganizationSubTypeDTO> organizationSubTypes =new HashSet<>();

    @Valid
    @NotEmpty(message = "error.message.serviceCategory.not.Selected")
    private Set<ServiceCategoryDTO> organizationServices=new HashSet<>();

    @Valid
    @NotEmpty(message = "error.message.serviceSubCategory.not.Selected")
    private Set<SubServiceCategoryDTO> organizationSubServices=new HashSet<>();

    private List<MasterProcessingActivityDTO> subProcessingActivities=new ArrayList<>();
}





