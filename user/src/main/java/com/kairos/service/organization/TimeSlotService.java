package com.kairos.service.organization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.enums.TimeSlotType;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.time_slot.TimeSlot;
import com.kairos.persistence.model.organization.time_slot.TimeSlotSet;
import com.kairos.persistence.model.organization.time_slot.TimeSlotSetTimeSlotRelationship;
import com.kairos.persistence.model.organization.time_slot.TimeSlotWrapper;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.time_slot.TimeSlotGraphRepository;
import com.kairos.persistence.repository.organization.time_slot.TimeSlotRelationshipGraphRepository;
import com.kairos.persistence.repository.organization.time_slot.TimeSlotSetRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotSetDTO;
import com.kairos.utils.DateUtil;
import com.kairos.commons.utils.ObjectMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.constants.AppConstants.*;
import static com.kairos.enums.time_slot.TimeSlotMode.ADVANCE;
import static com.kairos.enums.time_slot.TimeSlotMode.STANDARD;

/**
 * Created by oodles on 18/10/16.
 */
@Transactional
@Service
public class TimeSlotService {
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private TimeSlotGraphRepository timeSlotGraphRepository;
    @Inject
    private TimeSlotSetRepository timeSlotSetRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private TimeSlotRelationshipGraphRepository timeSlotRelationshipGraphRepository;
    private static final Logger logger = LoggerFactory.getLogger(TimeSlotService.class);

    public Map<String, Object> getTimeSlots(long unitId) {

        Organization organization = organizationGraphRepository.findOne(unitId, 0);
        if (organization == null) {
            exceptionService.dataNotFoundByIdException("message.organisation.notFound");

        }
        return prepareTimeSlotResponse(organization);
    }

    public List<TimeSlotWrapper> getTimeSlotByTimeSlotSet(Long timeSlotSetId) {
        return timeSlotGraphRepository.findTimeSlotsByTimeSlotSet(timeSlotSetId);
    }

    public Map<String, Object> getTimeSlotSets(Long unitId) {
        Organization organization = organizationGraphRepository.findOne(unitId, 0);
        if (organization == null) {
            exceptionService.dataNotFoundByIdException("message.organisation.notFound");
        }
        List<TimeSlotSet> timeSlotSets = timeSlotGraphRepository.findTimeSlotSetsByOrganizationId(unitId, organization.getTimeSlotMode(), TimeSlotType.TASK_PLANNING);
        Map<String, Object> timeSlotSetData = new HashMap<>();
        timeSlotSetData.put("timeSlotSets", timeSlotSets);
        timeSlotSetData.put("standardTimeSlot", STANDARD.equals(organization.getTimeSlotMode()) ? true : false);
        return timeSlotSetData;
    }

    public TimeSlotSet createTimeSlotSet(long unitId, TimeSlotSetDTO timeSlotSetDTO) {
        Organization unit = organizationGraphRepository.findOne(unitId, 0);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unit.id.notFound", unitId);

        }
        TimeSlotSet timeSlotSet = new TimeSlotSet(timeSlotSetDTO.getName(), timeSlotSetDTO.getStartDate(), unit.getTimeSlotMode());
        timeSlotSet.setEndDate(timeSlotSetDTO.getEndDate());
        saveTimeSlots(timeSlotSetDTO, timeSlotSet);
        List<TimeSlotSet> timeSlotSets = unit.getTimeSlotSets();
        timeSlotSets.add(timeSlotSet);
        unit.setTimeSlotSets(timeSlotSets);
        organizationGraphRepository.save(unit);
        return timeSlotSet;
    }

    public TimeSlotDTO createTimeSlot(Long timeSlotSetId, TimeSlotDTO timeSlotDTO) {
        TimeSlotSet timeSlotSet = timeSlotSetRepository.findOne(timeSlotSetId);
        if (!Optional.ofNullable(timeSlotSet).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.timeslot.id.notfound");

        }
        TimeSlot timeSlot = new TimeSlot(timeSlotDTO.getName());
        ObjectMapper objectMapper = new ObjectMapper();
        TimeSlotSetTimeSlotRelationship timeSlotSetTimeSlotRelationship = objectMapper.convertValue(timeSlotDTO, TimeSlotSetTimeSlotRelationship.class);
        timeSlotSetTimeSlotRelationship.setTimeSlot(timeSlot);
        timeSlotSetTimeSlotRelationship.setTimeSlotSet(timeSlotSet);
        timeSlotRelationshipGraphRepository.save(timeSlotSetTimeSlotRelationship);
        timeSlotDTO.setId(timeSlot.getId());
        return timeSlotDTO;
    }

    private void saveTimeSlots(TimeSlotSetDTO timeSlotSetDTO, TimeSlotSet timeSlotSet) {

        ObjectMapper objectMapper = new ObjectMapper();
        List<TimeSlotSetTimeSlotRelationship> timeSlotSetTimeSlotRelationships = new ArrayList<>();
        for (TimeSlotDTO timeSlotDTO : timeSlotSetDTO.getTimeSlots()) {

            TimeSlot timeSlot = (Optional.ofNullable(timeSlotDTO.getId()).isPresent()) ?
                    timeSlotGraphRepository.findOne(timeSlotDTO.getId()) : new TimeSlot(timeSlotDTO.getName());
            if (timeSlot == null) {
                exceptionService.dataNotFoundByIdException("message.timeslot.id.notfound");

            }
            TimeSlotSetTimeSlotRelationship timeSlotSetTimeSlotRelationship = objectMapper.convertValue
                    (timeSlotDTO, TimeSlotSetTimeSlotRelationship.class);
            timeSlotSetTimeSlotRelationship.setId(null);
            timeSlotSetTimeSlotRelationship.setTimeSlotSet(timeSlotSet);
            timeSlotSetTimeSlotRelationship.setTimeSlot(timeSlot);
            timeSlotSetTimeSlotRelationships.add(timeSlotSetTimeSlotRelationship);
        }
        timeSlotRelationshipGraphRepository.saveAll(timeSlotSetTimeSlotRelationships);
    }

    public List<TimeSlotSet> updateTimeSlotSet(Long unitId, Long timeSlotSetId, TimeSlotSetDTO timeSlotSetDTO) {
        Organization unit = organizationGraphRepository.findOne(unitId, 0);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.unitNotFoundException("message.unit.id.notFound", unitId);

        }
        TimeSlotSet timeSlotSet = timeSlotSetRepository.findOne(timeSlotSetId);
        if (!Optional.ofNullable(timeSlotSet).isPresent()) {
            logger.error("Invalid time slot id " + timeSlotSetId);
            exceptionService.dataNotFoundByIdException("message.timeslot.id.notfound");
        }
        List<TimeSlotSet> timeSlotSetsToValidate = timeSlotSetRepository.findTimeSlotSetByStartDateBetween(unitId, timeSlotSet.getEndDate(),
                timeSlotSetDTO.getEndDate(), timeSlotSet.getTimeSlotType());
        List<TimeSlotSet> timeSlotSetsToUpdate = new ArrayList<>();
        for (TimeSlotSet timeSlotSetToValidate : timeSlotSetsToValidate) {

            if (timeSlotSetToValidate.getEndDate().compareTo(timeSlotSetDTO.getEndDate()) <= 0) {
                timeSlotSetToValidate.setDeleted(true);
                timeSlotSetsToUpdate.add(timeSlotSetToValidate);
            } else {
                LocalDate dateAsLocalDate = timeSlotSetDTO.getEndDate();
                timeSlotSetToValidate.setStartDate(dateAsLocalDate.plusDays(1));
                timeSlotSetsToUpdate.add(timeSlotSetToValidate);
                break;
            }
        }
        updateTimeSlot(timeSlotSetDTO.getTimeSlots(), timeSlotSet.getId());
        timeSlotSet.updateTimeSlotSet(timeSlotSetDTO);
        timeSlotSet.setName(timeSlotSetDTO.getName());
        timeSlotSet.setEndDate(timeSlotSetDTO.getEndDate());
        timeSlotSetsToUpdate.add(timeSlotSet);
        timeSlotSetRepository.saveAll(timeSlotSetsToUpdate);
        return timeSlotSetsToUpdate;
    }


    public Map<String, Object> updateTimeSlotType(long unitId, boolean standardTimeSlot) {
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (organization == null) {
            exceptionService.dataNotFoundByIdException("message.organisation.notFound");

        }
        organization.setTimeSlotMode((standardTimeSlot) ? STANDARD : ADVANCE);
        organizationGraphRepository.save(organization);
        return getTimeSlotSets(unitId);

    }

    public List<TimeSlotDTO> updateTimeSlot(List<TimeSlotDTO> timeSlotDTOS, Long timeSlotSetId) {

        TimeSlotSet timeSlotSet = timeSlotSetRepository.findOne(timeSlotSetId);
        if (timeSlotSet == null) {
            exceptionService.dataNotFoundByIdException("message.timeslot.id.notfound");

        }

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
            timeSlotGraphRepository.updateTimeSlot(timeSlotSetId, timeSlotDTO.getId(), timeSlotDTO.getName(), timeSlotDTO.getStartHour(),
                    timeSlotDTO.getStartMinute(), timeSlotDTO.getEndHour(), timeSlotDTO.getEndMinute(), timeSlotDTO.isShiftStartTime());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        List<TimeSlotSetTimeSlotRelationship> timeSlotSetTimeSlotRelationships = new ArrayList<>();
        for (TimeSlotDTO timeSlotDTO : timeSlotsToCreate) {

            TimeSlot timeSlot = new TimeSlot(timeSlotDTO.getName());
            TimeSlotSetTimeSlotRelationship timeSlotSetTimeSlotRelationship = objectMapper.convertValue
                    (timeSlotDTO, TimeSlotSetTimeSlotRelationship.class);
            timeSlotSetTimeSlotRelationship.setId(null);
            timeSlotSetTimeSlotRelationship.setTimeSlotSet(timeSlotSet);
            timeSlotSetTimeSlotRelationship.setTimeSlot(timeSlot);
            timeSlotSetTimeSlotRelationships.add(timeSlotSetTimeSlotRelationship);
        }
        timeSlotRelationshipGraphRepository.saveAll(timeSlotSetTimeSlotRelationships);

        List<TimeSlotDTO> newCreatedTimeSlots = new ArrayList<>();
        for (TimeSlotSetTimeSlotRelationship timeSlotSetTimeSlotRelationship : timeSlotSetTimeSlotRelationships) {
            TimeSlotDTO timeSlotDTO = objectMapper.convertValue(timeSlotSetTimeSlotRelationship, TimeSlotDTO.class);
            timeSlotDTO.setId(timeSlotSetTimeSlotRelationship.getTimeSlot().getId());
            newCreatedTimeSlots.add(timeSlotDTO);
        }
        return newCreatedTimeSlots;
    }

    public boolean deleteTimeSlotSet(Long unitId, Long timeSlotSetId) {
        Organization unit = organizationGraphRepository.findOne(unitId, 0);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.unitNotFoundException("message.unit.id.notFound", unitId);

        }
        TimeSlotSet timeSlotSetToDelete = timeSlotSetRepository.findOne(timeSlotSetId);
        if (!Optional.ofNullable(timeSlotSetToDelete).isPresent()) {
            logger.error("Invalid time slot id " + timeSlotSetId);
            exceptionService.dataNotFoundByIdException("message.timeslot.id.notfound");

        }
        TimeSlotSet timeSlotSet = timeSlotSetRepository.findOneByStartDateAfter(unitId, timeSlotSetToDelete.getEndDate());
        if (Optional.ofNullable(timeSlotSet).isPresent()) {
            timeSlotSet.setStartDate(timeSlotSetToDelete.getEndDate());
            timeSlotSetRepository.save(timeSlotSet);
        }
        timeSlotSetToDelete.setDeleted(true);
        timeSlotSetRepository.save(timeSlotSetToDelete);
        return true;
    }

    public boolean deleteTimeSlot(long timeSlotId, Long timeSlotSetId) {
        timeSlotGraphRepository.deleteTimeSlot(timeSlotSetId, timeSlotId);
        return true;
    }

    private Map<String, Object> prepareTimeSlotResponse(Organization unit) {
        List<TimeSlotWrapper> timeSlotWrappers = timeSlotGraphRepository.getTimeSlots(unit.getId(), unit.getTimeSlotMode(),LocalDate.now());
        Map<String, Object> response = new HashMap<>();
        response.put("timeSlots", timeSlotWrappers);
        response.put("standardTimeSlot", STANDARD.equals(unit.getTimeSlotMode()) ? true : false);
        response.put("timeZone", unit.getTimeZone() != null ? unit.getTimeZone().getId() : null);
        return response;
    }

    public void createDefaultTimeSlots(Organization organization, TimeSlotType timeSlotType) {
        List<TimeSlot> timeSlots = timeSlotGraphRepository.findBySystemGeneratedTimeSlotsIsTrue();
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

    public void createDefaultTimeSlots(Organization organization, List<TimeSlot> timeSlots) {
        logger.info("Creating default time slot for organization "+organization.getName());
        if (timeSlots.isEmpty()){
             timeSlots = timeSlotGraphRepository.findBySystemGeneratedTimeSlotsIsTrue();
        }
        List<TimeSlotSet> timeSlotSets = new ArrayList<>();
        timeSlotSets.add(new TimeSlotSet(TIME_SLOT_SET_NAME, LocalDate.now(), organization.getTimeSlotMode(), TimeSlotType.SHIFT_PLANNING));
        timeSlotSets.add(new TimeSlotSet(TIME_SLOT_SET_NAME, LocalDate.now(), organization.getTimeSlotMode(), TimeSlotType.TASK_PLANNING));
        List<TimeSlotSetTimeSlotRelationship> timeSlotSetTimeSlotRelationships = new ArrayList<>();
        timeSlotSetTimeSlotRelationships.addAll(setTimeSlotSet(timeSlots, timeSlotSets.get(0)));
        timeSlotSetTimeSlotRelationships.addAll(setTimeSlotSet(timeSlots, timeSlotSets.get(1)));
        timeSlotRelationshipGraphRepository.saveAll(timeSlotSetTimeSlotRelationships);
        organization.setTimeSlotSets(timeSlotSets);
        organizationGraphRepository.save(organization);
    }

    /*private void validateTimeSlot(long unitId,OrganizationTimeSlotRelationship objToCreate,TimeSlot.TimeSlotMode timeSlotMode){

        Calendar startSlot = Calendar.getInstance();
        startSlot.set(Calendar.HOUR_OF_DAY,objToCreate.getStartHour());
        startSlot.set(Calendar.MINUTE,objToCreate.getStartMinute());

        Calendar endSlot = Calendar.getInstance();
        endSlot.set(Calendar.HOUR_OF_DAY,objToCreate.getEndHour());
        endSlot.set(Calendar.MINUTE,objToCreate.getEndMinute());

        if(objToCreate.getStartHour() > objToCreate.getEndHour()){
            endSlot.add(Calendar.DATE,1);
        }
        long diff = endSlot.getTime().getTime() - startSlot.getTime().getTime();
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;

        long totalDiff = diffHours * 60 * 60 + diffMinutes * 60 + diffSeconds;

        List<Map<String,Object>> organizationTimeSlotRelationships = organizationTimeSlotGraphRepository.getOrganizationTimeSlots(unitId, timeSlotMode);
        ObjectMapper objectMapperForQueryResult = new ObjectMapper();
        OrganizationTimeSlotRelationship  queryResult;
        Calendar dbObjStartSlot;
        Calendar dbObjEndSlot;
        for(Map<String,Object> map : organizationTimeSlotRelationships){
            queryResult = objectMapperForQueryResult.convertValue(map.get("data"),OrganizationTimeSlotRelationship.class);

            if(!queryResult.getId().equals(objToCreate.getId())){
                dbObjStartSlot = Calendar.getInstance();
                dbObjStartSlot.set(Calendar.HOUR_OF_DAY,queryResult.getStartHour());
                dbObjStartSlot.set(Calendar.MINUTE,queryResult.getStartMinute());

                dbObjEndSlot = Calendar.getInstance();
                dbObjEndSlot.set(Calendar.HOUR_OF_DAY,queryResult.getEndHour());
                dbObjEndSlot.set(Calendar.MINUTE,queryResult.getEndMinute());
                if(queryResult.getStartHour() > queryResult.getEndHour()){
                    dbObjEndSlot.add(Calendar.DATE,1);
                }
                long diffInTime = dbObjEndSlot.getTime().getTime() - dbObjStartSlot.getTime().getTime();
                long diffHoursOfQueryObj = diffInTime / (60 * 60 * 1000) % 24;
                long diffSecondsOfQueryObj = diffInTime / 1000 % 60;
                long diffMinutesOfQueryObj = diffInTime / (60 * 1000) % 60;
                totalDiff = totalDiff + diffHoursOfQueryObj * 60 * 60 + diffMinutesOfQueryObj * 60 + diffSecondsOfQueryObj;
            }
        }
        if(totalDiff > 86400){
            throw new InvalidTimeSlotException("Time slot cannot grater then 24");
        }
    }*/

    /**
     * @param unitId
     * @return
     * @auther anil maurya
     */
    public List<TimeSlotWrapper> getCurrentTimeSlotOfUnit(Long unitId) {
        Organization unit = organizationGraphRepository.findOne(unitId, 0);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unit.id.notFound", unitId);

        }
        List<TimeSlotWrapper> timeSlotWrappers = timeSlotGraphRepository.getTimeSlots(unit.getId(), unit.getTimeSlotMode(),LocalDate.now());
        return timeSlotWrappers;
    }

    public Map<String, Object> getTimeSlotByUnitIdAndTimeSlotExternalId(Long unitId, Long kmdExternalId) {
        Map<String, Object> timeSlot = timeSlotGraphRepository.getTimeSlotByUnitIdAndTimeSlotExternalId(unitId, kmdExternalId);
        return timeSlot;
    }

    public Map<String, Object> getTimeSlotByUnitIdAndTimeSlotId(Long unitId, Long timeSlotId) {
        Map<String, Object> timeSlotMap = timeSlotGraphRepository.getTimeSlotByUnitIdAndTimeSlotId(unitId, timeSlotId, LocalDate.now());
        return timeSlotMap;
    }


    //@prabjot
    //TODO for now i dont know the timeslot set for kmd, will implement it later
    /*public TimeSlot importTimeSlotsFromKMD(Organization unit, KMDTimeSlotDTO kmdTimeSlotDTO){
        try {
            TimeSlot timeSlot = timeSlotGraphRepository.findByKmdExternalId(kmdTimeSlotDTO.getId());
            if (unit == null) {
                throw new InternalError("Couldn't find any  organization");
            }
            if (!Optional.ofNullable(timeSlot).isPresent()) {
                timeSlot = new TimeSlot();
            }

            timeSlot.setName(kmdTimeSlotDTO.getTitle());
            timeSlot.setTimeSlotTimeSlotMode(TimeSlot.TimeSlotMode.ADVANCE);
            timeSlot.setKmdExternalId(kmdTimeSlotDTO.getId());
            timeSlotGraphRepository.removeTimeSlotExistByUnitIdAndTimeSlotId(unit.getId(), kmdTimeSlotDTO.getId());
            Boolean hasTimeSlotForGivenUnit = timeSlotGraphRepository.hasTimeSlotExistByUnitIdAndTimeSlotId(unit.getId(), kmdTimeSlotDTO.getId());
            if (!hasTimeSlotForGivenUnit) {

                OrganizationTimeSlotRelationship organizationTimeSlotRelationship = new OrganizationTimeSlotRelationship();
                //validateTimeSlot(unitId,organizationTimeSlotRelationship,TimeSlot.TimeSlotMode.ADVANCE);
                LocalTime startDuration = LocalTime.parse(kmdTimeSlotDTO.getStart());
                LocalTime endDuration = LocalTime.parse(kmdTimeSlotDTO.getEnd());
                organizationTimeSlotRelationship.setEnabled(true);
                organizationTimeSlotRelationship.setStartHour(startDuration.getHour());
                organizationTimeSlotRelationship.setStartMinute(startDuration.getMinute());
                organizationTimeSlotRelationship.setEndHour(endDuration.getHour());
                organizationTimeSlotRelationship.setEndMinute(endDuration.getMinute());
                organizationTimeSlotRelationship.setTimeSlot(timeSlot);
                organizationTimeSlotRelationship.setOrganization(unit);
                save(organizationTimeSlotRelationship);

            }
            return timeSlot;
        }catch (Exception exception){
            logger.error("Exception while importing time slot from KMD", exception);
            return null;
        }

    }*/


    public List<TimeSlot> getTimeSlotsOfCountry(Long countryId) {
        return timeSlotGraphRepository.findBySystemGeneratedTimeSlotsIsTrue();
    }


    public List<TimeSlotSet> getShiftPlanningTimeSlotSetsByUnit(Long unitId) {
        Organization organization = organizationGraphRepository.findById(unitId, 0).get();
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organisation.notFound");

        }
        return timeSlotGraphRepository.findTimeSlotSetsByOrganizationId(unitId, organization.getTimeSlotMode(), TimeSlotType.SHIFT_PLANNING);
    }

    public List<TimeSlotWrapper> getShiftPlanningTimeSlotsById(Long timeSlotSetId) {
        return timeSlotGraphRepository.findTimeSlotsByTimeSlotSet(timeSlotSetId);
    }

    public List<TimeSlotDTO> getShiftPlanningTimeSlotByUnit(Organization organization) {
        List<TimeSlotSet> timeSlotSets = timeSlotGraphRepository.findTimeSlotSetsByOrganizationId(organization.getId(), organization.getTimeSlotMode(), TimeSlotType.SHIFT_PLANNING);
        List<TimeSlotWrapper> timeSlotWrappers = timeSlotGraphRepository.findTimeSlotsByTimeSlotSet(timeSlotSets.get(0).getId());
        List<TimeSlotDTO> timeSlotDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(timeSlotWrappers, TimeSlotDTO.class);
        return timeSlotDTOS;
    }

}
