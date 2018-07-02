package com.kairos.persistence.repository.shift;

import com.kairos.persistence.model.shift.ShiftTemplate;
import com.kairos.response.dto.web.shift.ShiftTemplateDTO;
import com.kairos.response.dto.web.shift.ShiftTemplateWrapper;

import java.util.List;

public interface CustomShitTemplateRepository {
    List<ShiftTemplateDTO> getAllByUnitIdAndCreatedByAndDeletedFalse(Long unitId, Long createdBy);
}
