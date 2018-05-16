package com.kairos.response.dto.web.organizationtype_service_dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationServiceResponseDto {

    private Long id;
    private String name;
      private List<OrganizationServiceResponseDto>  organizationSubServices;

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

    public List<OrganizationServiceResponseDto> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<OrganizationServiceResponseDto> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }
}
