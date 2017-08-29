package com.kairos.service.organization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.custom_exception.InvalidTimeSlotException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationTimeSlotRelationship;
import com.kairos.persistence.model.organization.TimeSlot;
import com.kairos.persistence.model.organization.TimeSlotDTO;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationTimeSlotGraphRepository;
import com.kairos.persistence.repository.organization.TimeSlotGraphRepository;
import com.kairos.service.UserBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.constants.AppConstants.*;

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
    private OrganizationTimeSlotGraphRepository organizationTimeSlotGraphRepository;

    public Map<String, Object> getTimeSlots(long unitId) {

        Organization organization = organizationGraphRepository.findOne(unitId,0);
        if(organization == null){
            throw new InternalError("Organization can not found");
        }
        return prepareTimeSlotResponse(organization);
    }

    public Object createTimeSlot(long unitId, TimeSlotDTO timeSlotDTO) {
        Organization unit = organizationGraphRepository.findOne(unitId);
        if(unit == null){
            return null;
        }
        if(timeSlotDTO.isShiftStartTime()){
            timeSlotGraphRepository.updateShiftStartTime(unitId, TimeSlot.TYPE.ADVANCE);
        }
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setName(timeSlotDTO.getName());
        timeSlot.setTimeSlotType(TimeSlot.TYPE.ADVANCE);
        ObjectMapper objectMapper = new ObjectMapper();
        Map resultMap = objectMapper.convertValue(timeSlotDTO, Map.class);
        OrganizationTimeSlotRelationship organizationTimeSlotRelationship = objectMapper.convertValue(timeSlotDTO,OrganizationTimeSlotRelationship.class);
        //validateTimeSlot(unitId,organizationTimeSlotRelationship,TimeSlot.TYPE.ADVANCE);
        organizationTimeSlotRelationship.setTimeSlot(timeSlot);
        organizationTimeSlotRelationship.setOrganization(unit);
        organizationTimeSlotRelationship.setShiftStartTime(timeSlotDTO.isShiftStartTime());
        save(organizationTimeSlotRelationship);
        resultMap.put("id",timeSlot.getId());
        return resultMap;
    }

    public Map<String, Object> updateTimeSlotType(long unitId,boolean standardTimeSlot){
        Organization organization = organizationGraphRepository.findOne(unitId);
        if(organization == null){
            throw new InternalError("Organization can not found");
        }
        organization.setStandardTimeSlot(standardTimeSlot);
        organizationGraphRepository.save(organization);
        return prepareTimeSlotResponse(organization);

    }

    public List<Map<String,Object>> updateTimeSlot(long unitId,List<TimeSlotDTO> timeSlotDTO){

        List<Map<String,Object>> response = new ArrayList<>();
        for(TimeSlotDTO objectToUpdate : timeSlotDTO){
            TimeSlot timeSlot = timeSlotGraphRepository.findOne(objectToUpdate.getId());
            if(timeSlot == null){
                return null;
            }
            if(objectToUpdate.isShiftStartTime()){
                timeSlotGraphRepository.updateShiftStartTime(unitId,timeSlot.getTimeSlotType());
            }
            ObjectMapper objectMapper = new ObjectMapper();
            OrganizationTimeSlotRelationship organizationTimeSlotRelationship = objectMapper.convertValue(objectToUpdate,OrganizationTimeSlotRelationship.class);
            organizationTimeSlotRelationship.setId(timeSlot.getId());
            //validateTimeSlot(unitId,organizationTimeSlotRelationship,timeSlot.getTimeSlotType());

            Map<String,Object> queryResponse;
            if(timeSlot.getTimeSlotType().equals(TimeSlot.TYPE.ADVANCE)){
                queryResponse = timeSlotGraphRepository.updateAdvanceTimeSlot(unitId,timeSlot.getId(),objectToUpdate.getName(),objectToUpdate.getStartHour(),
                        objectToUpdate.getStartMinute(),objectToUpdate.getEndHour(),objectToUpdate.getEndMinute(),objectToUpdate.isShiftStartTime());
                response.add(queryResponse);
            } else {
                queryResponse = timeSlotGraphRepository.updateStandardTimeSlot(unitId,timeSlot.getId(),objectToUpdate.getStartHour(),
                        objectToUpdate.getStartMinute(),objectToUpdate.getEndHour(),objectToUpdate.getEndMinute(),objectToUpdate.isShiftStartTime());
                response.add(queryResponse);
            }
        }
        return response;
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
        if(unit.isStandardTimeSlot()){
            List<Map<String,Object>> standredTimeSlots = timeSlotGraphRepository.getTimeSlots(unit.getId(), TimeSlot.TYPE.STANDARD);
            timeSlots = new ArrayList<>(standredTimeSlots.size());
            for(Map<String,Object> standredTimeSlot : standredTimeSlots){
                timeSlots.add((Map<String,Object>) standredTimeSlot.get("timeSlot"));
            }
        } else {
            List<Map<String,Object>> advanceTimeSlots = timeSlotGraphRepository.getTimeSlots(unit.getId(), TimeSlot.TYPE.ADVANCE);
            timeSlots = new ArrayList<>(advanceTimeSlots.size());
            for(Map<String,Object> standredTimeSlot : advanceTimeSlots){
                timeSlots.add((Map<String,Object>) standredTimeSlot.get("timeSlot"));
            }
        }
        Map<String,Object> response = new HashMap<>();
        response.put("timeSlots",timeSlots);
        response.put("standardTimeSlot",unit.isStandardTimeSlot());
        return response;
    }

    public void createDefaultTimeSlots(Organization organization){

        List<TimeSlot> timeSlots = timeSlotGraphRepository.findByTimeSlotType(TimeSlot.TYPE.STANDARD);
        for(TimeSlot timeSlot :timeSlots){
            OrganizationTimeSlotRelationship organizationTimeSlotRelationship = new OrganizationTimeSlotRelationship();
            if(DAY.equals(timeSlot.getName())){
                organizationTimeSlotRelationship.setStartHour(DAY_START_HOUR);
                organizationTimeSlotRelationship.setEndHour(DAY_END_HOUR);
                organizationTimeSlotRelationship.setShiftStartTime(true);
            } else if(EVENING.equals(timeSlot.getName())){
                organizationTimeSlotRelationship.setStartHour(EVENING_START_HOUR);
                organizationTimeSlotRelationship.setEndHour(EVENING_END_HOUR);
            }  else if(NIGHT.equals(timeSlot.getName())){
                organizationTimeSlotRelationship.setStartHour(NIGHT_START_HOUR);
                organizationTimeSlotRelationship.setEndHour(NIGHT_END_HOUR);
            }
            organizationTimeSlotRelationship.setOrganization(organization);
            organizationTimeSlotRelationship.setTimeSlot(timeSlot);
            save(organizationTimeSlotRelationship);
        }
    }

    private void validateTimeSlot(long unitId,OrganizationTimeSlotRelationship objToCreate,TimeSlot.TYPE type){

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

        List<Map<String,Object>> organizationTimeSlotRelationships = organizationTimeSlotGraphRepository.getOrganizationTimeSlots(unitId, type);
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
    }

    /**
     * @auther anil maurya
     * @param unitId
     * @return
     */
    public List<Map<String,Object>> getCurrentTimeSlotOfUnit(Long unitId){
        List<Map<String,Object>> currentTimeSlots= timeSlotGraphRepository.getUnitCurrentTimeSlots(unitId);

        return currentTimeSlots;
    }

    public Map<String,Object> getTimeSlotByUnitIdAndTimeSlotName(Long unitId, String timeSlotName){
        System.out.println("request received in timeslot service "+unitId+ " timeSlotNam e"+timeSlotName);
        Map<String,Object> timeSlot = timeSlotGraphRepository.getTimeSlotByUnitIdAndTimeSlotName(unitId, timeSlotName);
        return timeSlot;
    }
}
