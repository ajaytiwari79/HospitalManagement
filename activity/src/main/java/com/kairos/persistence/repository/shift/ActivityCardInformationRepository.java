package com.kairos.persistence.repository.shift;

import com.kairos.enums.shift.ViewType;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.service.shift.ActivityCardInformation;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;

public interface ActivityCardInformationRepository extends MongoBaseRepository<ActivityCardInformation, BigInteger> {
    @Query("{unitId:?0,deleted:false,staffId:?1,viewType:?2}")
    ActivityCardInformation findByUnitIdAndStaffId(Long unitId, Long staffId, ViewType viewType);

    @Query("{unitId:?0,deleted:false,countryAdminSetting:?1,viewType:?2}")
    ActivityCardInformation findByUnitIdAndCountryAdminSetting(Long unitId,boolean countryAdminSetting, ViewType viewType);
}
