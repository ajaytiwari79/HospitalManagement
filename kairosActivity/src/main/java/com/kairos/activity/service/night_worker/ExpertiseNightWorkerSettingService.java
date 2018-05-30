package com.kairos.activity.service.night_worker;

import com.kairos.activity.client.StaffRestClient;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.model.night_worker.ExpertiseNightWorkerSetting;
import com.kairos.activity.persistence.repository.activity.ShiftMongoRepository;
import com.kairos.activity.persistence.repository.night_worker.ExpertiseNightWorkerSettingRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.shift.ShiftQueryResult;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.response.dto.web.night_worker.ExpertiseNightWorkerSettingDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ExpertiseNightWorkerSettingService extends MongoBaseService {

    @Inject
    private ExpertiseNightWorkerSettingRepository expertiseNightWorkerSettingRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private ShiftMongoRepository shiftMongoRepository;

    @Inject
    private StaffRestClient staffRestClient;

    public ExpertiseNightWorkerSettingDTO createExpertiseNightWorkerSettings(Long countryId, Long expertiseId, ExpertiseNightWorkerSettingDTO nightWorkerSettingDTO) {
        ExpertiseNightWorkerSetting expertiseNightWorkerSetting = ObjectMapperUtils.copyPropertiesByMapper(nightWorkerSettingDTO, ExpertiseNightWorkerSetting.class);
        save(expertiseNightWorkerSetting);
        return ObjectMapperUtils.copyPropertiesByMapper(expertiseNightWorkerSetting, ExpertiseNightWorkerSettingDTO.class);
    }

    public ExpertiseNightWorkerSettingDTO getExpertiseNightWorkerSettings(Long countryId, Long expertiseId){
        ExpertiseNightWorkerSetting expertiseNightWorkerSetting = expertiseNightWorkerSettingRepository.findByCountryAndExpertise(countryId, expertiseId);
        if(!Optional.ofNullable(expertiseNightWorkerSetting).isPresent()){
            exceptionService.dataNotFoundByIdException("message.nightWorker.setting.notFound", expertiseId);
        }
        return ObjectMapperUtils.copyPropertiesByMapper(expertiseNightWorkerSetting, ExpertiseNightWorkerSettingDTO.class);
    }

    public ExpertiseNightWorkerSettingDTO updateExpertiseNightWorkerSettings(Long countryId, Long expertiseId, ExpertiseNightWorkerSettingDTO nightWorkerSettingDTO) {
        ExpertiseNightWorkerSetting expertiseNightWorkerSetting   = expertiseNightWorkerSettingRepository.findByCountryAndExpertise(countryId, expertiseId);
        if (!Optional.ofNullable(expertiseNightWorkerSetting).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.nightWorker.setting.notFound", expertiseId);
        }
        expertiseNightWorkerSetting.setTimeSlot(nightWorkerSettingDTO.getTimeSlot());
        expertiseNightWorkerSetting.setMinMinutesToCheckNightShift(nightWorkerSettingDTO.getMinMinutesToCheckNightShift());
        expertiseNightWorkerSetting.setIntervalUnitToCheckNightWorker(nightWorkerSettingDTO.getIntervalUnitToCheckNightWorker());
        expertiseNightWorkerSetting.setIntervalValueToCheckNightWorker(nightWorkerSettingDTO.getIntervalValueToCheckNightWorker());
        expertiseNightWorkerSetting.setMinShiftsValueToCheckNightWorker(nightWorkerSettingDTO.getMinShiftsValueToCheckNightWorker());
        expertiseNightWorkerSetting.setMinShiftsUnitToCheckNightWorker(nightWorkerSettingDTO.getMinShiftsUnitToCheckNightWorker());

        save(expertiseNightWorkerSetting);
        return nightWorkerSettingDTO;
    }

    public Boolean updateNightWorkerStatusByUnitId(Long unitId){
        List<Map<Long,Long>> unitPositionIdAndExpertiseMap = staffRestClient.getUnitPositionExpertiseMap(unitId, unitId);
        List<ShiftQueryResult> shifts = shiftMongoRepository.getShiftsByUnitBeforeDate(unitId, DateUtils.getDate());
        return true;
    }

    // Method to be triggered when job will be executed for checking status of night worker as per the shifts done by staff
    public boolean updateNightWorkerStatus(){
        List<Long> unitIds = shiftMongoRepository.getUnitIdListOfShiftBeforeDate(DateUtils.getDate());

        unitIds.forEach(unitId -> {
            updateNightWorkerStatusByUnitId(unitId);
        });
        return true;
    }
}
