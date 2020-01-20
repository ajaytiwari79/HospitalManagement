package com.kairos.service.kpermissions;

import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.country.experties.ExpertiseDTO;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.dto.user.organization.union.UnionDTO;
import com.kairos.dto.user.team.TeamDTO;
import com.kairos.enums.StaffStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.newArrayList;

@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDefaultDataDTO {

    private List<ExpertiseDTO> expertises;
    private List<UnionDTO> unions;
    private List<EmploymentTypeDTO> employmentTypes;
    private List<TeamDTO> teams;
    private List<TagDTO> tags;
    private List<StaffStatusEnum> staffStatuses = newArrayList(StaffStatusEnum.values());

    //TODO Please Don't remove the Getters these all are the overrided method
    public List<ExpertiseDTO> getExpertise() {
        return expertises;
    }

    public List<UnionDTO> getUnion() {
        return unions;
    }

    public List<EmploymentTypeDTO> getEmploymentType() {
        return employmentTypes;
    }

    public List<TeamDTO> getTeam() {
        return teams;
    }

    public List<TagDTO> getTag() {
        return tags;
    }

    public List<StaffStatusEnum> getStaffStatuses() {
        return staffStatuses;
    }
}
