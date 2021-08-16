package com.kairos.persistence.repository.granularity_setting;

import com.kairos.dto.activity.granularity_setting.GranularitySettingDTO;
import com.kairos.persistence.model.granularity_setting.GranularitySetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface GranularitySettingMongoRepository extends MongoBaseRepository<GranularitySetting, BigInteger> {

    GranularitySetting findByCountryIdAndOrganisationTypeIdAndDeletedFalse(Long countryId, Long organisationTypeId);

    GranularitySetting findByUnitIdAndDeletedFalse(Long unitId);

    List<GranularitySettingDTO> findAllByCountryIdAndDeletedFalse(Long countryId);

    @Query("{'deleted':false,'unitId':?0,'startDate':{$lte:?1},'$or':[{'endDate':{$exists:false}},{'endDate':{$gte:?1}}]}")
    GranularitySetting findByUnitIdDate(Long unitId, LocalDate currentLocalDate);

    @Query("{deleted:false,unitId:?0,startDate:?1}")
    GranularitySetting findByUnitIdAndStartDate(Long unitId, LocalDate startDate);
}
