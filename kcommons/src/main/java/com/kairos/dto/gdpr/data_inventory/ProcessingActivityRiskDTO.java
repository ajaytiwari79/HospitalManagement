package com.kairos.dto.gdpr.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class ProcessingActivityRiskDTO {

    @NotNull(message = "error.message.id.notnull")
    private BigInteger id;

    @Valid
    @NotEmpty
    private List<OrganizationLevelRiskDTO> risks;

    private List<ProcessingActivityRiskDTO> subProcessingActivities=new ArrayList<>();

}
