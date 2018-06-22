package com.kairos.activity.service.planner.vrpPlanning;

import com.kairos.activity.client.StaffRestClient;
import com.kairos.activity.client.dto.RestTemplateResponseEnvelope;
import com.kairos.activity.client.planner.PlannerRestClient;
import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.activity.persistence.model.solver_config.SolverConfig;
import com.kairos.activity.persistence.model.task.Task;
import com.kairos.activity.persistence.repository.solver_config.SolverConfigRepository;
import com.kairos.activity.persistence.repository.task_type.TaskTypeSettingMongoRepository;
import com.kairos.activity.response.dto.TaskTypeSettingDTO;
import com.kairos.activity.response.dto.task.VRPTaskDTO;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.task_type.TaskService;
import com.kairos.activity.service.task_type.TaskTypeService;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.activity.util.ObjectUtils;
import com.kairos.dto.solverconfig.SolverConfigDTO;
import com.kairos.enums.solver_config.SolverConfigStatus;
import com.kairos.response.dto.web.StaffDTO;
import com.kairos.response.dto.web.planning.vrpPlanning.EmployeeDTO;
import com.kairos.response.dto.web.planning.vrpPlanning.ShiftDTO;
import com.kairos.response.dto.web.planning.vrpPlanning.TaskDTO;
import com.kairos.response.dto.web.planning.vrpPlanning.VrpTaskPlanningDTO;
import com.kairos.response.dto.web.staff.UnitStaffResponseDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
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

    public SolverConfigDTO submitToPlanner(Long unitId, BigInteger solverConfigId){
        SolverConfig solverConfig = solverConfigRepository.findOne(solverConfigId);
        SolverConfigDTO solverConfigDTO = ObjectMapperUtils.copyPropertiesByMapper(solverConfig,SolverConfigDTO.class);
        VrpTaskPlanningDTO vrpTaskPlanningDTO = getVRPTaskPlanningDTO(unitId,solverConfigDTO);
        plannerRestClient.publish(vrpTaskPlanningDTO,unitId, IntegrationOperation.CREATE);
        solverConfig.setStatus(SolverConfigStatus.IN_PROGRESS);
        save(solverConfig);
        solverConfigDTO.setStatus(SolverConfigStatus.IN_PROGRESS);
        return solverConfigDTO;
    }

    public SolverConfigDTO stopToPlannerBySolverConfig(Long unitId,BigInteger solverConfigId){
        SolverConfig solverConfig = solverConfigRepository.findOne(solverConfigId);
        solverConfig.setStatus(SolverConfigStatus.ON_HOLD);
        save(solverConfig);
        return ObjectMapperUtils.copyPropertiesByMapper(solverConfig,SolverConfigDTO.class);
    }


    public VrpTaskPlanningDTO getSolutionBySolverConfig(Long unitId,BigInteger solverConfigId){
        RestTemplateResponseEnvelope<VrpTaskPlanningDTO> responseEnvelope = plannerRestClient.publish(null,unitId, IntegrationOperation.GET,solverConfigId);
        VrpTaskPlanningDTO vrpTaskPlanningDTO = responseEnvelope.getData();
        Map<Long,List<VRPTaskDTO>> installationNOtasks = taskService.getAllTask(unitId).stream().collect(Collectors.groupingBy(VRPTaskDTO::getInstallationNumber,toList()));

        return vrpTaskPlanningDTO;
    }


    public VrpTaskPlanningDTO getVRPTaskPlanningDTO(Long unitId,SolverConfigDTO solverConfigDTO){
        List<TaskDTO> taskDTOS = getTaskForPlanning(unitId);
        List<EmployeeDTO> employeeDTOs = getEmployees(unitId);
        //List<ShiftDTO> shiftDTOS = getShifts(employeeDTOs);
        return new VrpTaskPlanningDTO(solverConfigDTO,null,employeeDTOs,taskDTOS);
    }

    public List<TaskDTO> getTaskForPlanning(Long unitId){
        List<VRPTaskDTO> tasks = taskService.getAllTask(unitId);
        List<VRPTaskDTO> uniqueTaskList = tasks.stream().filter(ObjectUtils.distinctByKey(task -> task.getInstallationNumber())).collect(toList());
        List<TaskDTO> taskDTOS = new ArrayList<>(uniqueTaskList.size());
        Map<Long,Integer> intallationandDuration = tasks.stream().collect(groupingBy(VRPTaskDTO::getInstallationNumber,summingInt(VRPTaskDTO::getDuration)));
        Map<Long,Set<String>> intallationandSkill = tasks.stream().collect(groupingBy(VRPTaskDTO::getInstallationNumber,mapping(v->v.getTaskType().getTitle(),toSet())));
        Map<Long,List<VRPTaskDTO>> installationNoTasks = tasks.stream().collect(Collectors.groupingBy(t->t.getInstallationNumber(),toList()));
        uniqueTaskList.forEach(t->{
            taskDTOS.add(new TaskDTO(t.getId().toString(),t.getInstallationNumber(),new Double(t.getAddress().getLatitude()),new Double(t.getAddress().getLongitude()),intallationandSkill.get(t.getInstallationNumber()),intallationandDuration.get(t.getInstallationNumber()),t.getAddress().getStreet(),new Integer(t.getAddress().getHouseNumber()),t.getAddress().getBlock(),t.getAddress().getFloorNo(),t.getAddress().getZip(),t.getAddress().getCity()));
        });
        return taskDTOS;
    }

    private List<ShiftDTO> getShifts(List<EmployeeDTO> employeeList){
        List<ShiftDTO> shifts = new ArrayList<>();
        employeeList.forEach(e->{
            for (int i=4;i<=8;i++) {
                shifts.add(new ShiftDTO(e.getId()+i, e, LocalDate.now().plusDays(1), null, null));
            }
        });
        return shifts;
    }

    private List<EmployeeDTO> getEmployees(Long unitId){
        List<UnitStaffResponseDTO> staffResponseDTOS = staffRestClient.getUnitWiseStaffList();
        List<Long> staffIds = staffResponseDTOS.stream().filter(s->s.getUnitId().equals(unitId)).flatMap(s->s.getStaffList().stream().map(st->st.getId())).collect(toList());
        List<TaskTypeSettingDTO> taskTypeSettingDTOS = taskTypeSettingMongoRepository.findByStaffIds(staffIds);
        Map<Long,List<TaskTypeSettingDTO>> staffSettingMap = taskTypeSettingDTOS.stream().collect(Collectors.groupingBy(t->t.getStaffId(),toList()));
        List<EmployeeDTO> employees = staffSettingMap.entrySet().stream().map(s->new EmployeeDTO(s.getKey().toString(),"",getSkill(s.getValue()),s.getValue().get(0).getEfficiency())).collect(toList());
        Map<Long,String> staffNameMap = staffResponseDTOS.stream().filter(s->s.getUnitId().equals(unitId)).flatMap(s->s.getStaffList().stream()).collect(Collectors.toMap(StaffDTO::getId,v->v.getFirstName()+""+v.getLastName()));
        employees.forEach(e->e.setName(staffNameMap.get(new Long(e.getId()))));
        return employees;
    }


    private Set<String> getSkill(List<TaskTypeSettingDTO> taskTypeSettings){
        return taskTypeSettings.stream().map(t->t.getTaskTypeId().toString()).collect(Collectors.toSet());
    }

}
