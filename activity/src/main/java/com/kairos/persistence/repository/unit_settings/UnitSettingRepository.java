package com.kairos.persistence.repository.unit_settings;

import com.kairos.persistence.model.unit_settings.UnitSetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.dto.activity.unit_settings.UnitSettingDTO;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface UnitSettingRepository extends MongoBaseRepository<UnitSetting,BigInteger> {

    @Query(value = "{'deleted' : false, 'unitId':?0}",fields = "{'openShiftPhaseSetting' : 1}")
    List<UnitSettingDTO> getOpenShiftPhaseSettings(Long unitId);

    @Query(value = "{'deleted' : false, 'unitId':?0}",fields = "{'openShiftPhaseSetting.minOpenShiftHours': 1}")
    UnitSettingDTO getMinOpenShiftHours(Long unitId);

    @Query(value = "{'deleted' : false, 'unitId':?0}",fields = "{'flexibleTimeSettings': 1}")
    UnitSettingDTO getFlexibleTimingByUnit(Long unitId);

    @Query(value="{'deleted':false,'unitId':{'$in':?0}}",fields ="{'flexibleTimeSettings': 1,'unitId':1}")
    List<UnitSettingDTO> getGlideTimeByUnitIds(List<Long> unitIds);

    UnitSetting findByUnitIdAndDeletedFalse(Long unitId);


}
