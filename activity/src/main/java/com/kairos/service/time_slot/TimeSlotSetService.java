package com.kairos.service.time_slot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.country.time_slot.TimeSlot;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotSetDTO;
import com.kairos.enums.TimeSlotType;
import com.kairos.persistence.model.time_slot.TimeSlotSet;
import com.kairos.persistence.model.unit_settings.UnitSetting;
import com.kairos.persistence.repository.time_slot.TimeSlotRepository;
import com.kairos.persistence.repository.unit_settings.UnitSettingRepository;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.constants.AppConstants.*;
import static com.kairos.enums.TimeSlotType.TASK_PLANNING;
import static com.kairos.enums.time_slot.TimeSlotMode.ADVANCE;
import static com.kairos.enums.time_slot.TimeSlotMode.STANDARD;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class TimeSlotSetService {
    @Inject
    private UnitGraphRepository unitGraphRepository;
    @Inject
    private UnitService unitService;
    @Inject
    private TimeSlotRepository timeSlotRepository;
    @Inject
    private OrganizationBaseRepository organizationBaseRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UnitSettingRepository unitSettingRepository;
    @Inject
    private TimeSlotRelationshipGraphRepository timeSlotRelationshipGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    private static final Logger logger = LoggerFactory.getLogger(TimeSlotSetService.class);

    public Map<String, Object> getTimeSlots(long unitId) {
        UnitSetting unitSetting=unitSettingRepository.findByUnitIdAndDeletedFalse(unitId);
        return prepareTimeSlotResponse(unitSetting);
    }

    public TimeSlotSetDTO getTimeSlotByTimeSlotSet(Long timeSlotSetId) {
        return timeSlotRepository.findById(timeSlotSetId);
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
        TimeSlot timeSlot = new TimeSlot(timeSlotDTO.getName(),timeSlotDTO.getStartHour(),timeSlotDTO.getStartMinute(),timeSlotDTO.getEndHour(),timeSlotDTO.getEndMinute(),false);
        timeSlotSet.getTimeSlots().add(timeSlot);
        timeSlotRepository.save(timeSlotSet);
        return timeSlotDTO;
    }


    public List<TimeSlotSet> updateTimeSlotSet(Long unitId, BigInteger timeSlotSetId, TimeSlotSetDTO timeSlotSetDTO) {
        TimeSlotSet timeSlotSet = timeSlotRepository.findById(timeSlotSetId).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage("MESSAGE_TIMESLOT_ID_NOTFOUND")));
        List<TimeSlotSet> timeSlotSetsToUpdate = new ArrayList<>();
        if(timeSlotSet.getTimeSlotType().equals(TASK_PLANNING)) {
            List<TimeSlotSet> timeSlotSetsToValidate = timeSlotRepository.findTimeSlotByUnitIdAndBetweenStartDateAndEndDateAndTimeSlotType(unitId, timeSlotSet.getStartDate(), timeSlotSetDTO.getEndDate(), timeSlotSet.getTimeSlotType());
            for (TimeSlotSet timeSlotSetToValidate : timeSlotSetsToValidate) {

                if (timeSlotSetToValidate.getEndDate().isBefore(timeSlotSetDTO.getEndDate())) {
                    timeSlotSetToValidate.setDeleted(true);
                } else {
                    LocalDate dateAsLocalDate = timeSlotSetDTO.getEndDate();
                    timeSlotSetToValidate.setStartDate(dateAsLocalDate.plusDays(1));
                    break;
                }
                timeSlotSetsToUpdate.add(timeSlotSetToValidate);
            }
            timeSlotSet.updateTimeSlotSet(timeSlotSetDTO);
            timeSlotSet.setName(timeSlotSetDTO.getName());
            timeSlotSet.setEndDate(timeSlotSetDTO.getEndDate());
            timeSlotSetsToUpdate.add(timeSlotSet);
            timeSlotSetRepository.saveAll(timeSlotSetsToUpdate);
        }
        updateTimeSlot(timeSlotSetDTO.getTimeSlots(), timeSlotSet.getId());
        return timeSlotSetsToUpdate;
    }


    public Map<String, Object> updateTimeSlotType(long unitId, boolean standardTimeSlot) {
        Unit unit = unitService.findByUnitId(unitId);
        unit.setTimeSlotMode((standardTimeSlot) ? STANDARD : ADVANCE);
        unitGraphRepository.save(unit);
        return getTimeSlotSets(unitId);

    }

    public List<TimeSlotDTO> updateTimeSlot(List<TimeSlotDTO> timeSlotDTOS, BigInteger timeSlotSetId) {
        TimeSlotSet timeSlotSet = timeSlotRepository.findById(timeSlotSetId).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage("MESSAGE_TIMESLOT_ID_NOTFOUND")));
        List<TimeSlotDTO> timeSlotsToUpdate = new ArrayList<>();
        List<TimeSlotDTO> timeSlotsToCreate = new ArrayList<>();
        for (TimeSlotDTO timeSlotDTO : timeSlotDTOS) {
            if (timeSlotDTO.getId() == null) {
                timeSlotsToCreate.add(timeSlotDTO);
            } else {
                timeSlotsToUpdate.add(timeSlotDTO);
            }
        }

        for (TimeSlotDTO timeSlotDTO : timeSlotsToUpdate) {
            timeSlotRepository.updateTimeSlot(timeSlotSetId, timeSlotDTO.getId(), timeSlotDTO.getName(), timeSlotDTO.getStartHour(),
                    timeSlotDTO.getStartMinute(), timeSlotDTO.getEndHour(), timeSlotDTO.getEndMinute(), timeSlotDTO.isShiftStartTime());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        List<TimeSlotSetTimeSlotRelationship> timeSlotSetTimeSlotRelationships = new ArrayList<>();
        prepareTimeSlotSetRelationShip(timeSlotSet, timeSlotsToCreate, objectMapper, timeSlotSetTimeSlotRelationships);
        timeSlotRelationshipGraphRepository.saveAll(timeSlotSetTimeSlotRelationships);

        List<TimeSlotDTO> newCreatedTimeSlots = new ArrayList<>();
        for (TimeSlotSetTimeSlotRelationship timeSlotSetTimeSlotRelationship : timeSlotSetTimeSlotRelationships) {
            TimeSlotDTO timeSlotDTO = objectMapper.convertValue(timeSlotSetTimeSlotRelationship, TimeSlotDTO.class);
            timeSlotDTO.setId(timeSlotSetTimeSlotRelationship.getTimeSlot().getId());
            newCreatedTimeSlots.add(timeSlotDTO);
        }
        return newCreatedTimeSlots;
    }

    private void prepareTimeSlotSetRelationShip(TimeSlotSet timeSlotSet, List<TimeSlotDTO> timeSlotsToCreate, ObjectMapper objectMapper, List<TimeSlotSetTimeSlotRelationship> timeSlotSetTimeSlotRelationships) {
        for (TimeSlotDTO timeSlotDTO : timeSlotsToCreate) {
            TimeSlot timeSlot = new TimeSlot(timeSlotDTO.getName());
            TimeSlotSetTimeSlotRelationship timeSlotSetTimeSlotRelationship = objectMapper.convertValue
                    (timeSlotDTO, TimeSlotSetTimeSlotRelationship.class);
            timeSlotSetTimeSlotRelationship.setId(null);
            timeSlotSetTimeSlotRelationship.setTimeSlotSet(timeSlotSet);
            timeSlotSetTimeSlotRelationship.setTimeSlot(timeSlot);
            timeSlotSetTimeSlotRelationships.add(timeSlotSetTimeSlotRelationship);
        }
    }

    public boolean deleteTimeSlotSet(Long unitId, Long timeSlotSetId) {
        Unit unit = unitGraphRepository.findOne(unitId, 0);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.unitNotFoundException(MESSAGE_UNIT_ID_NOTFOUND, unitId);

        }
        TimeSlotSet timeSlotSetToDelete = timeSlotSetRepository.findOne(timeSlotSetId);
        if (!Optional.ofNullable(timeSlotSetToDelete).isPresent()) {
            logger.error("Invalid time slot id {}" , timeSlotSetId);
            exceptionService.dataNotFoundByIdException(MESSAGE_TIMESLOT_ID_NOTFOUND);

        }
        TimeSlotSet timeSlotSet = timeSlotSetRepository.findOneByStartDateAfter(unitId, timeSlotSetToDelete.getEndDate().toString());
        if (Optional.ofNullable(timeSlotSet).isPresent()) {
            timeSlotSet.setStartDate(timeSlotSetToDelete.getEndDate());
            timeSlotSetRepository.save(timeSlotSet);
        }
        timeSlotSetToDelete.setDeleted(true);
        timeSlotSetRepository.save(timeSlotSetToDelete);
        return true;
    }

    public boolean deleteTimeSlot(long timeSlotId, Long timeSlotSetId) {
        timeSlotRepository.deleteTimeSlot(timeSlotSetId, timeSlotId);
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

    public void createDefaultTimeSlots(Organization organization, TimeSlotType timeSlotType) {
        List<TimeSlot> timeSlots = timeSlotRepository.findBySystemGeneratedTimeSlotsIsTrue();
        TimeSlotSet timeSlotSet = new TimeSlotSet(TIME_SLOT_SET_NAME, LocalDate.now(), organization.getTimeSlotMode());
        timeSlotSet.setDefaultSet(true);
        timeSlotSet.setTimeSlotType(timeSlotType);
        List<TimeSlotSetTimeSlotRelationship> timeSlotSetTimeSlotRelationships = new ArrayList<>();
        for (TimeSlot timeSlot : timeSlots) {
            TimeSlotSetTimeSlotRelationship timeSlotSetTimeSlotRelationship = new TimeSlotSetTimeSlotRelationship();
            if (DAY.equals(timeSlot.getName())) {
                timeSlotSetTimeSlotRelationship.setStartHour(DAY_START_HOUR);
                timeSlotSetTimeSlotRelationship.setEndHour(DAY_END_HOUR);
                timeSlotSetTimeSlotRelationship.setShiftStartTime(true);
            } else if (EVENING.equals(timeSlot.getName())) {
                timeSlotSetTimeSlotRelationship.setStartHour(EVENING_START_HOUR);
                timeSlotSetTimeSlotRelationship.setEndHour(EVENING_END_HOUR);
            } else if (NIGHT.equals(timeSlot.getName())) {
                timeSlotSetTimeSlotRelationship.setStartHour(NIGHT_START_HOUR);
                timeSlotSetTimeSlotRelationship.setEndHour(NIGHT_END_HOUR);
            }
            timeSlotSetTimeSlotRelationship.setTimeSlotSet(timeSlotSet);
            timeSlotSetTimeSlotRelationship.setTimeSlot(timeSlot);
            timeSlotSetTimeSlotRelationships.add(timeSlotSetTimeSlotRelationship);
        }
        timeSlotRelationshipGraphRepository.saveAll(timeSlotSetTimeSlotRelationships);
        List<TimeSlotSet> timeSlotSets = organization.getTimeSlotSets();
        timeSlotSets.add(timeSlotSet);
        organization.setTimeSlotSets(timeSlotSets);
        organizationGraphRepository.save(organization);
    }



    private List<TimeSlotSetTimeSlotRelationship> setTimeSlotSet(List<TimeSlot> timeSlots, TimeSlotSet timeSlotSet) {
        List<TimeSlotSetTimeSlotRelationship> timeSlotSetTimeSlotRelationships = new ArrayList<>();
        for (TimeSlot timeSlot : timeSlots) {
            TimeSlotSetTimeSlotRelationship timeSlotSetTimeSlotRelationship = new TimeSlotSetTimeSlotRelationship();
            if (DAY.equals(timeSlot.getName())) {
                timeSlotSetTimeSlotRelationship.setStartHour(DAY_START_HOUR);
                timeSlotSetTimeSlotRelationship.setEndHour(DAY_END_HOUR);
                timeSlotSetTimeSlotRelationship.setShiftStartTime(true);
            } else if (EVENING.equals(timeSlot.getName())) {
                timeSlotSetTimeSlotRelationship.setStartHour(EVENING_START_HOUR);
                timeSlotSetTimeSlotRelationship.setEndHour(EVENING_END_HOUR);
            } else if (NIGHT.equals(timeSlot.getName())) {
                timeSlotSetTimeSlotRelationship.setStartHour(NIGHT_START_HOUR);
                timeSlotSetTimeSlotRelationship.setEndHour(NIGHT_END_HOUR);
            }
            timeSlotSetTimeSlotRelationship.setTimeSlotSet(timeSlotSet);
            timeSlotSetTimeSlotRelationship.setTimeSlot(timeSlot);
            timeSlotSetTimeSlotRelationships.add(timeSlotSetTimeSlotRelationship);
        }
        return timeSlotSetTimeSlotRelationships;
    }

    public <T extends OrganizationBaseEntity> void createDefaultTimeSlots(T organization, List<TimeSlot> timeSlots) {
        logger.info("Creating default time slot for organization {}", organization.getName());
        if (timeSlots.isEmpty()){
            timeSlots = timeSlotRepository.findBySystemGeneratedTimeSlotsIsTrue();
        }
        List<TimeSlotSet> timeSlotSets = new ArrayList<>();
        timeSlotSets.add(new TimeSlotSet(TIME_SLOT_SET_NAME, LocalDate.now(), organization.getTimeSlotMode(), TimeSlotType.SHIFT_PLANNING));
        timeSlotSets.add(new TimeSlotSet(TIME_SLOT_SET_NAME, LocalDate.now(), organization.getTimeSlotMode(), TASK_PLANNING));
        List<TimeSlotSetTimeSlotRelationship> timeSlotSetTimeSlotRelationships = new ArrayList<>();
        timeSlotSetTimeSlotRelationships.addAll(setTimeSlotSet(timeSlots, timeSlotSets.get(0)));
        timeSlotSetTimeSlotRelationships.addAll(setTimeSlotSet(timeSlots, timeSlotSets.get(1)));
        timeSlotRelationshipGraphRepository.saveAll(timeSlotSetTimeSlotRelationships);
        organization.setTimeSlotSets(timeSlotSets);
        if(organization instanceof Organization) {
            organizationGraphRepository.save((Organization) organization);
        } else {
            unitGraphRepository.save((Unit) organization);
        }
    }



    /**
     * @param unitId
     * @return
     * @auther anil maurya
     */
    public List<TimeSlotWrapper> getCurrentTimeSlotOfUnit(Long unitId) {
        Unit unit = unitGraphRepository.findOne(unitId, 0);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_ID_NOTFOUND, unitId);
        }
        return timeSlotRepository.getTimeSlots(unit.getId(), unit.getTimeSlotMode());
    }

    public List<TimeSlotDTO> getUnitTimeSlot(Long unitId) {
        OrganizationBaseEntity unit = organizationBaseRepository.findOne(unitId, 0);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_ID_NOTFOUND, unitId);
        }
        List<TimeSlotWrapper> timeSlotWrappers = timeSlotRepository.getUnitTimeSlotsByType(unit.getId(), unit.getTimeSlotMode(), TimeSlotType.SHIFT_PLANNING);
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(timeSlotWrappers, TimeSlotDTO.class);
    }

    public List<TimeSlotDTO> getUnitTimeSlotByNames(Long unitId, Set<String> timeslotNames) {
        OrganizationBaseEntity unit = organizationBaseRepository.findOne(unitId, 0);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_ID_NOTFOUND, unitId);
        }
        List<TimeSlotWrapper> timeSlotWrappers = timeSlotRepository.getUnitTimeSlotsByIds(unit.getId(), timeslotNames, STANDARD, TimeSlotType.SHIFT_PLANNING);
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(timeSlotWrappers, TimeSlotDTO.class);
    }

    public Map<String, Object> getTimeSlotByUnitIdAndTimeSlotExternalId(Long unitId, Long kmdExternalId) {
        return timeSlotRepository.getTimeSlotByUnitIdAndTimeSlotExternalId(unitId, kmdExternalId);
    }

    public Map<String, Object> getTimeSlotByUnitIdAndTimeSlotId(Long unitId, Long timeSlotId) {
        return timeSlotRepository.getTimeSlotByUnitIdAndTimeSlotId(unitId, timeSlotId);
    }

    public List<TimeSlot> getTimeSlotsOfCountry() {
        return timeSlotRepository.findBySystemGeneratedTimeSlotsIsTrue();
    }


    public List<TimeSlotSet> getShiftPlanningTimeSlotSetsByUnit(Long unitId) {
        OrganizationBaseEntity unit = unitGraphRepository.findById(unitId, 0).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_ORGANISATION_NOTFOUND)));
        return timeSlotRepository.findTimeSlotSetsByOrganizationId(unitId, unit.getTimeSlotMode(), TimeSlotType.SHIFT_PLANNING);
    }

    public List<TimeSlotWrapper> getShiftPlanningTimeSlotsById(Long timeSlotSetId) {
        return timeSlotRepository.findTimeSlotsByTimeSlotSet(timeSlotSetId);
    }

    public List<TimeSlotDTO> getShiftPlanningTimeSlotByUnit(UnitSetting unitSetting) {
        return timeSlotRepository.findByUnitIdAndTimeSlotModeAndTimeSlotTypeOrderByStartDate(unitSetting.getUnitId(), unitSetting.getTimeSlotMode(), TimeSlotType.SHIFT_PLANNING).get(0).getTimeSlots();
    }

}
