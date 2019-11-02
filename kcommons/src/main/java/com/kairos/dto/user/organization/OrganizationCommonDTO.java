package com.kairos.dto.user.organization;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by vipul on 20/9/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class OrganizationCommonDTO {
    private String name;
    private Long id;
    List<OrganizationCommonDTO> children;

    public OrganizationCommonDTO(Long id,String name ) {
        this.id = id;
        this.name = name;

    }



}
