package com.kairos.dto.user.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * Created by oodles on 14/9/16.
 */


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class OrganizationTypeDTO {

    private Long id;
    @NotEmpty(message = "error.OrganizationType.name.notEmpty")
    @NotNull(message = "error.OrganizationType.name.notnull")
    private String name;
    private List<Long> levels;
    private String description;

    public OrganizationTypeDTO(String name, List<Long> levels) {
        this.name = name;
        this.levels = levels;
    }
    public OrganizationTypeDTO(Long id) {
        this.id = id;
    }

}

