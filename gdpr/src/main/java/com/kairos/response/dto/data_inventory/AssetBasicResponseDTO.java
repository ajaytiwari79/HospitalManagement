package com.kairos.response.dto.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.embeddables.ManagingOrganization;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class AssetBasicResponseDTO {

    private Long id;

    private String name;

    private String description;

    private String hostingLocation;

    private ManagingOrganization managingDepartment;

    private boolean active;

    private RelatedProcessingActivityResponseDTO processingActivity;


    public AssetBasicResponseDTO(BigInteger id, String name,BigInteger processingActivityId,String processingActivityName, boolean subProcessingActivity,BigInteger parentProcessingActivityId ,String parentProcessingActivityName) {
        this.id =id.longValue();
        this.name = name;
        this.processingActivity = new RelatedProcessingActivityResponseDTO(processingActivityId.longValue(), processingActivityName, subProcessingActivity,parentProcessingActivityId,parentProcessingActivityName);
    }
}
