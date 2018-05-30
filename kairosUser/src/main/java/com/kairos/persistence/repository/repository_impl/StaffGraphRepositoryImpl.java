package com.kairos.persistence.repository.repository_impl;

import com.kairos.persistence.model.user.staff.StaffUnitPositionQueryResult;
import com.kairos.persistence.repository.user.staff.CustomStaffGraphRepository;
import com.kairos.response.dto.web.StaffDTO;
import com.kairos.response.dto.web.open_shift.priority_group.StaffIncludeFilter;
import com.kairos.response.dto.web.open_shift.priority_group.StaffIncludeFilterDTO;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@Repository
public class StaffGraphRepositoryImpl implements CustomStaffGraphRepository {

    @Inject
    private Session session;

    public List<StaffUnitPositionQueryResult> getStaffByPriorityGroupStaffIncludeFilter(StaffIncludeFilterDTO staffIncludeFilterDTO, Long unitId) {

/*        String staffFilterQuery = "Match (staff:Staff)-[:" + BELONGS_TO_STAFF + "]-(up:UnitPosition)-[:"+IN_UNIT+"]-(org:Organization) where id(org)={unitId}" +
                "Match(up)-[:"+ HAS_EXPERTISE_IN+"]-(expertise:Expertise) where id(expertise) in {expertiseIds}";*/

        String staffFilterQuery = "Match (up:UnitPosition)-[:"+IN_UNIT +"]-(org:Organization) where id(org)={unitId} and up.endDate is null or up.endate>{maxDate}"+
                "Match(up)-[:"+HAS_EXPERTISE_IN+"]-(expertise:Expertise) where id(expertise) in {expertiseIds}";

        /*
                +"Match(up)-[:"+ HAS_EMPLOYMENT_TYPE+"]-(employmentType:EmploymentType) whereMap<String, Object> queryParameters = new HashMap();Map<String, Object> queryParameters = new HashMap();Map<String, Object> queryParameters = new HashMap(); id(employmentTYpe) in ";
*/
        StringBuilder stringBuilder = new StringBuilder();

        if(Optional.ofNullable(staffIncludeFilterDTO.getEmploymentTypeIds()).isPresent()&&!staffIncludeFilterDTO.getEmploymentTypeIds().isEmpty()||staffIncludeFilterDTO.isAllowForFlexPool()) {
            stringBuilder = new StringBuilder("Match(up)-[:"+ HAS_EMPLOYMENT_TYPE+"]-(employmentType:EmploymentType) where ");
            if(Optional.ofNullable(staffIncludeFilterDTO.getEmploymentTypeIds()).isPresent()&&!staffIncludeFilterDTO.getEmploymentTypeIds().isEmpty()) {
                stringBuilder.append("id(employmentType) in {employmentTypeIds} or ");
            }
            if(staffIncludeFilterDTO.isAllowForFlexPool()) {
                stringBuilder.append("employmentType.allowedForFlexPool = true or ");
            }

            int index = stringBuilder.lastIndexOf("or");
            stringBuilder.replace(index,index + "or".length(), "");

        }
        stringBuilder.append("Match(staff)-[:"+BELONGS_TO_STAFF+"]-(up) ");
        stringBuilder.append("return id(staff) as staffId, id(up) as unitPositionId, up.workingDaysInWeek as workingDaysPerWeek , up.totalWeeklyMinutes as contractedMinbyWeek, up.startDateMillis as startDate, up.endDateMillis as endDate ");
        staffFilterQuery += stringBuilder.toString();
        Map<String, Object> queryParameters = new HashMap();
        queryParameters.put("unitId", unitId);
        queryParameters.put("expertiseIds", staffIncludeFilterDTO.getExpertiseIds());
        queryParameters.put("maxDate", staffIncludeFilterDTO.getOpenShiftDate());
        queryParameters.put("employmentTypeIds",staffIncludeFilterDTO.getEmploymentTypeIds());
        Iterable<StaffUnitPositionQueryResult> staffsUnitPositions = session.query(StaffUnitPositionQueryResult.class , staffFilterQuery, queryParameters);
        List<StaffUnitPositionQueryResult> staffUnitPositionList = new ArrayList<>();
        staffsUnitPositions.forEach(staffUnitPositionList::add);

        return staffUnitPositionList;

    }


}
