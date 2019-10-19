package com.kairos.dto.user.country.system_setting;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

//  Created By vipul   On 10/8/18
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class AccountTypeDTO {
    private Long id;
    @NotNull
    private String name;
    private String description;

}
