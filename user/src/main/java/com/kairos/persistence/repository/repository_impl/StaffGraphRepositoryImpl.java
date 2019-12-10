package com.kairos.persistence.repository.repository_impl;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.open_shift.priority_group.StaffIncludeFilterDTO;
import com.kairos.enums.Employment;
import com.kairos.enums.FilterType;
import com.kairos.enums.ModuleId;
import com.kairos.persistence.model.staff.StaffEmploymentQueryResult;
import com.kairos.persistence.model.staff.StaffKpiFilterQueryResult;
import com.kairos.persistence.repository.user.staff.CustomStaffGraphRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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


        String staffFilterQuery = "Match (employment:Employment)-[:" + IN_UNIT + "]-(org:Unit) where id(org)={unitId} and (employment.startDateMillis<{maxDate} and (employment.endDateMillis is null or employment.endateMillis>{maxDate}))" +
                " Match(employment)-[:" + HAS_EXPERTISE_IN + "]-(expertise:Expertise) where id(expertise) in {expertiseIds}";

        StringBuilder stringBuilder = new StringBuilder();

        if (Optional.ofNullable(staffIncludeFilterDTO.getEmploymentTypeIds()).isPresent() && !staffIncludeFilterDTO.getEmploymentTypeIds().isEmpty() || staffIncludeFilterDTO.isAllowForFlexPool()) {
            stringBuilder = new StringBuilder(" Match(employment)-[:" + HAS_EMPLOYMENT_TYPE + "]-(employmentType:EmploymentType) where ");
            if (Optional.ofNullable(staffIncludeFilterDTO.getEmploymentTypeIds()).isPresent() && !staffIncludeFilterDTO.getEmploymentTypeIds().isEmpty()) {
                stringBuilder.append("id(employmentType) in {employmentTypeIds} or ");
            }
            if (staffIncludeFilterDTO.isAllowForFlexPool()) {
                stringBuilder.append("employmentType.allowedForFlexPool = true or ");
            }

            int index = stringBuilder.lastIndexOf("or");
            stringBuilder.replace(index, index + "or".length(), "");

        }
        stringBuilder.append(" Match(staff)-[:" + BELONGS_TO_STAFF + "]-(employment) ");
        stringBuilder.append("return {staffId:id(staff),staffEmail:staff.email,employmentId:id(employment),workingDaysPerWeek:employment.workingDaysInWeek,contractedMinByWeek:employment.totalWeeklyMinutes," +
                " startDate:employment.startDateMillis, endDate:employment.endDateMillis } as data ");
        staffFilterQuery += stringBuilder.toString();
        Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put("unitId", unitId);
        queryParameters.put("expertiseIds", staffIncludeFilterDTO.getExpertiseIds());
        queryParameters.put("maxDate", staffIncludeFilterDTO.getMaxOpenShiftDate());
        queryParameters.put("employmentTypeIds", staffIncludeFilterDTO.getEmploymentTypeIds());
        List<Map> result=StreamSupport.stream(Spliterators.spliteratorUnknownSize(session.query(Map.class , staffFilterQuery, queryParameters).iterator(), Spliterator.ORDERED), false).collect(Collectors.<Map> toList());
        return ObjectMapperUtils.copyPropertiesOfCollectionByMapper(result,StaffEmploymentQueryResult.class);
    }

    @Override
    public List<StaffKpiFilterQueryResult> getStaffsByFilter(Long organizationId, List<Long> unitIds, List<Long> employmentType, String startDate, String endDate, List<Long> staffIds,boolean parentOrganization) {
        Map<String, Object> queryParameters = new HashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("MATCH (org:Unit)");
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
            stringBuilder.append(" MATCH (org)-[:" + IN_UNIT + "]-(employment:Employment{deleted:false})-[:" + BELONGS_TO_STAFF + "]-(staff:Staff)-[:" + BELONGS_TO + "]->(user:User) ");
        }else {
            stringBuilder.append(" MATCH (org)-[:" + HAS_POSITIONS + "]-(position:Position)-[:" + BELONGS_TO + "]-(staff:Staff)-[:" + BELONGS_TO_STAFF + "]-(employment:Employment{deleted:false}) MATCH (staff)-[:" + BELONGS_TO + "]->(user:User) ");
        }
        if(CollectionUtils.isNotEmpty(staffIds)) {
            stringBuilder.append(" WHERE id(staff) IN {staffIds}");
            queryParameters.put("staffIds",staffIds);
        }
        stringBuilder.append(" MATCH (employment)-[:"+ HAS_EMPLOYMENT_LINES +"]-(employmentLine:EmploymentLine)"+
                "MATCH(employment)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise)"+
                "WHERE  date(employmentLine.startDate) <= date({endDate}) AND (NOT exists(employmentLine.endDate) OR date(employmentLine.endDate) >= date({startDate}))"+
                "MATCH (employmentLine)-[:"+HAS_EMPLOYMENT_TYPE+"]-(empType)  " +
                "WITH  COLLECT({totalWeeklyMinutes:(employmentLine.totalWeeklyMinutes % 60),startDate:employmentLine.startDate,totalWeeklyHours:(employmentLine.totalWeeklyMinutes / 60), hourlyCost:employmentLine.hourlyCost,id:id(employmentLine), workingDaysInWeek:employmentLine.workingDaysInWeek,\n" +
                "avgDailyWorkingHours:employmentLine.avgDailyWorkingHours,fullTimeWeeklyMinutes:employmentLine.fullTimeWeeklyMinutes,totalWeeklyMinutes:employmentLine.totalWeeklyMinutes,employmentTypeId:id(empType)}) as employmentLines,employment,staff,org,user,expertise "+
                "WITH {id:id(employment),startDate:employment.startDate,endDate:employment.endDate,employmentLines:employmentLines ,expertiseId:id(expertise)} as employment,staff,org,user,expertise\n" +
                "RETURN id(staff) as id,staff.firstName as firstName ,staff.lastName as lastName,user.cprNumber AS cprNumber,id(org) as unitId,org.name as unitName,collect(employment) as employment");
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

    public List<Map> getStaffWithFilters(Long unitId, List<Long> parentOrganizationIds, String moduleId,
                                         Map<FilterType, Set<String>> filters, String searchText, String imagePath) {
        searchText=searchText.replaceAll(" ","");
        Map<String, Object> queryParameters = new HashMap<>();

        queryParameters.put("unitId", unitId);
        queryParameters.put("parentOrganizationId", parentOrganizationIds);
        if (Optional.ofNullable(filters.get(FilterType.STAFF_STATUS)).isPresent()) {
            queryParameters.put("staffStatusList",
                    filters.get(FilterType.STAFF_STATUS));
        }
        if (Optional.ofNullable(filters.get(FilterType.GENDER)).isPresent()) {
            queryParameters.put("genderList",
                    filters.get(FilterType.GENDER));
        }
        if (Optional.ofNullable(filters.get(FilterType.EMPLOYMENT_TYPE)).isPresent()) {
            queryParameters.put("employmentTypeIds",
                    convertListOfStringIntoLong(filters.get(FilterType.EMPLOYMENT_TYPE)));
        }
        if (Optional.ofNullable(filters.get(FilterType.EXPERTISE)).isPresent()) {
            queryParameters.put("expertiseIds",
                    convertListOfStringIntoLong(filters.get(FilterType.EXPERTISE)));
        }
        if (Optional.ofNullable(filters.get(FilterType.SKILLS)).isPresent()) {
            queryParameters.put("skillIds",
                    convertListOfStringIntoLong(filters.get(FilterType.SKILLS)));
        }
        if (Optional.ofNullable(filters.get(FilterType.TEAM)).isPresent()) {
            queryParameters.put("teamIds",
                    convertListOfStringIntoLong(filters.get(FilterType.TEAM)));
        }
        if (Optional.ofNullable(filters.get(FilterType.TAGS)).isPresent()) {
            queryParameters.put("tagIds",
                    convertListOfStringIntoLong(filters.get(FilterType.TAGS)));
        }
        if (Optional.ofNullable(filters.get(FilterType.GROUPS)).isPresent()) {
            queryParameters.put("GroupStaffLists",
                    filters.get(FilterType.GROUPS));
        }
        if (Optional.ofNullable(filters.get(FilterType.FUNCTIONS)).isPresent()) {
            queryParameters.put("functionIds",
                    convertListOfStringIntoLong(filters.get(FilterType.FUNCTIONS)));
        }
        if (StringUtils.isNotBlank(searchText)) {
            queryParameters.put("searchText", searchText);
        }
        queryParameters.put("imagePath", imagePath);

        String query = "";
        if (ModuleId.SELF_ROSTERING_MODULE_ID.value.equals(moduleId)) {
            query = getSelfRosteringQuery(filters, searchText);
        }else if(ModuleId.Group_TAB_ID.value.equals(moduleId)){
            query = getGroupQuery(filters, searchText);
        } else if (Optional.ofNullable(filters.get(FilterType.EMPLOYMENT)).isPresent() && filters.get(FilterType.EMPLOYMENT).contains(Employment.STAFF_WITH_EMPLOYMENT.name()) && !filters.get(FilterType.EMPLOYMENT).contains(Employment.STAFF_WITHOUT_EMPLOYMENT.name()) && !ModuleId.SELF_ROSTERING_MODULE_ID.value.equals(moduleId)) {
            query += " MATCH (staff:Staff)-[:" + BELONGS_TO_STAFF + "]-(employment:Employment{deleted:false})-[:" + IN_UNIT + "]-(organization:Unit) where id(organization)={unitId}" +
                    " MATCH (staff)-[:" + BELONGS_TO + "]->(user:User) " + getMatchQueryForNameGenderStatusOfStaffByFilters(filters, searchText) + " WITH user, staff, employment,organization ";
        } else if (Optional.ofNullable(filters.get(FilterType.EMPLOYMENT)).isPresent() && filters.get(FilterType.EMPLOYMENT).contains(Employment.STAFF_WITHOUT_EMPLOYMENT.name()) && !filters.get(FilterType.EMPLOYMENT).contains(Employment.STAFF_WITH_EMPLOYMENT.name()) && !ModuleId.SELF_ROSTERING_MODULE_ID.value.equals(moduleId)) {
            query += " MATCH (organization:Organization)-[:" + HAS_POSITIONS + "]-(position:Position)-[:" + BELONGS_TO + "]-(staff:Staff) where id(organization) IN {parentOrganizationId} " +
                    " MATCH(unit:Unit) WHERE id(unit)={unitId}" +
                    " MATCH (staff) WHERE NOT (staff)-[:" + BELONGS_TO_STAFF + "]->(:Employment)-[:" + IN_UNIT + "]-(unit)"+
                    " MATCH (staff)-[:" + BELONGS_TO + "]->(user:User)  " + getMatchQueryForNameGenderStatusOfStaffByFilters(filters, searchText) +
                    " OPTIONAL MATCH (staff)-[:" + BELONGS_TO_STAFF + "]-(employment:Employment)" +
                    " WITH user, staff, employment,organization ";
        }
        else {
            query += " MATCH (organization:Organization)-[:" + HAS_POSITIONS + "]-(position:Position)-[:" + BELONGS_TO + "]-(staff:Staff) where id(organization) IN {parentOrganizationId} " +
                    " MATCH (staff)-[:" + BELONGS_TO + "]->(user:User)  " + getMatchQueryForNameGenderStatusOfStaffByFilters(filters, searchText) +
                    " with user, staff OPTIONAL MATCH (staff)-[:" + BELONGS_TO_STAFF + "]-(employment:Employment{deleted:false})-[:" + IN_UNIT + "]-(organization:Unit) where id(organization)={unitId} with user, staff, employment,organization ";
        }

        query += getMatchQueryForRelationshipOfStaffByFilters(filters);

        query += " WITH engineerType, staff,employments, user,expertiseList,employmentList,tags Optional MATCH (staff)-[:" + HAS_CONTACT_ADDRESS + "]-(contactAddress:ContactAddress) ";

        query += " RETURN distinct {id:id(staff),tags:tags, employments:employments,expertiseList:expertiseList,employmentList:collect(employmentList[0]),city:contactAddress.city,province:contactAddress.province, " +
                "firstName:user.firstName,lastName:user.lastName,employedSince :staff.employedSince," +
                "age:duration.between(date(user.dateOfBirth),date()).years,experienceInYears:duration.between(date(user.joiningDate),date()).years," +
                "badgeNumber:staff.badgeNumber, userName:staff.userName,externalId:staff.externalId, access_token:staff.access_token," +
                "cprNumber:user.cprNumber, visitourTeamId:staff.visitourTeamId, familyName: staff.familyName, " +
                "gender:user.gender, pregnant:user.pregnant,  profilePic:{imagePath} + staff.profilePic, engineerType:id(engineerType),user_id:staff.user_id } as staff ORDER BY staff.id\n";

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(session.query(Map.class, query, queryParameters).iterator(), Spliterator.ORDERED), false).collect(Collectors.<Map>toList());
    }

    public List<Long> convertListOfStringIntoLong(Set<String> listOfString) {
        return listOfString.stream().map(Long::parseLong).collect(Collectors.toList());
    }



    private String getSelfRosteringQuery(Map<FilterType, Set<String>> filters, String searchText) {
        String query = "";
        query = " MATCH (staff:Staff)-[:" + BELONGS_TO_STAFF + "]-(employment:Employment{deleted:false,published:true})-[:" + IN_UNIT + "]-(organization:Unit) where id(organization)={unitId}" +
                " MATCH (staff)-[:" + BELONGS_TO + "]->(user:User) " + getMatchQueryForNameGenderStatusOfStaffByFilters(filters, searchText) + " WITH user, staff, employment,organization ";
        if(Optional.ofNullable(filters.get(FilterType.SKILLS)).isPresent()) {
            query += " MATCH (staff:Staff)-[staffSkillRel:" + STAFF_HAS_SKILLS + "{isEnabled:true}]->(skill) WHERE id(skill) IN {skillIds} ";
        }
        if(Optional.ofNullable(filters.get(FilterType.TEAM)).isPresent()) {
            query += " Match (staff)<-[" + TEAM_HAS_MEMBER + "]-(team:Team) where id(team)  IN {teamIds} ";
        }
        query +=" WITH user, staff, employment,organization ";
        return query;
    }

    private String getGroupQuery(Map<FilterType, Set<String>> filters, String searchText) {
        String query = "";
        query = " MATCH (staff:Staff)-[:" + BELONGS_TO_STAFF + "]-(employment:Employment{deleted:false})-[:" + IN_UNIT + "]-(organization:Unit) where id(organization)={unitId}" +
                " MATCH (staff)-[:" + BELONGS_TO + "]->(user:User) " + getMatchQueryForNameGenderStatusOfStaffByFilters(filters, searchText) + " WITH user, staff, employment,organization ";
        if(Optional.ofNullable(filters.get(FilterType.FUNCTIONS)).isPresent()) {
            query +=" MATCH (staff)-[:BELONGS_TO_STAFF]->(employment:Employment{deleted:false})-[:HAS_EMPLOYMENT_LINES]->(employmentLine:EmploymentLine)-[:APPLICABLE_FUNCTION]->(function:Function)\n" +
                    " where id(function) IN {functionIds}";
        }
        if(Optional.ofNullable(filters.get(FilterType.SKILLS)).isPresent()) {
            query += " MATCH (staff)-[staffSkillRel:" + STAFF_HAS_SKILLS + "{isEnabled:true}]->(skill) WHERE id(skill) IN {skillIds} ";
        }
        if(Optional.ofNullable(filters.get(FilterType.TEAM)).isPresent()) {
            query += " Match (staff)<-[" + TEAM_HAS_MEMBER + "]-(team:Team) where id(team)  IN {teamIds} ";
        }
        query +=" WITH user, staff, employment,organization ";;

        return query;
    }

    public String getMatchQueryForNameGenderStatusOfStaffByFilters(Map<FilterType, Set<String>> filters, String searchText) {
        String matchQueryForStaff = "";
        int countOfSubString = 0;
        if (Optional.ofNullable(filters.get(FilterType.STAFF_STATUS)).isPresent()) {
            matchQueryForStaff += appendWhereOrAndPreFixOnQueryString(countOfSubString) + "  staff.currentStatus IN {staffStatusList} ";
            countOfSubString += 1;
        }
        if (Optional.ofNullable(filters.get(FilterType.GENDER)).isPresent()) {
            matchQueryForStaff += appendWhereOrAndPreFixOnQueryString(countOfSubString) + " user.gender IN {genderList} ";
            countOfSubString += 1;
        }
        if (Optional.ofNullable(filters.get(FilterType.GROUPS)).isPresent()) {
            matchQueryForStaff += appendWhereOrAndPreFixOnQueryString(countOfSubString) + "  id(staff) IN {GroupStaffLists} ";
            countOfSubString += 1;
        }
        if (StringUtils.isNotBlank(searchText)) {
            String s="";
            matchQueryForStaff += appendWhereOrAndPreFixOnQueryString(countOfSubString) +
                    " (  LOWER(staff.firstName+staff.lastName) CONTAINS LOWER({searchText}) OR user.cprNumber STARTS WITH {searchText} )";
        }
        return matchQueryForStaff;
    }

    public String getMatchQueryForRelationshipOfStaffByFilters(Map<FilterType, Set<String>> filters) {
        String matchRelationshipQueryForStaff = "";
        if (Optional.ofNullable(filters.get(FilterType.EMPLOYMENT_TYPE)).isPresent()) {
            matchRelationshipQueryForStaff += "MATCH(employment)-[:" + HAS_EMPLOYMENT_LINES + "]-(employmentLine:EmploymentLine)  " +
                    "MATCH (employmentLine)-[empRelation:" + HAS_EMPLOYMENT_TYPE + "]-(employmentType:EmploymentType) " +
                    "WHERE id(employmentType) IN {employmentTypeIds}  " +
                    "OPTIONAL MATCH(employment)-[:" + HAS_EXPERTISE_IN + "]-(exp:Expertise) WITH staff,organization,employment,user,exp,employmentType,employmentLine \n" +
                    "OPTIONAL MATCH(employmentLine)-[:" + APPLICABLE_FUNCTION + "]-(function:Function) " +
                    "WITH staff,organization,employment,user, CASE WHEN function IS NULL THEN [] ELSE COLLECT(distinct {id:id(function),name:function.name,icon:function.icon,code:function.code}) END as functions,employmentLine,exp,employmentType\n" +
                    "WITH staff,organization,employment,user, COLLECT(distinct {id:id(employmentLine),startDate:employmentLine.startDate,endDate:employmentLine.endDate,functions:functions}) as employmentLines,exp,employmentType\n" +
                    "with staff,user,CASE WHEN employmentType IS NULL THEN [] ELSE collect({id:id(employmentType),name:employmentType.name}) END as employmentList, \n" +
                    "COLLECT(distinct {id:id(employment),startDate:employment.startDate,endDate:employment.endDate,expertise:{id:id(exp),name:exp.name},employmentLines:employmentLines,employmentType:{id:id(employmentType),name:employmentType.name}}) as employments ";
        } else {
            matchRelationshipQueryForStaff += "OPTIONAL MATCH(employment)-[:" + HAS_EMPLOYMENT_LINES + "]-(employmentLine:EmploymentLine)  " +
                    "OPTIONAL MATCH(employment)-[:" + HAS_EXPERTISE_IN + "]-(exp:Expertise)\n" +
                    "OPTIONAL MATCH (employmentLine)-[empRelation:" + HAS_EMPLOYMENT_TYPE + "]-(employmentType:EmploymentType)  " +
                    "OPTIONAL MATCH(employmentLine)-[:" + APPLICABLE_FUNCTION + "]-(function:Function) " +
                    "WITH staff,organization,employment,user, CASE WHEN function IS NULL THEN [] ELSE COLLECT(distinct {id:id(function),name:function.name,icon:function.icon,code:function.code}) END as functions,employmentLine,exp,employmentType\n" +
                    "WITH staff,organization,employment,user, COLLECT(distinct {id:id(employmentLine),startDate:employmentLine.startDate,endDate:employmentLine.endDate,functions:functions}) as employmentLines,exp,employmentType\n" +
                    "with staff,user,CASE WHEN employmentType IS NULL THEN [] ELSE collect({id:id(employmentType),name:employmentType.name}) END as employmentList, \n" +
                    "COLLECT(distinct {id:id(employment),startDate:employment.startDate,endDate:employment.endDate,expertise:{id:id(exp),name:exp.name},employmentLines:employmentLines,employmentType:{id:id(employmentType),name:employmentType.name}}) as employments ";
        }

        if (Optional.ofNullable(filters.get(FilterType.TAGS)).isPresent()) {
            matchRelationshipQueryForStaff += " with staff,employments,user,employmentList  MATCH (staff)-[:" + BELONGS_TO_TAGS + "]-(tag:Tag) " +
                    "WHERE id(tag) IN {tagIds} ";
        } else {
            matchRelationshipQueryForStaff += " with staff,employments,user,employmentList  OPTIONAL MATCH (staff)-[:" + BELONGS_TO_TAGS + "]-(tag:Tag) ";
        }

        if (Optional.ofNullable(filters.get(FilterType.EXPERTISE)).isPresent()) {
            matchRelationshipQueryForStaff += " with staff,employments,user,employmentList,tag  MATCH (staff)-[" + HAS_EXPERTISE_IN + "]-(expertise:Expertise) " +
                    "WHERE id(expertise) IN {expertiseIds} ";
        } else {
            matchRelationshipQueryForStaff += " with staff,employments,user,employmentList,tag  OPTIONAL MATCH (staff)-[" + HAS_EXPERTISE_IN + "]-(expertise:Expertise)  ";
        }

        matchRelationshipQueryForStaff += " with staff,employments, user, employmentList, CASE WHEN tag IS NULL THEN [] ELSE collect(distinct {id:id(tag),name:tag.name,color:tag.color,shortName:tag.shortName,ultraShortName:tag.ultraShortName}) END AS tags, " +
                "CASE WHEN expertise IS NULL THEN [] ELSE collect({id:id(expertise),name:expertise.name})  END as expertiseList " +
                " with staff, employments,user, employmentList,expertiseList,tags  OPTIONAL Match (staff)-[:" + ENGINEER_TYPE + "]->(engineerType:EngineerType) " +
                " with engineerType,employments, staff, user, employmentList, expertiseList,tags";
        return matchRelationshipQueryForStaff;
    }

    public String appendWhereOrAndPreFixOnQueryString(int countOfSubString) {
        String value = (countOfSubString == 0 ? " WHERE" : " AND" );
        return countOfSubString<0 ? "" : value;
    }


}
