package com.kairos.persistence.repository.staff_settings;

import com.kairos.dto.user.staff.staff_settings.StaffActivitySettingDTO;
import com.kairos.persistence.model.staff_settings.StaffActivitySetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface StaffActivitySettingRepository extends MongoBaseRepository<StaffActivitySetting, BigInteger>, StaffActivitySettingRepositoryCustom {

    List<StaffActivitySettingDTO> findAllByUnitIdAndDeletedFalse(Long unitId);

    StaffActivitySetting findByIdAndDeletedFalse(BigInteger staffActivitySettingId);

    StaffActivitySettingDTO findByIdAndUnitIdAndDeletedFalse(BigInteger staffActivitySettingId,Long unitId);

    Set<StaffActivitySetting> findByStaffIdInAndActivityIdInAndDeletedFalse(Set<Long> staffIds, Set<BigInteger> activityIds);

    List<StaffActivitySetting> findByStaffIdAndActivityIdInAndDeletedFalse(Long staffId, Collection<BigInteger> activityId);

    StaffActivitySettingDTO findByActivityIdAndStaffIdAndUnitIdAndDeletedFalse(BigInteger activityId,Long staffId, Long unitId);

    List<StaffActivitySetting> findByActivityIdAndDeletedFalse(BigInteger activityId);

    @Query("{unitId :?0, activityId:{$in:?1}, deleted:false }")
    List<StaffActivitySetting> findByUnitIdAndActivityIdAndDeletedFalse(Long unitId, List<BigInteger> activityIds);

    @Query(value = "{staffId :?0, deleted:false }",fields = "{'activityId':1,'staffId':1,'earliestStartTime':1,'latestStartTime':1,'shortestTime':1,'longestTime':1}")
    List<StaffActivitySettingDTO> findAllByStaffIdAndDeletedFalse(Long staffId);
}
