package com.kairos.service.night_worker;

import com.kairos.rest_client.StaffRestClient;
import com.kairos.persistence.model.night_worker.ExpertiseNightWorkerSetting;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.night_worker.ExpertiseNightWorkerSettingRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.activity.shift.ShiftQueryResult;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.night_worker.ExpertiseNightWorkerSettingDTO;
import com.kairos.dto.activity.night_worker.ShiftAndExpertiseNightWorkerSettingDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

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
        Map<Long,Long> unitPositionIdAndExpertiseMap = staffRestClient.getUnitPositionExpertiseMap(unitId, unitId);
        List<ShiftQueryResult> shifts = shiftMongoRepository.getShiftsByUnitBeforeDate(unitId, DateUtils.getDate());
        Map<Long, List<ShiftQueryResult>> shiftsOfStaff = new HashMap<>();

        shifts.forEach(shiftQueryResult -> {
            shiftQueryResult.setExpertiseId(unitPositionIdAndExpertiseMap.get(shiftQueryResult.getUnitPositionId()));

            if(shiftsOfStaff.containsKey(shiftQueryResult.getStaffId())){
                shiftsOfStaff.get(shiftQueryResult.getStaffId()).add(shiftQueryResult);
            } else {
                shiftsOfStaff.put(shiftQueryResult.getStaffId(), new ArrayList<>(Arrays.asList(shiftQueryResult)));
            }
        });

        List<Long> expertiseIds =  (List<Long>) unitPositionIdAndExpertiseMap.values();
        List<ExpertiseNightWorkerSetting> expertiseNightWorkerSettings = expertiseNightWorkerSettingRepository.findAllByCountryAndExpertiseIds(expertiseIds);

        Map<Long, ExpertiseNightWorkerSettingDTO> expertiseSettingMap = new HashMap<>();
        expertiseNightWorkerSettings.stream().forEach(expertiseNightWorkerSetting -> {
            expertiseSettingMap.put(expertiseNightWorkerSetting.getExpertiseId(),
                    ObjectMapperUtils.copyPropertiesByMapper(expertiseNightWorkerSetting, ExpertiseNightWorkerSettingDTO.class)
                    );
        });

        shiftsOfStaff.forEach((staffId, shiftQueryResults) -> {
            ShiftAndExpertiseNightWorkerSettingDTO shiftAndExpertiseNightWorkerSettingDTO = new ShiftAndExpertiseNightWorkerSettingDTO(shifts, expertiseSettingMap);
            // DO trigger for night Worker as per staffId and unitId
        });

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
