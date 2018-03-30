package com.kairos.persistence.model.user.staff;
import com.kairos.persistence.model.enums.StaffStatusEnum;
import com.kairos.persistence.model.user.client.ContactDetail;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by prabjot on 10/1/17.
 */
public class StaffPersonalDetail {

    @NotEmpty(message = "error.Staff.firstname.notnull") @NotNull(message = "error.Staff.firstname.notnull")
    private String firstName;
    @NotEmpty(message = "error.Staff.lastname.notnull") @NotNull(message = "error.Staff.lastname.notnull")
    private String lastName;
    private String signature;
    private long visitourId;
    private ContactDetail contactDetail;
    private String inactiveFrom;
    private StaffStatusEnum currentStatus;
    private long languageId;
    private long expertiseId;
   // @NotEmpty(message = "error.cprnumber.notnull") @NotNull(message = "error.cprnumber.notnull")
    private String cprNumber;
   // @NotEmpty(message = "error.Staff.familyname.notnull") @NotNull(message = "error.Staff.familyname.notnull")
    private String familyName;

    // Visitour Speed Profile
    private Integer speedPercent;
    private Integer workPercent;
    private Integer overtime;
    private Float costDay;
    private Float costCall;
    private Float costKm;
    private Float costHour;
    private Float costHourOvertime;
    private Integer capacity;
    private String careOfName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public long getVisitourId() {
        return visitourId;
    }

    public void setVisitourId(long visitourId) {
        this.visitourId = visitourId;
    }

    public ContactDetail getContactDetail() {
        return contactDetail;
    }

    public void setContactDetail(ContactDetail contactDetail) {
        this.contactDetail = contactDetail;
    }

    public String getInactiveFrom() {
        return inactiveFrom;
    }

    public void setInactiveFrom(String inactiveFrom) {
        this.inactiveFrom = inactiveFrom;
    }

    public long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(long languageId) {
        this.languageId = languageId;
    }

    public long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(long expertiseId) {
        this.expertiseId = expertiseId;
    }

    public String getCprNumber() {
        return cprNumber;
    }

    public void setCprNumber(String cprNumber) {
        this.cprNumber = cprNumber;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public Integer getSpeedPercent() {
        return speedPercent;
    }

    public void setSpeedPercent(Integer speedPercent) {
        this.speedPercent = speedPercent;
    }

    public Integer getWorkPercent() {
        return workPercent;
    }

    public void setWorkPercent(Integer workPercent) {
        this.workPercent = workPercent;
    }

    public Integer getOvertime() {
        return overtime;
    }

    public void setOvertime(Integer overtime) {
        this.overtime = overtime;
    }

    public Float getCostDay() {
        return costDay;
    }

    public void setCostDay(Float costDay) {
        this.costDay = costDay;
    }

    public Float getCostCall() {
        return costCall;
    }

    public void setCostCall(Float costCall) {
        this.costCall = costCall;
    }

    public Float getCostKm() {
        return costKm;
    }

    public void setCostKm(Float costKm) {
        this.costKm = costKm;
    }

    public Float getCostHour() {
        return costHour;
    }

    public void setCostHour(Float costHour) {
        this.costHour = costHour;
    }

    public Float getCostHourOvertime() {
        return costHourOvertime;
    }

    public void setCostHourOvertime(Float costHourOvertime) {
        this.costHourOvertime = costHourOvertime;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getCareOfName() {
        return careOfName;
    }

    public void setCareOfName(String careOfName) {
        this.careOfName = careOfName;
    }

    public StaffStatusEnum getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(StaffStatusEnum currentStatus) {
        this.currentStatus = currentStatus;
    }
}
