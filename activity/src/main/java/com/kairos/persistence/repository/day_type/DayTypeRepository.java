package com.kairos.persistence.repository.day_type;

import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.persistence.model.day_type.DayType;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface DayTypeRepository extends MongoBaseRepository<DayType, BigInteger> {

    boolean existsByCountryIdAndNameOrColorCodeIgnoreCaseAndIdNotIn(Long countryId,  String name, int colorCode, BigInteger id);

    List<DayTypeDTO> findAllByCountryIdAndDeletedFalse(Long countryId);

    List<DayTypeDTO> findByValidDaysContains(List<String> validDays);

    DayTypeDTO getById(BigInteger id);

    List<DayTypeDTO> findAllByIdInAndDeletedFalse(List<BigInteger> ids);
}
