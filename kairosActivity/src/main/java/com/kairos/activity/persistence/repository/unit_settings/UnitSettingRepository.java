package com.kairos.activity.persistence.repository.unit_settings;

import com.kairos.activity.persistence.model.unit_settings.UnitSetting;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.web.unit_settings.OpenShiftPhaseSetting;
import com.kairos.response.dto.web.unit_settings.UnitSettingDTO;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface UnitSettingRepository extends MongoBaseRepository<UnitSetting,BigInteger> {

    @Query(value = "{deleted:false, unitId?0, unitSettingsId:?1}",fields = "{'openShiftPhaseSetting':1,'minShiftHours':1}")
    List<UnitSettingDTO> getOpenShiftPhaseSettings(Long unitId, BigInteger unitSettingsId);
}
