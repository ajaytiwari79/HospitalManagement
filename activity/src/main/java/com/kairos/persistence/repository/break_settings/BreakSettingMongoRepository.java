package com.kairos.persistence.repository.break_settings;

import com.kairos.persistence.model.break_settings.BreakSettings;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.activity.break_settings.BreakSettingsDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface BreakSettingMongoRepository extends MongoBaseRepository<BreakSettings, BigInteger> {

    List<BreakSettingsDTO> findAllByDeletedFalseAndUnitIdOrderByCreatedAtAsc(Long unitId);

    BreakSettings findByIdAndDeletedFalseAndUnitId(BigInteger id, Long unitId);

    BreakSettings findByDeletedFalseAndUnitIdAndShiftDurationInMinuteEquals(Long unitId, Long shiftDurationInMinute);

    List<BreakSettings> findAllByDeletedFalseAndUnitIdAndShiftDurationInMinuteLessThanEqualOrderByCreatedAtAsc(Long unitId, Long shiftDurationInMinute);

    List<BreakSettings> findAllByUnitIdAndDeletedFalseOrderByCreatedAtAsc(Long unitId);

}
