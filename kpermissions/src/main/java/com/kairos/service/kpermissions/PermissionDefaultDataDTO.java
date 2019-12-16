package com.kairos.service.kpermissions;

import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.country.experties.ExpertiseDTO;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.dto.user.organization.union.UnionDTO;
import com.kairos.dto.user.team.TeamDTO;
import com.kairos.enums.StaffStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.newArrayList;

@Getter
@Setter
public class PermissionDefaultDataDTO {

    private List<ExpertiseDTO> expertises;
    private List<UnionDTO> unions;
    private List<EmploymentTypeDTO> employmentTypes;
    private List<TeamDTO> teams;
    private List<TagDTO> tags;
    private List<StaffStatusEnum> staffStatusEnums = newArrayList(StaffStatusEnum.values());

}
