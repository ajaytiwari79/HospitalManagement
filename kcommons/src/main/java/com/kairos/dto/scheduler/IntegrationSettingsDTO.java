package com.kairos.dto.scheduler;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
@Getter
@Setter
public class IntegrationSettingsDTO {


    private BigInteger id;
    @NotEmpty(message = "name can not be null") @NotNull(message = "name can not be null")
    private String name;
    //@NotEmpty(message = "error.description.notnull") @NotNull(message = "error.description.notnull")
    private String description;

    @NotEmpty(message = "unique key can not be null") @NotNull(message = "unique key can not be null")
    private String uniqueKey;
    private boolean isEnabled = true;
    private BigInteger countryId;

}
