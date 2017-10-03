package com.kairos.client.dto;

import com.kairos.persistence.model.organization.OrganizationTypeAndSubTypeDTO;

import java.util.HashMap;
import java.util.Map;

public class OrganizationSkillAndOrganizationTypesDTO {
private OrganizationTypeAndSubTypeDTO organizationTypeAndSubTypeDTO;
private Map<String, Object> availableSkills=new HashMap<>();

    public OrganizationSkillAndOrganizationTypesDTO() {
        //default constructor
    }

    public OrganizationSkillAndOrganizationTypesDTO(OrganizationTypeAndSubTypeDTO organizationTypeAndSubTypeDTO,
         Map<String, Object> availableSkills) {
        this.organizationTypeAndSubTypeDTO = organizationTypeAndSubTypeDTO;
        this.availableSkills = availableSkills;
    }

    public OrganizationTypeAndSubTypeDTO getOrganizationTypeAndSubTypeDTO() {
        return organizationTypeAndSubTypeDTO;
    }

    public void setOrganizationTypeAndSubTypeDTO(OrganizationTypeAndSubTypeDTO organizationTypeAndSubTypeDTO) {
        this.organizationTypeAndSubTypeDTO = organizationTypeAndSubTypeDTO;
    }

    public Map<String, Object> getAvailableSkills() {
        return availableSkills;
    }

    public void setAvailableSkills(Map<String, Object> availableSkills) {
        this.availableSkills = availableSkills;
    }
}
