package com.kairos.dto.gdpr.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class OrganizationDataSubjectBasicDTO {


    private BigInteger id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed",regexp = "^[a-zA-Z\\s]+$")
    private String name;

    @NotEmpty(message = "Data Category  can't be  empty")
    @NotNull(message = "Data category  can't be  null")
    private Set<BigInteger> dataCategories;



}
