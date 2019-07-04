package com.kairos.response.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@JsonIgnoreProperties
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetaDataCommonResponseDTO {

    private Long id;

    private String name;

}
