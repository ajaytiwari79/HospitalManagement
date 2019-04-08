package com.kairos.response.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetaDataCommonResponseDTO {

    private Long id;

    private String name;

}
