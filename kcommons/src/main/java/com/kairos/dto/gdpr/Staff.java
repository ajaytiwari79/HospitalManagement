package com.kairos.dto.gdpr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Staff {

    @NotNull(message = "error.message.staffid.notnull")
    private Long staffId;
    private String lastName;
    @NotBlank(message = "error.message.staffName.notnull")
    private String firstName;

}
