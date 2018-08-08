package com.kairos.wrapper;

import com.kairos.persistence.model.organization.time_slot.TimeSlotWrapper;
import com.kairos.user.staff.client.ClientExceptionTypesDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 13/12/17.
 */
public class ClientPersonalCalenderPrerequisiteDTO {

    private List<ClientExceptionTypesDTO> exceptionTypes;
    private List<Map<String,Object>> temporaryAddresses;
    private List<TimeSlotWrapper> timeSlots;

    public ClientPersonalCalenderPrerequisiteDTO(List<ClientExceptionTypesDTO> exceptionTypes, List<Map<String, Object>> temporaryAddresses, List<TimeSlotWrapper> timeSlots) {
        this.exceptionTypes = exceptionTypes;
        this.temporaryAddresses = temporaryAddresses;
        this.timeSlots = timeSlots;
    }

    public List<ClientExceptionTypesDTO> getExceptionTypes() {
        return exceptionTypes;
    }

    public void setExceptionTypes(List<ClientExceptionTypesDTO> exceptionTypes) {
        this.exceptionTypes = exceptionTypes;
    }

    public List<Map<String, Object>> getTemporaryAddresses() {
        return temporaryAddresses;
    }

    public void setTemporaryAddresses(List<Map<String, Object>> temporaryAddresses) {
        this.temporaryAddresses = temporaryAddresses;
    }

    public List<TimeSlotWrapper> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlotWrapper> timeSlots) {
        this.timeSlots = timeSlots;
    }
}
