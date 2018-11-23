package com.kairos.persistence.repository.break_settings;

import com.kairos.persistence.model.break_settings.BreakSettings;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.dto.activity.break_settings.BreakSettingsDTO;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface BreakSettingMongoRepository extends MongoBaseRepository<BreakSettings, BigInteger> {

    List<BreakSettingsDTO> findAllByDeletedFalseAndExpertiseIdOrderByCreatedAtAsc(Long unitId);

    BreakSettings findByIdAndDeletedFalse(BigInteger id);

    BreakSettings findByDeletedFalseAndCountryIdAndExpertiseIdAndShiftDurationInMinuteEquals(Long countryId, Long expertiseId, Long shiftDurationInMinute);

    List<BreakSettings> findAllByDeletedFalseAndExpertiseIdOrderByCreatedAtAsc(Long expertiseId, Long shiftDurationInMinute);


    List<BreakSettings> findAllByDeletedFalseAndExpertiseIdInOrderByCreatedAtAsc(List<Long> unitId);

}
