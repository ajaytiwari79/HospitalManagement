 package com.kairos.persistence.repository.repository_impl;

 import com.kairos.commons.utils.DateUtils;
 import com.kairos.commons.utils.ObjectMapperUtils;
 import com.kairos.dto.activity.open_shift.priority_group.StaffIncludeFilterDTO;
 import com.kairos.dto.activity.shift.NotEligibleStaffDataDTO;
 import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
 import com.kairos.enums.Employment;
 import com.kairos.enums.FilterType;
 import com.kairos.enums.ModuleId;
 import com.kairos.persistence.model.staff.StaffEmploymentQueryResult;
 import com.kairos.persistence.model.staff.StaffKpiFilterQueryResult;
 import com.kairos.persistence.model.staff.personal_details.StaffEmploymentWithTag;
 import com.kairos.persistence.repository.user.staff.CustomStaffGraphRepository;
 import org.apache.commons.collections.CollectionUtils;
 import org.apache.commons.lang3.StringUtils;
 import org.neo4j.ogm.model.Result;
 import org.neo4j.ogm.session.Session;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.stereotype.Repository;

 import javax.inject.Inject;
 import java.time.LocalDate;
 import java.util.*;
 import java.util.stream.Collectors;
 import java.util.stream.StreamSupport;

 import static com.kairos.commons.utils.ObjectUtils.getBigIntegerString;
 import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
 import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@Repository
public class StaffGraphRepositoryImpl implements CustomStaffGraphRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaffGraphRepositoryImpl.class);
    public static final String UNIT_ID = "unitId";
    public static final String EXPERTISE_IDS = "expertiseIds";
    public static final String EMPLOYMENT_TYPE_IDS = "employmentTypeIds";
    public static final String TAG_IDS = "tagIds";
    public static final String IMAGE_PATH = "imagePath";
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
        queryParameters.put(UNIT_ID, unitId);
        queryParameters.put(EXPERTISE_IDS, staffIncludeFilterDTO.getExpertiseIds());
        queryParameters.put("maxDate", staffIncludeFilterDTO.getMaxOpenShiftDate());
        queryParameters.put(EMPLOYMENT_TYPE_IDS, staffIncludeFilterDTO.getEmploymentTypeIds());
        List<Map> result=StreamSupport.stream(Spliterators.spliteratorUnknownSize(session.query(Map.class , staffFilterQuery, queryParameters).iterator(), Spliterator.ORDERED), false).collect(Collectors.<Map> toList());
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(result,StaffEmploymentQueryResult.class);
    }

    @Override
    public List<StaffKpiFilterQueryResult> getStaffsByFilter(Long organizationId, List<Long> unitIds, List<Long> employmentType, String startDate, String endDate, List<Long> staffIds,boolean parentOrganization,List<Long> tagIds) {
        Map<String, Object> queryParameters = new HashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("MATCH (org:Unit)");
        addClauses(organizationId, unitIds, employmentType, staffIds, parentOrganization, tagIds, queryParameters, stringBuilder);
        stringBuilder.append(" MATCH (employment)-[:"+ HAS_EMPLOYMENT_LINES +"]-(employmentLine:EmploymentLine)-["+HAS_SENIORITY_LEVEL+"]-(seniorityLevel:SeniorityLevel)-["+HAS_BASE_PAY_GRADE+"]-(payGrade:PayGrade)"+
                "MATCH(employment)-[:" + HAS_EXPERTISE_IN + "]->(expertise:Expertise)-[r:" + HAS_EXPERTISE_LINES + "]-(expertiseLine:ExpertiseLine)"+
                "MATCH (employmentLine)-[:"+HAS_EMPLOYMENT_TYPE+"]-(empType) " +
                "OPTIONAL MATCH (staff)-[staffTeamRel:" + TEAM_HAS_MEMBER + "]-(team:Team) " +
                "OPTIONAL MATCH(staff)-[:" + HAS_CHILDREN + "]->(staffChildDetail:StaffChildDetail)" +
                " WITH  collect({id:id(expertiseLine),numberOfWorkingDaysInWeek:expertiseLine.numberOfWorkingDaysInWeek,fullTimeWeeklyMinutes:expertiseLine.fullTimeWeeklyMinutes,startDate:expertiseLine.startDate,endDate:expertiseLine.endDate}) as explinew,employmentLine,payGrade,empType,employment,staff,expertise,org,user,CASE WHEN staffTeamRel IS NULL THEN [] else COLLECT( distinct {id:id(team),name:team.name,teamType:staffTeamRel.teamType,activityId:team.activityId}) END as teams," +
                "CASE WHEN staffChildDetail IS NULL THEN [] ELSE COLLECT(distinct {id:id(staffChildDetail),name:staffChildDetail.name,cprNumber:staffChildDetail.cprNumber}) END as staffChildDetails " +
                "WITH  COLLECT({totalWeeklyMinutes:(employmentLine.totalWeeklyMinutes % 60),seniorityLevel:employmentLine.seniorityLevel,startDate:employmentLine.startDate,endDate:employmentLine.endDate,totalWeeklyHours:(employmentLine.totalWeeklyMinutes / 60),employmentStatus:employmentLine.employmentStatus, hourlyCost:employmentLine.hourlyCost,id:id(employmentLine), workingDaysInWeek:employmentLine.workingDaysInWeek,employmentSubType:employment.employmentSubType,\n" +
                "avgDailyWorkingHours:employmentLine.avgDailyWorkingHours,fullTimeWeeklyMinutes:employmentLine.fullTimeWeeklyMinutes,payGradeLevel:payGrade.payGradeLevel,totalWeeklyMinutes:employmentLine.totalWeeklyMinutes,employmentTypeId:id(empType)}) as employmentLines,employment,staff,org,user,{id:id(expertise),expertiseLines:explinew} as expertiseQueryResult,teams,staffChildDetails\n" +
                "WITH {id:id(employment),employmentTypeId:employment.employmentTypeId,startDate:employment.startDate,endDate:employment.endDate,unitId:employment.unitId,accumulatedTimebankMinutes:employment.accumulatedTimebankMinutes,accumulatedTimebankDate:employment.accumulatedTimebankDate, employmentLines:employmentLines ,expertise:expertiseQueryResult} as employment,staff,org,user,teams,staffChildDetails\n" +
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

    private void addClauses(Long organizationId, List<Long> unitIds, List<Long> employmentType, List<Long> staffIds, boolean parentOrganization, List<Long> tagIds, Map<String, Object> queryParameters, StringBuilder stringBuilder) {
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
        if(isCollectionNotEmpty(tagIds)){
            stringBuilder.append("MATCH (staff)-[:BELONGS_TO_TAGS]-(tag:Tag) WHERE id(tag) IN {tagIds}");
            queryParameters.put(TAG_IDS,tagIds);
        }
    }

    public <T> List<Map> getStaffWithFilters(Long unitId, List<Long> parentOrganizationIds, String moduleId,
                                         Map<FilterType, Set<T>> filters, String searchText, String imagePath,Long loggedInStaffId,LocalDate selectedDate) {
        Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put(UNIT_ID, unitId);
        queryParameters.put("parentOrganizationId", parentOrganizationIds);
        searchText = addParams(filters, searchText, imagePath, loggedInStaffId, selectedDate, queryParameters);
        String query = "";
        if (ModuleId.SELF_ROSTERING_MODULE_ID.value.equals(moduleId)) {
            query = getSelfRosteringQuery(filters, searchText,loggedInStaffId,selectedDate);
        }else if(ModuleId.GROUP_TAB_ID.value.equals(moduleId)){
            query = getGroupQuery(filters, searchText);
        } else if (Optional.ofNullable(filters.get(FilterType.EMPLOYMENT)).isPresent() && filters.get(FilterType.EMPLOYMENT).contains(Employment.STAFF_WITH_EMPLOYMENT.name()) && !filters.get(FilterType.EMPLOYMENT).contains(Employment.STAFF_WITHOUT_EMPLOYMENT.name()) && !ModuleId.SELF_ROSTERING_MODULE_ID.value.equals(moduleId)) {
            query += " MATCH (staff:Staff)-[:" + BELONGS_TO_STAFF + "]-(employment:Employment{deleted:false})-[:" + IN_UNIT + "]-(organization:Unit) where id(organization)={unitId}"+getMatchQueryForStaff(loggedInStaffId)+
                    " MATCH (staff)-[:" + BELONGS_TO + "]->(user:User) " + getMatchQueryForNameGenderStatusOfStaffByFilters(filters, searchText) + " WITH user, staff, employment,organization ";
        } else if (Optional.ofNullable(filters.get(FilterType.EMPLOYMENT)).isPresent() && filters.get(FilterType.EMPLOYMENT).contains(Employment.STAFF_WITHOUT_EMPLOYMENT.name()) && !filters.get(FilterType.EMPLOYMENT).contains(Employment.STAFF_WITH_EMPLOYMENT.name()) && !ModuleId.SELF_ROSTERING_MODULE_ID.value.equals(moduleId)) {
            query += " MATCH (organization:Organization)-[:" + HAS_POSITIONS + "]-(position:Position)-[:" + BELONGS_TO + "]-(staff:Staff) where id(organization) IN {parentOrganizationId} " + getMatchQueryForStaff(loggedInStaffId) +
                    " MATCH(unit:Unit) WHERE id(unit)={unitId}" + " MATCH (staff) WHERE NOT (staff)-[:" + BELONGS_TO_STAFF + "]->(:Employment)-[:" + IN_UNIT + "]-(unit)"+
                    " MATCH (staff)-[:" + BELONGS_TO + "]->(user:User)  " + getMatchQueryForNameGenderStatusOfStaffByFilters(filters, searchText) + " OPTIONAL MATCH (staff)-[:" + BELONGS_TO_STAFF + "]-(employment:Employment)" + " WITH user, staff, employment,organization ";
        }
        else {
            query += " MATCH (organization:Organization)-[:" + HAS_POSITIONS + "]-(position:Position)-[:" + BELONGS_TO + "]-(staff:Staff) where id(organization) IN {parentOrganizationId} " + getMatchQueryForStaff(loggedInStaffId)+
                    " MATCH (staff)-[:" + BELONGS_TO + "]->(user:User)  " + getMatchQueryForNameGenderStatusOfStaffByFilters(filters, searchText) + " with user, staff OPTIONAL MATCH (staff)-[:" + BELONGS_TO_STAFF + "]-(employment:Employment{deleted:false})-[:" + IN_UNIT + "]-(organization:Unit) where id(organization)={unitId} with user, staff, employment,organization ";
        }
        query += getMatchQueryForRelationshipOfStaffByFilters(filters);
        query += " WITH engineerType, staff,employments, user,expertiseList,employmentList,tags Optional MATCH (staff)-[:" + HAS_CONTACT_ADDRESS + "]-(contactAddress:ContactAddress) ";
        query += " RETURN distinct {id:id(staff),tags:tags, employments:employments,expertiseList:expertiseList,employmentList:collect(employmentList[0]),city:contactAddress.city,province:contactAddress.province, " + "firstName:user.firstName,lastName:user.lastName,employedSince :staff.employedSince," +
                "age:duration.between(date(user.dateOfBirth),date()).years,joiningDate:user.joiningDate,dateOfBirth:user.dateOfBirth," + "badgeNumber:staff.badgeNumber, userName:staff.userName,currentStatus:staff.currentStatus,externalId:staff.externalId, access_token:staff.access_token," +
                "cprNumber:user.cprNumber, visitourTeamId:staff.visitourTeamId, canRankTeam: staff.canRankTeam, familyName: staff.familyName, " + "gender:user.gender, pregnant:user.pregnant,  profilePic:{imagePath} + staff.profilePic, engineerType:id(engineerType),user_id:staff.user_id,userId:id(user) } as staff ORDER BY staff.id\n";
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(session.query(Map.class, query, queryParameters).iterator(), Spliterator.ORDERED), false).collect(Collectors.<Map>toList());
    }

    private <T> String addParams(Map<FilterType, Set<T>> filters, String searchText, String imagePath, Long loggedInStaffId, LocalDate selectedDate, Map<String, Object> queryParameters) {
        addStaffParam(filters, queryParameters);
        addEmploymentParam(filters, queryParameters);
        adSkillParams(filters, queryParameters);
        addTeamParam(filters, queryParameters);
        if (Optional.ofNullable(filters.get(FilterType.ACCESS_GROUPS)).isPresent()) {
            queryParameters.put("accessGroupIds", convertListOfStringIntoLong(filters.get(FilterType.ACCESS_GROUPS)));
        }
        if (Optional.ofNullable(filters.get(FilterType.TAGS)).isPresent()) {
            queryParameters.put(TAG_IDS, convertListOfStringIntoLong(filters.get(FilterType.TAGS)));
        }
        if (Optional.ofNullable(filters.get(FilterType.FUNCTIONS)).isPresent()) {
            queryParameters.put("functionIds", convertListOfStringIntoLong(filters.get(FilterType.FUNCTIONS)));
        }
        if (StringUtils.isNotBlank(searchText)) {
            searchText=searchText.replaceAll(" ","");
            queryParameters.put("searchText", searchText);
        }
        if (loggedInStaffId!=null) {
            queryParameters.put("loggedInStaffId", loggedInStaffId);
        }
        if (selectedDate==null) {
            selectedDate = LocalDate.now();
        }
        queryParameters.put("selectedDate", selectedDate.toString());
        queryParameters.put(IMAGE_PATH, imagePath);
        return searchText;
    }

    private <T> void addEmploymentParam(Map<FilterType, Set<T>> filters, Map<String, Object> queryParameters) {
        if (Optional.ofNullable(filters.get(FilterType.EMPLOYMENT_TYPE)).isPresent()) {
            queryParameters.put(EMPLOYMENT_TYPE_IDS, convertListOfStringIntoLong(filters.get(FilterType.EMPLOYMENT_TYPE)));
        }
        if (Optional.ofNullable(filters.get(FilterType.EXPERTISE)).isPresent()) {
            queryParameters.put(EXPERTISE_IDS, convertListOfStringIntoLong(filters.get(FilterType.EXPERTISE)));
        }
    }

    private <T> void addStaffParam(Map<FilterType, Set<T>> filters, Map<String, Object> queryParameters) {
        if (Optional.ofNullable(filters.get(FilterType.STAFF_STATUS)).isPresent()) {
            queryParameters.put("staffStatusList", filters.get(FilterType.STAFF_STATUS));
        }
        if (Optional.ofNullable(filters.get(FilterType.GENDER)).isPresent()) {
            queryParameters.put("genderList", filters.get(FilterType.GENDER));
        }
    }

    private <T> void adSkillParams(Map<FilterType, Set<T>> filters, Map<String, Object> queryParameters) {
        if (Optional.ofNullable(filters.get(FilterType.SKILLS)).isPresent()) {
            queryParameters.put("skillIds", convertListOfStringIntoLong(filters.get(FilterType.SKILLS)));
        }
        if (Optional.ofNullable(filters.get(FilterType.SKILL_LEVEL)).isPresent()) {
            queryParameters.put("skillLevels", filters.get(FilterType.SKILL_LEVEL));
        }
    }

    private <T> void addTeamParam(Map<FilterType, Set<T>> filters, Map<String, Object> queryParameters) {
        if (Optional.ofNullable(filters.get(FilterType.TEAM)).isPresent()) {
            queryParameters.put("teamIds", convertListOfStringIntoLong(filters.get(FilterType.TEAM)));
        }
        if (Optional.ofNullable(filters.get(FilterType.MAIN_TEAM)).isPresent()) {
            queryParameters.put("mainTeamIds", convertListOfStringIntoLong(filters.get(FilterType.MAIN_TEAM)));
        }
    }

    public <T> List<StaffEmploymentWithTag> getStaffWithFilterCriteria(final Map<FilterType, Set<T>> filters, final Long unitId, final LocalDate localDateToday, final String searchText, final Long loggedInUserId,String imagePath) {
        String today = DateUtils.formatLocalDate(localDateToday, "yyyy-MM-dd");
        Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put(UNIT_ID, unitId);
        queryParameters.put("today", today);
        queryParameters.put("loggedInUserId", loggedInUserId);
        queryParameters.put(IMAGE_PATH,imagePath);
        StringBuilder query = new StringBuilder();
        StringBuilder returnData = new StringBuilder();
        query.append("MATCH (user:User)<-[:BELONGS_TO]-(staff:Staff)-[:BELONGS_TO_STAFF]-(employments:Employment{published:true,deleted:false})-[:IN_UNIT]-(unit:Unit)\n" +
                "WHERE id(unit)={unitId} ");
        if (searchText != null && searchText.trim() != "") {
            String qText = "(?i)" + searchText + ".*";
            queryParameters.put("searchText", qText);
            query.append(" AND (staff.firstName=~ {searchText} OR staff.lastName=~ {searchText} OR user.cprNumber=~ {searchText} ) ");
        }
        returnData.append(" RETURN distinct id(staff) as id, staff.firstName as firstName,staff.lastName as lastName, ")
                .append(" user.gender as gender, {imagePath} + staff.profilePic as profilePic,staff.user_id as user_id,  ")
                .append(" staff.currentStatus as currentStatus, ")
                .append(" id(user) as userId, ")
                .append(" collect(distinct {id:id(employments),startDate:employments.startDate,endDate:employments.endDate, employmentType: { id: id(empType),name:empType.name } , employmentSubType: employments.employmentSubType,expertise: {id : id(expertise),name:expertise.name,startDate:employments.startDate,endDate:employments.endDate   },employmentLines:employmentLines  }) as employments, ")
                .append(" CASE contactAddress WHEN contactAddress IS NULL THEN '' ELSE contactAddress.province END ");
        addMatchingCriteria(filters, queryParameters, query);
        query.append(" WITH staff,employments,user,contactAddress OPTIONAL MATCH (staff)-[:BELONGS_TO_TAGS]-(selectedTags:Tag) ");
        query.append(" WITH staff,employments,user,contactAddress,selectedTags MATCH (employments)-[:HAS_EXPERTISE_IN]->(expertise:Expertise) ");
        query.append(" WITH staff,employments,user,contactAddress,selectedTags,expertise MATCH (employments)-[empL:HAS_EMPLOYMENT_LINES]->(employmentLines:EmploymentLine)-[het:HAS_EMPLOYMENT_TYPE]->(empType:EmploymentType) ");
        query.append(" WITH staff,employments,user,contactAddress,selectedTags,expertise,empType,employmentLines OPTIONAL MATCH (employmentLines)-[:APPLICABLE_FUNCTION]-(applicableFunctions:Function) ");
        query.append(" WITH staff,employments,user,contactAddress,selectedTags,expertise,empType,applicableFunctions, ");
        query.append(" collect({id: id(employmentLines), startDate:employmentLines.startDate,endDate:employmentLines.endDate,totalWeeklyMinutes:employmentLines.totalWeeklyMinutes,fullTimeWeeklyMinutes:employmentLines.fullTimeWeeklyMinutes,avgDailyWorkingHours:employmentLines.avgDailyWorkingHours,workingDaysInWeek:employmentLines.workingDaysInWeek,hourlyCost:employmentLines.hourlyCost, employmentType: { id: id(empType),name:empType.name } }) as employmentLines " +
                "MATCH(staff)-["+STAFF_HAS_EXPERTISE+"]->(expList:Expertise) " +
                "OPTIONAL MATCH (staff)-[:"+STAFF_HAS_SKILLS+"{isEnabled:true}]->(skillList:Skill{isEnabled:true}) ");
        returnData.append(" , collect( distinct selectedTags) as tags , collect( distinct { id : id(empType),name: empType.name}) as employmentList, collect( distinct { id : id(expList),name: expList.name}) as expertiseList , CASE WHEN skillList IS NULL THEN [] ELSE collect( distinct { id : id(skillList),name: skillList.name}) END as skillList ").append(" ORDER BY staff.currentStatus, staff.firstName");
        query.append(returnData);
        LOGGER.debug(query.toString());
        Result staffEmployments = session.query(query.toString(), queryParameters);
        LOGGER.info("staff with employments found are {}", staffEmployments.queryResults());
        List<StaffEmploymentWithTag> staffEmploymentWithTags = new ArrayList<>();
        Iterator si = staffEmployments.iterator();
        while (si.hasNext()) {
            staffEmploymentWithTags.add(ObjectMapperUtils.copyPropertiesByMapper(si.next(), StaffEmploymentWithTag.class));
        }
        return staffEmploymentWithTags;
    }

    public StaffEmploymentWithTag getLoggedInStaffDetails(final Long unitId, final Long loggedInUserId,String imagePath) {
        StringBuilder query = new StringBuilder();
        Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put(UNIT_ID, unitId);
        queryParameters.put("loggedInUserId", loggedInUserId);
        queryParameters.put(IMAGE_PATH,imagePath);
        query.append("MATCH (user:User)<-[:BELONGS_TO]-(staff:Staff)-[:BELONGS_TO_STAFF]-(employments:Employment)-[:IN_UNIT]-(unit:Unit)\n" +
                "WHERE id(unit)={unitId} AND employments.startDate IS NOT null AND id(user)={loggedInUserId} " );
        query.append(" WITH staff,employments,user MATCH (staff)-[:HAS_CONTACT_ADDRESS]-(contactAddress:ContactAddress) ");
        query.append(" WITH staff,employments,user,contactAddress MATCH (staff)-[:BELONGS_TO_TAGS]-(selectedTags:Tag) ");
        query.append(" WITH staff,employments,user,contactAddress,selectedTags MATCH (employments)-[:HAS_EXPERTISE_IN]->(expertise:Expertise) ");
        query.append(" WITH staff,employments,user,contactAddress,selectedTags,expertise MATCH (employments)-[empL:HAS_EMPLOYMENT_LINES]->(employmentLines:EmploymentLine)-[het:HAS_EMPLOYMENT_TYPE]->(empType:EmploymentType) ");
        query.append(" WITH staff,employments,user,contactAddress,selectedTags,expertise,empType,employmentLines OPTIONAL MATCH (employmentLines)-[:APPLICABLE_FUNCTION]-(applicableFunctions:Function) ");
        query.append(" WITH staff,employments,user,contactAddress,selectedTags,expertise,empType,applicableFunctions, ");
        query.append(" collect({id: id(employmentLines), startDate:employmentLines.startDate,endDate:employmentLines.endDate,totalWeeklyMinutes:employmentLines.totalWeeklyMinutes,fullTimeWeeklyMinutes:employmentLines.fullTimeWeeklyMinutes,avgDailyWorkingHours:employmentLines.avgDailyWorkingHours,workingDaysInWeek:employmentLines.workingDaysInWeek,hourlyCost:employmentLines.hourlyCost, employmentType: { id: id(empType),name:empType.name } }) as employmentLines" +
                " MATCH(staff)-["+STAFF_HAS_EXPERTISE+"]->(expList:Expertise) " +
                "OPTIONAL MATCH (staff)-[:"+STAFF_HAS_SKILLS+"]->(skillList:Skill) " );

        StringBuilder returnData = new StringBuilder();
        returnData.append(" RETURN distinct id(staff) as id, staff.firstName as firstName,staff.lastName as lastName, ")
                .append(" user.gender as gender, {imagePath} + staff.profilePic as profilePic,staff.user_id as user_id,  ")
                .append(" staff.currentStatus as currentStatus, ")
                .append(" id(user) as userId, ")
                .append(" collect(distinct {id:id(employments),startDate:employments.startDate,endDate:employments.endDate, employmentType: { id: id(empType),name:empType.name } , employmentSubType: employments.employmentSubType,expertise: {id : id(expertise),name:expertise.name,startDate:employments.startDate,endDate:employments.endDate   },employmentLines:employmentLines  }) as employments, ")
                .append(" CASE contactAddress WHEN contactAddress IS NULL THEN '' ELSE contactAddress.province END ")
                .append(" , collect( distinct selectedTags) as tags , collect( distinct { id : id(empType),name: empType.name}) as employmentList , collect( distinct { id : id(expList),name: expList.name}) as expertiseList , CASE WHEN skillList IS NULL THEN [] ELSE collect( distinct { id : id(skillList),name: skillList.name}) END as skillList ");
        query.append(returnData);
        Result staffEmploymentDetails = session.query(query.toString(), queryParameters);
        Iterator si = staffEmploymentDetails.iterator();
        StaffEmploymentWithTag staffEmploymentWithTag = null;
        while (si.hasNext()) {
            staffEmploymentWithTag = ObjectMapperUtils.copyPropertiesByMapper(si.next(), StaffEmploymentWithTag.class);
        }
        return staffEmploymentWithTag;
    }


    private <T> void addMatchingCriteria(Map<FilterType, Set<T>> filters, Map<String, Object> queryParameters, StringBuilder query) {
        ageMatcher(query, filters);
        employedSinceMatcher(query, filters);
        birthdayMatcher(query, filters);
//        organizationalExperienceMatcher(query,filters);
        staffStatusMatcher(query, filters, queryParameters);
        genderMatcher(query, filters, queryParameters);
        teamMatcher(query, filters, queryParameters);
        expertiseMatcher(query, filters, queryParameters);
        seniorityAndPaygradeMatcher(query,filters,queryParameters);
        skillMatcher(query,filters,queryParameters);
        tagsMatcher(query,filters,queryParameters);
        employmentTypeMatcher(query,filters,queryParameters);
        accessGroupMatcher(query,filters,queryParameters);
        functionsMatcher(query,filters,queryParameters);
        addressMatcher(query);
    }

    private <T> StringBuilder addComparisonValuesToQuery(StringBuilder query,String propertyToCompare,Set <Map<String,T>> customQuerySet){
        for(Map<String,T> c:customQuerySet){
            for(Map.Entry entry : c.entrySet()){
                query.append(" AND ");
                query.append(propertyToCompare);
                query.append(entry.getKey()).append(" ").append(entry.getValue());
            }
        }
        return query;
    }

    private <T> void ageMatcher(final StringBuilder query,final Map<FilterType,Set<T>> filters){
        if(Optional.ofNullable(filters.get(FilterType.AGE)).isPresent() && filters.get(FilterType.AGE).size()!=0) {
            Set <Map<String,Number>> customQuerySet = (Set<Map<String, Number>>) filters.get(FilterType.AGE);
            addComparisonValuesToQuery(query," DATE(user.dateOfBirth) ",customQuerySet);
        }
    }

    private <T> void employedSinceMatcher(final StringBuilder query,final Map<FilterType,Set<T>> filters){
        if(Optional.ofNullable(filters.get(FilterType.EMPLOYED_SINCE)).isPresent() && filters.get(FilterType.EMPLOYED_SINCE).size()!=0) {
            Set <Map<String,String>> customQuerySet = (Set<Map<String, String>>) filters.get(FilterType.EMPLOYED_SINCE);
            addComparisonValuesToQuery(query," DATE(employments.startDate) ",customQuerySet);
        }
    }

    private <T> void birthdayMatcher(final StringBuilder query,final Map<FilterType,Set<T>> filters){
        if(Optional.ofNullable(filters.get(FilterType.BIRTHDAY)).isPresent() && filters.get(FilterType.BIRTHDAY).size()!=0) {
            Set <Map<String,String>> customQuerySet = (Set<Map<String, String>>) filters.get(FilterType.BIRTHDAY);
            addComparisonValuesToQuery(query," DATE(staff.dateOfBirth) ",customQuerySet);
        }
    }

    private <T> void staffStatusMatcher(final StringBuilder query,final Map<FilterType,Set<T>> filters,final Map<String,Object> queryParameters){
        if (Optional.ofNullable(filters.get(FilterType.STAFF_STATUS)).isPresent() && filters.get(FilterType.STAFF_STATUS).size()!=0) {
            queryParameters.put("statusNames",filters.get(FilterType.STAFF_STATUS));
            query.append(" AND staff.currentStatus in {statusNames} ");
        }
    }

    private <T> void genderMatcher(final StringBuilder query,final Map<FilterType,Set<T>> filters,final Map<String,Object> queryParameters){
        if (Optional.ofNullable(filters.get(FilterType.GENDER)).isPresent() && filters.get(FilterType.GENDER).size()!=0) {
            queryParameters.put("genderList", filters.get(FilterType.GENDER));
            query.append(" AND user.gender in {genderList} ");
        }
    }

    private <T> void teamMatcher(final StringBuilder query,final Map<FilterType,Set<T>> filters,final Map<String,Object> queryParameters){
        if (Optional.ofNullable(filters.get(FilterType.TEAM)).isPresent() && filters.get(FilterType.TEAM).size()!=0) {
            queryParameters.put("teamIds", convertListOfStringIntoLong(filters.get(FilterType.TEAM)));
            query.append(" WITH staff,employments,user MATCH (staff)<-[teamRel:TEAM_HAS_MEMBER]-(team:Team) where id(team) in {teamIds} ");
            if(Optional.ofNullable(filters.get(FilterType.MAIN_TEAM)).isPresent()){
                query.append(" AND teamRel.teamType='MAIN')");
            }
        }
    }

    private <T> void expertiseMatcher(final StringBuilder query,final Map<FilterType,Set<T>> filters,final Map<String,Object> queryParameters){
        if (Optional.ofNullable(filters.get(FilterType.EXPERTISE)).isPresent() && filters.get(FilterType.EXPERTISE).size()!=0) {
            queryParameters.put(EXPERTISE_IDS, convertListOfStringIntoLong(filters.get(FilterType.EXPERTISE)));
            query.append(" WITH staff,employments,user MATCH (staff)-[:STAFF_HAS_EXPERTISE]->(expertise:Expertise)<-[:HAS_EXPERTISE_IN]-(employments) where id(expertise) in {expertiseIds}");
        }
    }

    private <T> void seniorityAndPaygradeMatcher(final StringBuilder query,final Map<FilterType,Set<T>> filters,final Map<String,Object> queryParameters){
        if(Optional.ofNullable(filters.get(FilterType.SENIORITY)).isPresent() || Optional.ofNullable(filters.get(FilterType.PAY_GRADE_LEVEL)).isPresent() ) {
            Set <Map<String,String>> customQuerySet = (Set<Map<String, String>>) filters.get(FilterType.SENIORITY);
            query.append(" WITH staff,employments,user ");
            query.append(" MATCH (employments)-[:HAS_EMPLOYMENT_LINES]->(el:EmploymentLine)-[:HAS_SENIORITY_LEVEL]->(sl:SeniorityLevel)-[:HAS_BASE_PAY_GRADE]->(pg:PayGrade) ");
            if( filters.get(FilterType.SENIORITY).size()!=0) {
                query.append(" WHERE DATE(el.endDate) <= DATE(employments.endDate) ");
                addComparisonValuesToQuery(query, " sl.to ", customQuerySet);
            }
            if(filters.get(FilterType.PAY_GRADE_LEVEL).size()!=0) {
                addComparisonValuesToQuery(query, " pg.payGradeLevel ", customQuerySet);
            }
        }
    }

    private <T> void skillMatcher(final StringBuilder query,final Map<FilterType,Set<T>> filters,final Map<String,Object> queryParameters){
        if (Optional.ofNullable(filters.get(FilterType.SKILLS)).isPresent() && filters.get(FilterType.SKILLS).size()!=0) {
            queryParameters.put("skillIds", convertListOfStringIntoLong(filters.get(FilterType.SKILLS)));
            query.append(" WITH staff,employments,user MATCH (staff)-[:STAFF_HAS_SKILLS]->(skills:Skill) where id(skills) in {skillIds}");
        }
        /*  if (Optional.ofNullable(filters.get(FilterType.SKILL_LEVEL)).isPresent()) {
            queryParameters.put("skillLevels",
                    filters.get(FilterType.SKILL_LEVEL));
        }*/
    }

    private <T> void tagsMatcher(final StringBuilder query,final Map<FilterType,Set<T>> filters,final Map<String,Object> queryParameters){
        if (Optional.ofNullable(filters.get(FilterType.TAGS)).isPresent() && filters.get(FilterType.TAGS).size()!=0) {
            queryParameters.put(TAG_IDS, convertListOfStringIntoLong(filters.get(FilterType.TAGS)));
            query.append(" WITH staff,employments,user MATCH (staff)-[:BELONGS_TO_TAGS]->(tags:Tag) where id(tags) in {tagIds}");
        }
    }

    private <T> void employmentTypeMatcher(final StringBuilder query,final Map<FilterType,Set<T>> filters,final Map<String,Object> queryParameters){
        if (Optional.ofNullable(filters.get(FilterType.EMPLOYMENT_TYPE)).isPresent()  && filters.get(FilterType.EMPLOYMENT_TYPE).size()!=0) {
            queryParameters.put(EMPLOYMENT_TYPE_IDS, convertListOfStringIntoLong(filters.get(FilterType.EMPLOYMENT_TYPE)));
            query.append(" WITH staff,employments,user MATCH (employmentType:EmploymentType)<-[:HAS_EMPLOYMENT_TYPE]-(el:EmploymentLine)<-[:HAS_EMPLOYMENT_LINES]-(employments) WHERE id(employmentType) in {employmentTypeIds}");
        }
    }

    private <T> void accessGroupMatcher(final StringBuilder query,final Map<FilterType,Set<T>> filters,final Map<String,Object> queryParameters){
        if (Optional.ofNullable(filters.get(FilterType.ACCESS_GROUPS)).isPresent() && filters.get(FilterType.ACCESS_GROUPS).size()!=0) {
            queryParameters.put("accessGroupIds", convertListOfStringIntoLong(filters.get(FilterType.ACCESS_GROUPS)));
            query.append("WITH staff,employments,user MATCH (staff)<-[:"+BELONGS_TO+"]-(position:Position)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission:UnitPermission)-[:"+HAS_ACCESS_GROUP+"]->(accessGroups:AccessGroup)\n" +
                    " where id(accessGroups) IN {accessGroupIds} ") ;
        }
    }

    private <T> void functionsMatcher(final StringBuilder query,final Map<FilterType,Set<T>> filters,final Map<String,Object> queryParameters){
        if(Optional.ofNullable(filters.get(FilterType.FUNCTIONS)).isPresent() && filters.get(FilterType.FUNCTIONS).size()!=0) {
            queryParameters.put("functionIds",filters.get(FilterType.FUNCTIONS));
            query.append("WITH staff,employments,user MATCH (staff)-[:BELONGS_TO_STAFF]->(employment:Employment{deleted:false})-[:HAS_EMPLOYMENT_LINES]->(employmentLine:EmploymentLine)-[:APPLICABLE_FUNCTION]->(function:Function)\n" +
                    " where id(function) IN {functionIds}");
        }
    }

    private <T> void organizationalExperienceMatcher(final StringBuilder query,final Map<FilterType,Set<T>> filters){
        Date localDateToday = DateUtils.asDate(LocalDate.now());
        if(Optional.ofNullable(filters.get(FilterType.ORGANIZATION_EXPERIENCE)).isPresent() && filters.get(FilterType.ORGANIZATION_EXPERIENCE).size()!=0) {
            Set<Map<String, String>> customQuerySet = (Set<Map<String, String>>) filters.get(FilterType.ORGANIZATION_EXPERIENCE);

            for (Map<String, String> c : customQuerySet) {
                for (Map.Entry entry : c.entrySet()) {
                    query.append(" AND ");
                    query.append("employments.startDate ");
                    query.append(entry.getKey()).append(" ").append(localDateToday.toString());
                }
            }
        }
    }

    private void addressMatcher(final StringBuilder query){
        query.append(" WITH staff,employments,user MATCH (staff)-[:HAS_CONTACT_ADDRESS]-(contactAddress:ContactAddress) ");
    }

    public <T> List<Long> convertListOfStringIntoLong(Set<T> listOfString) {
        return listOfString.stream().map(list->Long.valueOf(list.toString())).collect(Collectors.toList());
    }

    private <T> String getSelfRosteringQuery(Map<FilterType, Set<T>> filters, String searchText,Long loggedInStaffId,LocalDate selectedDate) {
        String query = "";
        query = " MATCH (staff:Staff)-[:" + BELONGS_TO_STAFF + "]-(employment:Employment{deleted:false,published:true})-[:" + IN_UNIT + "]-(organization:Unit) where id(organization)={unitId} " +getMatchQueryForStaff(loggedInStaffId)+
                " MATCH (staff)-[:" + BELONGS_TO + "]->(user:User) " + getMatchQueryForNameGenderStatusOfStaffByFilters(filters, searchText) + " WITH user, staff, employment,organization ";
        if(Optional.ofNullable(filters.get(FilterType.SKILLS)).isPresent()) {
            query += " MATCH (staff:Staff)-[staffSkillRel:" + STAFF_HAS_SKILLS + "{isEnabled:true}]->(skill) WHERE id(skill) IN {skillIds} ";
        }
        if(Optional.ofNullable(filters.get(FilterType.TEAM)).isPresent()) {
            query += " Match (staff)<-[tRel:" + TEAM_HAS_MEMBER + "]-(team:Team) where id(team)  IN {teamIds} and DATE(tRel.startDate) <= DATE({selectedDate}) AND (tRel.endDate is null OR DATE(tRel.endDate)>=DATE({selectedDate})) ";
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
        query = addTeamCriteria(filters, query);
        query +=" WITH user, staff, employment,organization ";

        return query;
    }

    private <T> String addTeamCriteria(Map<FilterType, Set<T>> filters, String query) {
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
                    " (  REPLACE(LOWER(staff.firstName+staff.lastName),\" \",\"\") CONTAINS REPLACE(LOWER({searchText}),\" \",\"\") OR user.cprNumber STARTS WITH {searchText} )";
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
        matchRelationshipQueryForStaff = addTagCriteria(filters, matchRelationshipQueryForStaff);
        matchRelationshipQueryForStaff = addExpertiseCriteria(filters, matchRelationshipQueryForStaff);
        matchRelationshipQueryForStaff += " with staff,employments, user, employmentList, CASE WHEN tag IS NULL THEN [] ELSE collect(distinct {id:id(tag),name:tag.name,color:tag.color,shortName:tag.shortName,ultraShortName:tag.ultraShortName}) END AS tags, " +
                "CASE WHEN expertise IS NULL THEN [] ELSE collect(distinct {id:id(expertise),name:expertise.name,expertiseStartDateInMillis:expRel.expertiseStartDate})  END as expertiseList " +
                " with staff, employments,user, employmentList,expertiseList,tags  OPTIONAL Match (staff)-[:" + ENGINEER_TYPE + "]->(engineerType:EngineerType) " +
                " with engineerType,employments, staff, user, employmentList, expertiseList,tags";
        return matchRelationshipQueryForStaff;
    }

    private <T> String addExpertiseCriteria(Map<FilterType, Set<T>> filters, String matchRelationshipQueryForStaff) {
        if (Optional.ofNullable(filters.get(FilterType.EXPERTISE)).isPresent()) {
            matchRelationshipQueryForStaff += " with staff,employments,user,employmentList,tag  MATCH (staff)-[expRel:" + STAFF_HAS_EXPERTISE + "]-(expertise:Expertise) " +
                    "WHERE id(expertise) IN {expertiseIds} ";
        } else {
            matchRelationshipQueryForStaff += " with staff,employments,user,employmentList,tag  OPTIONAL MATCH (staff)-[expRel:" + STAFF_HAS_EXPERTISE + "]-(expertise:Expertise)  ";
        }
        return matchRelationshipQueryForStaff;
    }

    private <T> String addTagCriteria(Map<FilterType, Set<T>> filters, String matchRelationshipQueryForStaff) {
        if (Optional.ofNullable(filters.get(FilterType.TAGS)).isPresent()) {
            matchRelationshipQueryForStaff += " with staff,employments,user,employmentList  MATCH (staff)-[:" + BELONGS_TO_TAGS + "]-(tag:Tag) " +
                    "WHERE id(tag) IN {tagIds} ";
        } else {
            matchRelationshipQueryForStaff += " with staff,employments,user,employmentList  OPTIONAL MATCH (staff)-[:" + BELONGS_TO_TAGS + "]-(tag:Tag) ";
        }
        return matchRelationshipQueryForStaff;
    }

    public String appendWhereOrAndPreFixOnQueryString(int countOfSubString) {
        String value = (countOfSubString == 0 ? " WHERE" : " AND" );
        return countOfSubString<0 ? "" : value;
    }

    @Override
    public List<StaffAdditionalInfoDTO> getEligibleStaffsForCoverShift(Long unitId,NotEligibleStaffDataDTO notEligibleStaffDataDTO){
        Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put(UNIT_ID, unitId);
        queryParameters.put("date",DateUtils.formatLocalDate(notEligibleStaffDataDTO.getShiftDate(), "yyyy-MM-dd"));
        StringBuilder query = new StringBuilder("MATCH (organization:Unit)<-[:IN_UNIT]-(employment:Employment) WHERE id(organization)={unitId} AND (employment.endDate is null OR date(employment.endDate) >= date({date}))");
        if(isCollectionNotEmpty(notEligibleStaffDataDTO.getEmploymentTypeIds())){
            queryParameters.put(EMPLOYMENT_TYPE_IDS, notEligibleStaffDataDTO.getEmploymentTypeIds());
            query.append(" MATCH (employment)-[:HAS_EMPLOYMENT_LINES]-(empLine:EmploymentLine)-[:HAS_EMPLOYMENT_TYPE]-(empType) where id(empType) in {employmentTypeIds}");
        }
        if(isCollectionNotEmpty(notEligibleStaffDataDTO.getStaffIds())){
            queryParameters.put("notIncludeStaffIds", notEligibleStaffDataDTO.getStaffIds());
            query.append(" MATCH (employment)<-[:BELONGS_TO_STAFF]-(staff:Staff)<-[:BELONGS_TO]-(position:Position)-[:HAS_UNIT_PERMISSIONS]->(up:UnitPermission) WHERE NOT id(staff) in {notIncludeStaffIds}");
        }else{
            query.append(" MATCH (employment)<-[:BELONGS_TO_STAFF]-(staff:Staff)<-[:BELONGS_TO]-(position:Position)-[:HAS_UNIT_PERMISSIONS]->(up:UnitPermission)");
        }
        query.append(" WITH staff,organization,collect(id(employment)) AS employmentIds ")
        .append(" MATCH (staff)-[:BELONGS_TO]->(user:User) WITH staff,organization,employmentIds,user")
                .append(" OPTIONAL MATCH (staff)-[:HAS_CHILDREN]->(staffChildDetail:StaffChildDetail) WITH staff,collect(staffChildDetail) AS  staffChildDetails,organization,employmentIds,user");
        if(isCollectionNotEmpty(notEligibleStaffDataDTO.getActivityIds())){
            //queryParameters.put("activityIds", notEligibleStaffDataDTO.getActivityIds());
            query.append(" MATCH (teams:Team)-[:TEAM_HAS_MEMBER{isEnabled:true}]->(staff) where teams.activityId in").append(getBigIntegerString(notEligibleStaffDataDTO.getActivityIds().iterator()));
        }else{
            query.append(" OPTIONAL MATCH (teams:Team)-[:TEAM_HAS_MEMBER{isEnabled:true}]->(staff)");
        }
        query.append(" WITH staff,collect(id(teams)) AS teams,organization,employmentIds,user,staffChildDetails");
        if(isCollectionNotEmpty(notEligibleStaffDataDTO.getTagIds())){
            queryParameters.put(TAG_IDS, notEligibleStaffDataDTO.getTagIds());
            query.append(" MATCH (staff)-[:BELONGS_TO_TAGS]->(tag:Tag) where id(tag) in {tagIds}");
        }else {
            query.append(" MATCH (staff)-[:BELONGS_TO_TAGS]->(tag:Tag)");
        }
        query.append(" WITH staff,staffChildDetails,teams,organization,employmentIds,user,COLLECT(tag) AS tags RETURN id(staff) AS id,staff.firstName AS firstName,staff.lastName as lastName,staff.profilePic AS profilePic,teams,id(organization) AS unitId,employmentIds as employmentIds,id(user) AS staffUserId,user.cprNumber AS cprNumber,staffChildDetails,tags");
        Result result = session.query(query.toString(), queryParameters);
        Iterator si = result.iterator();
        List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS = new ArrayList<>();
        while (si.hasNext()) {
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = ObjectMapperUtils.copyPropertiesByMapper(si.next(), StaffAdditionalInfoDTO.class);
            staffAdditionalInfoDTOS.add(staffAdditionalInfoDTO);
        }
        return staffAdditionalInfoDTOS;
    }



}
