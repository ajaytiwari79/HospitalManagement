package com.kairos.persistence.model.user.staff;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by prabjot on 10/1/17.
 */
public class StaffEmploymentDetail {

    private String cardNumber;
    @NotEmpty(message = "error.Staff.cardnumber.notnull") @NotNull(message = "error.Staff.cardnumber.notnull")
    private String sendNotificationBy;
    private String email;
    private boolean copyKariosMailToLogin;
    @NotEmpty(message = "error.Staff.employedsince.notnull") @NotNull(message = "error.Staff.employedsince.notnull")
    private String employedSince;
    private long visitourId;
    private long engineerTypeId;
    private Long timeCareExternalId;

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
}
