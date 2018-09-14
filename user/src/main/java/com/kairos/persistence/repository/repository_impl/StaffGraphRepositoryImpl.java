package com.kairos.persistence.repository.repository_impl;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.persistence.model.staff.StaffUnitPositionQueryResult;
import com.kairos.persistence.repository.user.staff.CustomStaffGraphRepository;
import com.kairos.dto.activity.open_shift.priority_group.StaffIncludeFilterDTO;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@Repository
public class StaffGraphRepositoryImpl implements CustomStaffGraphRepository {

    @Inject
    private Session session;

    public List<StaffUnitPositionQueryResult> getStaffByPriorityGroupStaffIncludeFilter(StaffIncludeFilterDTO staffIncludeFilterDTO, Long unitId) {


        String staffFilterQuery = "Match (up:UnitPosition)-[:" + IN_UNIT + "]-(org:Organization) where id(org)={unitId} and (up.startDateMillis<{maxDate} and (up.endDateMillis is null or up.endateMillis>{maxDate}))" +
                " Match(up)-[:" + HAS_EXPERTISE_IN + "]-(expertise:Expertise) where id(expertise) in {expertiseIds}";

        StringBuilder stringBuilder = new StringBuilder();

        if (Optional.ofNullable(staffIncludeFilterDTO.getEmploymentTypeIds()).isPresent() && !staffIncludeFilterDTO.getEmploymentTypeIds().isEmpty() || staffIncludeFilterDTO.isAllowForFlexPool()) {
            stringBuilder = new StringBuilder(" Match(up)-[:" + HAS_EMPLOYMENT_TYPE + "]-(employmentType:EmploymentType) where ");
            if (Optional.ofNullable(staffIncludeFilterDTO.getEmploymentTypeIds()).isPresent() && !staffIncludeFilterDTO.getEmploymentTypeIds().isEmpty()) {
                stringBuilder.append("id(employmentType) in {employmentTypeIds} or ");
            }
            if (staffIncludeFilterDTO.isAllowForFlexPool()) {
                stringBuilder.append("employmentType.allowedForFlexPool = true or ");
            }

            int index = stringBuilder.lastIndexOf("or");
            stringBuilder.replace(index, index + "or".length(), "");

        }
        stringBuilder.append(" Match(staff)-[:" + BELONGS_TO_STAFF + "]-(up) ");
        stringBuilder.append("return {staffId:id(staff),staffEmail:staff.email,unitPositionId:id(up),workingDaysPerWeek:up.workingDaysInWeek,contractedMinByWeek:up.totalWeeklyMinutes," +
                " startDate:up.startDateMillis, endDate:up.endDateMillis } as data ");
        staffFilterQuery += stringBuilder.toString();
        Map<String, Object> queryParameters = new HashMap();
        queryParameters.put("unitId", unitId);
        queryParameters.put("expertiseIds", staffIncludeFilterDTO.getExpertiseIds());
        queryParameters.put("maxDate", staffIncludeFilterDTO.getMaxOpenShiftDate());
        queryParameters.put("employmentTypeIds", staffIncludeFilterDTO.getEmploymentTypeIds());
        List<Map> my=StreamSupport.stream(Spliterators.spliteratorUnknownSize(session.query(Map.class , staffFilterQuery, queryParameters).iterator(), Spliterator.ORDERED), false).collect(Collectors.<Map> toList());
        List<StaffUnitPositionQueryResult> staffUnitPositionList = ObjectMapperUtils.copyPropertiesOfListByMapper(my,StaffUnitPositionQueryResult.class);


        return staffUnitPositionList;

    }


}
