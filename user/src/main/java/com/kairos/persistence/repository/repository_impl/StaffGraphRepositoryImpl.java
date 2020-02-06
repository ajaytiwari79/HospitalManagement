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
                "MATCH(employment)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise)-[r:" + HAS_EXPERTISE_LINES + "]-(expertiseLine:ExpertiseLine)"+
                "MATCH (employmentLine)-[:"+HAS_EMPLOYMENT_TYPE+"]-(empType) " +
                "MATCH (staff)-[staffTeamRel:" + TEAM_HAS_MEMBER + "]-(team:Team) " +
                "OPTIONAL MATCH(staff)-[:" + HAS_CHILDREN + "]->(staffChildDetail:StaffChildDetail)" +
                " WITH  collect({id:id(expertiseLine),numberOfWorkingDaysInWeek:expertiseLine.numberOfWorkingDaysInWeek,fullTimeWeeklyMinutes:expertiseLine.fullTimeWeeklyMinutes,startDate:expertiseLine.startDate,endDate:expertiseLine.endDate}) as explinew,employmentLine,empType,employment,staff,expertise,org,user,COLLECT( distinct {id:id(team),name:team.name,teamType:staffTeamRel.teamType,activityIds:team.activityIds}) as teams," +
                "CASE WHEN staffChildDetail IS NULL THEN [] ELSE COLLECT(distinct {id:id(staffChildDetail),name:staffChildDetail.name,cprNumber:staffChildDetail.cprNumber}) END as staffChildDetails " +
                "WITH  COLLECT({totalWeeklyMinutes:(employmentLine.totalWeeklyMinutes % 60),seniorityLevel:employmentLine.seniorityLevel,startDate:employmentLine.startDate,totalWeeklyHours:(employmentLine.totalWeeklyMinutes / 60),employmentStatus:employmentLine.employmentStatus, hourlyCost:employmentLine.hourlyCost,id:id(employmentLine), workingDaysInWeek:employmentLine.workingDaysInWeek,employmentSubType:employment.employmentSubType,\n" +
                "avgDailyWorkingHours:employmentLine.avgDailyWorkingHours,fullTimeWeeklyMinutes:employmentLine.fullTimeWeeklyMinutes,totalWeeklyMinutes:employmentLine.totalWeeklyMinutes,employmentTypeId:id(empType)}) as employmentLines,employment,staff,org,user,{id:id(expertise),expertiseLines:explinew} as expertiseQueryResult,teams,staffChildDetails\n" +
                "WITH {id:id(employment),startDate:employment.startDate,endDate:employment.endDate,unitId:employment.unitId,accumulatedTimebankMinutes:employment.accumulatedTimebankMinutes,accumulatedTimebankDate:employment.accumulatedTimebankDate, employmentLines:employmentLines ,expertiseQueryResult:expertiseQueryResult} as employment,staff,org,user,teams,staffChildDetails\n" +
                "RETURN id(staff) as id,staff.firstName as firstName ,staff.lastName as lastName,user.cprNumber AS cprNumber,id(org) as unitId,org.name as unitName,collect(employment) as employment,teams,staffChildDetails");
        queryParameters.put("endDate", endDate);
        queryParameters.put("startDate", startDate);

        Result results=session.query( stringBuilder.toString(), queryParameters);
        List<StaffKpiFilterQueryResult> staffKpiFilterQueryResults= new ArrayList<>();
        results.forEach(result->
            staffKpiFilterQueryResults.add(ObjectMapperUtils.copyPropertiesByMapper(result,StaffKpiFilterQueryResult.class))
        );
        return staffKpiFilterQueryResults;
    }

    public <T> List<Map> getStaffWithFilters(Long unitId, List<Long> parentOrganizationIds, String moduleId,
                                         Map<FilterType, Set<T>> filters, String searchText, String imagePath,Long loggedInStaffId) {
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
        if (Optional.ofNullable(filters.get(FilterType.MAIN_TEAM)).isPresent()) {
            queryParameters.put("mainTeamIds",
                    convertListOfStringIntoLong(filters.get(FilterType.MAIN_TEAM)));
        }
        if (Optional.ofNullable(filters.get(FilterType.SKILL_LEVEL)).isPresent()) {
            queryParameters.put("skillLevels",
                    filters.get(FilterType.SKILL_LEVEL));
        }
        if (Optional.ofNullable(filters.get(FilterType.ACCESS_GROUPS)).isPresent()) {
            queryParameters.put("accessGroupIds",
                    convertListOfStringIntoLong(filters.get(FilterType.ACCESS_GROUPS)));
        }
        if (Optional.ofNullable(filters.get(FilterType.TAGS)).isPresent()) {
            queryParameters.put("tagIds",
                    convertListOfStringIntoLong(filters.get(FilterType.TAGS)));
        }
        if (Optional.ofNullable(filters.get(FilterType.FUNCTIONS)).isPresent()) {
            queryParameters.put("functionIds",
                    convertListOfStringIntoLong(filters.get(FilterType.FUNCTIONS)));
        }
        if (StringUtils.isNotBlank(searchText)) {
            searchText=searchText.replaceAll(" ","");
            queryParameters.put("searchText", searchText);
        }
        if (loggedInStaffId!=null) {
            queryParameters.put("loggedInStaffId", loggedInStaffId);
        }
        queryParameters.put("imagePath", imagePath);

        String query = "";
        if (ModuleId.SELF_ROSTERING_MODULE_ID.value.equals(moduleId)) {
            query = getSelfRosteringQuery(filters, searchText,loggedInStaffId);
        }else if(ModuleId.Group_TAB_ID.value.equals(moduleId)){
            query = getGroupQuery(filters, searchText);
        } else if (Optional.ofNullable(filters.get(FilterType.EMPLOYMENT)).isPresent() && filters.get(FilterType.EMPLOYMENT).contains(Employment.STAFF_WITH_EMPLOYMENT.name()) && !filters.get(FilterType.EMPLOYMENT).contains(Employment.STAFF_WITHOUT_EMPLOYMENT.name()) && !ModuleId.SELF_ROSTERING_MODULE_ID.value.equals(moduleId)) {
            query += " MATCH (staff:Staff)-[:" + BELONGS_TO_STAFF + "]-(employment:Employment{deleted:false})-[:" + IN_UNIT + "]-(organization:Unit) where id(organization)={unitId}"+getMatchQueryForStaff(loggedInStaffId)+
                    " MATCH (staff)-[:" + BELONGS_TO + "]->(user:User) " + getMatchQueryForNameGenderStatusOfStaffByFilters(filters, searchText) + " WITH user, staff, employment,organization ";
        } else if (Optional.ofNullable(filters.get(FilterType.EMPLOYMENT)).isPresent() && filters.get(FilterType.EMPLOYMENT).contains(Employment.STAFF_WITHOUT_EMPLOYMENT.name()) && !filters.get(FilterType.EMPLOYMENT).contains(Employment.STAFF_WITH_EMPLOYMENT.name()) && !ModuleId.SELF_ROSTERING_MODULE_ID.value.equals(moduleId)) {
            query += " MATCH (organization:Organization)-[:" + HAS_POSITIONS + "]-(position:Position)-[:" + BELONGS_TO + "]-(staff:Staff) where id(organization) IN {parentOrganizationId} " + getMatchQueryForStaff(loggedInStaffId) +
                    " MATCH(unit:Unit) WHERE id(unit)={unitId}" +
                    " MATCH (staff) WHERE NOT (staff)-[:" + BELONGS_TO_STAFF + "]->(:Employment)-[:" + IN_UNIT + "]-(unit)"+
                    " MATCH (staff)-[:" + BELONGS_TO + "]->(user:User)  " + getMatchQueryForNameGenderStatusOfStaffByFilters(filters, searchText) +
                    " OPTIONAL MATCH (staff)-[:" + BELONGS_TO_STAFF + "]-(employment:Employment)" +
                    " WITH user, staff, employment,organization ";
        }
        else {
            query += " MATCH (organization:Organization)-[:" + HAS_POSITIONS + "]-(position:Position)-[:" + BELONGS_TO + "]-(staff:Staff) where id(organization) IN {parentOrganizationId} " + getMatchQueryForStaff(loggedInStaffId)+
                    " MATCH (staff)-[:" + BELONGS_TO + "]->(user:User)  " + getMatchQueryForNameGenderStatusOfStaffByFilters(filters, searchText) +
                    " with user, staff OPTIONAL MATCH (staff)-[:" + BELONGS_TO_STAFF + "]-(employment:Employment{deleted:false})-[:" + IN_UNIT + "]-(organization:Unit) where id(organization)={unitId} with user, staff, employment,organization ";
        }

        query += getMatchQueryForRelationshipOfStaffByFilters(filters);

        query += " WITH engineerType, staff,employments, user,expertiseList,employmentList,tags Optional MATCH (staff)-[:" + HAS_CONTACT_ADDRESS + "]-(contactAddress:ContactAddress) ";

        query += " RETURN distinct {id:id(staff),tags:tags, employments:employments,expertiseList:expertiseList,employmentList:collect(employmentList[0]),city:contactAddress.city,province:contactAddress.province, " +
                "firstName:user.firstName,lastName:user.lastName,employedSince :staff.employedSince," +
                "age:duration.between(date(user.dateOfBirth),date()).years,joiningDate:user.joiningDate,dateOfBirth:user.dateOfBirth," +
                "badgeNumber:staff.badgeNumber, userName:staff.userName,currentStatus:staff.currentStatus,externalId:staff.externalId, access_token:staff.access_token," +
                "cprNumber:user.cprNumber, visitourTeamId:staff.visitourTeamId, familyName: staff.familyName, " +
                "gender:user.gender, pregnant:user.pregnant,  profilePic:{imagePath} + staff.profilePic, engineerType:id(engineerType),user_id:staff.user_id,userId:id(user) } as staff ORDER BY staff.id\n";

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(session.query(Map.class, query, queryParameters).iterator(), Spliterator.ORDERED), false).collect(Collectors.<Map>toList());
    }

    public <T> List<Long> convertListOfStringIntoLong(Set<T> listOfString) {
        return listOfString.stream().map(list->Long.valueOf(list.toString())).collect(Collectors.toList());
    }

    private <T> String getSelfRosteringQuery(Map<FilterType, Set<T>> filters, String searchText,Long loggedInStaffId) {
        String query = "";
        query = " MATCH (staff:Staff)-[:" + BELONGS_TO_STAFF + "]-(employment:Employment{deleted:false,published:true})-[:" + IN_UNIT + "]-(organization:Unit) where id(organization)={unitId} " +getMatchQueryForStaff(loggedInStaffId)+
                " MATCH (staff)-[:" + BELONGS_TO + "]->(user:User) " + getMatchQueryForNameGenderStatusOfStaffByFilters(filters, searchText) + " WITH user, staff, employment,organization ";
        if(Optional.ofNullable(filters.get(FilterType.SKILLS)).isPresent()) {
            query += " MATCH (staff:Staff)-[staffSkillRel:" + STAFF_HAS_SKILLS + "{isEnabled:true}]->(skill) WHERE id(skill) IN {skillIds} ";
        }
        if(Optional.ofNullable(filters.get(FilterType.TEAM)).isPresent()) {
            query += " Match (staff)<-[:" + TEAM_HAS_MEMBER + "]-(team:Team) where id(team)  IN {teamIds} ";
        }
        query +=" WITH user, staff, employment,organization ";
        return query;
    }

    private <T> String getGroupQuery(Map<FilterType, Set<T>> filters, String searchText) {
        String query = "";
        query = " MATCH (staff:Staff)-[:" + BELONGS_TO_STAFF + "]-(employment:Employment{deleted:false})-[:" + IN_UNIT + "]-(organization:Unit) where id(organization)={unitId}" +
                " MATCH (staff)-[:" + BELONGS_TO + "]->(user:User) " + getMatchQueryForNameGenderStatusOfStaffByFilters(filters, searchText) + " WITH user, staff, employment,organization ";
        if(Optional.ofNullable(filters.get(FilterType.FUNCTIONS)).isPresent()) {
            query +=" MATCH (staff)-[:BELONGS_TO_STAFF]->(employment:Employment{deleted:false})-[:HAS_EMPLOYMENT_LINES]->(employmentLine:EmploymentLine)-[:APPLICABLE_FUNCTION]->(function:Function)\n" +
                    " where id(function) IN {functionIds}";
        }
        if(Optional.ofNullable(filters.get(FilterType.ACCESS_GROUPS)).isPresent()) {
            query +=" MATCH (staff)<-[:"+BELONGS_TO+"]-(position:Position)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)-[:"+HAS_ACCESS_GROUP+"]->(accessGroups:AccessGroup)\n" +
                    " where id(accessGroups) IN {accessGroupIds}";
        }
        if(Optional.ofNullable(filters.get(FilterType.SKILLS)).isPresent() || Optional.ofNullable(filters.get(FilterType.SKILL_LEVEL)).isPresent()) {
            query += " MATCH (staff)-[staffSkillRel:" + STAFF_HAS_SKILLS + "{isEnabled:true}]->(skill) WHERE ";
            boolean isSkill = false;
            if(Optional.ofNullable(filters.get(FilterType.SKILLS)).isPresent()){
                query += "id(skill) IN {skillIds} ";
                isSkill = true;
            }
            if(Optional.ofNullable(filters.get(FilterType.SKILL_LEVEL)).isPresent()){
                query += isSkill ? " AND " : " ";
                query += "staffSkillRel.skillLevel IN {skillLevels}";
            }
        }
        if(Optional.ofNullable(filters.get(FilterType.TEAM)).isPresent() || Optional.ofNullable(filters.get(FilterType.MAIN_TEAM)).isPresent()) {
            query += " Match (staff)<-[mainTeamRel:" + TEAM_HAS_MEMBER + "]-(team:Team) where ";
            boolean isTeam = false;
            if(Optional.ofNullable(filters.get(FilterType.TEAM)).isPresent()){
                query += "id(team) IN {teamIds} ";
                isTeam = true;
            }
            if(Optional.ofNullable(filters.get(FilterType.MAIN_TEAM)).isPresent()){
                query += isTeam ? " OR " : " ";
                query += "(id(team) IN {mainTeamIds} AND mainTeamRel.teamType='MAIN')";
            }
        }
        query +=" WITH user, staff, employment,organization ";

        return query;
    }

    public <T> String getMatchQueryForNameGenderStatusOfStaffByFilters(Map<FilterType, Set<T>> filters, String searchText) {
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
        if (StringUtils.isNotBlank(searchText)) {
           matchQueryForStaff += appendWhereOrAndPreFixOnQueryString(countOfSubString) +
                    " (  LOWER(staff.firstName+staff.lastName) CONTAINS LOWER({searchText}) OR user.cprNumber STARTS WITH {searchText} )";
        }
        return matchQueryForStaff;
    }

    public String getMatchQueryForStaff(Long loggedInStaffId){
        return loggedInStaffId==null?" ":"AND id(staff)={loggedInStaffId}";
    }

    public <T> String getMatchQueryForRelationshipOfStaffByFilters(Map<FilterType, Set<T>> filters) {
        String matchRelationshipQueryForStaff = "";
        if (Optional.ofNullable(filters.get(FilterType.EMPLOYMENT_TYPE)).isPresent()) {
            matchRelationshipQueryForStaff += "MATCH(employment)-[:" + HAS_EMPLOYMENT_LINES + "]-(employmentLine:EmploymentLine)-[:"+HAS_SENIORITY_LEVEL+"]->(seniorityLevel:SeniorityLevel)-[:"+HAS_BASE_PAY_GRADE+"]->(payGrade:PayGrade)  " +
                    "MATCH (employmentLine)-[empRelation:" + HAS_EMPLOYMENT_TYPE + "]-(employmentType:EmploymentType) " +
                    "WHERE id(employmentType) IN {employmentTypeIds}  " +
                    "OPTIONAL MATCH(employment)-[:" + HAS_EXPERTISE_IN + "]-(exp:Expertise) WITH staff,organization,employment,user,exp,employmentType,employmentLine,payGrade \n" +
                    "OPTIONAL MATCH(employmentLine)-[:" + APPLICABLE_FUNCTION + "]-(function:Function) " +
                    "WITH staff,organization,employment,user, CASE WHEN function IS NULL THEN [] ELSE COLLECT(distinct {id:id(function),name:function.name,icon:function.icon,code:function.code}) END as functions,employmentLine,exp,employmentType,CASE WHEN payGrade IS NULL THEN [] ELSE COLLECT(distinct {id:id(payGrade),payGradeLevel:payGrade.payGradeLevel}) END as payGrades\n" +
                    "WITH staff,organization,employment,user, COLLECT(distinct {id:id(employmentLine),startDate:employmentLine.startDate,endDate:employmentLine.endDate,functions:functions,payGrades:payGrades}) as employmentLines,exp,employmentType\n" +
                    "with staff,user,CASE WHEN employmentType IS NULL THEN [] ELSE collect({id:id(employmentType),name:employmentType.name}) END as employmentList, \n" +
                    "COLLECT(distinct {id:id(employment),startDate:employment.startDate,endDate:employment.endDate,expertise:{id:id(exp),name:exp.name},employmentLines:employmentLines,employmentType:{id:id(employmentType),name:employmentType.name}}) as employments ";
        } else {
            matchRelationshipQueryForStaff += "OPTIONAL MATCH(employment)-[:" + HAS_EMPLOYMENT_LINES + "]-(employmentLine:EmploymentLine)-[:"+HAS_SENIORITY_LEVEL+"]->(seniorityLevel:SeniorityLevel)-[:"+HAS_BASE_PAY_GRADE+"]->(payGrade:PayGrade)  " +
                    "OPTIONAL MATCH(employment)-[:" + HAS_EXPERTISE_IN + "]-(exp:Expertise)\n" +
                    "OPTIONAL MATCH (employmentLine)-[empRelation:" + HAS_EMPLOYMENT_TYPE + "]-(employmentType:EmploymentType)  " +
                    "OPTIONAL MATCH(employmentLine)-[:" + APPLICABLE_FUNCTION + "]-(function:Function) " +
                    "WITH staff,organization,employment,user, CASE WHEN function IS NULL THEN [] ELSE COLLECT(distinct {id:id(function),name:function.name,icon:function.icon,code:function.code}) END as functions,employmentLine,exp,employmentType,CASE WHEN payGrade IS NULL THEN [] ELSE COLLECT(distinct {id:id(payGrade),payGradeLevel:payGrade.payGradeLevel}) END as payGrades\n" +
                    "WITH staff,organization,employment,user, COLLECT(distinct {id:id(employmentLine),startDate:employmentLine.startDate,endDate:employmentLine.endDate,functions:functions,payGrades:payGrades}) as employmentLines,exp,employmentType\n" +
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
            matchRelationshipQueryForStaff += " with staff,employments,user,employmentList,tag  MATCH (staff)-[expRel:" + STAFF_HAS_EXPERTISE + "]-(expertise:Expertise) " +
                    "WHERE id(expertise) IN {expertiseIds} ";
        } else {
            matchRelationshipQueryForStaff += " with staff,employments,user,employmentList,tag  OPTIONAL MATCH (staff)-[expRel:" + STAFF_HAS_EXPERTISE + "]-(expertise:Expertise)  ";
        }

        matchRelationshipQueryForStaff += " with staff,employments, user, employmentList, CASE WHEN tag IS NULL THEN [] ELSE collect(distinct {id:id(tag),name:tag.name,color:tag.color,shortName:tag.shortName,ultraShortName:tag.ultraShortName}) END AS tags, " +
                "CASE WHEN expertise IS NULL THEN [] ELSE collect(distinct {id:id(expertise),name:expertise.name,expertiseStartDateInMillis:expRel.expertiseStartDate})  END as expertiseList " +
                " with staff, employments,user, employmentList,expertiseList,tags  OPTIONAL Match (staff)-[:" + ENGINEER_TYPE + "]->(engineerType:EngineerType) " +
                " with engineerType,employments, staff, user, employmentList, expertiseList,tags";
        return matchRelationshipQueryForStaff;
    }

    public String appendWhereOrAndPreFixOnQueryString(int countOfSubString) {
        String value = (countOfSubString == 0 ? " WHERE" : " AND" );
        return countOfSubString<0 ? "" : value;
    }


}
