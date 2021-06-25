package com.kairos.persistence.repository.protected_day_off;

import com.kairos.dto.activity.unit_settings.ProtectedDaysOffSettingDTO;
import com.kairos.persistence.model.protected_day_off.ProtectedDaysOff;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ProtectedDaysOffRepository extends MongoBaseRepository<ProtectedDaysOff, BigInteger> {

    ProtectedDaysOff findByExpertiseIdAndHolidayIdAndDeletedFalse(Long expertiseId, BigInteger holidayId);

    List<ProtectedDaysOffSettingDTO> findAllByExpertiseIdAndDeletedFalse(Long expertiseId);
}
