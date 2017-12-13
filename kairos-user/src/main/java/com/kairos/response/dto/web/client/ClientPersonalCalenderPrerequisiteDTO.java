package com.kairos.response.dto.web.client;

import com.kairos.persistence.model.organization.time_slot.TimeSlotWrapper;

import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 13/12/17.
 */
public class ClientPersonalCalenderPrerequisiteDTO {

    private List<ClientExceptionTypesDTO> clientExceptionTypesDTOList;
    private List<Map<String,Object>> clientTemporaryAddresses;
    private List<TimeSlotWrapper> timeSlotWrappers;

    public ClientPersonalCalenderPrerequisiteDTO(List<ClientExceptionTypesDTO> clientExceptionTypesDTOList, List<Map<String, Object>> clientTemporaryAddresses, List<TimeSlotWrapper> timeSlotWrappers) {
        this.clientExceptionTypesDTOList = clientExceptionTypesDTOList;
        this.clientTemporaryAddresses = clientTemporaryAddresses;
        this.timeSlotWrappers = timeSlotWrappers;
    }

    public List<ClientExceptionTypesDTO> getClientExceptionTypesDTOList() {
        return clientExceptionTypesDTOList;
    }

    public void setClientExceptionTypesDTOList(List<ClientExceptionTypesDTO> clientExceptionTypesDTOList) {
        this.clientExceptionTypesDTOList = clientExceptionTypesDTOList;
    }

    public List<Map<String, Object>> getClientTemporaryAddresses() {
        return clientTemporaryAddresses;
    }

    public void setClientTemporaryAddresses(List<Map<String, Object>> clientTemporaryAddresses) {
        this.clientTemporaryAddresses = clientTemporaryAddresses;
    }

    public List<TimeSlotWrapper> getTimeSlotWrappers() {
        return timeSlotWrappers;
    }

    public void setTimeSlotWrappers(List<TimeSlotWrapper> timeSlotWrappers) {
        this.timeSlotWrappers = timeSlotWrappers;
    }
}
