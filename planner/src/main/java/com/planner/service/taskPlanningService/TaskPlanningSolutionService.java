package com.planner.service.taskPlanningService;

import com.kairos.planning.domain.*;
import com.kairos.planning.enums.SkillType;
import com.kairos.planning.solution.TaskPlanningSolution;
import com.planner.domain.citizen.PlanningCitizen;
import com.planner.domain.location.LocationDistance;
import com.planner.domain.location.PlanningLocation;
import com.planner.domain.skill.PlanningSkill;
import com.planner.domain.skill.SkillWithLevel;
import com.planner.domain.staff.PlanningShift;
import com.planner.domain.staff.PlanningStaff;
import com.planner.domain.task.PlanningTask;
import com.planner.domain.task.PlanningTaskType;
import com.planner.repository.taskPlanningRepository.PlanningRepository;
import com.planner.responseDto.PlanningDto.taskplanning.TaskPlanningDTO;
import com.planner.service.citizenService.CitizenService;
import com.planner.service.locationService.LocationService;
import com.planner.service.skillService.SkillService;
import com.planner.service.staffService.ShiftService;
import com.planner.service.staffService.TaskStaffService;
import com.planner.service.taskService.PlanningTaskService;
import com.planner.service.taskService.TaskTypeService;
import com.planner.service.vehicleService.VehicleService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.LongStream;

@Service
public class TaskPlanningSolutionService {

    private static Logger logger = LoggerFactory.getLogger(TaskPlanningSolutionService.class);

    @Autowired private PlanningTaskService planningTaskService;
    @Autowired private TaskTypeService taskTypeService;
    @Autowired private SkillService skillService;
    @Autowired private TaskStaffService taskStaffService;
    @Autowired private VehicleService vehicleService;
    @Autowired private CitizenService citizenService;
    @Autowired private LocationService locationService;
    @Autowired private ShiftService shiftService;
    @Autowired private PlanningRepository planningRepository;



    public TaskPlanningSolution getTaskPlanningSolutionByDate(TaskPlanningDTO taskPlanningDTO) {
        TaskPlanningSolution taskPlanningSolution = new TaskPlanningSolution();
        long unitId = taskPlanningDTO.getUnitId();
        Date startDate = taskPlanningDTO.getStartDateTime();
        Date endDate = taskPlanningDTO.getEndDateTime();
        Map<String,Skill> skillMap = getSkillMap(unitId);
        getTaskList(taskPlanningSolution,unitId,startDate,endDate,skillMap);
        getEmployeeList(taskPlanningSolution,unitId,startDate,endDate,skillMap);
        taskPlanningSolution.setVehicleList(getVehicleList(taskPlanningSolution));
        return taskPlanningSolution;

    }

    private Map<String,Skill> getSkillMap(Long unitId){
        List<SkillWithLevel> skillWithLevels = skillService.getAllSKillWithLevelByUnitId(unitId);
        Map<String,PlanningSkill> planningSkillMap = getPlanningSkillMap();
        Map<String,Skill> skillMap = new HashMap<>(skillWithLevels.size());
        for (SkillWithLevel skillWithLevel:skillWithLevels) {
            Skill skill = new Skill();
            skill.setId(skillWithLevel.getId());
            skill.setSkillType(SkillType.getEnumByString(skillWithLevel.getSkillLevel().toValue()));
            skill.setName(planningSkillMap.get(skillWithLevel.getSkillId()).getName());
            skillMap.put(skillWithLevel.getId(),skill);
        }
        return skillMap;
    }

    private Map<String,PlanningSkill> getPlanningSkillMap(){
        List<PlanningSkill> planningSkills = skillService.getAll();
        Map<String,PlanningSkill> planningSkillMap = new HashMap<>(planningSkills.size());
        for (PlanningSkill planningSkill:planningSkills) {
            planningSkillMap.put(planningSkill.getId(),planningSkill);
        }
        return planningSkillMap;
    }

   /* private List<Skill> getSkillsBySkill(){
        List<Skill> skills = new ArrayList<>();
        Skill skill;
        skill = new Skill();
        skill.setId(0l);
        skill.setName("Alle ydelser");
        skills.add(skill);
        return skills;
    }*/


    private void getTaskList(TaskPlanningSolution taskPlanningSolution, long unitId,Date startDate,Date endDate,Map<String,Skill> skillMap) {
        Map<String,Location> locationsMap = getLocationsMap();
        List<PlanningTask> planningTasks = planningTaskService.getAllTasksForPLanning(unitId,startDate,endDate);
        Map<String,Citizen> citizenMap = getCitizenMap(unitId);
        Map<String,TaskType> taskTypeMap = getTaskTypesMap(unitId,skillMap);
        List<Task> tasks = new ArrayList<>();
        List<Location> locations = new ArrayList<>();
        List<Citizen> citizens = new ArrayList<>();
        List<TaskType> taskTypes = new ArrayList<>();
        for (PlanningTask planningTask : planningTasks) {
            Location location = locationsMap.get(planningTask.getLocationId());
            if (location != null) {
                locations.add(location);
                Citizen citizen = citizenMap.get(planningTask.getCitizenId());
                Task task = new Task();
                if (citizen!=null){
                    citizens.add(citizen);
                    task.setCitizen(citizen);
                }
                task.setTaskName(planningTask.getTaskName());
                task.setId(planningTask.getId());
                task.setPriority(planningTask.getPriority());
                task.setLocation(location);
                task.setSlaDurationStart1(planningTask.getFirstStartSlaDurationMin());
                task.setSlaDurationStart2(planningTask.getSecondStartSlaDurationMin());
                task.setSlaDurationEnd1(planningTask.getFirstEndSlaDurationMin());
                task.setSlaDurationEnd2(planningTask.getSecondEndSlaDurationMin());
                task.setDuration(planningTask.getDurationInMin());
                task.setInitialStartTime1(new DateTime(planningTask.getFirstStartDateTime()));
                task.setInitialEndTime1(new DateTime(planningTask.getFirstEndDateTime()));
                task.setInitialStartTime2(new DateTime(planningTask.getSecondStartDateTime()));
                task.setInitialEndTime2(new DateTime(planningTask.getSecondEndDateTime()));
                TaskType taskType = taskTypeMap.get(planningTask.getTasktypeId());
                if (taskType!=null){
                    taskTypes.add(taskType);
                    task.setTaskType(taskType);
                }
                tasks.add(task);
            }
        }
        taskPlanningSolution.setLocationList(locations);
        taskPlanningSolution.setTaskTypeList(taskTypes);
        taskPlanningSolution.setCitizenList(citizens);
        taskPlanningSolution.setTaskList(tasks);
    }

    private Map<String,Location> getLocationsMap(){
        List<PlanningLocation> planningLocations = locationService.getAllPlanningLocations();
        Map<String,List<LocationInfo>> distanceMap = getLocationDistanceMap(planningLocations.size());
        Map<String,Location> locationMap = new HashMap<>();
        for (PlanningLocation planningLocation:planningLocations) {
            Location location = new Location();
           // location.setId(planningLocation.getId());
            location.setLatitude(planningLocation.getLatitude());
            location.setLongitude(planningLocation.getLongitude());
            location.setLocationInfos(distanceMap.get(planningLocation.getId()));
           // locationMap.put(planningLocation.getId(),location);
        }
        return locationMap;
    }

    private Map<String,List<LocationInfo>> getLocationDistanceMap(int size){
        List<LocationDistance> locationDistances = locationService.getAllLocationDistances();
        Map<String,List<LocationInfo>> listMap = new HashMap<>(size);
        for (LocationDistance locationDistance:locationDistances) {
            listMap.put(locationDistance.getFirstLocationId(),getLocationDistancesById(locationDistance.getFirstLocationId(),locationDistances));
        }
        return listMap;
    }

    private List<LocationInfo> getLocationDistancesById(String firstLocationId,List<LocationDistance> locationDistances){
        List<LocationInfo> locationInfos = new ArrayList<>();
        for (LocationDistance locationDistance:locationDistances) {
            if(firstLocationId.equals(locationDistance.getFirstLocationId())){
                LocationInfo locationInfo = new LocationInfo();
                locationInfo.setLocationId(locationDistance.getSecondLocationId());
                locationInfo.setDistance(locationDistance.getDistanceByCar());
                locationInfo.setTime(locationDistance.getTimeByCar());
                locationInfos.add(locationInfo);
            }
        }
        return locationInfos;
    }

    private Map<String,Citizen> getCitizenMap(long unitId){
        List<PlanningCitizen> planningCitizens = citizenService.getAllByUnitId(unitId);
        Map<String,Citizen> citizenMap = new HashMap<>(planningCitizens.size());
        for (PlanningCitizen planningCitizen:planningCitizens) {
            Citizen citizen = new Citizen(planningCitizen.getId(),planningCitizen.getFirstName()+" "+planningCitizen.getLastName());
            //citizen.setLocationId(planningCitizen.getLocationId());
            citizenMap.put(planningCitizen.getId(),citizen);
        }
        return citizenMap;
    }


    private Map<String,TaskType> getTaskTypesMap(long unitId,Map<String,Skill> skillMap){
        List<TaskType> taskTypes = new ArrayList<>();
        List<PlanningTaskType> planningTaskTypes = taskTypeService.getAllByUnitId(unitId);
        Map<String,TaskType> taskTypeMap = new HashMap<>(planningTaskTypes.size());
        for (PlanningTaskType planningTasktype:planningTaskTypes) {
            TaskType taskType = new TaskType();
            taskType.setId(planningTasktype.getId());
            taskType.setRequiredSkillList(getSkillByTaskType(planningTasktype.getSkillWithIds(),skillMap));
            taskType.setTitle(planningTasktype.getTitle());
            taskTypeMap.put(planningTasktype.getId(),taskType);
        }
        return taskTypeMap;
    }

    /*private List<Skill> getSkillByPlanningSkills(List<PlanningSkill> planningSkills){
        List<Skill> skills = new ArrayList<>();
        for (PlanningSkill planningSkill:planningSkills) {
            Skill skill = new Skill();
            skill.setId(planningSkill.getId());
            skill.setName(planningSkill.getName());
            skill.setSkillLevels(SkillLevel.getEnumByString(planningSkill.getSkillLevels().toValue()));
            skills.add(skill);
        }
        return skills;
    }*/

    /*private Location getLocationByTask(List<Location> locations,long locationId){
        for (Location location:locations) {
            if(location.getId()==locationId) return location;
        }
        return null;
    }*/

    /*private List<Location> getLocationsByPlanningLocations(long unitId){
        List<PlanningLocation> planningLocations = locationService.getAllByUnitIdWithUnitLocation(unitId);
        List<LocationDistance> locationDistances = locationService.getAllLocationDistances();
        List<Location> locations = new ArrayList<>();
        for (PlanningLocation planningLocation:planningLocations) {
            Location location = new Location();
            location.setId(planningLocation.getId());
            location.setName(planningLocation.getCity());
            location.setLongitude(planningLocation.getLongitude());
            location.setLatitude(planningLocation.getLongitude());
            location.setLocationInfos(getLocationInfo(planningLocation.getId(),locationDistances));
            locations.add(location);
        }
        return locations;
    }
    
    private List<LocationInfo> getLocationInfo(long plannigLocationId,List<LocationDistance> locationDistances){
        List<LocationInfo> locationInfos = new ArrayList<>();
        for (LocationDistance locationDistance:locationDistances) {
            LocationInfo locationInfo = new LocationInfo();
            locationInfo.setDistance(locationDistance.getDistanceByCar());
            locationInfo.setTime(locationDistance.getTimeByCar());
            locationInfo.setLocationId(locationDistance.getSecondLocationId());
            locationInfos.add(locationInfo);
        }
        return locationInfos;
    }*/


    /*private Citizen getCitizentByTask(PlanningTask task,List<Citizen> citizens){
        for (Citizen citizen:citizens) {
            if(task.getCitizenId()==citizen.getId()) return citizen;
        }
        return null;
    }*/


    private void getEmployeeList(TaskPlanningSolution taskPlanningSolution, long unitId,Date startDate,Date endDate,Map<String,Skill> skillMap) {
        Location unitLocation = taskPlanningSolution.getLocationList().get(0);
        List<Skill> skills = taskPlanningSolution.getSkillList();
        Map<String,Employee> employeeMap = getEmployeeMap(unitId,skillMap);
        List<Employee> employees = new ArrayList<>();
        List<AvailabilityRequest> availabilityRequests = new ArrayList<>();
        List<PlanningShift> planningShifts = shiftService.getAllByUnitId(startDate,endDate,unitId);
        for (PlanningShift planningShift : planningShifts) {
            AvailabilityRequest availabilityRequest = new AvailabilityRequest();
            Employee employee = employeeMap.get(planningShift.getStaffId());
            if(employee!=null){
                availabilityRequest.setEmployee(employeeMap.get(planningShift.getStaffId()));
                employees.add(employee);
                availabilityRequest.setStartTime(new DateTime(planningShift.getStartTime()));
                availabilityRequest.setEndTime(new DateTime(planningShift.getEndTime()));
               // availabilityRequest.setId(planningShift.getId());
//                availabilityRequest.setExternalId(planningShift.getExternalId());
                availabilityRequests.add(availabilityRequest);

            }else continue;
        }
        taskPlanningSolution.setAvailabilityList(availabilityRequests);
        //taskPlanningSolution.setUnavailabilityRequests(shiftList.get(1));
        taskPlanningSolution.setEmployeeList(employees);
    }

    private Map<String,Employee> getEmployeeMap(Long unitId,Map<String,Skill> skillMap){
        List<PlanningStaff> planningStaffs = taskStaffService.getAllByUnitId(unitId);
        Map<String,Employee> employeeMap = new HashMap<>(planningStaffs.size());
        for (PlanningStaff planningStaff:planningStaffs) {
            Employee employee = new Employee();
            employee.setExternalId(planningStaff.getExternalId());
            employee.setId(planningStaff.getId());
            employee.setName(planningStaff.getFirstName()+" "+planningStaff.getLastName());
            employee.setSkillSet(getSkillByStaff(planningStaff.getSkillIds(),skillMap));
            employeeMap.put(planningStaff.getId(),employee);
        }
        return employeeMap;
    }

    private Set<Skill> getSkillByStaff(List<String> skillIds,Map<String,Skill> skillMap){
        Set<Skill> staffSkills = new HashSet<>();
        for (String skillId:skillIds) {
            staffSkills.add(skillMap.get(skillId));
        }
        return staffSkills;
    }

/*
    private List<List> getShiftByStaffId(List<PlanningShift> planningShifts, Long staffid){
        List list = new ArrayList();
        List<AvailabilityRequest> availabilityRequests = new ArrayList<>();
        List<UnavailabilityRequest> unavailabilityRequests = new ArrayList<>();
        for (PlanningShift planningShift : planningShifts) {
            if (planningShift.getStaffId()==staffid && planningShift.getShiftType().equals(ShiftType.PRESENT))
            {
                AvailabilityRequest availabilityRequest = new AvailabilityRequest();
                availabilityRequest.setId(planningShift.getId());
                availabilityRequest.setEndTime(new DateTime(planningShift.getEndTime()));
                availabilityRequest.setStartTime(new DateTime(planningShift.getStartTime()));
                availabilityRequests.add(availabilityRequest);
            }else{
                UnavailabilityRequest unavailabilityRequest = new UnavailabilityRequest();
                unavailabilityRequest.setId(planningShift.getId());
                unavailabilityRequest.setStartTime(new DateTime(planningShift.getStartTime()));
                unavailabilityRequest.setEndTime(new DateTime(planningShift.getEndTime()));
                unavailabilityRequests.add(unavailabilityRequest);
            }
        }
        list.add(availabilityRequests);
        list.add(unavailabilityRequests);
        return list;
    }
*/

    private List<Vehicle> getVehicleList(TaskPlanningSolution taskPlanningSolution) {
        Location location = taskPlanningSolution.getLocationList().get(0);
        List<Skill> skills = taskPlanningSolution.getSkillList();
        List<Vehicle> vehicles = new ArrayList<>();
        LongStream.range(0, 50).forEach(i->{
            vehicles.add(createVehicle(location,i,skills));
                }
        );
        return vehicles;
    }
    private Vehicle createVehicle(Location location, Long id, List<Skill> skills){
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        vehicle.setType("Car");
        vehicle.setSpeedKmpm(1.5);
        vehicle.setLocation(location);
        vehicle.setRequiredSkillList(skills);
        return vehicle;
    }


      /*private TaskType getTaskTypeBytask(PlanningTask planningTask,List<TaskType> taskTypes){
          for (TaskType taskType: taskTypes) {
              if(planningTask.getTasktypeId()==taskType.getId()){
                  return taskType;
              }
          }
          return null;
      }*/

      private List<Skill> getSkillByTaskType(List<String> skillIds, Map<String,Skill> skillMap){
          List<Skill> taskTypeskills = new ArrayList<>();
          for (String skillId:skillIds) {
              taskTypeskills.add(skillMap.get(skillIds));
          }
          return taskTypeskills;
      }

}