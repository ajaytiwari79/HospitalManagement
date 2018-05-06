package com.planner.service.staff;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.kairos.response.dto.web.UnitPositionDTO;
import com.kairos.response.dto.web.UnitPositionWtaDTO;
import com.kairos.response.dto.web.wta.WTAResponseDTO;
import com.planner.domain.staff.UnitPosition;
import com.planner.domain.wta.WTABaseRuleTemplate;
import com.planner.domain.wta.templates.WorkingTimeAgreement;
import com.planner.repository.staff.UnitPositionRepository;
import com.planner.util.wta.WTABuilderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Service
@Transactional
public class UnitPositionService  {
    @Autowired
    private UnitPositionRepository unitPositionRepository;
    public void addUnitPosition(Long staffKairosId, Long unitId, UnitPositionWtaDTO unitPositionWtaDTO) {
        UnitPosition unitPosition=new UnitPosition();
        WorkingTimeAgreement wta= createWTA(unitPositionWtaDTO.getWtaResponseDTO());
        BeanUtils.copyProperties(unitPositionWtaDTO,unitPosition,"id","wtaResponseDTO");
        unitPosition.setWorkingTimeAgreement(wta);
        unitPosition.setKairosId(BigInteger.valueOf(unitPositionWtaDTO.getId()));
        unitPositionRepository.save(unitPosition);
    }

    public void updateUnitPosition(Long staffKairosId, Long unitId, Long unitPositionKairosId, UnitPositionWtaDTO unitPositionWtaDTO) {
        UnitPosition unitPosition=unitPositionRepository.findByKairosId(BigInteger.valueOf(unitPositionKairosId)).get();
        WorkingTimeAgreement wta= createWTA(unitPositionWtaDTO.getWtaResponseDTO());
        BeanUtils.copyProperties(unitPositionWtaDTO,unitPosition,"id","wtaResponseDTO");
        unitPosition.setWorkingTimeAgreement(wta);
        unitPosition.setKairosId(BigInteger.valueOf(unitPositionWtaDTO.getId()));
        unitPositionRepository.save(unitPosition);
    }

    private WorkingTimeAgreement createWTA(WTAResponseDTO wtaResponseDTO){
        List<WTABaseRuleTemplate> templates=WTABuilderService.copyRuleTemplates(wtaResponseDTO.getRuleTemplates());
        WorkingTimeAgreement wta= new WorkingTimeAgreement(wtaResponseDTO.getName(),wtaResponseDTO.getDescription(),wtaResponseDTO.getStartDate(),wtaResponseDTO.getEndDate(),templates,wtaResponseDTO.getId());
        return wta;
    }

    public void removePosition(Long unitPositionId, Long unitId) {
        UnitPosition unitPosition=unitPositionRepository.findByKairosId(BigInteger.valueOf(unitPositionId)).get();
        unitPosition.setDeleted(true);
        unitPositionRepository.save(unitPosition);
    }

    public void updateWTA(Long unitPositionId, Long unitId, WTAResponseDTO wtaResponseDTO) {
        UnitPosition unitPosition=unitPositionRepository.findByKairosId(BigInteger.valueOf(unitPositionId)).get();
        unitPosition.setWorkingTimeAgreement(createWTA(wtaResponseDTO));
        unitPositionRepository.save(unitPosition);
    }
}
