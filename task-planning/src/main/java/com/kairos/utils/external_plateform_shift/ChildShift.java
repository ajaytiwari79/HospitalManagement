package com.kairos.utils.external_plateform_shift;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.Date;

/**
 * Created by oodles on 25/1/17.
 */
public class ChildShift {
    @JacksonXmlProperty
    private Date StartDateTime;
    @JacksonXmlProperty
    private String UpdateTypeFlag;
    @JacksonXmlProperty
    private String PersonId;
    @JacksonXmlProperty
    private String ActivityId;
    @JacksonXmlProperty
    private String WorkPlaceId;
    @JacksonXmlProperty
    private String UpdatePerId;
    @JacksonXmlProperty
    private String Length;
    @JacksonXmlProperty
    private Date EndDateTime;
    @JacksonXmlProperty
    private String Id;
    @JacksonXmlProperty
    private Date UpdateDate;
    @JacksonXmlProperty
    private String Day;
    @JacksonXmlProperty
    private String ParentShiftId;
    @JacksonXmlProperty
    private String UpdateUser;
    @JacksonXmlProperty
    public Date getStartDateTime ()
    {
        return StartDateTime;
    }

    public void setStartDateTime (Date StartDateTime)
    {
        this.StartDateTime = StartDateTime;
    }

    public String getUpdateTypeFlag ()
    {
        return UpdateTypeFlag;
    }

    public void setUpdateTypeFlag (String UpdateTypeFlag)
    {
        this.UpdateTypeFlag = UpdateTypeFlag;
    }

    public String getPersonId ()
    {
        return PersonId;
    }

    public void setPersonId (String PersonId)
    {
        this.PersonId = PersonId;
    }

    public String getActivityId ()
    {
        return ActivityId;
    }

    public void setActivityId (String ActivityId)
    {
        this.ActivityId = ActivityId;
    }

    public String getWorkPlaceId ()
    {
        return WorkPlaceId;
    }

    public void setWorkPlaceId (String WorkPlaceId)
    {
        this.WorkPlaceId = WorkPlaceId;
    }

    public String getUpdatePerId ()
    {
        return UpdatePerId;
    }

    public void setUpdatePerId (String UpdatePerId)
    {
        this.UpdatePerId = UpdatePerId;
    }

    public String getLength ()
    {
        return Length;
    }

    public void setLength (String Length)
    {
        this.Length = Length;
    }

    public Date getEndDateTime ()
    {
        return EndDateTime;
    }

    public void setEndDateTime (Date EndDateTime)
    {
        this.EndDateTime = EndDateTime;
    }

    public String getId ()
    {
        return Id;
    }

    public void setId (String Id)
    {
        this.Id = Id;
    }

    public Date getUpdateDate ()
    {
        return UpdateDate;
    }

    public void setUpdateDate (Date UpdateDate)
    {
        this.UpdateDate = UpdateDate;
    }

    public String getDay ()
    {
        return Day;
    }

    public void setDay (String Day)
    {
        this.Day = Day;
    }

    public String getParentShiftId ()
    {
        return ParentShiftId;
    }

    public void setParentShiftId (String ParentShiftId)
    {
        this.ParentShiftId = ParentShiftId;
    }

    public String getUpdateUser ()
    {
        return UpdateUser;
    }

    public void setUpdateUser (String UpdateUser)
    {
        this.UpdateUser = UpdateUser;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [StartDateTime = "+StartDateTime+", UpdateTypeFlag = "+UpdateTypeFlag+", PersonId = "+PersonId+", ActivityId = "+ActivityId+", WorkPlaceId = "+WorkPlaceId+", UpdatePerId = "+UpdatePerId+", Length = "+Length+", EndDateTime = "+EndDateTime+", Id = "+Id+", UpdateDate = "+UpdateDate+", Day = "+Day+", ParentShiftId = "+ParentShiftId+", UpdateUser = "+UpdateUser+"]";
    }
}
