package com.kairos.persistence.repository.staff_settings;

import com.kairos.persistence.model.activity.Shift;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.user.staff.staff_settings.StaffActivitySettingDTO;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface StaffActivitySettingRepository extends MongoBaseRepository<Shift, BigInteger> {
    List<StaffActivitySettingDTO> findAllByUnitIdAndDeletedFalse(Long unitId);

}
