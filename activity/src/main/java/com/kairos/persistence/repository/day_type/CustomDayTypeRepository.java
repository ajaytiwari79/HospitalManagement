package com.kairos.persistence.repository.day_type;

import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

public interface CustomDayTypeRepository {

    List<DayTypeDTO> findAllByIdInAndDeletedFalse(Collection<BigInteger> ids);
    List<DayTypeDTO> findAllByCountryIdAndDeletedFalse(Long countryId);
}
