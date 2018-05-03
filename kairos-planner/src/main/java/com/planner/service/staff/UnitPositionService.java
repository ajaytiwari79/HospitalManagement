package com.planner.service.staff;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.kairos.response.dto.web.UnitPositionDTO;
import com.kairos.response.dto.web.UnitPositionWtaDTO;
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
        WorkingTimeAgreement wta= createWTA(unitPositionWtaDTO);
        BeanUtils.copyProperties(unitPositionWtaDTO,unitPosition,"id","wtaResponseDTO");
        unitPosition.setWorkingTimeAgreement(wta);
        unitPosition.setKairosId(BigInteger.valueOf(unitPositionWtaDTO.getId()));
        unitPositionRepository.save(unitPosition);
    }

    public void updateUnitPosition(Long staffKairosId, Long unitId, Long unitPositionKairosId, UnitPositionWtaDTO unitPositionWtaDTO) {
        UnitPosition unitPosition=unitPositionRepository.findByKairosId(BigInteger.valueOf(unitPositionKairosId)).get();
        WorkingTimeAgreement wta= createWTA(unitPositionWtaDTO);
        BeanUtils.copyProperties(unitPositionWtaDTO,unitPosition,"id","wtaResponseDTO");
        unitPosition.setWorkingTimeAgreement(wta);
        unitPosition.setKairosId(BigInteger.valueOf(unitPositionWtaDTO.getId()));
        unitPositionRepository.save(unitPosition);
    }

    private WorkingTimeAgreement createWTA(UnitPositionWtaDTO unitPositionWtaDTO){
        List<WTABaseRuleTemplate> templates=WTABuilderService.copyRuleTemplates(unitPositionWtaDTO.getWtaResponseDTO().getRuleTemplates());
        WorkingTimeAgreement wta= new WorkingTimeAgreement(unitPositionWtaDTO.getWtaResponseDTO().getName(),unitPositionWtaDTO.getWtaResponseDTO().getDescription(),unitPositionWtaDTO.getWtaResponseDTO().getStartDate(),unitPositionWtaDTO.getWtaResponseDTO().getEndDate(),templates,unitPositionWtaDTO.getWtaResponseDTO().getId());
        return wta;
    }
}
