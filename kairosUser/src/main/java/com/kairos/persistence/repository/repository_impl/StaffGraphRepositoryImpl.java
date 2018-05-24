package com.kairos.persistence.repository.repository_impl;

import com.kairos.persistence.repository.user.staff.CustomStaffGraphRepository;
import com.kairos.response.dto.web.open_shift.priority_group.StaffIncludeFilter;
import org.neo4j.ogm.session.Session;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO_STAFF;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_EMPLOYMENT_TYPE;
import static com.kairos.persistence.model.constants.RelationshipConstants.IN_UNIT;

public class StaffGraphRepositoryImpl implements CustomStaffGraphRepository {

    @Inject
    private Session session;

    public Set<Long> getStaffByPriorityGroupStaffIncludeFilter(StaffIncludeFilter staffIncludeFilter, Long unitId) {

        String staffFilterQuery = "Match (staff:Staff)-[:" + BELONGS_TO_STAFF + "]-(up:UnitPosition)-[:"+IN_UNIT+"]-(org:Organization) where id(org)={unitId}";
/*
                +"Match(up)-[:"+ HAS_EMPLOYMENT_TYPE+"]-(employmentType:EmploymentType) where id(employmentTYpe) in ";
*/
        StringBuilder stringBuilder = new StringBuilder();

        if(Optional.ofNullable(staffIncludeFilter.getEmploymentTypeIds()).isPresent()&&!staffIncludeFilter.getEmploymentTypeIds().isEmpty()||staffIncludeFilter.isAllowForFlexPool()) {
            stringBuilder = new StringBuilder("Match(up)-[:"+ HAS_EMPLOYMENT_TYPE+"]-(employmentType:EmploymentType) where ");
            if(Optional.ofNullable(staffIncludeFilter.getEmploymentTypeIds()).isPresent()&&!staffIncludeFilter.getEmploymentTypeIds().isEmpty()) {
                stringBuilder.append("id(employmentType) in {employmentTypeIds} or");
            }
            if(staffIncludeFilter.isAllowForFlexPool()) {
                stringBuilder.append("employmentType.allowedForFlexPool = true or");
            }

            int index = stringBuilder.lastIndexOf("or");
            stringBuilder.replace(index,"or".length(), "");

        }
        stringBuilder.append("return id(staff) as ids");
        staffFilterQuery += stringBuilder.toString();
        Map<String, Object> queryParameters = new HashMap();
        queryParameters.put("unitId", unitId);
        queryParameters.put("employmentTypeIds",staffIncludeFilter.getEmploymentTypeIds());
        Iterable<Long> staffIds = session.query(Long.class , staffFilterQuery, queryParameters);
        Set<Long> staffIdsSet = new HashSet<Long>();
        staffIds.forEach(staffIdsSet::add);

        return staffIdsSet;

    }


}
