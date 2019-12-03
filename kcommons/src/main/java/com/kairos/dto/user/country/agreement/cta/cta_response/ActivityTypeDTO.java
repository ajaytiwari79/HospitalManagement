package com.kairos.dto.user.country.agreement.cta.cta_response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ActivityTypeDTO {
    private Long id;
    private String name;
    private CTAAndWTASettingsActivityTabDTO ctaAndWtaSettingsActivityTab;
    private BigInteger categoryId;

}
