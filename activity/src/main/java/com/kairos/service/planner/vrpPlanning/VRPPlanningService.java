package com.kairos.service.planner.vrpPlanning;

import com.kairos.dto.activity.task_type.TaskTypeSettingDTO;
import com.kairos.dto.planner.vrp.vrpPlanning.*;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.solver_config.PlannerUrl;
import com.kairos.enums.solver_config.PlanningType;
import com.kairos.enums.solver_config.SolverConfigStatus;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.solver_config.SolverConfig;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.solver_config.ConstraintRepository;
import com.kairos.persistence.repository.solver_config.SolverConfigRepository;
import com.kairos.persistence.repository.task_type.TaskTypeSettingMongoRepository;
import com.kairos.dto.planner.solverconfig.ConstraintDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.rest_client.RestTemplateResponseEnvelope;
import com.kairos.rest_client.StaffRestClient;
import com.kairos.rest_client.planner.PlannerRestClient;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.solver_config.SolverConfigService;
import com.kairos.service.task_type.TaskService;
import com.kairos.service.task_type.TaskTypeService;
import com.kairos.dto.user.staff.staff.StaffDTO;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.planner.vrp.task.VRPTaskDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;

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
    @Inject private ConstraintRepository constraintRepository;
    @Inject private SolverConfigService solverConfigService;

    public SolverConfigDTO submitToPlanner(Long unitId, BigInteger solverConfigId,SolverConfigDTO configDTO){
        //createShift();
        SolverConfigDTO solverConfigDTO = solverConfigRepository.getOneById(solverConfigId);
        solverConfigDTO.setPlannerNumber(configDTO.getPlannerNumber());
        solverConfigDTO.setNumberOfThread(configDTO.getNumberOfThread());
        solverConfigDTO.setTerminationTime(configDTO.getTerminationTime());
        List<ConstraintDTO> constraints = constraintRepository.getAllVRPPlanningConstraints(unitId, PlanningType.VRPPLANNING);
        Map<BigInteger,ConstraintDTO> constraintDTOMap = constraints.stream().collect(Collectors.toMap(k->k.getId(),v->v));
        solverConfigDTO.getConstraints().forEach(c->{
            ConstraintDTO constraintDTO = constraintDTOMap.get(c.getId());
            c.setCategory(constraintDTO.getCategory());
            c.setName(constraintDTO.getName());
            c.setDescription(constraintDTO.getDescription());
        });
        VrpTaskPlanningDTO vrpTaskPlanningDTO = getVRPTaskPlanningDTO(unitId,solverConfigDTO);
        SolverConfig solverConfig = solverConfigRepository.findOne(solverConfigId);
        solverConfig.setStatus(SolverConfigStatus.IN_PROGRESS);
        solverConfig.setNumberOfThread(configDTO.getNumberOfThread());
        solverConfig.setTerminationTime(configDTO.getTerminationTime());
        solverConfig.setPlannerNumber(configDTO.getPlannerNumber());
        solverConfig.setLastSubmittedDate(new Date());
        save(solverConfig);
        solverConfigDTO.setStatus(SolverConfigStatus.IN_PROGRESS);
        plannerRestClient.publish(solverConfigDTO.getPlannerNumber(),vrpTaskPlanningDTO,unitId, IntegrationOperation.CREATE,PlannerUrl.SUBMIT_VRP_PROBLEM);
        return solverConfigDTO;
    }

    public SolverConfigDTO resubmitToPlanner(Long unitId, SolverConfigDTO solverConfigDTO){
        SolverConfigDTO newSolverConfigDTO = solverConfigService.createSolverConfigOnReSubmistion(unitId,solverConfigDTO);
        List<ConstraintDTO> constraints = constraintRepository.getAllVRPPlanningConstraints(unitId, PlanningType.VRPPLANNING);
        Map<BigInteger,ConstraintDTO> constraintDTOMap = constraints.stream().collect(Collectors.toMap(k->k.getId(),v->v));
        newSolverConfigDTO.getConstraints().forEach(c->{
            ConstraintDTO constraintDTO = constraintDTOMap.get(c.getId());
            c.setCategory(constraintDTO.getCategory());
            c.setName(constraintDTO.getName());
            c.setDescription(constraintDTO.getDescription());
        });
        VrpTaskPlanningDTO vrpTaskPlanningDTO = getVRPTaskPlanningDTO(unitId,newSolverConfigDTO);
        plannerRestClient.publish(solverConfigDTO.getPlannerNumber(),vrpTaskPlanningDTO,unitId, IntegrationOperation.CREATE,PlannerUrl.SUBMIT_VRP_PROBLEM);
        return newSolverConfigDTO;
    }


    public void createShift(){
        List<Long> staffs = Arrays.asList(5728l, 3361l, 3374l, 3122l, 5217l);
        List<Shift> shifts = new ArrayList<>();
        /*staffs.forEach(s->{
            IntStream.range(0,4).forEachOrdered(i->{
                Date startDate = Date.from(ZonedDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).plusDays(i).with(LocalTime.of(07,00)).toInstant());
                Date endDate = Date.from(ZonedDateTime.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).plusDays(i).with(LocalTime.of(16,00)).plusDays(0).toInstant());
                shifts.add(new Shift(startDate,endDate,s,new BigInteger("145")));
            });
            Date startDate = Date.from(ZonedDateTime.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY)).with(LocalTime.of(07,00)).toInstant());
            Date endDate = Date.from(ZonedDateTime.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY)).with(LocalTime.of(12,30)).plusDays(0).toInstant());
            shifts.add(new Shift(startDate,endDate,s,new BigInteger("145")));

        });*/
        LocalDate weekStart= LocalDate.of(2018,Month.JULY,16);
        staffs.forEach(s->{
            IntStream.range(0,5).forEachOrdered(i->{
                Date startDate = Date.from(weekStart.plusDays(i).atStartOfDay().with(LocalTime.of(07,00)).toInstant(ZoneOffset.UTC));
                Date endDate = Date.from(weekStart.plusDays(i).atStartOfDay().with(i==4?LocalTime.of(12,30):LocalTime.of(16,00)).toInstant(ZoneOffset.UTC));
                shifts.add(new Shift(startDate,endDate,s,new BigInteger("145")));
            });

        });
        save(shifts);
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
        plannerRestClient.publish(solverConfig.getPlannerNumber(),null,unitId, IntegrationOperation.DELETE,PlannerUrl.STOP_VRP_PROBLEM,solverConfigId);
        return ObjectMapperUtils.copyPropertiesByMapper(solverConfig,SolverConfigDTO.class);
    }

    public VrpTaskPlanningDTO getSolutionBySubmition(Long unitId, BigInteger solverConfigId){
        SolverConfig solverConfig = solverConfigRepository.findOne(solverConfigId);
        RestTemplateResponseEnvelope<VrpTaskPlanningDTO> responseEnvelope = plannerRestClient.publish(solverConfig.getPlannerNumber(),null,unitId, IntegrationOperation.GET,PlannerUrl.GET_VRP_SOLUTION,solverConfigId);

        VrpTaskPlanningDTO vrpTaskPlanningDTO = ObjectMapperUtils.copyPropertiesByMapper(responseEnvelope.getData(),VrpTaskPlanningDTO.class);
        if(vrpTaskPlanningDTO==null || vrpTaskPlanningDTO.getTasks().isEmpty()){
            exceptionService.dataNotFoundByIdException("message.solution.datanotFound");
        }
        return vrpTaskPlanningDTO;
    }

    public VrpTaskPlanningDTO getSolutionBySolverConfigByDate(Long unitId, BigInteger solverConfigId, LocalDate date){
        VrpTaskPlanningDTO vrpTaskPlanningDTO = getSolutionBySubmition(unitId, solverConfigId);
        List<TaskDTO> drivingTimeList = vrpTaskPlanningDTO.getDrivingTimeList().stream().filter(t->t.getPlannedStartTime().toLocalDate().equals(date)).collect(toList());
        Object[] objects = getTasks(unitId,vrpTaskPlanningDTO,date);
        drivingTimeList.forEach(t->{
            t.setStartTime(Date.from(t.getPlannedStartTime().atZone(ZoneId.systemDefault()).toInstant()).getTime());
            t.setEndTime(Date.from(t.getPlannedEndTime().atZone(ZoneId.systemDefault()).toInstant()).getTime());
        });
        List<ShiftDTO> shiftDTOS = vrpTaskPlanningDTO.getShifts().stream().filter(s-> DateUtils.asLocalDate(new Date(s.getStartTime())).equals(date)).collect(toList());
        vrpTaskPlanningDTO.setShifts(shiftDTOS);
        vrpTaskPlanningDTO.setDrivingTimeList(drivingTimeList);
        vrpTaskPlanningDTO.setTasks((List<TaskDTO>)objects[0]);
        vrpTaskPlanningDTO.setEscalatedTaskList((List<TaskDTO>)objects[1]);
        return vrpTaskPlanningDTO;
    }


    public VrpTaskPlanningDTO getSolutionBySolverConfig(Long unitId, BigInteger solverConfigId){
        VrpTaskPlanningDTO vrpTaskPlanningDTO = getSolutionBySubmition(unitId, solverConfigId);

        List<TaskDTO> drivingTimeList = vrpTaskPlanningDTO.getDrivingTimeList();
        drivingTimeList.forEach(t->{
            t.setStartTime(Date.from(t.getPlannedStartTime().atZone(ZoneId.systemDefault()).toInstant()).getTime());
            t.setEndTime(Date.from(t.getPlannedEndTime().atZone(ZoneId.systemDefault()).toInstant()).getTime());
        });

        Object[] objects = getTasks(unitId,vrpTaskPlanningDTO,null);
        vrpTaskPlanningDTO.setTasks((List<TaskDTO>)objects[0]);
        //vrpTaskPlanningDTO.setEscalatedTaskList((List<TaskDTO>)objects[1]);
        vrpTaskPlanningDTO.setDrivingTimeList(drivingTimeList);
        return vrpTaskPlanningDTO;
    }




    public VRPIndictmentDTO getIndictmentBySolverConfig(Long unitId, BigInteger solverConfigId){
        SolverConfig solverConfig = solverConfigRepository.findOne(solverConfigId);
        RestTemplateResponseEnvelope<VrpTaskPlanningDTO> responseEnvelope = plannerRestClient.publish(solverConfig.getPlannerNumber(),null,unitId, IntegrationOperation.GET,PlannerUrl.GET_INDICTMENT,solverConfigId);
        VRPIndictmentDTO  vrpIndictmentDTO = ObjectMapperUtils.copyPropertiesByMapper(responseEnvelope.getData(),VRPIndictmentDTO.class);
        return vrpIndictmentDTO;
    }

    public Object[] getTasks(Long unitId,VrpTaskPlanningDTO vrpTaskPlanningDTO,LocalDate date){
        List<TaskDTO> taskDTOS = date!=null ? vrpTaskPlanningDTO.getTasks().stream().filter(t->t.getPlannedStartTime().toLocalDate().equals(date)).collect(toList()):vrpTaskPlanningDTO.getTasks();
        if(taskDTOS.isEmpty()){
            exceptionService.dataNotFoundByIdException("message.solution.datanotFound");
        }
        Map<String,EmployeeDTO> employeeDTOMap = vrpTaskPlanningDTO.getEmployees().stream().collect(Collectors.toMap(k->k.getId(), v->v));
        Map<Long,TaskDTO> taskMap = taskDTOS.stream().collect(Collectors.toMap(k->k.getInstallationNumber(), v->v));
        Map<Long,List<VRPTaskDTO>> installationNOtasks = taskService.getAllTask(unitId).stream().collect(Collectors.groupingBy(VRPTaskDTO::getInstallationNumber,toList()));
        List<TaskDTO> tasks = new ArrayList<>();
        List<TaskDTO> escalatedTasks = new ArrayList<>();
        for (Map.Entry<Long, List<VRPTaskDTO>> installationTasks : installationNOtasks.entrySet()) {
            TaskDTO taskDTO = taskMap.get(installationTasks.getKey());
            if(taskDTO!=null){
                LocalDateTime updatedStartTime = taskDTO.getPlannedStartTime();
                for (VRPTaskDTO vrpTaskDTO : installationTasks.getValue()) {
                    TaskDTO task= new TaskDTO(vrpTaskDTO.getId().toString(),vrpTaskDTO.getInstallationNumber(),new Double(vrpTaskDTO.getAddress().getLatitude()),new Double(vrpTaskDTO.getAddress().getLongitude()),null,vrpTaskDTO.getDuration(),vrpTaskDTO.getAddress().getStreet(),new Integer(vrpTaskDTO.getAddress().getHouseNumber()),vrpTaskDTO.getAddress().getBlock(),vrpTaskDTO.getAddress().getFloorNo(),vrpTaskDTO.getAddress().getZip(),vrpTaskDTO.getAddress().getCity());
                    task.setEscalated(taskDTO.isEscalated());
                    task.setStaffId(taskDTO.getStaffId());
                    task.setName(vrpTaskDTO.getTaskType().getTitle());
                    task.setStartTime(Date.from(updatedStartTime.atZone(ZoneId.systemDefault()).toInstant()).getTime());
                    task.setCitizenName(vrpTaskDTO.getCitizenName());
                    EmployeeDTO employeeDTO = employeeDTOMap.get(taskDTO.getStaffId().toString());
                    int taskDuration = (int)Math.ceil(vrpTaskDTO.getDuration()/(employeeDTO.getEfficiency()/100d));
                    task.setActualDuration(vrpTaskDTO.getDuration());
                    task.setDuration(taskDuration);
                    updatedStartTime = updatedStartTime.plusMinutes(taskDuration);
                    task.setEndTime(Date.from(updatedStartTime.atZone(ZoneId.systemDefault()).toInstant()).getTime());
                    task.setColor(vrpTaskDTO.getTaskType().getColorForGantt());
                    tasks.add(task);
                    if(taskDTO.isEscalated()){
                        escalatedTasks.add(task);
                    }
                }
            }
        }

        return new Object[]{tasks,escalatedTasks};
    }

    public int[] getPlannedDuration(int duration,EmployeeDTO employeeDTO){
        Double taskDuration = duration/(employeeDTO.getEfficiency()/100d);
        int minutes = (int)Math.floor( taskDuration );
        BigDecimal bd = new BigDecimal( taskDuration - minutes);
        bd = bd.setScale(4, RoundingMode.HALF_DOWN);
        return new int[]{minutes,bd.intValue()};
    }

    public VrpTaskPlanningDTO getVRPTaskPlanningDTO(Long unitId,SolverConfigDTO solverConfigDTO){
        List<TaskDTO> taskDTOS = getTaskForPlanning(unitId);
        Object[] objects = getEmployees();
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
        List<Shift> shifts = shiftMongoRepository.findAllShiftsByStaffIds(staffIds,startDate,endDate);
        List<ShiftDTO> shiftDTOS = new ArrayList<>(shifts.size());
        shifts.forEach(s->{
            shiftDTOS.add(new ShiftDTO(s.getId().toString(),s.getName(),employeeDTOMap.get(s.getStaffId()),DateUtils.asLocalDate(s.getStartDate()),s.getStartDate(),s.getEndDate()));
        });
        return shiftDTOS;
    }

    private Object[] getEmployees(){
        List<StaffDTO> staffs = staffRestClient.getStaffListByUnit();
        List<Long> staffIds = staffs.stream().map(st->st.getId()).collect(toList());
        List<TaskTypeSettingDTO> taskTypeSettingDTOS = taskTypeSettingMongoRepository.findByStaffIds(staffIds);
        if(taskTypeSettingDTOS.isEmpty()){
            exceptionService.invalidRequestException("message.taskType.settings.notFound");
        }
        Map<Long,List<TaskTypeSettingDTO>> staffSettingMap = taskTypeSettingDTOS.stream().collect(Collectors.groupingBy(t->t.getStaffId(),toList()));
        List<EmployeeDTO> employees = staffSettingMap.entrySet().stream().map(s->new EmployeeDTO(s.getKey().toString(),"",getSkill(s.getValue()),s.getValue().get(0).getEfficiency())).collect(toList());
        Map<Long,String> staffNameMap = staffs.stream().collect(Collectors.toMap(StaffDTO::getId,v->v.getFirstName()+" "+v.getLastName()));
        employees.forEach(e->e.setName(staffNameMap.get(new Long(e.getId()))));
        return new Object[]{employees,staffIds};
    }


    private Set<String> getSkill(List<TaskTypeSettingDTO> taskTypeSettings){
        return taskTypeSettings.stream().map(t->t.getTaskType().getTitle()).collect(Collectors.toSet());
    }

}
