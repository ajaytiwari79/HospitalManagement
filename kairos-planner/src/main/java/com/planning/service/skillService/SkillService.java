package com.planning.service.skillService;

import com.planning.domain.skill.PlanningSkill;
import com.planning.domain.skill.SkillWithLevel;
import com.planning.enums.SkillLevel;
import com.planning.repository.skillRepository.SkillRepository;
import com.planning.responseDto.skillDto.OptaSkillDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SkillService {

    private static Logger logger = LoggerFactory.getLogger(SkillService.class);

    @Autowired
    private SkillRepository skillRepository;


    public boolean exits(Long externalId) {
        if (externalId == null) return false;
        return skillRepository.findOneByExternalId(externalId)!=null;
    }

    public List<PlanningSkill> getAll() {
        return skillRepository.getAll(PlanningSkill.class);
    }


    public boolean exitsByIds(List<Long> skillIds) {
        for (Long id : skillIds) {
            if (id == null) return false;
            else if (!exits(id)) return false;
        }
        return true;
    }

    public List<SkillWithLevel> getAllSKillWithLevelByUnitId(Long unitId){
        return skillRepository.getAllSKillWithLevelByUnitId(unitId);
    }

    public List<String> saveSkillWithLevel(List<OptaSkillDTO> optaSkillDTOS) {
        List<String> optaTaskTypeSkillIds = new ArrayList<>();
        for (OptaSkillDTO optaSkillDTO : optaSkillDTOS) {
            PlanningSkill planningSkill = skillRepository.findOneByExternalId(optaSkillDTO.getKairosId());
            SkillWithLevel skillWithLevel = skillRepository.getOneSkillWithLevel(planningSkill.getId(), SkillLevel.valueOf(optaSkillDTO.getSkillLevel()).name(), optaSkillDTO.getUnitId());
            if (skillWithLevel == null) {
                skillWithLevel = new SkillWithLevel();
                skillWithLevel.setUnitId(optaSkillDTO.getUnitId());
                skillWithLevel.setSkillId(optaSkillDTO.getOptaPlannerId());
                skillWithLevel.setSkillLevel(SkillLevel.valueOf(optaSkillDTO.getSkillLevel()));
                skillWithLevel = skillRepository.save(skillWithLevel);
                optaTaskTypeSkillIds.add(skillWithLevel.getId());
            } else optaTaskTypeSkillIds.add(skillWithLevel.getId());
        }
        return optaTaskTypeSkillIds;
    }

    public void saveList(List<PlanningSkill> planningSkills) {
        skillRepository.saveList(planningSkills);
    }

    public void save(PlanningSkill planningSkill) {
        skillRepository.save(planningSkill);
    }

    public PlanningSkill findOne(String id) {
        return (PlanningSkill) skillRepository.findById(id, PlanningSkill.class);
    }

    public List<PlanningSkill> findByIds(List<String> ids) {
        return (List<PlanningSkill>) skillRepository.findByIds(ids, PlanningSkill.class);
    }

    public void deleteList(List<PlanningSkill> planningSkills) {
        skillRepository.deleteList(planningSkills);
    }


    public List<OptaSkillDTO> saveSkills(List<OptaSkillDTO> optaSkillDTOS) {
        List<OptaSkillDTO> updatedOptaSkillDtos = new ArrayList<>();
        for (OptaSkillDTO optaSkillDto : optaSkillDTOS) {
            PlanningSkill planningSkill = null;
            if (optaSkillDto.getKairosId() != null) planningSkill = skillRepository.findOneByExternalId(optaSkillDto.getKairosId());
            if(planningSkill==null){
                planningSkill = new PlanningSkill();
            }
            planningSkill.setName(optaSkillDto.getName());
            planningSkill.setExternalId(optaSkillDto.getKairosId());
            planningSkill = (PlanningSkill) skillRepository.save(planningSkill);
            optaSkillDto.setOptaPlannerId(planningSkill.getId());
            updatedOptaSkillDtos.add(optaSkillDto);
        }
        return updatedOptaSkillDtos;
    }

    public OptaSkillDTO saveSkill(OptaSkillDTO optaSkillDto) {
        List<OptaSkillDTO> updatedOptaSkillDtos = new ArrayList<>();
        PlanningSkill planningSkill = null;
        if (optaSkillDto.getKairosId() != null) {
            planningSkill = skillRepository.findOneByExternalId(optaSkillDto.getKairosId());
        }
        planningSkill = new PlanningSkill();
        planningSkill.setName(optaSkillDto.getName());
        planningSkill.setExternalId(optaSkillDto.getKairosId());
        planningSkill = (PlanningSkill) skillRepository.save(planningSkill);
        optaSkillDto.setOptaPlannerId(planningSkill.getId());
        return optaSkillDto;
    }

    public List<PlanningSkill> getAllByUnitId(long unitId) {
        return skillRepository.getAllByUnitId(unitId);
    }

    public boolean deleteByObject(OptaSkillDTO optaSkillDTO) {
        PlanningSkill planningSkill = skillRepository.findOneByExternalId(optaSkillDTO.getKairosId());
        if(planningSkill!=null){
            skillRepository.deleteById(planningSkill.getId(),PlanningSkill.class);
        }
        return true;
    }

    public OptaSkillDTO updateSkill(OptaSkillDTO optaSkillDTO) {
        PlanningSkill planningSkill1 = skillRepository.findOneByExternalId(optaSkillDTO.getKairosId());
        if(planningSkill1!=null) {
            planningSkill1.setName(optaSkillDTO.getName());
            planningSkill1 = (PlanningSkill) skillRepository.save(planningSkill1);
            optaSkillDTO.setOptaPlannerId(planningSkill1.getId());
        }
        return optaSkillDTO;
    }

    public List<OptaSkillDTO> updateList(List<OptaSkillDTO> optaSkillDTOS) {
        List<OptaSkillDTO> updatedOptaSkillDtos = new ArrayList<>();
        for (OptaSkillDTO optaSkillDTO : optaSkillDTOS) {
            PlanningSkill planningSkill1 = skillRepository.findOneByExternalId(optaSkillDTO.getKairosId());
            if(planningSkill1!=null) {
                planningSkill1.setName(optaSkillDTO.getName());
                planningSkill1 = (PlanningSkill) skillRepository.save(planningSkill1);
                optaSkillDTO.setOptaPlannerId(planningSkill1.getId());
            }
            updatedOptaSkillDtos.add(optaSkillDTO);
        }
        return updatedOptaSkillDtos;
    }

}
