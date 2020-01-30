package com.kairos.persistence.model.staff;

import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.staff.staff.StaffChildDetailDTO;
import com.kairos.persistence.model.organization.team.TeamDTO;
import com.kairos.persistence.model.user.employment.query_result.EmploymentQueryResult;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
@Getter
@Setter
public class StaffKpiFilterQueryResult {
    private Long id;
    private String firstName;
    private String lastName;
    private List<Long> unitIds;
    private Long unitId;
    private String unitName;
    private String cprNumber;
    private int staffAge;
    private List<EmploymentQueryResult> employment;
    private List<DayTypeDTO> dayTypeDTOS;
    private List<TeamDTO> teams;
    private List<StaffChildDetailDTO> staffChildDetails;

}
