package com.planner.service.taskService;

import com.planner.domain.task.PlanningTaskType;
import com.planner.repository.taskRepository.TaskTypeRepository;
import com.planner.responseDto.skillDto.OptaSkillDTO;
import com.planner.responseDto.taskDto.OptaTaskTypeDTO;
import com.planner.service.skillService.SkillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskTypeService {

    private static Logger logger = LoggerFactory.getLogger(PlanningTaskService.class);

    @Autowired
    private TaskTypeRepository taskTypeRepository;
    @Autowired
    private SkillService skillService;

    public void saveList(List<PlanningTaskType> planningTaskTypeList) {
        taskTypeRepository.saveList(planningTaskTypeList);
    }

    public void saveTaskType(PlanningTaskType planningTaskType) {
        taskTypeRepository.save(planningTaskType);
    }

    public PlanningTaskType findOne(String id) {
        return (PlanningTaskType) taskTypeRepository.findById(id, PlanningTaskType.class);
    }

    public List<PlanningTaskType> findAll(List<PlanningTaskType> planningTaskTypeList) {
        List<String> ids = new ArrayList<>();
        for (PlanningTaskType planningTaskType : planningTaskTypeList) {
            ids.add(planningTaskType.getId());
        }
        return (List<PlanningTaskType>) taskTypeRepository.findByIds(ids, PlanningTaskType.class);
    }

    public List<OptaTaskTypeDTO> saveTaskTypes(List<OptaTaskTypeDTO> optaTaskTypeDTOS) {
        List<OptaTaskTypeDTO> updatedOptaTaskTypeDtos = new ArrayList<>();
        for (OptaTaskTypeDTO optaTaskTypeDto : optaTaskTypeDTOS) {
            if (optaTaskTypeDto.getOptaSkills() == null || skillService.exitsByIds(getExternalIds(optaTaskTypeDto.getOptaSkills()))) {
                PlanningTaskType planningTaskType = null;
                if (optaTaskTypeDto.getKairosId() != null)
                    planningTaskType = taskTypeRepository.findByExternalId(optaTaskTypeDto.getKairosId(),optaTaskTypeDto.getUnitId(),PlanningTaskType.class);
                if(planningTaskType==null){
                    planningTaskType = new PlanningTaskType();
                }
                if (optaTaskTypeDto.getOptaSkills() != null) {
                    List<String> skillWithLevelIds = skillService.saveSkillWithLevel(optaTaskTypeDto.getOptaSkills());
                    planningTaskType.setSkillWithIds(skillWithLevelIds);
                }
                planningTaskType.setExternalId(optaTaskTypeDto.getKairosId());
                planningTaskType.setForbiddenAllow(optaTaskTypeDto.isForbiddenAllow());
                planningTaskType.setTitle(optaTaskTypeDto.getTitle());
                planningTaskType.setUnitId(optaTaskTypeDto.getUnitId());
                planningTaskType = (PlanningTaskType) taskTypeRepository.save(planningTaskType);
                optaTaskTypeDto.setOptaPlannerId(planningTaskType.getId());
                updatedOptaTaskTypeDtos.add(optaTaskTypeDto);
            }else {
                logger.warn("task Type's skills doesn't exists taskTypeId "+optaTaskTypeDto.getKairosId());
            }
        }
        return updatedOptaTaskTypeDtos;
    }

    public OptaTaskTypeDTO saveTaskType(OptaTaskTypeDTO optaTaskTypeDTO) {
        if (optaTaskTypeDTO.getOptaSkills() == null || skillService.exitsByIds(getExternalIds(optaTaskTypeDTO.getOptaSkills()))) {

            PlanningTaskType planningTaskType = new PlanningTaskType();
            if (optaTaskTypeDTO.getKairosId() != null)
                planningTaskType = taskTypeRepository.findByExternalId(optaTaskTypeDTO.getKairosId(),optaTaskTypeDTO.getUnitId(),PlanningTaskType.class);
            if (optaTaskTypeDTO.getOptaSkills() != null) {
                List<String> skillWithLevelIds = skillService.saveSkillWithLevel(optaTaskTypeDTO.getOptaSkills());
                planningTaskType.setSkillWithIds(skillWithLevelIds);
            }
            planningTaskType.setExternalId(optaTaskTypeDTO.getKairosId());
            planningTaskType.setForbiddenAllow(optaTaskTypeDTO.isForbiddenAllow());
            planningTaskType.setTitle(optaTaskTypeDTO.getTitle());
            planningTaskType.setUnitId(optaTaskTypeDTO.getUnitId());
            planningTaskType = (PlanningTaskType) taskTypeRepository.save(planningTaskType);
            optaTaskTypeDTO.setOptaPlannerId(planningTaskType.getId());
        }
        return optaTaskTypeDTO;
    }

    private List<Long> getExternalIds(List<OptaSkillDTO> optaSkillDTOS) {
        List<Long> optaSkillids = new ArrayList<>();
        for (OptaSkillDTO optaSkillDTO : optaSkillDTOS) {
            optaSkillids.add(optaSkillDTO.getKairosId());
        }
        return optaSkillids;
    }

    public OptaTaskTypeDTO updateTaskType(OptaTaskTypeDTO optaTaskTypeDTO) {
        if (optaTaskTypeDTO.getOptaSkills() == null || skillService.exitsByIds(getExternalIds(optaTaskTypeDTO.getOptaSkills()))) {
            PlanningTaskType planningTaskType = taskTypeRepository.findByExternalId(optaTaskTypeDTO.getKairosId(),optaTaskTypeDTO.getUnitId(),PlanningTaskType.class);
            if (planningTaskType == null) planningTaskType = new PlanningTaskType();
            if (optaTaskTypeDTO.getOptaSkills() != null) {
                List<String> taskTypeSkillLevelIds = skillService.saveSkillWithLevel(optaTaskTypeDTO.getOptaSkills());
                planningTaskType.setSkillWithIds(taskTypeSkillLevelIds);
            }
            planningTaskType.setExternalId(optaTaskTypeDTO.getKairosId());
            planningTaskType.setForbiddenAllow(optaTaskTypeDTO.isForbiddenAllow());
            planningTaskType.setTitle(optaTaskTypeDTO.getTitle());
            planningTaskType.setUnitId(optaTaskTypeDTO.getUnitId());
            planningTaskType = (PlanningTaskType) taskTypeRepository.save(planningTaskType);
            optaTaskTypeDTO.setOptaPlannerId(planningTaskType.getId());
        }else {
            logger.warn("task Type's skills doesn't exists taskTypeId "+optaTaskTypeDTO.getKairosId());
        }
        return optaTaskTypeDTO;
    }

    public List<OptaTaskTypeDTO> updateList(List<OptaTaskTypeDTO> optaTaskTypeDTOS) {
        List<OptaTaskTypeDTO> updatedOptaTaskTypeDtos = new ArrayList<>();
        for (OptaTaskTypeDTO optaTaskTypeDTO : optaTaskTypeDTOS) {
            updatedOptaTaskTypeDtos.add(updateTaskType(optaTaskTypeDTO));
        }
        return updatedOptaTaskTypeDtos;
    }

    public PlanningTaskType findByExternalId(Long externalId,Long unitId){
        return taskTypeRepository.findByExternalId(externalId,unitId,PlanningTaskType.class);
    }

    public List<PlanningTaskType> getAllByUnitId(long unitId) {
        return taskTypeRepository.getAllByUnitId(unitId,PlanningTaskType.class);
    }

    public boolean exits(Long id, Long unitId) {
        if (id!=null || unitId==null) return false;
        return taskTypeRepository.exist(id, unitId);
    }

    public boolean deleteByObject(OptaTaskTypeDTO optaTaskTypeDTO) {
        return taskTypeRepository.findByExternalId(optaTaskTypeDTO.getKairosId(),optaTaskTypeDTO.getUnitId(),PlanningTaskType.class);
    }

    public void deleteList(List<PlanningTaskType> planningTaskTypes) {
        taskTypeRepository.deleteList(planningTaskTypes);
    }

    public void delete(PlanningTaskType planningTaskType) {
        taskTypeRepository.deleteByObject(planningTaskType);
    }
}
