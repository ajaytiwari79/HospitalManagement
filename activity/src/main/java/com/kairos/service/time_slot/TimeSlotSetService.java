package com.kairos.service.time_slot;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.user.country.time_slot.TimeSlot;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotSetDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotsDeductionDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.persistence.model.time_slot.TimeSlotSet;
import com.kairos.persistence.model.unit_settings.UnitSetting;
import com.kairos.persistence.repository.time_slot.TimeSlotMongoRepository;
import com.kairos.persistence.repository.unit_settings.UnitSettingRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isCollectionEmpty;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_TIMESLOT_ID_NOTFOUND;
import static com.kairos.constants.ActivityMessagesConstants.TIMESLOT_NOT_FOUND_FOR_UNIT;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.enums.TimeSlotType.SHIFT_PLANNING;
import static com.kairos.enums.TimeSlotType.TASK_PLANNING;
import static com.kairos.enums.time_slot.TimeSlotMode.ADVANCE;
import static com.kairos.enums.time_slot.TimeSlotMode.STANDARD;

@Service
public class TimeSlotSetService {
    @Inject
    private TimeSlotMongoRepository timeSlotMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UnitSettingRepository unitSettingRepository;
    @Inject
    private UserIntegrationService userIntegrationService;

    private static final Logger logger = LoggerFactory.getLogger(TimeSlotSetService.class);



    public List<TimeSlot> getTimeSlotByTimeSlotSet(BigInteger timeSlotSetId) {
        return timeSlotMongoRepository.findById(timeSlotSetId).get().getTimeSlots();
    }

    public Map<String, Object> getTimeSlotSets(Long unitId) {
        UnitSetting unitSetting=unitSettingRepository.findByUnitIdAndDeletedFalse(unitId);
        List<TimeSlotSetDTO> timeSlotSets = timeSlotMongoRepository.findByUnitIdAndTimeSlotModeAndTimeSlotTypeOrderByStartDate(unitId, unitSetting.getTimeSlotMode(), TASK_PLANNING);
        Map<String, Object> timeSlotSetData = new HashMap<>();
        timeSlotSetData.put("timeSlotSets", timeSlotSets);
        timeSlotSetData.put("standardTimeSlot", STANDARD.equals(unitSetting.getTimeSlotMode()));
        return timeSlotSetData;
    }

    @CacheEvict(value = "findByUnitIdAndTimeSlotTypeOrderByStartDate",key = "#unitId")
    public TimeSlotSetDTO createTimeSlotSet(Long unitId, TimeSlotSetDTO timeSlotSetDTO) {
        UnitSetting unitSetting=unitSettingRepository.findByUnitIdAndDeletedFalse(unitId);
        TimeSlotSet timeSlotSet = new TimeSlotSet(timeSlotSetDTO.getName(), timeSlotSetDTO.getStartDate(), unitSetting.getTimeSlotMode(),unitId);
        timeSlotSet.setEndDate(timeSlotSetDTO.getEndDate());
        timeSlotSet.setTimeSlotType(timeSlotSetDTO.getTimeSlotType());
        timeSlotSet.setTimeSlots(ObjectMapperUtils.copyCollectionPropertiesByMapper(timeSlotSetDTO.getTimeSlots(), TimeSlot.class));
        timeSlotMongoRepository.save(timeSlotSet);
        return timeSlotSetDTO;
    }

    @CacheEvict(value = "findByUnitIdAndTimeSlotTypeOrderByStartDate",allEntries = true)
    public TimeSlotDTO createTimeSlot(BigInteger timeSlotSetId, TimeSlotDTO timeSlotDTO) {
        TimeSlotSet timeSlotSet = timeSlotMongoRepository.findOne(timeSlotSetId);
        if (!Optional.ofNullable(timeSlotSet).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_TIMESLOT_ID_NOTFOUND);
        }
        TimeSlot timeSlot = new TimeSlot(null,timeSlotDTO.getName(),timeSlotDTO.getStartHour(),timeSlotDTO.getStartMinute(),timeSlotDTO.getEndHour(),timeSlotDTO.getEndMinute(),false);
        timeSlotSet.getTimeSlots().add(timeSlot);
        timeSlotMongoRepository.save(timeSlotSet);
        return timeSlotDTO;
    }

    @CacheEvict(value = "findByUnitIdAndTimeSlotTypeOrderByStartDate",allEntries = true)
    public List<TimeSlotSet> updateTimeSlotSet(BigInteger timeSlotSetId, TimeSlotSetDTO timeSlotSetDTO) {
        TimeSlotSet timeSlotSet = timeSlotMongoRepository.findById(timeSlotSetId).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage("MESSAGE_TIMESLOT_ID_NOTFOUND")));
        timeSlotSet.setTimeSlots(ObjectMapperUtils.copyCollectionPropertiesByMapper(timeSlotSetDTO.getTimeSlots(),TimeSlot.class));
            timeSlotSet.setName(timeSlotSetDTO.getName());
            timeSlotSet.setEndDate(timeSlotSetDTO.getEndDate());
            timeSlotSet.setStartDate(timeSlotSetDTO.getStartDate());
            timeSlotMongoRepository.save(timeSlotSet);

        return Arrays.asList(timeSlotSet);
    }

    @CacheEvict(value = "findByUnitIdAndTimeSlotTypeOrderByStartDate",key = "#unitId")
    public Map<String, Object> updateTimeSlotType(long unitId, boolean standardTimeSlot) {
        UnitSetting unitSetting = unitSettingRepository.findByUnitIdAndDeletedFalse(unitId);
        unitSetting.setTimeSlotMode((standardTimeSlot) ? STANDARD : ADVANCE);
        unitSettingRepository.save(unitSetting);
        return getTimeSlotSets(unitId);

    }

    @CacheEvict(value = "findByUnitIdAndTimeSlotTypeOrderByStartDate",allEntries = true)
    public boolean deleteTimeSlotSet(BigInteger timeSlotSetId) {
        TimeSlotSet timeSlotSetToDelete = timeSlotMongoRepository.findOne(timeSlotSetId);
        if (!Optional.ofNullable(timeSlotSetToDelete).isPresent()) {
            logger.error("Invalid time slot id {}" , timeSlotSetId);
            exceptionService.dataNotFoundByIdException(MESSAGE_TIMESLOT_ID_NOTFOUND);

        }
        timeSlotSetToDelete.setDeleted(true);
        timeSlotMongoRepository.save(timeSlotSetToDelete);
        return true;
    }

    public List<TimeSlot> prepareDefaultTimeSlot() {
        List<TimeSlot> timeSlotList=new ArrayList<>(3);
        timeSlotList.add(new TimeSlot(new BigInteger("1"),AppConstants.DAY,DAY_START_HOUR,DAY_END_HOUR,true));
        timeSlotList.add(new TimeSlot(new BigInteger("2"),AppConstants.EVENING,EVENING_START_HOUR,EVENING_END_HOUR,true));
        timeSlotList.add(new TimeSlot(new BigInteger("3"),AppConstants.NIGHT,NIGHT_START_HOUR,NIGHT_END_HOUR,true));
        return timeSlotList;
    }


    public void createTimeSlotsInAllUnits(){
        List<Long> unitIds=userIntegrationService.getUnitIds(UserContext.getUserDetails().getCountryId());
        for (Long unitId:unitIds) {
            createDefaultTimeSlots(unitId);
        }
    }

    @CacheEvict(value = "findByUnitIdAndTimeSlotTypeOrderByStartDate",key = "#unitId")
    public void createDefaultTimeSlots(Long unitId) {
        boolean alreadyCreated= timeSlotMongoRepository.existsByUnitId(unitId);
        if(!alreadyCreated) {
            logger.info("Creating default time slot for organization {}", unitId);
            List<TimeSlotSet> timeSlotSets = new ArrayList<>();
            timeSlotSets.add(new TimeSlotSet(TIME_SLOT_SET_NAME, LocalDate.now(), null, STANDARD, SHIFT_PLANNING, false, unitId, prepareDefaultTimeSlot()));
            timeSlotSets.add(new TimeSlotSet(TIME_SLOT_SET_NAME, LocalDate.now(), null, STANDARD, TASK_PLANNING, false, unitId, prepareDefaultTimeSlot()));
            timeSlotMongoRepository.saveEntities(timeSlotSets);
        }
    }

    public List<TimeSlotDTO> getCurrentTimeSlotOfUnit(Long unitId) {
        UnitSetting unit = unitSettingRepository.findByUnitIdAndDeletedFalse(unitId);
        return timeSlotMongoRepository.getByUnitIdAndTimeSlotMode(unitId, unit.getTimeSlotMode()).get(0).getTimeSlots();
    }

    public List<TimeSlotDTO> getUnitTimeSlot(Long unitId) {
        return getShiftPlanningTimeSlotSetsByUnit(unitId).get(0).getTimeSlots();
    }

    public List<TimeSlotDTO> getUnitTimeSlotByNames(Long unitId, Set<String> timeslotNames) {
        if(ObjectUtils.isCollectionNotEmpty(timeslotNames)){
            return  timeSlotMongoRepository.getByUnitIdAndNameInAndAndTimeSlotModeAndTimeSlotType(unitId, timeslotNames, STANDARD, SHIFT_PLANNING).get(0).getTimeSlots();
        }
        return Collections.emptyList();

    }
    public List<TimeSlotSetDTO> getShiftPlanningTimeSlotSetsByUnit(Long unitId) {
        return timeSlotMongoRepository.findByUnitIdAndTimeSlotType(unitId,  SHIFT_PLANNING);
    }

    public List<TimeSlot> getShiftPlanningTimeSlotsById(BigInteger timeSlotSetId) {
        return timeSlotMongoRepository.findById(timeSlotSetId).get().getTimeSlots();
    }

    public List<TimeSlotDTO> getShiftPlanningTimeSlotByUnit(Long unitId) {
        List<TimeSlotSetDTO> timeSlot = getShiftPlanningTimeSlotSetsByUnit(unitId);
        if(isCollectionEmpty(timeSlot)){
            exceptionService.dataNotFoundException(TIMESLOT_NOT_FOUND_FOR_UNIT);
        }
        return timeSlot.get(0).getTimeSlots();
    }

    public TimeSlotsDeductionDTO saveTimeSlotPercentageDeduction(Long unitId, TimeSlotsDeductionDTO timeSlotsDeductionDTO) {
        UnitSetting unitSetting = unitSettingRepository.findByUnitIdAndDeletedFalse(unitId);
        unitSetting.setDayShiftTimeDeduction(timeSlotsDeductionDTO.getDayShiftTimeDeduction());
        unitSetting.setNightShiftTimeDeduction(timeSlotsDeductionDTO.getNightShiftTimeDeduction());
        unitSettingRepository.save(unitSetting);
        return timeSlotsDeductionDTO;

    }

    public TimeSlotsDeductionDTO getTimeSlotPercentageDeduction(Long unitId) {
        UnitSetting unitSetting = unitSettingRepository.findByUnitIdAndDeletedFalse(unitId);
        TimeSlotsDeductionDTO timeSlotsDeductionDTO = new TimeSlotsDeductionDTO();
        timeSlotsDeductionDTO.setNightShiftTimeDeduction(unitSetting.getNightShiftTimeDeduction());
        timeSlotsDeductionDTO.setDayShiftTimeDeduction(unitSetting.getDayShiftTimeDeduction());
        return timeSlotsDeductionDTO;
    }

    public List<TimeSlotDTO> getDefaultTimeSlot() {
        List<TimeSlotDTO> timeSlotDTOS = new ArrayList<>(3);
        timeSlotDTOS.add(new TimeSlotDTO(DAY, DAY_START_HOUR, 00, DAY_END_HOUR, 00));
        timeSlotDTOS.add(new TimeSlotDTO(EVENING, EVENING_START_HOUR, 00, EVENING_END_HOUR, 00));
        timeSlotDTOS.add(new TimeSlotDTO(NIGHT, NIGHT_START_HOUR, 00, NIGHT_END_HOUR, 00));
        return timeSlotDTOS;
    }


}
