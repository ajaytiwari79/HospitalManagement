package com.kairos.response.dto.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.ManagingOrganization;
import com.kairos.dto.gdpr.Staff;
import com.kairos.dto.gdpr.data_inventory.RelatedDataSubjectDTO;
import com.kairos.response.dto.common.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessingActivityResponseDTO {

    private Long id;
    private String name;
    private String description;
    private ManagingOrganization managingDepartment;
    private Staff processOwner;
    private List<ProcessingPurposeResponseDTO> processingPurposes;
    private List<DataSourceResponseDTO> dataSources;
    private List<AccessorPartyResponseDTO> accessorParties;
    private List<TransferMethodResponseDTO> transferMethods;
    private List<ProcessingLegalBasisResponseDTO> processingLegalBasis;
    private ResponsibilityTypeResponseDTO responsibilityType;
    private List<RiskBasicResponseDTO> risks;
    private Integer controllerContactInfo;
    private Integer dpoContactInfo;
    private Integer jointControllerContactInfo;
    private Long minDataSubjectVolume;
    private Long maxDataSubjectVolume;
    private Integer dataRetentionPeriod;
    private Boolean active;
    private Boolean suggested;
    private List<ProcessingActivityResponseDTO> subProcessingActivities=new ArrayList<>();
    private List<RelatedDataSubjectDTO> dataSubjectList = new ArrayList<>();
    private List<AssetBasicResponseDTO> assets=new ArrayList<>();

}
