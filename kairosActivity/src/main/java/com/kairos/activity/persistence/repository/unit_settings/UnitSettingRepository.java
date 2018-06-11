package com.kairos.activity.persistence.repository.unit_settings;

import com.kairos.activity.persistence.model.unit_settings.UnitSetting;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.web.unit_settings.UnitSettingDTO;
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
}
