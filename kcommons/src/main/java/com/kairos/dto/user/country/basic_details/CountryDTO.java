package com.kairos.dto.user.country.basic_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author pradeep
 * @date - 11/4/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class CountryDTO {

    private Long id;
    private String name;
    private Long currencyId;

    public CountryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
