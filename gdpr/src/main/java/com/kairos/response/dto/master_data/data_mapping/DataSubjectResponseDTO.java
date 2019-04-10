package com.kairos.response.dto.master_data.data_mapping;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class DataSubjectResponseDTO {

    private Long id;

    private String name;

    private String description;

    private Long countryId;

    private List<OrganizationTypeDTO> organizationTypes;

    private List<OrganizationSubTypeDTO> organizationSubTypes;

    private List<DataCategoryResponseDTO> dataCategories;

}
