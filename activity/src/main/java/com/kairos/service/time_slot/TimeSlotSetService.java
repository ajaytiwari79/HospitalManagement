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
import com.kairos.enums.TimeSlotType;
import com.kairos.persistence.model.time_slot.TimeSlotSet;
import com.kairos.persistence.model.unit_settings.UnitSetting;
import com.kairos.persistence.repository.time_slot.TimeSlotRepository;
import com.kairos.persistence.repository.unit_settings.UnitSettingRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.constants.AppConstants.*;
import static com.kairos.enums.TimeSlotType.SHIFT_PLANNING;
import static com.kairos.enums.TimeSlotType.TASK_PLANNING;
import static com.kairos.enums.time_slot.TimeSlotMode.ADVANCE;
import static com.kairos.enums.time_slot.TimeSlotMode.STANDARD;
import static java.util.Collections.*;

@Service
public class TimeSlotSetService {
    @Inject
    private TimeSlotRepository timeSlotRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UnitSettingRepository unitSettingRepository;
    @Inject
    private UserIntegrationService userIntegrationService;

    private static final Logger logger = LoggerFactory.getLogger(TimeSlotSetService.class);



    public List<TimeSlot> getTimeSlotByTimeSlotSet(BigInteger timeSlotSetId) {
        return timeSlotRepository.findById(timeSlotSetId).get().getTimeSlots();
    }

    public Map<String, Object> getTimeSlotSets(Long unitId) {
        UnitSetting unitSetting=unitSettingRepository.findByUnitIdAndDeletedFalse(unitId);
        List<TimeSlotSetDTO> timeSlotSets = timeSlotRepository.findByUnitIdAndTimeSlotModeAndTimeSlotTypeOrderByStartDate(unitId, unitSetting.getTimeSlotMode(), TASK_PLANNING);
        Map<String, Object> timeSlotSetData = new HashMap<>();
        timeSlotSetData.put("timeSlotSets", timeSlotSets);
        timeSlotSetData.put("standardTimeSlot", STANDARD.equals(unitSetting.getTimeSlotMode()));
        return timeSlotSetData;
    }

    public TimeSlotSetDTO createTimeSlotSet(long unitId, TimeSlotSetDTO timeSlotSetDTO) {
        UnitSetting unitSetting=unitSettingRepository.findByUnitIdAndDeletedFalse(unitId);
        TimeSlotSet timeSlotSet = new TimeSlotSet(timeSlotSetDTO.getName(), timeSlotSetDTO.getStartDate(), unitSetting.getTimeSlotMode(),unitId);
        timeSlotSet.setEndDate(timeSlotSetDTO.getEndDate());
        timeSlotSet.setTimeSlotType(timeSlotSetDTO.getTimeSlotType());
        timeSlotSet.setTimeSlots(ObjectMapperUtils.copyCollectionPropertiesByMapper(timeSlotSetDTO.getTimeSlots(), TimeSlot.class));
        timeSlotRepository.save(timeSlotSet);
        return timeSlotSetDTO;
    }

    public TimeSlotDTO createTimeSlot(BigInteger timeSlotSetId, TimeSlotDTO timeSlotDTO) {
        TimeSlotSet timeSlotSet = timeSlotRepository.findOne(timeSlotSetId);
        if (!Optional.ofNullable(timeSlotSet).isPresent()) {
            exceptionService.dataNotFoundByIdException("MESSAGE_TIMESLOT_ID_NOTFOUND");
        }
        TimeSlot timeSlot = new TimeSlot(null,timeSlotDTO.getName(),timeSlotDTO.getStartHour(),timeSlotDTO.getStartMinute(),timeSlotDTO.getEndHour(),timeSlotDTO.getEndMinute(),false);
        timeSlotSet.getTimeSlots().add(timeSlot);
        timeSlotRepository.save(timeSlotSet);
        return timeSlotDTO;
    }


    public List<TimeSlotSet> updateTimeSlotSet(BigInteger timeSlotSetId, TimeSlotSetDTO timeSlotSetDTO) {
        TimeSlotSet timeSlotSet = timeSlotRepository.findById(timeSlotSetId).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage("MESSAGE_TIMESLOT_ID_NOTFOUND")));
        timeSlotSet.setTimeSlots(ObjectMapperUtils.copyCollectionPropertiesByMapper(timeSlotSetDTO.getTimeSlots(),TimeSlot.class));
            timeSlotSet.setName(timeSlotSetDTO.getName());
            timeSlotSet.setEndDate(timeSlotSetDTO.getEndDate());
            timeSlotSet.setStartDate(timeSlotSetDTO.getStartDate());
            timeSlotRepository.save(timeSlotSet);

        return Arrays.asList(timeSlotSet);
    }


    public Map<String, Object> updateTimeSlotType(long unitId, boolean standardTimeSlot) {
        UnitSetting unitSetting = unitSettingRepository.findByUnitIdAndDeletedFalse(unitId);
        unitSetting.setTimeSlotMode((standardTimeSlot) ? STANDARD : ADVANCE);
        unitSettingRepository.save(unitSetting);
        return getTimeSlotSets(unitId);

    }


    public boolean deleteTimeSlotSet(BigInteger timeSlotSetId) {
        TimeSlotSet timeSlotSetToDelete = timeSlotRepository.findOne(timeSlotSetId);
        if (!Optional.ofNullable(timeSlotSetToDelete).isPresent()) {
            logger.error("Invalid time slot id {}" , timeSlotSetId);
            exceptionService.dataNotFoundByIdException("MESSAGE_TIMESLOT_ID_NOTFOUND");

        }
        timeSlotSetToDelete.setDeleted(true);
        timeSlotRepository.save(timeSlotSetToDelete);
        return true;
    }


    private Map<String, Object> prepareTimeSlotResponse(UnitSetting unitSetting) {
        List<TimeSlotSetDTO> timeSlotWrappers = timeSlotRepository.getByUnitIdAndTimeSlotMode(unitSetting.getUnitId(), unitSetting.getTimeSlotMode());
        Map<String, Object> response = new HashMap<>();
        response.put("timeSlots", timeSlotWrappers);
        response.put("standardTimeSlot", STANDARD.equals(unitSetting.getTimeSlotMode()));
        response.put("timeZone", unitSetting.getTimeZone() != null ? unitSetting.getTimeZone().getId() : null);
        return response;
    }

    public void createDefaultTimeSlots(Long unitId, TimeSlotType timeSlotType) {
        TimeSlotSet timeSlotSet = new TimeSlotSet(TIME_SLOT_SET_NAME, LocalDate.now(), STANDARD,unitId);
        timeSlotSet.setDefaultSet(true);
        timeSlotSet.setTimeSlotType(timeSlotType);
        List<TimeSlot> timeSlotList = prepareDefaultTimeSlot();
        timeSlotSet.setTimeSlots(timeSlotList);
        timeSlotRepository.save(timeSlotSet);
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

    public void createDefaultTimeSlots(Long unitId) {
        boolean alreadyCreated=timeSlotRepository.existsByUnitId(unitId);
        if(!alreadyCreated) {
            logger.info("Creating default time slot for organization {}", unitId);
            List<TimeSlotSet> timeSlotSets = new ArrayList<>();
            timeSlotSets.add(new TimeSlotSet(TIME_SLOT_SET_NAME, LocalDate.now(), null, STANDARD, SHIFT_PLANNING, false, unitId, prepareDefaultTimeSlot()));
            timeSlotSets.add(new TimeSlotSet(TIME_SLOT_SET_NAME, LocalDate.now(), null, STANDARD, TASK_PLANNING, false, unitId, prepareDefaultTimeSlot()));
            timeSlotRepository.saveEntities(timeSlotSets);
        }
    }



    /**
     * @param unitId
     * @return
     * @auther anil maurya
     */
    public List<TimeSlotDTO> getCurrentTimeSlotOfUnit(Long unitId) {
        UnitSetting unit = unitSettingRepository.findByUnitIdAndDeletedFalse(unitId);
        return timeSlotRepository.getByUnitIdAndTimeSlotMode(unitId, unit.getTimeSlotMode()).get(0).getTimeSlots();
    }

    public List<TimeSlotDTO> getUnitTimeSlot(Long unitId) {
        UnitSetting unit = unitSettingRepository.findByUnitIdAndDeletedFalse(unitId);
        return timeSlotRepository.findByUnitIdAndTimeSlotModeAndTimeSlotTypeOrderByStartDate(unitId, unit.getTimeSlotMode(),SHIFT_PLANNING).get(0).getTimeSlots();
    }

    public List<TimeSlotDTO> getUnitTimeSlotByNames(Long unitId, Set<String> timeslotNames) {
        if(ObjectUtils.isCollectionNotEmpty(timeslotNames)){
            return  timeSlotRepository.getByUnitIdAndNameInAndAndTimeSlotModeAndTimeSlotType(unitId, timeslotNames, STANDARD, SHIFT_PLANNING).get(0).getTimeSlots();
        }
        return Collections.emptyList();

    }


    public List<TimeSlotSetDTO> getShiftPlanningTimeSlotSetsByUnit(Long unitId) {
        UnitSetting unitSetting=unitSettingRepository.findByUnitIdAndDeletedFalse(unitId);
        return timeSlotRepository.findByUnitIdAndTimeSlotModeAndTimeSlotTypeOrderByStartDate(unitId, unitSetting.getTimeSlotMode(), SHIFT_PLANNING);
    }

    public List<TimeSlot> getShiftPlanningTimeSlotsById(BigInteger timeSlotSetId) {
        return timeSlotRepository.findById(timeSlotSetId).get().getTimeSlots();
    }

    public List<TimeSlotDTO> getShiftPlanningTimeSlotByUnit(Long unitId) {
        UnitSetting unitSetting=unitSettingRepository.findByUnitIdAndDeletedFalse(unitId);
        return timeSlotRepository.findByUnitIdAndTimeSlotModeAndTimeSlotTypeOrderByStartDate(unitSetting.getUnitId(), unitSetting.getTimeSlotMode(), SHIFT_PLANNING).get(0).getTimeSlots();
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
