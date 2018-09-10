package com.planner.service.shiftPlanningService;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.dto.activity.staffing_level.StaffingLevelDto;
import com.kairos.dto.activity.staffing_level.presence.PresenceStaffingLevelDto;
import com.kairos.shiftplanning.domain.*;
import com.kairos.shiftplanning.domain.activityConstraint.ActivityConstraints;
import com.kairos.shiftplanning.domain.cta.CollectiveTimeAgreement;
import com.kairos.shiftplanning.domain.wta.WorkingTimeConstraints;
import com.kairos.shiftplanning.enums.SkillType;
import com.kairos.shiftplanning.executioner.ShiftPlanningGenerator;
import com.kairos.shiftplanning.executioner.ShiftPlanningSolver;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import com.planner.repository.staffRepository.TaskStaffRepository;
import com.planner.responseDto.PlanningDto.shiftPlanningDto.*;
import com.planner.service.skillService.SkillService;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
public class ShiftRequestPhasePlanningSolutionService {

    private static Logger logger = LoggerFactory.getLogger(ShiftRequestPhasePlanningSolutionService.class);

    @Autowired
    private TaskStaffRepository taskStaffRepository;
    @Autowired
    private SkillService skillService;
    @Autowired private ShiftWtaInfoService shiftWtaInfoService;
   // @Autowired private RestTemplate restTemplate;

    public ShiftRequestPhasePlanningSolution getShiftPlanningSolution(Map<String, Object> shiftPlanningInfo) {
        //Map<String, Object> shiftPlanningInfo = getDataFromKairos(planningDTO.getUnitId(),planningDTO.getStartFrom(),planningDTO.getEndTo());
        ShiftRequestPhasePlanningSolution shiftPlanningSolution = new ShiftRequestPhasePlanningSolution();
        ObjectMapper mapper = new ObjectMapper();
        List<ActivityDTO> activityDTOS = mapper.convertValue(shiftPlanningInfo.get("activities"),new TypeReference<List<ActivityDTO>>(){});
        //mapper.registerModule(new JavaTimeModule());
        List<PresenceStaffingLevelDto> staffingLevelDtos = mapper.convertValue(shiftPlanningInfo.get("staffingLevel"),new TypeReference<List<StaffingLevelDto>>(){});
        Map<BigInteger,Activity> activityMap = getActivityMap(activityDTOS);
        List<StaffDTO> staffDTOS = mapper.convertValue(shiftPlanningInfo.get("staffs"),new TypeReference<List<StaffDTO>>(){});
        Object[] objects = getStaffingLevel(staffingLevelDtos,activityMap);
        shiftPlanningSolution.setActivityLineIntervals((List<ActivityLineInterval>)objects[0]);
        shiftPlanningSolution.setSkillLineIntervals((List<SkillLineInterval>)objects[1]);
        shiftPlanningSolution.setActivities(new ArrayList<>((Set<Activity>)objects[2]));
        List<LocalDate> weekDates = new ArrayList<LocalDate>((Set)objects[3]);
        shiftPlanningSolution.setWeekDates(weekDates);
        List<Employee> employees = getEmployee(staffDTOS,activityMap.entrySet().stream().map(a->a.getValue()).collect(Collectors.toList()));
        shiftPlanningSolution.setShifts(generateShiftForAssignments(employees,weekDates));
        shiftPlanningSolution.setEmployees(employees);
        //shiftPlanningSolution.setPossibleStartDateTimes();
        shiftPlanningSolution.setUnitId(new Long((Integer)shiftPlanningInfo.get("unitId")));
        shiftPlanningSolution.setId("1234");
        ShiftPlanningSolver shiftPlanningSolver = new ShiftPlanningSolver("src/main/resources/com/kairos/shiftplanning/configuration/shiftPlanning.xml");
        try {
            new ShiftPlanningSolver().toXml(shiftPlanningSolution,"problemFromKairos");
        } catch (Exception e) {
            e.printStackTrace();
        }
        shiftPlanningSolver.runSolverOnRequest(shiftPlanningSolution);

        return shiftPlanningSolution;
    }


    private List<File> getDrlFileList(){
        return Arrays.asList(new File("src/main/resources/com/kairos/shiftplanning/rules/shiftplanning_activityLine.drl"),
                new File("src/main/resources/com/kairos/shiftplanning/rules/wtaConstraintsRule.drl")
                ,new File("src/main/resources/com/kairos/shiftplanning/rules/activityConstraintsRule.drl"));
    }

    private List<Employee> getEmployee(List<StaffDTO> staffDTOS, List<Activity> activities){
        List<Employee> employees = new ArrayList<>(staffDTOS.size());
        ShiftPlanningGenerator shiftPlanningGenerator = new ShiftPlanningGenerator();
        WorkingTimeConstraints workingTimeConstraints = shiftPlanningGenerator.getWTA();
        CollectiveTimeAgreement collectiveTimeAgreement = shiftPlanningGenerator.getCTA(activities);
        staffDTOS.forEach(s->{
            Employee employee =null;// new Employee(s.getId().toString(),s.getFirstName(),getEmployeeSkills(s.getSkillSet()), expertiseId);
            employee.setWorkingTimeConstraints(workingTimeConstraints);
            employee.setBaseCost(new BigDecimal(1.5));
            employee.setCollectiveTimeAgreement(collectiveTimeAgreement);
            employee.setPrevShiftsInfo(shiftPlanningGenerator.getPreShiftsInfo());
            employees.add(employee);
        });
        return employees;
    }

    public List<ShiftRequestPhase> generateShiftForAssignments(List<Employee> employees, List<LocalDate> weekDates) {
        List<ShiftRequestPhase> shiftList = new ArrayList<>();
        employees.forEach(e->{
            weekDates.forEach(w->{
                ShiftRequestPhase shift= new ShiftRequestPhase();
                shift.setEmployee(e);
                shift.setId(UUID.randomUUID());
                shift.setDate(w);
                shiftList.add(shift);
            });
        });
        return shiftList;
    }

    public Map<BigInteger,Activity> getActivityMap(List<ActivityDTO> activityDTOS){
        Map<BigInteger,Activity> activityMap = new HashMap<>(activityDTOS.size());
        ActivityConstraints activityConstraints = new ShiftPlanningGenerator().getActivityContraints();
        int i=0;
        for (ActivityDTO activityDTO : activityDTOS) {
            Activity activity = new Activity(activityDTO.getId().toString(), getSkills(activityDTO.getSkillActivityTab().getActivitySkills()), 5, activityDTO.getName(),new TimeType(activityDTO.getTimeType().getId().toString(),activityDTO.getTimeType().getLabel()), ++i,i, null);
            activity.setActivityConstraints(activityConstraints);
            activityMap.put(activityDTO.getId(), activity);
        }
        return activityMap;
    }

    public List<Skill> getSkills(List<ActivitySkill> activitySkills){
        return activitySkills.stream().map(as->new Skill(as.getSkillId().toString(),as.getName(),SkillType.BASIC)).collect(Collectors.toList());

    }

    public Set<Skill> getEmployeeSkills(Set<SkillDTO> skillDTOS){
        return skillDTOS.stream().map(as->new Skill(as.getId().toString(),as.getName(),SkillType.BASIC)).collect(Collectors.toSet());

    }

    public Object[] getStaffingLevel(List<PresenceStaffingLevelDto> staffingLevelDtos, Map<BigInteger,Activity> activityMap){
        List<ActivityLineInterval> activityLineIntervals = new ArrayList<>();
        List<SkillLineInterval> skillLineIntervals = new ArrayList<>();
        Set<LocalDate> weekDates = new HashSet<>(staffingLevelDtos.size());
        Set<Activity> activities = new HashSet<>();
        staffingLevelDtos.forEach(stl->{
            stl.getPresenceStaffingLevelInterval().forEach(sli->{
                sli.getStaffingLevelActivities().forEach(sla->{
                    IntStream.range(0,sla.getMinNoOfStaff()).forEachOrdered(i->{
                        Activity activity = activityMap.get(new BigInteger(sla.getActivityId().toString()));
                        //need to clarify require and staffNo.
                        ActivityLineInterval activityLineInterval;
                        if(activity.isTypeAbsence()){
                            activityLineInterval = new ActivityLineInterval(new Random().toString(),asDate(sli.getStaffingLevelDuration().getFrom()).withTimeAtStartOfDay(), 1440, true, activity,12);
                        }else {
                            activityLineInterval = new ActivityLineInterval(new Random().toString(), asDate(sli.getStaffingLevelDuration().getFrom()), 15, true, activity, i);
                        }
                        activities.add(activity);
                        activityLineIntervals.add(activityLineInterval);
                    });
                });
                sli.getStaffingLevelSkills().forEach(sls->{
                    IntStream.range(0,sls.getNoOfStaff()).forEachOrdered(i->{
                        skillLineIntervals.add(new SkillLineInterval(asDate(sli.getStaffingLevelDuration().getFrom()),asDate(sli.getStaffingLevelDuration().getTo()),true,new Skill(""+1,"",SkillType.BASIC)));
                    });
                });
                weekDates.add(new LocalDate(stl.getCurrentDate()));
            });
        });

        return new Object[]{activityLineIntervals,skillLineIntervals,activities,weekDates};
    }

    public static DateTime asDate(LocalTime localTime) {
        Instant instant = localTime.atDate(java.time.LocalDate.now()).
                atZone(ZoneId.systemDefault()).toInstant();
        return new DateTime(Date.from(instant));
    }

    /*public Map<String,Object> getDataFromKairos(Long unitId,Date startDate,Date endDate){
        final String baseUrl=new StringBuilder(" http://192.168.6.43:5555/kairos/activity/api/v1/organization/").append(71).append("/unit/").append(unitId).append("/getShiftPlanningInfo").toString();


        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("startDate", startDate)
                .queryParam("endDate", endDate);
        try {
            ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String,Object>>> typeReference = new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String,Object>>>() {};
            ResponseEntity<RestTemplateResponseEnvelope<Map<String,Object>>> restExchange =
                    new RestTemplate().exchange(
                            builder.build().encode().toUri(),
                            HttpMethod.GET,
                            null, typeReference);
            RestTemplateResponseEnvelope<Map<String,Object>> response = restExchange.getBody();
            if (restExchange.getStatusCode().is2xxSuccessful()) {

                Map<String,Object> shiftPlanningInfo =  response.getData();
                return shiftPlanningInfo;
            } else {
                throw new RuntimeException(response.getMessage());
            }
        }catch (HttpClientErrorException e) {
            logger.info("status {}",e.getStatusCode());
            logger.info("response {}",e.getResponseBodyAsString());
            throw new RuntimeException("exception occurred in user micro service "+e.getMessage());
        }

    }*/

}
