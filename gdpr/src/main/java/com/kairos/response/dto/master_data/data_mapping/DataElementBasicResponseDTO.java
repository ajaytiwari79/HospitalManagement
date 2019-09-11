package com.kairos.response.dto.master_data.data_mapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class DataElementBasicResponseDTO {

    private Long id;

    private String name;

    private Boolean deleted;

}
