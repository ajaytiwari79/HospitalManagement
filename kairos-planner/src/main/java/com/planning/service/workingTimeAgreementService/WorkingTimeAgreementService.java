package com.planning.service.workingTimeAgreementService;

import com.planning.domain.staff.UnitStaffRelationShip;
import com.planning.domain.workTimeAgreement.WorkingTimeAgreement;
import com.planning.repository.WorkTimeAgreementRepository.WorkingTimeAgreementRepository;
import com.planning.service.staffService.StaffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkingTimeAgreementService {

    /*private static Logger logger = LoggerFactory.getLogger(WorkingTimeAgreementService.class);

    @Autowired private WorkingTimeAgreementRepository workingTimeAgreementRepository;
    @Autowired private StaffService staffService;

    public WorkingTimeAggrementDTO saveWorkingTimeAgreement(WorkingTimeAggrementDTO workingTimeAggrementDTO){
        UnitStaffRelationShip unitStaffRelationShip = staffService.getOneByUnitStaffRelation(workingTimeAggrementDTO.getUnitId(),workingTimeAggrementDTO.getStaffId());
        if(unitStaffRelationShip!=null) {
            WorkingTimeAgreement workingTimeAgreement = new WorkingTimeAgreement();
            workingTimeAgreement.setDescription(workingTimeAggrementDTO.getDescription());
            workingTimeAgreement.setName(workingTimeAggrementDTO.getName());
            workingTimeAgreement.setValue(workingTimeAggrementDTO.getValue());
            workingTimeAgreement.setExternalId(workingTimeAggrementDTO.getKairosId());
            workingTimeAgreement.setUnitId(workingTimeAggrementDTO.getUnitId());
            workingTimeAgreement = workingTimeAgreementRepository.save(workingTimeAgreement);
            unitStaffRelationShip.setWorkingTimeAgreementId(workingTimeAgreement.getId());
            workingTimeAgreementRepository.save(unitStaffRelationShip);
            workingTimeAggrementDTO.setOptaPlannerId(workingTimeAgreement.getId());
        }
        return workingTimeAggrementDTO;
    }

    public WorkingTimeAggrementDTO updateWorkingTimeAgreement(WorkingTimeAggrementDTO workingTimeAggrementDTO){
        WorkingTimeAgreement workingTimeAgreement = workingTimeAgreementRepository.findByExternalId(workingTimeAggrementDTO.getKairosId(), workingTimeAggrementDTO.getUnitId(),WorkingTimeAgreement.class);
        if(workingTimeAgreement!=null){
            workingTimeAgreement.setValue(workingTimeAggrementDTO.getValue());
            workingTimeAgreement.setName(workingTimeAggrementDTO.getName());
            workingTimeAgreement.setDescription(workingTimeAggrementDTO.getDescription());
            workingTimeAgreementRepository.save(workingTimeAgreement);
            workingTimeAggrementDTO.setOptaPlannerId(workingTimeAgreement.getId());
        }
        return workingTimeAggrementDTO;
    }

    public boolean deleteWorkingTimeAgreement(WorkingTimeAggrementDTO workingTimeAggrementDTO){
        workingTimeAgreementRepository.deleteByExternalId(workingTimeAggrementDTO.getUnitId(), workingTimeAggrementDTO.getKairosId(),WorkingTimeAgreement.class);
        return true;
    }

    public List<WorkingTimeAgreement> getWorkingTimeAgreementByUnitId(long unitId){
        return workingTimeAgreementRepository.getAllByUnitId(unitId,WorkingTimeAgreement.class);
    }
*/
}
