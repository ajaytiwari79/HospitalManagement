package com.kairos.activity.service.planner.vrpPlanning;

import com.kairos.activity.client.StaffRestClient;
import com.kairos.activity.client.dto.RestTemplateResponseEnvelope;
import com.kairos.activity.client.planner.PlannerRestClient;
import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.activity.persistence.model.solver_config.SolverConfig;
import com.kairos.activity.persistence.repository.activity.ShiftMongoRepository;
import com.kairos.activity.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.activity.persistence.repository.solver_config.SolverConfigRepository;
import com.kairos.activity.persistence.repository.task_type.TaskTypeSettingMongoRepository;
import com.kairos.activity.response.dto.TaskTypeSettingDTO;
import com.kairos.activity.response.dto.task.VRPTaskDTO;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.service.task_type.TaskService;
import com.kairos.activity.service.task_type.TaskTypeService;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.activity.util.ObjectUtils;
import com.kairos.dto.solverconfig.SolverConfigDTO;
import com.kairos.enums.solver_config.SolverConfigStatus;
import com.kairos.activity.client.dto.staff.StaffDTO;
import com.kairos.response.dto.web.planning.vrpPlanning.EmployeeDTO;
import com.kairos.response.dto.web.planning.vrpPlanning.ShiftDTO;
import com.kairos.response.dto.web.planning.vrpPlanning.TaskDTO;
import com.kairos.response.dto.web.planning.vrpPlanning.VrpTaskPlanningDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toSet;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@Service
public class VRPPlanningService extends MongoBaseService{

    @Inject private SolverConfigRepository solverConfigRepository;
    @Inject private StaffRestClient staffRestClient;
    @Inject private TaskService taskService;
    @Inject private TaskTypeService taskTypeService;
    @Inject private TaskTypeSettingMongoRepository taskTypeSettingMongoRepository;
    @Inject private PlannerRestClient plannerRestClient;
    @Inject private ShiftMongoRepository shiftMongoRepository;
    @Inject private PhaseMongoRepository phaseMongoRepository;
    @Inject private ExceptionService exceptionService;

    public SolverConfigDTO submitToPlanner(Long unitId, BigInteger solverConfigId){
        SolverConfigDTO solverConfigDTO = solverConfigRepository.getOneById(solverConfigId);
        VrpTaskPlanningDTO vrpTaskPlanningDTO = getVRPTaskPlanningDTO(unitId,solverConfigDTO);
        SolverConfig solverConfig = solverConfigRepository.findOne(solverConfigId);
        solverConfig.setStatus(SolverConfigStatus.IN_PROGRESS);
        save(solverConfig);
        solverConfigDTO.setStatus(SolverConfigStatus.IN_PROGRESS);
        plannerRestClient.publish(vrpTaskPlanningDTO,unitId, IntegrationOperation.CREATE);
        return solverConfigDTO;
    }

    public Boolean planningCompleted(Long unitId,BigInteger solverConfigId){
        SolverConfig solverConfig = solverConfigRepository.findOne(solverConfigId);
        solverConfig.setStatus(SolverConfigStatus.COMPLETED);
        save(solverConfig);
        return true;
    }

    public SolverConfigDTO stopToPlannerBySolverConfig(Long unitId,BigInteger solverConfigId){
        SolverConfig solverConfig = solverConfigRepository.findOne(solverConfigId);
        solverConfig.setStatus(SolverConfigStatus.ON_HOLD);
        save(solverConfig);
        return ObjectMapperUtils.copyPropertiesByMapper(solverConfig,SolverConfigDTO.class);
    }


    public VrpTaskPlanningDTO getSolutionBySolverConfig(Long unitId,BigInteger solverConfigId,LocalDate date){
        RestTemplateResponseEnvelope<VrpTaskPlanningDTO> responseEnvelope = plannerRestClient.publish(null,unitId, IntegrationOperation.GET,solverConfigId);

        VrpTaskPlanningDTO vrpTaskPlanningDTO = ObjectMapperUtils.copyPropertiesByMapper(responseEnvelope.getData(),VrpTaskPlanningDTO.class);
        if(vrpTaskPlanningDTO==null || vrpTaskPlanningDTO.getTasks().isEmpty()){
            exceptionService.dataNotFoundByIdException("message.solution.datanotFound");
        }
        List<TaskDTO> taskDTOS = vrpTaskPlanningDTO.getTasks().stream().filter(t->t.getPlannedStartTime().toLocalDate().equals(date)).collect(toList());
        taskDTOS = getTasks(unitId,taskDTOS);
        if(taskDTOS.isEmpty()){
            exceptionService.dataNotFoundByIdException("message.solution.datanotFound");
        }
        List<TaskDTO> drivingTimeList = vrpTaskPlanningDTO.getDrivingTimeList().stream().filter(t->t.getPlannedStartTime().toLocalDate().equals(date)).collect(toList());
        drivingTimeList.forEach(t->{
            t.setStartTime(Date.from(t.getPlannedStartTime().atZone(ZoneId.systemDefault()).toInstant()).getTime());
            t.setEndTime(Date.from(t.getPlannedEndTime().atZone(ZoneId.systemDefault()).toInstant()).getTime());
        });
        List<ShiftDTO> shiftDTOS = vrpTaskPlanningDTO.getShifts().stream().filter(s-> DateUtils.asLocalDate(new Date(s.getStartTime())).equals(date)).collect(toList());
        vrpTaskPlanningDTO.setShifts(shiftDTOS);
        vrpTaskPlanningDTO.setDrivingTimeList(drivingTimeList);
        vrpTaskPlanningDTO.setTasks(taskDTOS);
        return vrpTaskPlanningDTO;
    }

    public List<TaskDTO> getTasks(Long unitId,List<TaskDTO> taskDTOS){
        Map<Long,TaskDTO> taskMap = taskDTOS.stream().collect(Collectors.toMap(k->k.getInstallationNumber(), v->v));
        Map<Long,List<VRPTaskDTO>> installationNOtasks = taskService.getAllTask(unitId).stream().collect(Collectors.groupingBy(VRPTaskDTO::getInstallationNumber,toList()));
        List<TaskDTO> tasks = new ArrayList<>();
        for (Map.Entry<Long, List<VRPTaskDTO>> installationTasks : installationNOtasks.entrySet()) {
            TaskDTO taskDTO = taskMap.get(installationTasks.getKey());
            if(taskDTO!=null){
                LocalDateTime updatedStartTime = taskDTO.getPlannedStartTime();;
                for (VRPTaskDTO vrpTaskDTO : installationTasks.getValue()) {
                    TaskDTO task= new TaskDTO(vrpTaskDTO.getId().toString(),vrpTaskDTO.getInstallationNumber(),new Double(vrpTaskDTO.getAddress().getLatitude()),new Double(vrpTaskDTO.getAddress().getLongitude()),null,vrpTaskDTO.getDuration(),vrpTaskDTO.getAddress().getStreet(),new Integer(vrpTaskDTO.getAddress().getHouseNumber()),vrpTaskDTO.getAddress().getBlock(),vrpTaskDTO.getAddress().getFloorNo(),vrpTaskDTO.getAddress().getZip(),vrpTaskDTO.getAddress().getCity());
                    task.setStaffId(taskDTO.getStaffId());
                    task.setName(vrpTaskDTO.getTaskType().getTitle());
                    task.setStartTime(Date.from(updatedStartTime.atZone(ZoneId.systemDefault()).toInstant()).getTime());
                    task.setEndTime(Date.from(updatedStartTime.plusMinutes(vrpTaskDTO.getDuration()).atZone(ZoneId.systemDefault()).toInstant()).getTime());
                    task.setColor(vrpTaskDTO.getTaskType().getColorForGantt());
                    updatedStartTime = updatedStartTime.plusMinutes(vrpTaskDTO.getDuration());
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }


    public VrpTaskPlanningDTO getVRPTaskPlanningDTO(Long unitId,SolverConfigDTO solverConfigDTO){
        List<TaskDTO> taskDTOS = getTaskForPlanning(unitId);
        Object[] objects = getShiftAndEmployees();
        List<EmployeeDTO> employeeDTOS = (List<EmployeeDTO>)objects[0];
        List<ShiftDTO> shiftDTOS = getShifts(employeeDTOS,(List<Long>)objects[1]);
        return new VrpTaskPlanningDTO(solverConfigDTO,shiftDTOS,null,taskDTOS,null,null);
    }

    public List<TaskDTO> getTaskForPlanning(Long unitId){
        List<VRPTaskDTO> tasks = taskService.getAllTask(unitId);
        List<VRPTaskDTO> uniqueTaskList = tasks.stream().filter(ObjectUtils.distinctByKey(task -> task.getInstallationNumber())).collect(toList());
        List<TaskDTO> taskDTOS = new ArrayList<>(uniqueTaskList.size());
        Map<Long,Integer> intallationandDuration = tasks.stream().collect(groupingBy(VRPTaskDTO::getInstallationNumber,summingInt(VRPTaskDTO::getDuration)));
        Map<Long,Set<String>> intallationandSkill = tasks.stream().collect(groupingBy(VRPTaskDTO::getInstallationNumber,mapping(v->v.getTaskType().getTitle(),toSet())));
        uniqueTaskList.forEach(t->{
            taskDTOS.add(new TaskDTO(t.getId().toString(),t.getInstallationNumber(),new Double(t.getAddress().getLatitude()),new Double(t.getAddress().getLongitude()),intallationandSkill.get(t.getInstallationNumber()),intallationandDuration.get(t.getInstallationNumber()),t.getAddress().getStreet(),new Integer(t.getAddress().getHouseNumber()),t.getAddress().getBlock(),t.getAddress().getFloorNo(),t.getAddress().getZip(),t.getAddress().getCity()));
        });
        return taskDTOS;
    }

    private List<ShiftDTO> getShifts(List<EmployeeDTO> employeeList,List<Long> staffIds){
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        Date startDate = DateUtils.getDateByZonedDateTime(zonedDateTime.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).truncatedTo(ChronoUnit.DAYS));
        Date endDate = DateUtils.getDateByZonedDateTime(zonedDateTime.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).plusWeeks(1).truncatedTo(ChronoUnit.DAYS));
        Map<Long,EmployeeDTO> employeeDTOMap = employeeList.stream().collect(Collectors.toMap(k->new Long(k.getId()),v->v));
        List<ShiftDTO> shifts = shiftMongoRepository.findAllShiftsByStaffIds(staffIds,startDate,endDate);
        shifts.forEach(s->{
            s.setLocalDate(DateUtils.asLocalDate(new Date(s.getStartTime())));
            s.setEmployee(employeeDTOMap.get(s.getStaffId()));
        });
        return shifts;
    }

    private Object[] getShiftAndEmployees(){
        List<StaffDTO> staffs = staffRestClient.getStaffListByUnit();
        List<Long> staffIds = staffs.stream().map(st->st.getId()).collect(toList());
        List<TaskTypeSettingDTO> taskTypeSettingDTOS = taskTypeSettingMongoRepository.findByStaffIds(staffIds);
        Map<Long,List<TaskTypeSettingDTO>> staffSettingMap = taskTypeSettingDTOS.stream().collect(Collectors.groupingBy(t->t.getStaffId(),toList()));
        List<EmployeeDTO> employees = staffSettingMap.entrySet().stream().map(s->new EmployeeDTO(s.getKey().toString(),"",getSkill(s.getValue()),s.getValue().get(0).getEfficiency())).collect(toList());
        Map<Long,String> staffNameMap = staffs.stream().collect(Collectors.toMap(StaffDTO::getId,v->v.getFirstName()+""+v.getLastName()));
        employees.forEach(e->e.setName(staffNameMap.get(new Long(e.getId()))));
        return new Object[]{employees,staffIds};
    }


    private Set<String> getSkill(List<TaskTypeSettingDTO> taskTypeSettings){
        return taskTypeSettings.stream().map(t->t.getTaskTypeId().toString()).collect(Collectors.toSet());
    }

}
