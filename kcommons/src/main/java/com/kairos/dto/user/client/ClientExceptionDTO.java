package com.kairos.dto.user.client;

import com.kairos.dto.user.organization.AddressDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oodles on 14/2/17.
 */
@Getter
@Setter
public class ClientExceptionDTO {

    private boolean fullDay;
    private String exceptionTypeId;
    private Long clientId;
    private List<String> selectedDates;

    private String fromTime;
    private String toTime;

    private String moveToDay;
    private Long moveToTimeslotId;
    private String info1;
    private String info2;
    private Long temporaryAddress;

    private boolean updateTaskDuration;
    private boolean updateTaskPriority;
    private List<Long> houseHoldMembers=new ArrayList<>();

    private int newTaskDuration;
    private int newTaskPriority;
    private int daysToReview = 1;

    private boolean exceptionHandled;

    private AddressDTO tempAddress;


    @Override
    public String toString() {
        return "ClientExceptionDTO{" +
                "fullDay=" + fullDay +
                ", exceptionTypeId='" + exceptionTypeId + '\'' +
                ", clientId=" + clientId +
                ", selectedDates=" + selectedDates +
                ", fromTime='" + fromTime + '\'' +
                ", toTime='" + toTime + '\'' +
                ", moveToDay='" + moveToDay + '\'' +
                ", moveToTimeslotId=" + moveToTimeslotId +
                ", info1='" + info1 + '\'' +
                ", info2='" + info2 + '\'' +
                ", temporaryAddress=" + temporaryAddress +
                ", newTaskDuration='" + newTaskDuration + '\'' +
                ", newTaskPriority='" + newTaskPriority + '\'' +
                ", daysToReview=" + daysToReview +
                ", exceptionHandled=" + exceptionHandled +
                ", tempAddress=" + tempAddress +
                '}';
    }

}
