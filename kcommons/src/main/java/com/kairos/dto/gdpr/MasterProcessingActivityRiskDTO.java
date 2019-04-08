package com.kairos.dto.gdpr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class MasterProcessingActivityRiskDTO {

    @NotNull(message = "error.message.id.notnull")
    private Long id;

    @Valid
    private List<BasicRiskDTO> risks=new ArrayList<>();

    @Valid
    private List<MasterProcessingActivityRiskDTO> subProcessingActivities=new ArrayList<>();

}
