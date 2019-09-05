package com.kairos.dto.user.organization.skill;

import com.kairos.dto.user.organization.OrganizationTypeAndSubTypeDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrganizationSkillAndOrganizationTypesDTO {
private OrganizationTypeAndSubTypeDTO organizationTypeAndSubTypeDTO;
private List<Map<String,Object>> availableSkills=new ArrayList<>();

    public OrganizationSkillAndOrganizationTypesDTO() {
        //default constructor
    }

    public OrganizationSkillAndOrganizationTypesDTO(OrganizationTypeAndSubTypeDTO organizationTypeAndSubTypeDTO,
                                                    List<Map<String,Object>> availableSkills) {
        this.organizationTypeAndSubTypeDTO = organizationTypeAndSubTypeDTO;
        this.availableSkills = availableSkills;
    }

    public OrganizationTypeAndSubTypeDTO getOrganizationTypeAndSubTypeDTO() {
        return organizationTypeAndSubTypeDTO;
    }

    public void setOrganizationTypeAndSubTypeDTO(OrganizationTypeAndSubTypeDTO organizationTypeAndSubTypeDTO) {
        this.organizationTypeAndSubTypeDTO = organizationTypeAndSubTypeDTO;
    }

    public List<Map<String, Object>> getAvailableSkills() {
        return availableSkills;
    }


    public void setAvailableSkills(List<Map<String, Object>> availableSkills) {
        this.availableSkills = availableSkills;
    }
}
