package com.kairos.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.user.patient.PatientResourceList;

import java.util.Date;
import java.util.List;

/**
 * Created by oodles on 25/7/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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

    public String getFontColor ()
    {
        return fontColor;
    }

    public void setFontColor (String fontColor)
    {
        this.fontColor = fontColor;
    }


    public EventType getEventType ()
    {
        return eventType;
    }

    public void setEventType (EventType eventType)
    {
        this.eventType = eventType;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String getDashboardDescription ()
    {
        return dashboardDescription;
    }

    public void setDashboardDescription (String dashboardDescription)
    {
        this.dashboardDescription = dashboardDescription;
    }

    public String getVersion ()
    {
        return version;
    }

    public void setVersion (String version)
    {
        this.version = version;
    }

    public String getEventOccurrenceKey ()
    {
        return eventOccurrenceKey;
    }

    public void setEventOccurrenceKey (String eventOccurrenceKey)
    {
        this.eventOccurrenceKey = eventOccurrenceKey;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getResourceId ()
    {
        return resourceId;
    }

    public void setResourceId (String resourceId)
    {
        this.resourceId = resourceId;
    }


    public String getGeneratedId ()
    {
        return generatedId;
    }

    public void setGeneratedId (String generatedId)
    {
        this.generatedId = generatedId;
    }


    public String getEventIdentifier ()
    {
        return eventIdentifier;
    }

    public void setEventIdentifier (String eventIdentifier)
    {
        this.eventIdentifier = eventIdentifier;
    }


    public String[] getChangeStrategies ()
    {
        return changeStrategies;
    }

    public void setChangeStrategies (String[] changeStrategies)
    {
        this.changeStrategies = changeStrategies;
    }

    public List<PatientResourceList> getPatientResourceList() {
        return patientResourceList;
    }

    public void setPatientResourceList(List<PatientResourceList> patientResourceList) {
        this.patientResourceList = patientResourceList;
    }

    public String getLockedOnStartTime ()
    {
        return lockedOnStartTime;
    }

    public void setLockedOnStartTime (String lockedOnStartTime)
    {
        this.lockedOnStartTime = lockedOnStartTime;
    }

    public String getStatusColor ()
    {
        return statusColor;
    }

    public void setStatusColor (String statusColor)
    {
        this.statusColor = statusColor;
    }


    public String getProfessional ()
    {
        return professional;
    }

    public void setProfessional (String professional)
    {
        this.professional = professional;
    }


    public String getLockedOnCalendarResource ()
    {
        return lockedOnCalendarResource;
    }

    public void setLockedOnCalendarResource (String lockedOnCalendarResource)
    {
        this.lockedOnCalendarResource = lockedOnCalendarResource;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public String getBackground ()
    {
        return background;
    }

    public void setBackground (String background)
    {
        this.background = background;
    }


    public String getSameSupplier ()
    {
        return sameSupplier;
    }

    public void setSameSupplier (String sameSupplier)
    {
        this.sameSupplier = sameSupplier;
    }

    public String getRoadTime ()
    {
        return roadTime;
    }

    public void setRoadTime (String roadTime)
    {
        this.roadTime = roadTime;
    }



    public String getWithinShift ()
    {
        return withinShift;
    }

    public void setWithinShift (String withinShift)
    {
        this.withinShift = withinShift;
    }

    public Date getOriginalDate() {
        return originalDate;
    }

    public void setOriginalDate(Date originalDate) {
        this.originalDate = originalDate;
    }

    public String getGroupPlannedGrantSubscriptionResources() {
        return groupPlannedGrantSubscriptionResources;
    }

    public void setGroupPlannedGrantSubscriptionResources(String groupPlannedGrantSubscriptionResources) {
        this.groupPlannedGrantSubscriptionResources = groupPlannedGrantSubscriptionResources;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeliveredEvent() {
        return deliveredEvent;
    }

    public void setDeliveredEvent(String deliveredEvent) {
        this.deliveredEvent = deliveredEvent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLinked() {
        return linked;
    }

    public void setLinked(String linked) {
        this.linked = linked;
    }

    public String getVariationId() {
        return variationId;
    }

    public void setVariationId(String variationId) {
        this.variationId = variationId;
    }

    public String getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(String readOnly) {
        this.readOnly = readOnly;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDraggable ()
    {
        return draggable;
    }

    public void setDraggable (String draggable)
    {
        this.draggable = draggable;
    }

    public String getRepetition ()
    {
        return repetition;
    }

    public void setRepetition (String repetition)
    {
        this.repetition = repetition;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [originalDate = "+originalDate+", groupPlannedGrantSubscriptionResources = "+groupPlannedGrantSubscriptionResources+", fontColor = "+fontColor+", locationName = "+locationName+", eventType = "+eventType+", type = "+type+", dashboardDescription = "+dashboardDescription+", version = "+version+", eventOccurrenceKey = "+eventOccurrenceKey+", id = "+id+", resourceId = "+resourceId+", title = "+title+", generatedId = "+generatedId+", deliveredEvent = "+deliveredEvent+", eventIdentifier = "+eventIdentifier+", description = "+description+", changeStrategies = "+changeStrategies+", linked = "+linked+", end = "+end+", patientResourceList = "+patientResourceList+", lockedOnStartTime = "+lockedOnStartTime+", statusColor = "+statusColor+", professional = "+professional+", variationId = "+variationId+", lockedOnCalendarResource = "+lockedOnCalendarResource+", readOnly = "+readOnly+", start = "+start+", background = "+background+", sameSupplier = "+sameSupplier+", roadTime = "+roadTime+", withinShift = "+withinShift+", comments = "+comments+", draggable = "+draggable+", repetition = "+repetition+"]";
    }
}
