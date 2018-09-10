package com.kairos.utils.external_plateform_shift;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Date;

/**
 * Created by oodles on 14/12/16.
 */
public class GetWorkShiftsFromWorkPlaceByIdResult {
    @JacksonXmlProperty
    private String EmploymentId;
    @JacksonXmlProperty
    private String UpdateTypeFlag;
    @JacksonXmlProperty
    private String ActivityId;
    @JacksonXmlProperty
    private String IsParent;
    @JacksonXmlProperty
    private Long WorkPlaceId;
    @JacksonXmlProperty
    private String ExternalEmploymentId;
    @JacksonXmlProperty
    private String VacationFactory;
    @JacksonXmlProperty
    private Date EndDateTime;
    @JacksonXmlProperty
    private String ArrayOfWorkBreak;
    @JacksonXmlProperty
    private String Day;
    @JacksonXmlProperty
    private Date StartDateTime;
    @JacksonXmlProperty
    private String CalculatedBreak;
    @JacksonXmlProperty
    private Activity Activity;
    @JacksonXmlProperty
    private String PersonId;
    @JacksonXmlProperty
    @JacksonXmlElementWrapper(useWrapping = false)
    private ArrayOfChildShift ArrayOfChildShift;
    @JacksonXmlProperty
    private String UpdatePerId;
    @JacksonXmlProperty
    private Float Length;
    @JacksonXmlProperty
    private String IsBorrowed;
    @JacksonXmlProperty
    private String Id;
    @JacksonXmlProperty
    private Date UpdateDate;
    @JacksonXmlProperty
    private WorkPlace WorkPlace;
    @JacksonXmlProperty
    private Person Person;
    @JacksonXmlProperty
    private String UpdateUser;

    public GetWorkShiftsFromWorkPlaceByIdResult() {
    }

    public String getEmploymentId() {
        return EmploymentId;
    }

    public void setEmploymentId(String EmploymentId) {
        this.EmploymentId = EmploymentId;
    }

    public String getUpdateTypeFlag() {
        return UpdateTypeFlag;
    }

    public void setUpdateTypeFlag(String UpdateTypeFlag) {
        this.UpdateTypeFlag = UpdateTypeFlag;
    }

    public String getActivityId() {
        return ActivityId;
    }

    public void setActivityId(String ActivityId) {
        this.ActivityId = ActivityId;
    }

    public String getIsParent() {
        return IsParent;
    }

    public void setIsParent(String IsParent) {
        this.IsParent = IsParent;
    }

    public Long getWorkPlaceId() {
        return WorkPlaceId;
    }

    public void setWorkPlaceId(Long WorkPlaceId) {
        this.WorkPlaceId = WorkPlaceId;
    }

    public String getExternalEmploymentId() {
        return ExternalEmploymentId;
    }

    public void setExternalEmploymentId(String ExternalEmploymentId) {
        this.ExternalEmploymentId = ExternalEmploymentId;
    }

    public String getVacationFactory() {
        return VacationFactory;
    }

    public void setVacationFactory(String VacationFactory) {
        this.VacationFactory = VacationFactory;
    }

    public Date getEndDateTime() {
        DateTime dateTime = new DateTime(EndDateTime).toDateTime(DateTimeZone.UTC);
        return dateTime.toDate();
    }

    public void setEndDateTime(Date EndDateTime) {

        this.EndDateTime = EndDateTime;
    }

    public String getArrayOfWorkBreak() {
        return ArrayOfWorkBreak;
    }

    public void setArrayOfWorkBreak(String ArrayOfWorkBreak) {
        this.ArrayOfWorkBreak = ArrayOfWorkBreak;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String Day) {
        this.Day = Day;
    }

    public Date getStartDateTime() {

        return StartDateTime;
    }

    public void setStartDateTime(Date StartDateTime) {
        this.StartDateTime = StartDateTime;
    }

    public String getCalculatedBreak() {
        return CalculatedBreak;
    }

    public void setCalculatedBreak(String CalculatedBreak) {
        this.CalculatedBreak = CalculatedBreak;
    }

    public Activity getActivity() {
        return Activity;
    }

    public void setActivity(Activity Activity) {
        this.Activity = Activity;
    }

    public String getPersonId() {
        return PersonId;
    }

    public void setPersonId(String PersonId) {
        this.PersonId = PersonId;
    }

    public ArrayOfChildShift getArrayOfChildShift() {
        return ArrayOfChildShift;
    }

    public void setArrayOfChildShift(ArrayOfChildShift ArrayOfChildShift) {
        this.ArrayOfChildShift = ArrayOfChildShift;
    }

    public String getUpdatePerId() {
        return UpdatePerId;
    }

    public void setUpdatePerId(String UpdatePerId) {
        this.UpdatePerId = UpdatePerId;
    }

    public Float getLength() {
        return Length;
    }

    public void setLength(Float Length) {
        this.Length = Length;
    }

    public String getIsBorrowed() {
        return IsBorrowed;
    }

    public void setIsBorrowed(String IsBorrowed) {
        this.IsBorrowed = IsBorrowed;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public Date getUpdateDate() {
        return UpdateDate;
    }

    public void setUpdateDate(Date UpdateDate) {
        this.UpdateDate = UpdateDate;
    }

    public WorkPlace getWorkPlace() {
        return WorkPlace;
    }

    public void setWorkPlace(WorkPlace WorkPlace) {
        this.WorkPlace = WorkPlace;
    }

    public Person getPerson() {
        return Person;
    }

    public void setPerson(Person Person) {
        this.Person = Person;
    }

    public String getUpdateUser() {
        return UpdateUser;
    }

    public void setUpdateUser(String UpdateUser) {
        this.UpdateUser = UpdateUser;
    }

    @Override
    public String toString() {
        return "ClassPojo [EmploymentId = " + EmploymentId + ", UpdateTypeFlag = " + UpdateTypeFlag + ", ActivityId = " + ActivityId + ", IsParent = " + IsParent + ", WorkPlaceId = " + WorkPlaceId + ", ExternalEmploymentId = " + ExternalEmploymentId + ", VacationFactory = " + VacationFactory + ", EndDateTime = " + EndDateTime + ", ArrayOfWorkBreak = " + ArrayOfWorkBreak + ", Day = " + Day + ", StartDateTime = " + StartDateTime + ", CalculatedBreak = " + CalculatedBreak + ", Activity = " + Activity + ", PersonId = " + PersonId + ", ArrayOfChildShift = " + ArrayOfChildShift + ", UpdatePerId = " + UpdatePerId + ", Length = " + Length + ", IsBorrowed = " + IsBorrowed + ", Id = " + Id + ", UpdateDate = " + UpdateDate + ", WorkPlace = " + WorkPlace + ", Person = " + Person + ", UpdateUser = " + UpdateUser + "]";
    }
}
