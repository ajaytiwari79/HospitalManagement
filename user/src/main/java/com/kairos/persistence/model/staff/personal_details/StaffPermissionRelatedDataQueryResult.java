package com.kairos.persistence.model.staff.personal_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.kpermissions.OtherPermissionDTO;
import com.kairos.enums.StaffStatusEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.newHashSet;

@Getter
@Setter
@QueryResult
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffPermissionRelatedDataQueryResult {


    private Long staffId;
    private Set<Long> teamIds;
    private Set<Long> employmentTypeIds;
    private Set<Long> expertiseIds;
    private Set<StaffStatusEnum> staffStatuses;
    private Set<Long> tagIds;
    private Set<Long> unionIds;

    public OtherPermissionDTO getForOtherPermissions() {
        return new OtherPermissionDTO(expertiseIds,unionIds,teamIds,employmentTypeIds,tagIds,staffStatuses,newHashSet(),staffId);
    }

}
