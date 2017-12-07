package com.kairos.service.organization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.UnitNotFoundException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.time_slot.TimeSlot;
import com.kairos.persistence.model.organization.time_slot.TimeSlotSet;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.time_slot.TimeSlotGraphRepository;
import com.kairos.persistence.repository.organization.time_slot.TimeSlotSetRepository;
import com.kairos.response.dto.web.organization.time_slot.TimeSlotDTO;
import com.kairos.response.dto.web.organization.time_slot.TimeSlotSetDTO;
import com.kairos.service.UserBaseService;
import com.kairos.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.*;
import static com.kairos.persistence.model.enums.time_slot.TimeSlotMode.ADVANCE;
import static com.kairos.persistence.model.enums.time_slot.TimeSlotMode.STANDARD;

/**
 * Created by oodles on 18/10/16.
 */
@Transactional
@Service
public class TimeSlotService extends UserBaseService {
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private TimeSlotGraphRepository timeSlotGraphRepository;
    @Inject
    private TimeSlotSetRepository timeSlotSetRepository;

    private static final Logger logger = LoggerFactory.getLogger(TimeSlotService.class);

    public Map<String, Object> getTimeSlots(long unitId) {

        Organization organization = organizationGraphRepository.findOne(unitId,0);
        if(organization == null){
            throw new InternalError("Organization can not found");
        }
        return prepareTimeSlotResponse(organization);
    }

    public List<TimeSlot> getTimeSlotByTimeSlotSet(Long timeSlotSetId){
        return timeSlotGraphRepository.findTimeSlotsByTimeSlotSet(timeSlotSetId);
    }

    public List<TimeSlotSet> getTimeSlotSets(Long unitId) {
        Organization organization = organizationGraphRepository.findOne(unitId,0);
        if(organization == null){
            throw new InternalError("Organization can not found");
        }
        return timeSlotGraphRepository.findTimeSlotsByOrganizationId(unitId,organization.getTimeSlotMode());
    }

    public TimeSlotSet createTimeSlot(long unitId, TimeSlotSetDTO timeSlotSetDTO) {
        Organization unit = organizationGraphRepository.findOne(unitId,0);
        if(!Optional.ofNullable(unit).isPresent()){
            throw new InternalError("Unit is not present");
        }
        TimeSlotSet timeSlotSet = new TimeSlotSet(timeSlotSetDTO.getName(),timeSlotSetDTO.getStartDate());
        timeSlotSet.setEndDate(timeSlotSetDTO.getEndDate());
        ObjectMapper objectMapper = new ObjectMapper();
        List<TimeSlot> timeSlots = timeSlotSetDTO.getTimeSlots().stream().map(timeSlot -> objectMapper.convertValue(timeSlot,TimeSlot.class)).collect(Collectors.toList());
        timeSlotSet.setTimeSlots(timeSlots);
        List<TimeSlotSet> timeSlotSets = unit.getTimeSlotSets();
        timeSlotSets.add(timeSlotSet);
        unit.setTimeSlotSets(timeSlotSets);
        save(unit);
        return timeSlotSet;
    }

    public Map<String, Object> updateTimeSlotType(long unitId,boolean standardTimeSlot){
        Organization organization = organizationGraphRepository.findOne(unitId);
        if(organization == null){
            throw new InternalError("Organization can not found");
        }
        organization.setTimeSlotMode((standardTimeSlot)? STANDARD:ADVANCE);
        organizationGraphRepository.save(organization);
        return prepareTimeSlotResponse(organization);

    }

    public Iterable<TimeSlot> updateTimeSlot(List<TimeSlotDTO> timeSlotDTOS){
        List<Long> idsOfTimeSlotToUpdate = timeSlotDTOS.stream().map(timeSlotDTO->timeSlotDTO.getId()).collect(Collectors.toList());
        Iterable<TimeSlot> timeSlotsToUpdate = timeSlotGraphRepository.findAll(idsOfTimeSlotToUpdate);
        Iterator<TimeSlot> timeSlotIterator = timeSlotsToUpdate.iterator();
        while (timeSlotIterator.hasNext()){
            TimeSlot timeSlotToUpdate = timeSlotIterator.next();
            Optional<TimeSlotDTO> result = timeSlotDTOS.stream().filter(element -> element.getId().equals(timeSlotToUpdate.getId())).findFirst();
            if(result.isPresent()){
                TimeSlotDTO timeSlotDTO = result.get();
                timeSlotToUpdate.updateTimeSlot(timeSlotDTO);
            }
        }
        return timeSlotGraphRepository.save(timeSlotsToUpdate);
    }

    public List<TimeSlotSet> updateTimeSlotSet(Long unitId,Long timeSlotSetId,TimeSlotSetDTO timeSlotSetDTO){
        Organization unit = organizationGraphRepository.findOne(unitId,0);
        if(!Optional.ofNullable(unit).isPresent()){
            throw new UnitNotFoundException("Invalid unit id ");
        }
        TimeSlotSet timeSlotSet = timeSlotSetRepository.findOne(timeSlotSetId);
        if(!Optional.ofNullable(timeSlotSet).isPresent()){
            logger.error("Invalid time slot id " + timeSlotSetId);
            throw new DataNotFoundByIdException("Invalid time slot id");
        }
        List<TimeSlotSet> timeSlotSetsToValidate = timeSlotSetRepository.findByStartDateBetween(timeSlotSet.getEndDate(),timeSlotSetDTO.getEndDate(),
                new Sort(Sort.Direction.ASC,"startDate"));
        List<TimeSlotSet> timeSlotSetsToUpdate = new ArrayList<>();
        for(TimeSlotSet timeSlotSetToValidate : timeSlotSetsToValidate){
            if(timeSlotSetToValidate.getEndDate().compareTo(timeSlotSetDTO.getEndDate()) <=0){
                timeSlotSetToValidate.setDeleted(true);
                timeSlotSetsToUpdate.add(timeSlotSetToValidate);
            } else {
                LocalDate dateAsLocalDate = DateUtil.asLocalDate(timeSlotSetDTO.getEndDate());
                timeSlotSetToValidate.setStartDate(DateUtil.asDate(dateAsLocalDate.plusDays(1)));
                timeSlotSetsToUpdate.add(timeSlotSetToValidate);
                break;
            }
        }
        timeSlotSet.updateTimeSlotSet(timeSlotSetDTO);
        timeSlotSetsToUpdate.add(timeSlotSet);
        timeSlotSetRepository.save(timeSlotSetsToUpdate);
        return timeSlotSetsToUpdate;
    }

    public boolean deleteTimeSlotSet(Long unitId,Long timeSlotSetId){
        Organization unit = organizationGraphRepository.findOne(unitId,0);
        if(!Optional.ofNullable(unit).isPresent()){
            throw new UnitNotFoundException("Invalid unit id ");
        }
        TimeSlotSet timeSlotSetToDelete = timeSlotSetRepository.findOne(timeSlotSetId);
        if(!Optional.ofNullable(timeSlotSetToDelete).isPresent()){
            logger.error("Invalid time slot id " + timeSlotSetId);
            throw new DataNotFoundByIdException("Invalid time slot id");
        }
        TimeSlotSet timeSlotSet = timeSlotSetRepository.findByStartDateAfter(timeSlotSetToDelete.getEndDate(),
                new PageRequest(0,1,new Sort(Sort.DEFAULT_DIRECTION,"startDate")));
        if(Optional.ofNullable(timeSlotSet).isPresent()){
            timeSlotSet.setStartDate(timeSlotSetToDelete.getEndDate());
            save(timeSlotSet);
        }
        timeSlotSetToDelete.setDeleted(true);
        return true;
    }

    public boolean deleteTimeSlot(long unitId,long timeSlotId){
        TimeSlot timeSlot= timeSlotGraphRepository.findOne(timeSlotId);
        if(timeSlot == null){
            return false;
        }
        return timeSlotGraphRepository.deleteTimeSlot(unitId,timeSlotId);
    }

    private Map<String,Object> prepareTimeSlotResponse(Organization unit){
        List<Map<String,Object>> timeSlots;
        if(STANDARD.equals(unit.getTimeSlotMode())){
            List<Map<String,Object>> standredTimeSlots = timeSlotGraphRepository.getTimeSlots(unit.getId(), STANDARD);
            timeSlots = new ArrayList<>(standredTimeSlots.size());
            for(Map<String,Object> standredTimeSlot : standredTimeSlots){
                timeSlots.add((Map<String,Object>) standredTimeSlot.get("timeSlot"));
            }
        } else {
            List<Map<String,Object>> advanceTimeSlots = timeSlotGraphRepository.getTimeSlots(unit.getId(), ADVANCE);
            timeSlots = new ArrayList<>(advanceTimeSlots.size());
            for(Map<String,Object> standredTimeSlot : advanceTimeSlots){
                timeSlots.add((Map<String,Object>) standredTimeSlot.get("timeSlot"));
            }
        }
        Map<String,Object> response = new HashMap<>();
        response.put("timeSlots",timeSlots);
        response.put("standardTimeSlot",unit.getTimeSlotMode().equals(STANDARD)?true:false);
        return response;
    }

    public void createDefaultTimeSlots(Organization organization){
        List<TimeSlot> timeSlots = new ArrayList<>();
        for(String timeSlotType :Arrays.asList(DAY,EVENING,NIGHT)){
            TimeSlot timeSlot;
            if(DAY.equals(timeSlotType)){
                timeSlot = new TimeSlot(timeSlotType,DAY_START_HOUR,DAY_END_HOUR, STANDARD);
                timeSlot.setShiftStartTime(true);
            } else if(EVENING.equals(timeSlotType)){
                timeSlot = new TimeSlot(timeSlotType,EVENING_START_HOUR,EVENING_END_HOUR, STANDARD);
            }  else if(NIGHT.equals(timeSlotType)){
                timeSlot = new TimeSlot(timeSlotType,NIGHT_START_HOUR,NIGHT_END_HOUR, STANDARD);
            } else {
                throw new InternalError("Invalid time slot value ");
            }
            timeSlots.add(timeSlot);
        }
        TimeSlotSet timeSlotSet = new TimeSlotSet(TIME_SLOT_SET_NAME, new Date());
        timeSlotSet.setTimeSlots(timeSlots);
        List<TimeSlotSet> timeSlotSets = organization.getTimeSlotSets();
        timeSlotSets.add(timeSlotSet);
        organization.setTimeSlotSets(timeSlotSets);
        save(organization);
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
     * @auther anil maurya
     * @param unitId
     * @return
     */
    public List<Map<String,Object>> getCurrentTimeSlotOfUnit(Long unitId){
        List<Map<String,Object>> currentTimeSlots= timeSlotGraphRepository.getUnitCurrentTimeSlots(unitId);

        return currentTimeSlots;
    }

    public Map<String,Object> getTimeSlotByUnitIdAndTimeSlotExternalId(Long unitId, Long kmdExternalId){
        Map<String,Object> timeSlot = timeSlotGraphRepository.getTimeSlotByUnitIdAndTimeSlotExternalId(unitId, kmdExternalId);
        return timeSlot;
    }

    public Map<String, Object> getTimeSlotByUnitIdAndTimeSlotId(Long unitId, Long timeSlotId){
        Map<String, Object> timeSlotMap = timeSlotGraphRepository.getTimeSlotByUnitIdAndTimeSlotId(unitId, timeSlotId);
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
}
