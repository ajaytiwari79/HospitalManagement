package com.kairos.dto.gdpr.data_inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.ManagingOrganization;
import com.kairos.dto.gdpr.Staff;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class ProcessingActivityDTO {


    private Long id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;

    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;

    @NotNull(message = "error.message.managingDepartment.notNull")
    private ManagingOrganization managingDepartment;

    @NotNull(message = "error.message.processOwner.notNull")
    private Staff processOwner;
    private Set<Long> processingPurposes;
    private Set<Long> dataSources;
    private Set<Long> transferMethods;
    private Set<Long> accessorParties;
    private Set<Long> processingLegalBasis;
    @Valid
    private List<ProcessingActivityDTO> subProcessingActivities=new ArrayList<>();
    @Valid
    private List<RelatedDataSubjectDTO> dataSubjectList =new ArrayList<>();
    private Long responsibilityType;
    private Integer controllerContactInfo;
    private Integer dpoContactInfo;
    private Integer jointControllerContactInfo;
    private Long minDataSubjectVolume;
    private Long maxDataSubjectVolume;
    private Integer dataRetentionPeriod;
    private boolean suggestToCountryAdmin;
    private boolean suggested=false;
    private Set<Long> assetIds;

    @Valid
    private List<OrganizationLevelRiskDTO> risks = new ArrayList<>();


    public void setName(String name) {
        this.name = name.trim();
    }

    public void setDescription(String description) {
        this.description = description.trim();
    }
}
