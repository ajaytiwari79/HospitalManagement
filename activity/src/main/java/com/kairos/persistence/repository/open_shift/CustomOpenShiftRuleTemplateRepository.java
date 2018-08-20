package com.kairos.persistence.repository.open_shift;

import com.kairos.persistence.model.open_shift.OpenShiftRuleTemplateDTO;
import java.util.List;


public interface CustomOpenShiftRuleTemplateRepository {


    List<OpenShiftRuleTemplateDTO> findOpenShiftRuleTemplatesWithInterval(Long unitId);
}
