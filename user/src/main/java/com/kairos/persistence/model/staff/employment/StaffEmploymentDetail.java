package com.kairos.persistence.model.staff.employment;

import org.neo4j.ogm.annotation.typeconversion.DateLong;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by prabjot on 10/1/17.
 */
public class StaffEmploymentDetail {

    private String cardNumber;
    private String sendNotificationBy;
    private String email;
    private boolean copyKariosMailToLogin;
    @NotNull(message = "error.Staff.employedsince.notnull")
    private String employedSince;
    private long visitourId;
    private long engineerTypeId;
    private Long timeCareExternalId;
    @DateLong
    private Date dateOfBirth;

    public String getCardNumber() {
        return cardNumber;
    }

    public String getSendNotificationBy() {
        return sendNotificationBy;
    }

    public String getEmail() {
        return email;
    }

    public boolean isCopyKariosMailToLogin() {
        return copyKariosMailToLogin;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setSendNotificationBy(String sendNotificationBy) {
        this.sendNotificationBy = sendNotificationBy;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCopyKariosMailToLogin(boolean copyKariosMailToLogin) {
        this.copyKariosMailToLogin = copyKariosMailToLogin;
    }

    public String getEmployedSince() {
        return employedSince;
    }

    public void setEmployedSince(String employedSince) {
        this.employedSince = employedSince;
    }

    public long getVisitourId() {
        return visitourId;
    }

    public void setVisitourId(long visitourId) {
        this.visitourId = visitourId;
    }

    public long getEngineerTypeId() {
        return engineerTypeId;
    }

    public void setEngineerTypeId(long engineerTypeId) {
        this.engineerTypeId = engineerTypeId;
    }

    public Long getTimeCareExternalId() {
        return timeCareExternalId;
    }

    public void setTimeCareExternalId(Long timeCareExternalId) {
        this.timeCareExternalId = timeCareExternalId;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
