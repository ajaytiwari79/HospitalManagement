package com.planning.service.staffService;

import com.planning.domain.staff.PlanningStaff;
import com.planning.domain.staff.UnitStaffRelationShip;
import com.planning.repository.staffRepository.StaffRepository;
import com.planning.responseDto.skillDto.OptaSkillDTO;
import com.planning.responseDto.staffDto.OptaStaffDTO;
import com.planning.service.skillService.SkillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StaffService {

    private static Logger log = LoggerFactory.getLogger(StaffService.class);

    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private SkillService skillService;

    public void saveList(List<PlanningStaff> planningStaffs) {
        staffRepository.saveList(planningStaffs);
    }

    public void save(PlanningStaff planningSkill) {
        staffRepository.save(planningSkill);
    }

    public PlanningStaff findOne(String id) {
        return (PlanningStaff) staffRepository.findById(id, PlanningStaff.class);
    }

    public List<PlanningStaff> findByIds(List<String> ids) {
        return (List<PlanningStaff>) staffRepository.findByIds(ids, PlanningStaff.class);
    }

    public void deleteList(List<PlanningStaff> planningSkills) {
        staffRepository.deleteList(planningSkills);
    }


    public OptaStaffDTO updateStaff(OptaStaffDTO optaStaffDto) {
        if (optaStaffDto.getOptaSkillDTOS() == null || skillService.exitsByIds(getOptaIds(optaStaffDto.getOptaSkillDTOS()))) {
            PlanningStaff planningStaff = null;
            if(optaStaffDto.getKairosId()!=null){
                planningStaff = staffRepository.findByExternalId(optaStaffDto.getKairosId(),optaStaffDto.getUnitId(),UnitStaffRelationShip.class);
            }if(planningStaff==null){
                optaStaffDto.setOptaPlannerId(null);
                return optaStaffDto;
            }
            if (optaStaffDto.getOptaSkillDTOS() != null) {
                List<String> skillWithLevelIds = skillService.saveSkillWithLevel(optaStaffDto.getOptaSkillDTOS());
                planningStaff.setSkillIds(skillWithLevelIds);
            }
            if(optaStaffDto.getCostByPerHr()!=null){
                planningStaff.setCostPerHour(optaStaffDto.getCostByPerHr());
            }
            planningStaff.setExternalId(optaStaffDto.getKairosId());
            planningStaff.setFirstName(optaStaffDto.getFirstName());
            planningStaff.setLastName(optaStaffDto.getLastName());
            planningStaff = (PlanningStaff) staffRepository.save(planningStaff);
            optaStaffDto.setOptaPlannerId(planningStaff.getId());
        }else{
            log.warn("staff's Skills doesn't exists staffId "+optaStaffDto.getKairosId());
        }
        return optaStaffDto;
    }

    public List<PlanningStaff> getAllByUnitId(long unitId) {
        List<UnitStaffRelationShip> unitStaffRelationShips = staffRepository.getAllByUnitId(unitId,UnitStaffRelationShip.class);
        return staffRepository.getAllByIds(getStaffIds(unitStaffRelationShips));
    }

    public UnitStaffRelationShip getOneByUnitStaffRelation(Long unitId,Long staffId) {
        PlanningStaff planningStaff = staffRepository.getOneStaffByExternalId(staffId);
        UnitStaffRelationShip unitStaffRelationShips = staffRepository.getOneUnitStaffRelationship(unitId,planningStaff.getId());
        return unitStaffRelationShips;
    }

    private List<String> getStaffIds(List<UnitStaffRelationShip> unitStaffRelationShips){
        List<String> staffIds = new ArrayList<>(unitStaffRelationShips.size());
        for (UnitStaffRelationShip unitStaffRelation:unitStaffRelationShips) {
            staffIds.add(unitStaffRelation.getStaffId());
        }
        return staffIds;
    }

    public boolean exits(Long externalId, Long unitId) {
        if(externalId!=null || unitId == null) return false;
        UnitStaffRelationShip unitStaffRelationShip = staffRepository.findByExternalId(externalId,unitId,UnitStaffRelationShip.class);
        return staffRepository.findById(unitStaffRelationShip.getStaffId(),PlanningStaff.class)!=null;
    }

    public boolean deleteByObject(OptaStaffDTO optaStaffDTO) {
        UnitStaffRelationShip unitStaffRelationShip = staffRepository.findByExternalId(optaStaffDTO.getKairosId(),optaStaffDTO.getUnitId(),UnitStaffRelationShip.class);
        staffRepository.deleteById(unitStaffRelationShip.getStaffId(),PlanningStaff.class);
        staffRepository.deleteById(unitStaffRelationShip.getId(),UnitStaffRelationShip.class);
        return true;
    }

    public OptaStaffDTO saveStaff(OptaStaffDTO optaStaffDto){
        if (optaStaffDto.getOptaSkillDTOS() == null || skillService.exitsByIds(getOptaIds(optaStaffDto.getOptaSkillDTOS()))) {
            PlanningStaff planningStaff = null;
            if(optaStaffDto.getOptaPlannerId()!=null){
                UnitStaffRelationShip unitStaffRelationShip = staffRepository.findByExternalId(optaStaffDto.getKairosId(),optaStaffDto.getUnitId(),PlanningStaff.class);
                planningStaff = findOne(unitStaffRelationShip.getStaffId());
            }if(planningStaff==null){
                planningStaff = new PlanningStaff();
            }
            if (optaStaffDto.getOptaSkillDTOS() != null) {
                List<String> skillWithLevelIds = skillService.saveSkillWithLevel(optaStaffDto.getOptaSkillDTOS());
                planningStaff.setSkillIds(skillWithLevelIds);
            }
            planningStaff.setExternalId(optaStaffDto.getKairosId());
            planningStaff.setCostPerHour(optaStaffDto.getCostByPerHr());
            planningStaff.setFirstName(optaStaffDto.getFirstName());
            planningStaff.setLastName(optaStaffDto.getLastName());
            planningStaff.setUnitId(optaStaffDto.getUnitId());
            planningStaff = (PlanningStaff) staffRepository.save(planningStaff);
            optaStaffDto.setOptaPlannerId(planningStaff.getId());
        }else{
            log.warn("staff's Skills doesn't exists staffId "+optaStaffDto.getKairosId());
        }
        return optaStaffDto;
    }

    private List<Long> getOptaIds(List<OptaSkillDTO> optaSkillDTOS) {
        List<Long> optaSkillids = new ArrayList<>();
        for (OptaSkillDTO optaSkillDTO : optaSkillDTOS) {
            optaSkillids.add(optaSkillDTO.getKairosId());
        }
        return optaSkillids;
    }

    public List<OptaStaffDTO> saveStaffs(List<OptaStaffDTO> optaStaffDTOS) {
        List<OptaStaffDTO> updatesOptaStaffDto = new ArrayList<>();
        for (OptaStaffDTO optaStaffDto : optaStaffDTOS) {
            if (optaStaffDto.getOptaSkillDTOS() == null || skillService.exitsByIds(getOptaIds(optaStaffDto.getOptaSkillDTOS()))) {
                PlanningStaff planningStaff = null;
                if(optaStaffDto.getOptaPlannerId()!=null){
                    planningStaff = staffRepository.findByExternalId(optaStaffDto.getKairosId(),optaStaffDto.getUnitId(),PlanningStaff.class);
                }if(planningStaff==null){
                    planningStaff = new PlanningStaff();
                }
                if (optaStaffDto.getOptaSkillDTOS() != null) {
                    List<String> skillWithLevelIds = skillService.saveSkillWithLevel(optaStaffDto.getOptaSkillDTOS());
                    planningStaff.setSkillIds(skillWithLevelIds);
                }
                planningStaff.setExternalId(optaStaffDto.getKairosId());
                planningStaff.setCostPerHour(optaStaffDto.getCostByPerHr());
                planningStaff.setFirstName(optaStaffDto.getFirstName());
                planningStaff.setLastName(optaStaffDto.getLastName());
                //planningStaff.setUnitId(optaStaffDto.getUnitId());
                planningStaff = (PlanningStaff) staffRepository.save(planningStaff);
                UnitStaffRelationShip unitstaffRelationShip = new UnitStaffRelationShip(planningStaff.getId(),optaStaffDto.getUnitId());
                unitstaffRelationShip.setExternalId(optaStaffDto.getKairosId());
                staffRepository.save(unitstaffRelationShip);
                optaStaffDto.setOptaPlannerId(planningStaff.getId());
                updatesOptaStaffDto.add(optaStaffDto);
            }else{
                log.warn("staff's Skills doesn't exists staffId "+optaStaffDto.getKairosId());
            }
        }
        return updatesOptaStaffDto;
    }

    /*public List<OptaStaffDTO> updateList(List<OptaStaffDTO> optaStaffDTOS) {
        List<OptaStaffDTO> updatedOptaStaffDTOS = new ArrayList<>();
        for (OptaStaffDTO optaStaffDTO : optaStaffDTOS) {
            PlanningStaff planningStaff = staffRepository.findByExternalId(optaStaffDTO.getKairosId(),optaStaffDTO.getUnitId(),PlanningStaff.class);
            if (planningStaff == null) planningStaff = new PlanningStaff();
            planningStaff.setFirstName(optaStaffDTO.getFirstName());
            planningStaff.setLastName(optaStaffDTO.getLastName());
            planningStaff.setUnitId(optaStaffDTO.getUnitId());
            planningStaff = (PlanningStaff) staffRepository.save(planningStaff);
            optaStaffDTO.setOptaPlannerId(planningStaff.getId());
            updatedOptaStaffDTOS.add(optaStaffDTO);
        }
        return updatedOptaStaffDTOS;
    }*/

}
