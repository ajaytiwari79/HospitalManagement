package com.kairos.persistence.repository.repository_impl;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.persistence.model.staff.StaffKpiFilterQueryResult;
import com.kairos.persistence.model.staff.StaffEmploymentQueryResult;
import com.kairos.persistence.repository.user.staff.CustomStaffGraphRepository;
import com.kairos.dto.activity.open_shift.priority_group.StaffIncludeFilterDTO;
import org.apache.commons.collections.CollectionUtils;
import org.neo4j.ogm.model.Result;
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

    public List<StaffEmploymentQueryResult> getStaffByPriorityGroupStaffIncludeFilter(StaffIncludeFilterDTO staffIncludeFilterDTO, Long unitId) {


        String staffFilterQuery = "Match (up:Employment)-[:" + IN_UNIT + "]-(org:Organization) where id(org)={unitId} and (up.startDateMillis<{maxDate} and (up.endDateMillis is null or up.endateMillis>{maxDate}))" +
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
        List<Map> result=StreamSupport.stream(Spliterators.spliteratorUnknownSize(session.query(Map.class , staffFilterQuery, queryParameters).iterator(), Spliterator.ORDERED), false).collect(Collectors.<Map> toList());
        List<StaffEmploymentQueryResult> staffUnitPositionList = ObjectMapperUtils.copyPropertiesOfListByMapper(result,StaffEmploymentQueryResult.class);


        return staffUnitPositionList;

    }

    @Override
    public List<StaffKpiFilterQueryResult> getStaffsByFilter(Long organizationId, List<Long> unitIds, List<Long> employmentType, String startDate, String endDate, List<Long> staffIds,boolean parentOrganization) {
        Map<String, Object> queryParameters = new HashMap();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("MATCH (org:Organization)");
        if(CollectionUtils.isNotEmpty(unitIds)){
            stringBuilder.append(" WHERE id(org) IN {unitIds}");
            queryParameters.put("unitIds", unitIds);
        }else{
            stringBuilder.append(" WHERE id(org) = {organizationId}");
            queryParameters.put("organizationId", organizationId);
        }
        if(CollectionUtils.isNotEmpty(employmentType)) {
            stringBuilder.append(" MATCH(empType:EmploymentType) WHERE id(empType) IN {employmentType}");
            queryParameters.put("employmentType", employmentType);
        }
        if(CollectionUtils.isNotEmpty(unitIds) || !parentOrganization){
            stringBuilder.append(" MATCH (org)-[:" + IN_UNIT + "]-(up:Employment{deleted:false})-[:" + BELONGS_TO_STAFF + "]-(staff:Staff)");
        }else {
            stringBuilder.append(" MATCH (org)-[:" + HAS_POSITIONS + "]-(position:Position)-[:" + BELONGS_TO + "]-(staff:Staff)-[:" + BELONGS_TO_STAFF + "]-(up:Employment{deleted:false}) ");
        }
        if(CollectionUtils.isNotEmpty(staffIds)) {
            stringBuilder.append(" WHERE id(staff) IN {staffIds}");
            queryParameters.put("staffIds",staffIds);
        }
        stringBuilder.append(" MATCH (up)-[:"+ HAS_EMPLOYMENT_LINES +"]-(employmentLine:EmploymentLine)"+
                "WHERE  date(employmentLine.startDate) <= date({endDate}) AND (NOT exists(employmentLine.endDate) OR date(employmentLine.endDate) >= date({startDate}))"+
                "MATCH (employmentLine)-[:"+HAS_EMPLOYMENT_TYPE+"]-(empType)  " +
                "WITH  COLLECT({totalWeeklyMinutes:(employmentLine.totalWeeklyMinutes % 60),startDate:employmentLine.startDate,totalWeeklyHours:(employmentLine.totalWeeklyMinutes / 60), hourlyCost:employmentLine.hourlyCost,id:id(employmentLine), workingDaysInWeek:employmentLine.workingDaysInWeek,\n" +
                "avgDailyWorkingHours:employmentLine.avgDailyWorkingHours,fullTimeWeeklyMinutes:employmentLine.fullTimeWeeklyMinutes,totalWeeklyMinutes:employmentLine.totalWeeklyMinutes}) as ups,up,staff,org "+
                "WITH {id:id(up),startDate:up.startDate,endDate:up.endDate,employmentLines:ups } as up,staff,org\n" +
                "RETURN id(staff) as id,staff.firstName as firstName ,staff.lastName as lastName,id(org) as unitId,org.name as unitName,collect(up) as unitPosition");
        queryParameters.put("endDate", endDate);
        queryParameters.put("startDate", startDate);

        Result results=session.query( stringBuilder.toString(), queryParameters);
        List<StaffKpiFilterQueryResult> staffKpiFilterQueryResults= new ArrayList<>();
        results.forEach(result->{
            staffKpiFilterQueryResults.add(ObjectMapperUtils.copyPropertiesByMapper(result,StaffKpiFilterQueryResult.class));
        });

        return staffKpiFilterQueryResults;
//
    }


}
