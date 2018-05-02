package com.planner.service.staff;

import com.kairos.response.dto.web.UnitPositionDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UnitPositionService  {
    public void addUnitPosition(Long staffKairosId, Long unitId, UnitPositionDTO unitPositionDTO) {
    }

    public void updateUnitPosition(Long staffKairosId, Long unitId, Long unitPositionKairosId, UnitPositionDTO unitPositionDTO) {
    }
}
