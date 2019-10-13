package com.kairos.service.night_worker;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.night_worker.ExpertiseNightWorkerSettingDTO;
import com.kairos.dto.activity.night_worker.ShiftAndExpertiseNightWorkerSettingDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.user.country.time_slot.TimeSlot;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.enums.CalculationUnit;
import com.kairos.enums.DurationType;
import com.kairos.persistence.model.night_worker.ExpertiseNightWorkerSetting;
import com.kairos.persistence.repository.night_worker.ExpertiseNightWorkerSettingRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_NIGHTWORKER_SETTING_NOTFOUND;
import static com.kairos.constants.AppConstants.NIGHT;

@Service
@Transactional
public class ExpertiseNightWorkerSettingService{

    @Inject
    private ExpertiseNightWorkerSettingRepository expertiseNightWorkerSettingRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private ShiftMongoRepository shiftMongoRepository;

    @Inject
    private UserIntegrationService userIntegrationService;

    public ExpertiseNightWorkerSettingDTO createExpertiseNightWorkerSettings(Long countryId, Long expertiseId, ExpertiseNightWorkerSettingDTO nightWorkerSettingDTO) {
        ExpertiseNightWorkerSetting expertiseNightWorkerSetting = ObjectMapperUtils.copyPropertiesByMapper(nightWorkerSettingDTO, ExpertiseNightWorkerSetting.class);
        expertiseNightWorkerSettingRepository.save(expertiseNightWorkerSetting);
        nightWorkerSettingDTO.setId(expertiseNightWorkerSetting.getId());
        return nightWorkerSettingDTO;
    }

    public ExpertiseNightWorkerSettingDTO getExpertiseNightWorkerSettings(Long countryId, Long expertiseId) {
        ExpertiseNightWorkerSetting expertiseNightWorkerSetting = expertiseNightWorkerSettingRepository.findByExpertiseIdAndCountryId(expertiseId, countryId);
        TimeSlotDTO timeSlot = userIntegrationService.getDefaultTimeSlot(countryId).stream().filter(timeSlotDTO -> timeSlotDTO.getName().equals(NIGHT)).findFirst().get();
        if (!Optional.ofNullable(expertiseNightWorkerSetting).isPresent()) {
            expertiseNightWorkerSetting = new ExpertiseNightWorkerSetting(new TimeSlot(timeSlot.getStartHour(),timeSlot.getEndHour()),0, DurationType.WEEKS,0,0, CalculationUnit.HOURS,countryId,expertiseId);
        }
        expertiseNightWorkerSetting.setTimeSlot(new TimeSlot(timeSlot.getStartHour(),timeSlot.getEndHour()));
        return ObjectMapperUtils.copyPropertiesByMapper(expertiseNightWorkerSetting, ExpertiseNightWorkerSettingDTO.class);
    }

    public ExpertiseNightWorkerSettingDTO updateExpertiseNightWorkerSettings(Long countryId, Long expertiseId, ExpertiseNightWorkerSettingDTO nightWorkerSettingDTO) {
        ExpertiseNightWorkerSetting expertiseNightWorkerSetting = expertiseNightWorkerSettingRepository.findByExpertiseIdAndCountryId(expertiseId, countryId);
        TimeSlotDTO timeSlot = userIntegrationService.getDefaultTimeSlot(countryId).stream().filter(timeSlotDTO -> timeSlotDTO.getName().equals(NIGHT)).findFirst().get();
        if (!Optional.ofNullable(expertiseNightWorkerSetting).isPresent()) {
            expertiseNightWorkerSetting = new ExpertiseNightWorkerSetting(new TimeSlot(timeSlot.getStartHour(),timeSlot.getEndHour()),0, DurationType.WEEKS,0,0, CalculationUnit.HOURS,countryId,expertiseId);
        }
        expertiseNightWorkerSetting.setTimeSlot(new TimeSlot(timeSlot.getStartHour(),timeSlot.getEndHour()));
        expertiseNightWorkerSetting.setMinMinutesToCheckNightShift(nightWorkerSettingDTO.getMinMinutesToCheckNightShift());
        expertiseNightWorkerSetting.setIntervalUnitToCheckNightWorker(nightWorkerSettingDTO.getIntervalUnitToCheckNightWorker());
        expertiseNightWorkerSetting.setIntervalValueToCheckNightWorker(nightWorkerSettingDTO.getIntervalValueToCheckNightWorker());
        expertiseNightWorkerSetting.setMinShiftsValueToCheckNightWorker(nightWorkerSettingDTO.getMinShiftsValueToCheckNightWorker());
        expertiseNightWorkerSetting.setMinShiftsUnitToCheckNightWorker(nightWorkerSettingDTO.getMinShiftsUnitToCheckNightWorker());
        expertiseNightWorkerSettingRepository.save(expertiseNightWorkerSetting);
        return nightWorkerSettingDTO;
    }

    public ExpertiseNightWorkerSettingDTO updateExpertiseNightWorkerSettingsInUnit(Long unitId, Long expertiseId, ExpertiseNightWorkerSettingDTO nightWorkerSettingDTO) {
        ExpertiseNightWorkerSetting expertiseNightWorkerSetting = expertiseNightWorkerSettingRepository.findOne(nightWorkerSettingDTO.getId());
        TimeSlotDTO timeSlot = userIntegrationService.getUnitTimeSlot(unitId).stream().filter(timeSlotDTO -> timeSlotDTO.getName().equals(NIGHT)).findFirst().get();;
        if (!Optional.ofNullable(expertiseNightWorkerSetting).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_NIGHTWORKER_SETTING_NOTFOUND, nightWorkerSettingDTO.getId());
        }
        if (expertiseNightWorkerSetting.getCountryId() != null) {  // this is country's night worker settings so we are personalizing and creating for unit
            expertiseNightWorkerSetting = new ExpertiseNightWorkerSetting(unitId,expertiseId,nightWorkerSettingDTO.getTimeSlot(), nightWorkerSettingDTO.getMinMinutesToCheckNightShift(),
                    nightWorkerSettingDTO.getIntervalUnitToCheckNightWorker(),nightWorkerSettingDTO.getIntervalValueToCheckNightWorker(), nightWorkerSettingDTO.getMinShiftsValueToCheckNightWorker(),
                    nightWorkerSettingDTO.getMinShiftsUnitToCheckNightWorker());
        } else {
            expertiseNightWorkerSetting.setExpertiseId(expertiseId);
            expertiseNightWorkerSetting.setTimeSlot(new TimeSlot(timeSlot.getStartHour(),timeSlot.getEndHour()));
            expertiseNightWorkerSetting.setMinMinutesToCheckNightShift(nightWorkerSettingDTO.getMinMinutesToCheckNightShift());
            expertiseNightWorkerSetting.setIntervalUnitToCheckNightWorker(nightWorkerSettingDTO.getIntervalUnitToCheckNightWorker());
            expertiseNightWorkerSetting.setIntervalValueToCheckNightWorker(nightWorkerSettingDTO.getIntervalValueToCheckNightWorker());
            expertiseNightWorkerSetting.setMinShiftsValueToCheckNightWorker(nightWorkerSettingDTO.getMinShiftsValueToCheckNightWorker());
            expertiseNightWorkerSetting.setMinShiftsUnitToCheckNightWorker(nightWorkerSettingDTO.getMinShiftsUnitToCheckNightWorker());
        }
        expertiseNightWorkerSettingRepository.save(expertiseNightWorkerSetting);
        nightWorkerSettingDTO.setId(expertiseNightWorkerSetting.getId());
        return nightWorkerSettingDTO;
    }

    public Boolean updateNightWorkerStatusByUnitId(Long unitId) {
        Map<Long, Long> employmentIdAndExpertiseMap = userIntegrationService.getEmploymentExpertiseMap(unitId);
        List<ShiftDTO> shifts = shiftMongoRepository.getShiftsByUnitBeforeDate(unitId, DateUtils.getDate());
        Map<Long, List<ShiftDTO>> shiftsOfStaff = new HashMap<>();

        shifts.forEach(shiftQueryResult -> {
            shiftQueryResult.setExpertiseId(employmentIdAndExpertiseMap.get(shiftQueryResult.getEmploymentId()));

            if (shiftsOfStaff.containsKey(shiftQueryResult.getStaffId())) {
                shiftsOfStaff.get(shiftQueryResult.getStaffId()).add(shiftQueryResult);
            } else {
                shiftsOfStaff.put(shiftQueryResult.getStaffId(), new ArrayList<>(Arrays.asList(shiftQueryResult)));
            }
        });

        List<Long> expertiseIds = (List<Long>) employmentIdAndExpertiseMap.values();
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
    public boolean updateNightWorkerStatus() {
        List<Long> unitIds = shiftMongoRepository.getUnitIdListOfShiftBeforeDate(DateUtils.getDate());

        unitIds.forEach(unitId -> {
            updateNightWorkerStatusByUnitId(unitId);
        });
        return true;
    }

    public ExpertiseNightWorkerSettingDTO getExpertiseNightWorkerSettingsForUnit(Long unitId, Long expertiseId) {
        ExpertiseNightWorkerSetting expertiseNightWorkerSetting = expertiseNightWorkerSettingRepository.findByExpertiseIdAndUnitId( expertiseId,unitId);
        TimeSlotDTO timeSlot = userIntegrationService.getUnitTimeSlot(unitId).stream().filter(timeSlotDTO -> timeSlotDTO.getName().equals(NIGHT)).findFirst().get();;
        if (!Optional.ofNullable(expertiseNightWorkerSetting).isPresent()) {
            // find country level settings
            expertiseNightWorkerSetting = expertiseNightWorkerSettingRepository.findByExpertiseIdAndDeletedFalseAndCountryIdExistsTrue(expertiseId);
        }
        expertiseNightWorkerSetting.setTimeSlot(new TimeSlot(timeSlot.getStartHour(),timeSlot.getEndHour()));
        return ObjectMapperUtils.copyPropertiesByMapper(expertiseNightWorkerSetting, ExpertiseNightWorkerSettingDTO.class);
    }
}
