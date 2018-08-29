package com.kairos.persistence.repository.shift;

import com.kairos.activity.shift.ActivityAndShiftStatusSettingsDTO;
import com.kairos.persistence.model.shift.ActivityAndShiftStatusSettings;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/*
 *Created By Pavan on 29/8/18
 *
 */
@Repository
public interface ActivityAndShiftStatusSettingsRepository extends MongoBaseRepository<ActivityAndShiftStatusSettings,BigInteger> ,CustomActivityAndShiftStatusSettingsRepository {

    List<ActivityAndShiftStatusSettingsDTO> findAllByCountryIdAndDeletedFalse(Long countryId);
}
