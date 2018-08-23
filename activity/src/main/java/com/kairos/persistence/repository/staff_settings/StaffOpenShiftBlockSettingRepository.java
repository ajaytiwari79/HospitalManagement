package com.kairos.persistence.repository.staff_settings;
/*
 *Created By Pavan on 17/8/18
 *
 */

import com.kairos.persistence.model.staff_settings.StaffOpenShiftBlockSetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface StaffOpenShiftBlockSettingRepository extends MongoBaseRepository<StaffOpenShiftBlockSetting,BigInteger> {

    Optional<StaffOpenShiftBlockSetting> findByStaffId(Long staffId);
}
