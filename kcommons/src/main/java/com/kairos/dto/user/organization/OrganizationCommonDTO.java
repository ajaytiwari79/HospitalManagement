package com.kairos.dto.user.organization;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by vipul on 20/9/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationCommonDTO {
    private String name;
    private Long id;
    List<OrganizationCommonDTO> children;

    public OrganizationCommonDTO() {

    }

    public OrganizationCommonDTO(Long id,String name ) {
        this.name = name;
        this.id = id;
    }

    public List<OrganizationCommonDTO> getChildren() {
        return children;
    }

    public void setChildren(List<OrganizationCommonDTO> children) {
        this.children = children;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
