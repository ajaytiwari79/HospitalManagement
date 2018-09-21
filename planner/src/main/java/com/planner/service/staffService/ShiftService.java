package com.planner.service.staffService;


import com.planner.domain.staff.PlanningShift;
import com.planner.domain.staff.PlanningStaff;
import com.planner.enums.ShiftType;
import com.planner.repository.staffRepository.ShiftRepository;
import com.planner.repository.staffRepository.TaskStaffRepository;
import com.planner.responseDto.staffDto.OptaShiftDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ShiftService {

    private static Logger log = LoggerFactory.getLogger(ShiftService.class);

    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private TaskStaffService taskStaffService;
    @Autowired private TaskStaffRepository taskStaffRepository;


    public void saveList(List<PlanningShift> planningShifts) {
        //shiftRepository.saveList(planningShifts);
    }

    public void save(PlanningShift planningShift) {
        //shiftRepository.save(planningShift);
    }

    public List<OptaShiftDTO> saveShifts(List<OptaShiftDTO> optaShiftDTOS) {
        List<OptaShiftDTO> updatedOptaShiftDtos = new ArrayList<>();
        for (OptaShiftDTO optaShiftDto : optaShiftDTOS) {
            if (taskStaffService.exits(optaShiftDto.getKairosId(),optaShiftDto.getUnitId())) {
                PlanningShift planningShift = new PlanningShift();
               /* planningShift.setUnitId(optaShiftDto.getUnitId());
                planningShift.setExternalId(optaShiftDto.getKairosId());*/
                planningShift.setStaffId(getStaffId(optaShiftDto));
                planningShift.setShiftType(ShiftType.getEnumByString(optaShiftDto.getShiftType()));
                planningShift.setStartTime(optaShiftDto.getStartDateTime());
                planningShift.setEndTime(optaShiftDto.getEndDateTime());
                //planningShift = (PlanningShift) shiftRepository.save(planningShift);
                //optaShiftDto.setOptaPlannerId(planningShift.getId());
            }else{
                log.warn("shift's staff not exits"+optaShiftDto.getStaffId());
            }
            updatedOptaShiftDtos.add(optaShiftDto);
        }
        return updatedOptaShiftDtos;
    }

    private String getStaffId(OptaShiftDTO optaShiftDTO){
        PlanningStaff planningStaff = taskStaffRepository.findByExternalId(optaShiftDTO.getKairosId(),optaShiftDTO.getUnitId(),PlanningStaff.class);
        return planningStaff.getId();
    }
    public PlanningShift findOne(String id) {
        return (PlanningShift) shiftRepository.findById(id, PlanningShift.class);
    }

    public List<PlanningShift> findByIds(List<String> ids) {
        return (List<PlanningShift>) shiftRepository.findByIds(ids, PlanningShift.class);
    }

    public boolean exits(Long externalId,Long unitId) {
        return shiftRepository.exist(externalId,unitId);
    }

    public boolean deleteByObject(OptaShiftDTO optaShiftDTO) {
        return shiftRepository.deleteByExternalId(optaShiftDTO.getKairosId(),optaShiftDTO.getUnitId(),PlanningShift.class);
    }

    public void deleteList(List<PlanningShift> planningShifts) {
        shiftRepository.deleteList(planningShifts);
    }

    public List<PlanningShift> getAllByUnitId(Date startDate, Date endDate, long unitId) {
        return shiftRepository.getAllByUnitId(startDate, endDate, unitId);
    }

    public OptaShiftDTO updateShift(OptaShiftDTO optaShiftDTO) {
        PlanningShift planningShift = shiftRepository.findByExternalId(optaShiftDTO.getKairosId(),optaShiftDTO.getUnitId(),PlanningShift.class);
        if (planningShift == null){
            optaShiftDTO.setOptaPlannerId(null);
            return optaShiftDTO;
        }
        //planningShift.setUnitId(optaShiftDTO.getUnitId());
        planningShift.setStaffId(getStaffId(optaShiftDTO));
        planningShift.setShiftType(ShiftType.valueOf(optaShiftDTO.getShiftType()));
        planningShift.setStartTime(optaShiftDTO.getStartDateTime());
        planningShift.setEndTime(optaShiftDTO.getEndDateTime());
        //planningShift = (PlanningShift) shiftRepository.save(planningShift);
       // optaShiftDTO.setOptaPlannerId(planningShift.getId());
        return optaShiftDTO;
    }

    public List<OptaShiftDTO> updateList(List<OptaShiftDTO> optaShiftDTOS) {
        List<OptaShiftDTO> updatedOptaShiftDtos = new ArrayList<>();
        for (OptaShiftDTO optaShiftDTO : optaShiftDTOS) {
            PlanningShift planningShift = shiftRepository.findByExternalId(optaShiftDTO.getKairosId(),optaShiftDTO.getUnitId(),PlanningShift.class);
            if (planningShift == null){
                optaShiftDTO.setOptaPlannerId(null);
            }else{
            //planningShift.setUnitId(optaShiftDTO.getUnitId());
            planningShift.setStaffId(getStaffId(optaShiftDTO));
            planningShift.setShiftType(ShiftType.valueOf(optaShiftDTO.getShiftType()));
            planningShift.setStartTime(optaShiftDTO.getStartDateTime());
            planningShift.setEndTime(optaShiftDTO.getEndDateTime());
           // planningShift = (PlanningShift) shiftRepository.save(planningShift);
            //optaShiftDTO.setOptaPlannerId(planningShift.getId());
            }
            updatedOptaShiftDtos.add(optaShiftDTO);
        }
        return updatedOptaShiftDtos;
    }

}
