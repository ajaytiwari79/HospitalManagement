package com.kairos.dto.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.patient.PatientResourceList;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * Created by oodles on 25/7/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ImportTaskDTO {
    @JsonIgnoreProperties
    private Date originalDate;
    @JsonIgnoreProperties
    private String groupPlannedGrantSubscriptionResources;
    @JsonIgnoreProperties
    private String fontColor;
    @JsonIgnoreProperties
    private String locationName;
    @JsonIgnoreProperties
    private EventType eventType;
    @JsonIgnoreProperties
    private String type;
    @JsonIgnoreProperties
    private String dashboardDescription;
    @JsonIgnoreProperties
    private String version;
    @JsonIgnoreProperties
    private String eventOccurrenceKey;
    @JsonIgnoreProperties
    private String id;
    @JsonIgnoreProperties
    private String resourceId;
    @JsonIgnoreProperties
    private String title;
    @JsonIgnoreProperties
    private String generatedId;
    @JsonIgnoreProperties
    private String deliveredEvent;
    @JsonIgnoreProperties
    private String eventIdentifier;
    @JsonIgnoreProperties
    private String description;

    @JsonIgnoreProperties
    private String[] changeStrategies;
    @JsonIgnoreProperties
    private String linked;
    @JsonIgnoreProperties
    private Date end;

    @JsonIgnoreProperties
    private List<PatientResourceList> patientResourceList;
    @JsonIgnoreProperties
    private String lockedOnStartTime;
    @JsonIgnoreProperties
    private String statusColor;

    @JsonIgnoreProperties
    private String professional;
    @JsonIgnoreProperties
    private String variationId;
    @JsonIgnoreProperties
    private String lockedOnCalendarResource;
    @JsonIgnoreProperties
    private String readOnly;
    @JsonIgnoreProperties
    private Date start;
    @JsonIgnoreProperties
    private String background;
    @JsonIgnoreProperties
    private String sameSupplier;
    @JsonIgnoreProperties
    private String roadTime;

    @JsonIgnoreProperties
    private String withinShift;
    @JsonIgnoreProperties
    private String comments;

    @JsonIgnoreProperties
    private String draggable;

    @JsonIgnoreProperties
    private String repetition;


    @Override
    public String toString()
    {
        return "ClassPojo [originalDate = "+originalDate+", groupPlannedGrantSubscriptionResources = "+groupPlannedGrantSubscriptionResources+", fontColor = "+fontColor+", locationName = "+locationName+", eventType = "+eventType+", type = "+type+", dashboardDescription = "+dashboardDescription+", version = "+version+", eventOccurrenceKey = "+eventOccurrenceKey+", id = "+id+", resourceId = "+resourceId+", title = "+title+", generatedId = "+generatedId+", deliveredEvent = "+deliveredEvent+", eventIdentifier = "+eventIdentifier+", description = "+description+", changeStrategies = "+changeStrategies+", linked = "+linked+", end = "+end+", patientResourceList = "+patientResourceList+", lockedOnStartTime = "+lockedOnStartTime+", statusColor = "+statusColor+", professional = "+professional+", variationId = "+variationId+", lockedOnCalendarResource = "+lockedOnCalendarResource+", readOnly = "+readOnly+", start = "+start+", background = "+background+", sameSupplier = "+sameSupplier+", roadTime = "+roadTime+", withinShift = "+withinShift+", comments = "+comments+", draggable = "+draggable+", repetition = "+repetition+"]";
    }
}
