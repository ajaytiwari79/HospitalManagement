package com.kairos.persistence.repository.shift;

import com.kairos.response.dto.web.shift.IndividualShiftTemplateDTO;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface CustomIndividualShiftTemplateRepository {

    List<IndividualShiftTemplateDTO> getAllIndividualShiftTemplateByIdsIn(Set<BigInteger> individualShiftTemplateIds);
}
