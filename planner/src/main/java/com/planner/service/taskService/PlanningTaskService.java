package com.planner.service.taskService;

import com.planner.domain.citizen.PlanningCitizen;
import com.planner.domain.location.PlanningLocation;
import com.planner.domain.task.PlanningTask;
import com.planner.domain.task.PlanningTaskType;
import com.planner.enums.TaskStatus;
import com.planner.repository.citizenRepository.CitizenRepository;
import com.planner.repository.taskRepository.PlanningTaskRepository;
import com.planner.responseDto.taskDto.OptaTaskDTO;
import com.planner.service.locationService.LocationService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlanningTaskService {

    private static Logger logger = LoggerFactory.getLogger(PlanningTaskService.class);

    @Autowired
    private PlanningTaskRepository planningTaskRepository;
    @Autowired
    private TaskTypeService taskTypeService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private CitizenRepository citizenRepository;


    public List<OptaTaskDTO> saveTasks(List<OptaTaskDTO> optaTaskDTOList) {
        Map<Long, OptaTaskDTO> taskDtoMap = getTaskDTOMap(optaTaskDTOList);
        Map<Long, String> taskTypeIdsMap = getTaskTypeIdsMap(optaTaskDTOList.get(0).getUnitId());
        Map<Long, String> citizenIdsMap = getCitizenIdsMap(optaTaskDTOList.get(0).getUnitId());
        Map<String, String> locationIdsMap = getLocationsMap();
        Map<Long, String> relatedTaskIdsMap = saveRelatedTasks(taskDtoMap, optaTaskDTOList);
        boolean isNewPlanningLocation = false;
        for (OptaTaskDTO optaTaskDto : optaTaskDTOList) {
            String planningTaskTypeId = taskTypeIdsMap.get(optaTaskDto.getTaskTypeId());
            String planningCitizenId = citizenIdsMap.get(optaTaskDto.getCitizenId());
            if (!StringUtils.isEmpty(planningTaskTypeId) && !StringUtils.isEmpty(planningCitizenId)) {
                PlanningTask planningTask = null;
                if (optaTaskDto.getKairosId() != null) {
                    planningTask = planningTaskRepository.findByExternalId(optaTaskDto.getKairosId(), optaTaskDto.getUnitId(), PlanningTask.class);
                }
                if (planningTask == null) {
                    planningTask = new PlanningTask();
                }
                planningTask.setExternalId(optaTaskDto.getKairosId());
                planningTask.setStatus(TaskStatus.ABORTED);
                planningTask.setCitizenId(planningCitizenId);
                planningTask.setDurationInMin(optaTaskDto.getDuration());
                planningTask.setPriority(optaTaskDto.getPriority());
                planningTask.setTaskName(optaTaskDto.getName());
                planningTask.setUnitId(optaTaskDto.getUnitId());
                planningTask.setStatus(TaskStatus.valueOf(optaTaskDto.getTaskStatus()));
                planningTask.setTasktypeId(planningTaskTypeId);
                planningTask.setFirstEndDateTime(optaTaskDto.getTimeTo());
                planningTask.setFirstStartDateTime(optaTaskDto.getTimeFrom());
                planningTask.setSecondStartDateTime(optaTaskDto.getStartDate());
                planningTask.setSecondEndDateTime(optaTaskDto.getEndDate());
                if (optaTaskDto.getSlaStartDuration() != null) {
                    planningTask.setFirstStartSlaDurationMin(optaTaskDto.getSlaStartDuration());
                }
                planningTask.setFirstEndSlaDurationMin(optaTaskDto.getSlaEndDuration());
                planningTask.setSecondStartSlaDurationMin(optaTaskDto.getSlaStartDuration());
                planningTask.setSecondEndSlaDurationMin(optaTaskDto.getSlaEndDuration());
                planningTask.setRelatedTaskid(relatedTaskIdsMap.get(optaTaskDto.getRelatedTaskid()));
                String planningLocationId = locationIdsMap.get(optaTaskDto.getAddress().getLatitude().toString()+"-"+optaTaskDto.getAddress().getLongitude().toString());
                if (!StringUtils.isEmpty(planningLocationId)) {
                    planningTask.setLocationId(planningLocationId);
                } else {
                    PlanningLocation planningLocation = locationService.saveLocation(optaTaskDto.getAddress());
                    isNewPlanningLocation = true;
                   // planningTask.setLocationId(planningLocation.getId());
                    locationIdsMap.put(Double.toString(planningLocation.getLatitude())+"-"+Double.toString(planningLocation.getLongitude()),null);
                }
                planningTask = (PlanningTask) planningTaskRepository.save(planningTask);
                optaTaskDto.setOptaPlannerId(planningTask.getId());
            }else {
                logger.warn("task's taskType or citizen doesn't exist, taskTypeId "+optaTaskDto.getTaskTypeId()+" citizen id "+optaTaskDto.getCitizenId());
            }
        }
        if (isNewPlanningLocation) {
            //locationService.saveLocationDistances();
        }
        return optaTaskDTOList;
    }


    private Map<Long, String> getTaskTypeIdsMap(Long unitId){
        List<PlanningTaskType> planningTaskTypes = taskTypeService.getAllByUnitId(unitId);
        Map<Long, String> taskTypeIdsMap = new HashMap<>(planningTaskTypes.size());
        for (PlanningTaskType planningTaskType:planningTaskTypes) {
            taskTypeIdsMap.put(planningTaskType.getExternalId(),planningTaskType.getId());
        }
        return taskTypeIdsMap;
    }

    private Map<Long, String> getCitizenIdsMap(Long unitId){
        List<PlanningCitizen> planningCitizens = citizenRepository.getAllByUnitId(unitId,PlanningCitizen.class);
        Map<Long, String> citizenIdsMap = new HashMap<>(planningCitizens.size());
        for (PlanningCitizen planningCitizen:planningCitizens) {
            citizenIdsMap.put(planningCitizen.getExternalId(),planningCitizen.getId());
        }
        return citizenIdsMap;
    }

    private Map<String, String> getLocationsMap(){
        List<PlanningLocation> planningLocations = locationService.getAllPlanningLocations();
        Map<String, String> locationIdsMap = new HashMap<>(planningLocations.size());
        for (PlanningLocation planningLocation:planningLocations) {
           // locationIdsMap.put(Double.toString(planningLocation.getLatitude())+"-"+Double.toString(planningLocation.getLongitude()),planningLocation.getId());
        }
        return locationIdsMap;
    }



    private Map<Long, String> saveRelatedTasks(Map<Long, OptaTaskDTO> taskDTOMap, List<OptaTaskDTO> optaTaskDTOS) {
        Map<Long, String> relatedTaskIdsMap = new HashMap<>();
        for (OptaTaskDTO optaTaskDTO : optaTaskDTOS) {
            if (optaTaskDTO.getRelatedTaskid() != null) {
                OptaTaskDTO relatedOptaTaskDTO = taskDTOMap.get(optaTaskDTO.getRelatedTaskid());
                relatedOptaTaskDTO = saveRelatedTask(relatedOptaTaskDTO);
                relatedTaskIdsMap.put(relatedOptaTaskDTO.getKairosId(), relatedOptaTaskDTO.getOptaPlannerId());
            }
        }
        return relatedTaskIdsMap;
    }

    private Map<Long, OptaTaskDTO> getTaskDTOMap(List<OptaTaskDTO> optaTaskDTOS) {
        Map<Long, OptaTaskDTO> relatedTaskId = new HashMap<>();
        for (OptaTaskDTO optaTaskDTO : optaTaskDTOS) {
            if (optaTaskDTO.getMultiStaffTask()!=null && optaTaskDTO.getMultiStaffTask()) {
                relatedTaskId.put(optaTaskDTO.getKairosId(), optaTaskDTO);
            }
        }
        return relatedTaskId;
    }

    private OptaTaskDTO saveRelatedTask(OptaTaskDTO optaTaskDto) {
        PlanningTaskType planningTaskType = taskTypeService.findByExternalId(optaTaskDto.getTaskTypeId(), optaTaskDto.getUnitId());
        PlanningCitizen planningCitizen = citizenRepository.findByExternalId(optaTaskDto.getCitizenId(), optaTaskDto.getUnitId(), PlanningCitizen.class);
        if (planningTaskType != null && planningCitizen != null) {
            PlanningTask planningTask = null;
            if (optaTaskDto.getKairosId() != null && optaTaskDto.getUnitId()!=null) {
                planningTask = planningTaskRepository.findByExternalId(optaTaskDto.getKairosId(), optaTaskDto.getUnitId(), PlanningTask.class);
            }
            if (planningTask == null) {
                planningTask = new PlanningTask();
            }
            planningTask.setExternalId(optaTaskDto.getKairosId());
            planningTask.setStatus(TaskStatus.ABORTED);
            planningTask.setCitizenId(planningCitizen.getId());
            planningTask.setDurationInMin(optaTaskDto.getDuration());
            planningTask.setPriority(optaTaskDto.getPriority());
            planningTask.setTaskName(optaTaskDto.getName());
            planningTask.setUnitId(optaTaskDto.getUnitId());
            planningTask.setMultiStaffTask(optaTaskDto.getMultiStaffTask());
            planningTask.setStatus(TaskStatus.valueOf(optaTaskDto.getTaskStatus()));
            planningTask.setTasktypeId(planningTaskType.getId());
            planningTask.setFirstEndDateTime(optaTaskDto.getEndDate());
            planningTask.setFirstStartDateTime(optaTaskDto.getStartDate());
            planningTask.setSecondStartDateTime(optaTaskDto.getStartDate());
            planningTask.setSecondEndDateTime(optaTaskDto.getEndDate());
            if (optaTaskDto.getSlaStartDuration() != null) {
                planningTask.setFirstStartSlaDurationMin(optaTaskDto.getSlaStartDuration());
            }
            planningTask.setFirstEndSlaDurationMin(optaTaskDto.getSlaEndDuration());
            planningTask.setSecondStartSlaDurationMin(optaTaskDto.getSlaStartDuration());
            planningTask.setSecondEndSlaDurationMin(optaTaskDto.getSlaEndDuration());
            PlanningLocation planningLocation = locationService.getLocationByLatLong(optaTaskDto.getAddress().getLatitude(), optaTaskDto.getAddress().getLongitude());
            if (planningLocation != null) {
               // planningTask.setLocationId(planningLocation.getId());
            } else {
                planningLocation = locationService.saveLocation(optaTaskDto.getAddress());
                //planningTask.setLocationId(planningLocation.getId());
            }
            planningTask = (PlanningTask) planningTaskRepository.save(planningTask);
            optaTaskDto.setOptaPlannerId(planningTask.getId());
        }else {
            logger.warn("task's taskType or citizen doesn't exist, taskTypeId "+optaTaskDto.getTaskTypeId()+" citizen id "+optaTaskDto.getCitizenId());
        }
        return optaTaskDto;
    }

    public OptaTaskDTO saveTask(OptaTaskDTO optaTaskDto) {
        boolean isNewPlanningLocation = false;
        if (taskTypeService.exits(optaTaskDto.getKairosId(), optaTaskDto.getUnitId())) {
            PlanningTask planningTask = null;
            if (optaTaskDto.getKairosId() != null) {
                planningTask = planningTaskRepository.findByExternalId(optaTaskDto.getKairosId(), optaTaskDto.getUnitId(), PlanningTask.class);
            }
            if (planningTask == null) {
                planningTask = new PlanningTask();
            }
            planningTask.setExternalId(optaTaskDto.getKairosId());
            planningTask.setStatus(TaskStatus.ABORTED);
            PlanningCitizen planningCitizen = citizenRepository.findByExternalId(optaTaskDto.getCitizenId(), optaTaskDto.getUnitId(), PlanningCitizen.class);
            planningTask.setCitizenId(planningCitizen.getId());
            planningTask.setDurationInMin(optaTaskDto.getDuration());
            planningTask.setPriority(optaTaskDto.getPriority());
            planningTask.setTaskName(optaTaskDto.getName());
            planningTask.setUnitId(optaTaskDto.getUnitId());
            planningTask.setStatus(TaskStatus.valueOf(optaTaskDto.getTaskStatus()));
            PlanningTaskType planningTaskType = taskTypeService.findByExternalId(optaTaskDto.getTaskTypeId(), optaTaskDto.getUnitId());
            planningTask.setTasktypeId(planningTaskType.getId());
            planningTask.setFirstEndDateTime(optaTaskDto.getEndDate());
            planningTask.setFirstStartDateTime(optaTaskDto.getStartDate());
            planningTask.setSecondStartDateTime(optaTaskDto.getStartDate());
            planningTask.setSecondEndDateTime(optaTaskDto.getEndDate());
            if (optaTaskDto.getSlaStartDuration() != null) {
                planningTask.setFirstStartSlaDurationMin(optaTaskDto.getSlaStartDuration());
            }
            planningTask.setFirstEndSlaDurationMin(optaTaskDto.getSlaEndDuration());
            planningTask.setSecondStartSlaDurationMin(optaTaskDto.getSlaStartDuration());
            planningTask.setSecondEndSlaDurationMin(optaTaskDto.getSlaEndDuration());
            planningTask.setRelatedTaskid(optaTaskDto.getRelatedOptaTaskId());
            PlanningLocation planningLocation = locationService.getLocationByLatLong(optaTaskDto.getAddress().getLatitude(), optaTaskDto.getAddress().getLongitude());
            if (planningLocation != null) {
               // planningTask.setLocationId(planningLocation.getId());
            } else {
                planningLocation = locationService.saveLocation(optaTaskDto.getAddress());
                isNewPlanningLocation = true;
                //planningTask.setLocationId(planningLocation.getId());
            }
            planningTask = (PlanningTask) planningTaskRepository.save(planningTask);
            optaTaskDto.setOptaPlannerId(planningTask.getId());
        }
        if (isNewPlanningLocation) {
            //locationService.saveLocationDistances();
        }
        return optaTaskDto;
    }


    public OptaTaskDTO updateTask(OptaTaskDTO optaTaskDto) {
        PlanningTask planningTask = planningTaskRepository.findByExternalId(optaTaskDto.getKairosId(), optaTaskDto.getUnitId(), PlanningTask.class);
        if (planningTask != null) {
            planningTask.setDurationInMin(optaTaskDto.getDuration());
            planningTask.setPriority(optaTaskDto.getPriority());
            planningTask.setTaskName(optaTaskDto.getName());
            planningTask.setUnitId(optaTaskDto.getUnitId());
            planningTask.setTasktypeId(optaTaskDto.getTaskType().getOptaPlannerId());
            planningTask.setFirstEndDateTime(optaTaskDto.getEndDate());
            planningTask.setFirstStartDateTime(optaTaskDto.getStartDate());
            planningTask.setSecondStartDateTime(optaTaskDto.getStartDate());
            planningTask.setSecondEndDateTime(optaTaskDto.getEndDate());
            planningTask.setFirstStartSlaDurationMin(optaTaskDto.getSlaStartDuration());
            planningTask.setFirstEndSlaDurationMin(optaTaskDto.getSlaEndDuration());
            planningTask.setSecondStartSlaDurationMin(optaTaskDto.getSlaStartDuration());
            planningTask.setSecondEndSlaDurationMin(optaTaskDto.getSlaEndDuration());
            PlanningLocation planningLocation = locationService.getLocationByLatLong(optaTaskDto.getAddress().getLatitude(), optaTaskDto.getAddress().getLongitude());
            if (planningLocation != null) planningTask.setLocationId(null);
            else {
                planningLocation = locationService.saveLocation(optaTaskDto.getAddress());
                //planningTask.setLocationId(planningLocation.getId());
            }
            planningTask = (PlanningTask) planningTaskRepository.save(planningTask);
            optaTaskDto.setOptaPlannerId(planningTask.getId());
        }
        return optaTaskDto;
    }

    public List<OptaTaskDTO> updateTasks(List<OptaTaskDTO> optaTaskDTOS) {
        List<OptaTaskDTO> updatedOptaTaskDtos = new ArrayList<>();
        for (OptaTaskDTO optaTaskDto : optaTaskDTOS) {
            PlanningTask planningTask = planningTaskRepository.findByExternalId(optaTaskDto.getKairosId(), optaTaskDto.getUnitId(), PlanningTask.class);
            if (planningTask != null) {
                planningTask.setDurationInMin(optaTaskDto.getDuration());
                planningTask.setPriority(optaTaskDto.getPriority());
                planningTask.setTaskName(optaTaskDto.getName());
                planningTask.setUnitId(optaTaskDto.getUnitId());
                planningTask.setTasktypeId(optaTaskDto.getTaskType().getOptaPlannerId());
                planningTask.setFirstEndDateTime(optaTaskDto.getEndDate());
                planningTask.setFirstStartDateTime(optaTaskDto.getStartDate());
                planningTask.setSecondStartDateTime(optaTaskDto.getStartDate());
                planningTask.setSecondEndDateTime(optaTaskDto.getEndDate());
                planningTask.setFirstStartSlaDurationMin(optaTaskDto.getSlaStartDuration());
                planningTask.setFirstEndSlaDurationMin(optaTaskDto.getSlaEndDuration());
                planningTask.setSecondStartSlaDurationMin(optaTaskDto.getSlaStartDuration());
                planningTask.setSecondEndSlaDurationMin(optaTaskDto.getSlaEndDuration());
                PlanningLocation planningLocation = locationService.getLocationByLatLong(optaTaskDto.getAddress().getLatitude(), optaTaskDto.getAddress().getLongitude());
                if (planningLocation != null) planningTask.setLocationId(null);
                else {
                    planningLocation = locationService.saveLocation(optaTaskDto.getAddress());
                    //planningTask.setLocationId(planningLocation.getId());
                }
                planningTask = (PlanningTask) planningTaskRepository.save(planningTask);
                updatedOptaTaskDtos.add(optaTaskDto);
            }
        }
        return updatedOptaTaskDtos;
    }

    public boolean exits(String taskExternalId, Long unitId) {
        return planningTaskRepository.exist(taskExternalId, unitId);
    }

    public PlanningTask findOne(String id) {
        return (PlanningTask) planningTaskRepository.findById(id, PlanningTask.class);
    }

    public boolean deleteByObject(OptaTaskDTO optaTaskDTO) {
        return planningTaskRepository.deleteByExternalId(optaTaskDTO.getKairosId(), optaTaskDTO.getUnitId(), PlanningTask.class);
    }

    public List<PlanningTask> getAllTasksForPLanning(long unitId, Date startDate, Date endDate) {
        return planningTaskRepository.getAllTasksForPLanning(unitId, startDate, endDate);
    }


}
