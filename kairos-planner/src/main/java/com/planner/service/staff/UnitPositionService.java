package com.planner.service.staff;

import com.kairos.response.dto.web.UnitPositionDTO;
import com.kairos.response.dto.web.UnitPositionWtaDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UnitPositionService  {
    public void addUnitPosition(Long staffKairosId, Long unitId, UnitPositionWtaDTO unitPositionWtaDTO) {
    }

    public void updateUnitPosition(Long staffKairosId, Long unitId, Long unitPositionKairosId, UnitPositionWtaDTO unitPositionWtaDTO) {
    }
}
