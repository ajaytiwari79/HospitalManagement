package com.kairos.persistence.model.staff.personal_details;

import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.persistence.model.organization.time_slot.TimeSlotWrapper;
import com.kairos.persistence.model.user.employment.query_result.StaffEmploymentDetails;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by prabjot on 17/5/17.
 */
@QueryResult
@Getter
@Setter
@NoArgsConstructor
public class StaffAdditionalInfoQueryResult {
    private String name;
    private long id;
    private List<Long> teams;
    private List<Long> skills;
    private String profilePic;
    private Long unitId;
    private StaffEmploymentDetails employments;
    private Date organizationNightStartTimeFrom;
    private Date organizationNightEndTimeTo;
    private List<DayTypeDTO> dayTypes;
    private ZoneId unitTimeZone;
    private List<TimeSlotWrapper> timeSlotSets;
    private UserAccessRoleDTO user;
    private UserAccessRoleDTO userAccessRoleDTO;
    private Long staffUserId;
    private String cprNumber;
    private List<StaffChildDetail> staffChildDetails;
}
