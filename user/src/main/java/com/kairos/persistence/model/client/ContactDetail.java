package com.kairos.persistence.model.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.annotations.KPermissionField;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by oodles on 28/9/16.
 */
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactDetail extends UserBaseEntity {

    @KPermissionField
    private String workEmail;
    @KPermissionField
    private String privateEmail;
    @KPermissionField
    private String workPhone;
    @KPermissionField
    private String landLinePhone;
    @KPermissionField
    private String privatePhone;
    @KPermissionField
    private String mobilePhone;
    @KPermissionField
    private String facebookAccount;
    private String messenger;
    @KPermissionField
    private String twitterAccount;
    @KPermissionField
    private String linkedInAccount;
    private boolean hidePrivatePhone;
    private boolean hideLandlinePhone;
    private boolean hideWorkPhone;
    private boolean hideMobilePhone;
    private boolean protectedIdentity;
    private String emergencyPhone;
    private boolean hideEmergencyPhone;


    public ContactDetail(String workEmail, String privateEmail, String workPhone, String landLinePhone, String privatePhone, String mobilePhone, String facebookAccount, String twitterAccount) {
        this.workEmail = workEmail;
        this.privateEmail = privateEmail;
        this.workPhone = workPhone;
        this.landLinePhone = landLinePhone;
        this.privatePhone = privatePhone;
        this.mobilePhone = mobilePhone;
        this.facebookAccount = facebookAccount;
        this.twitterAccount = twitterAccount;
    }

    public ContactDetail(String workEmail, String privateEmail, String privatePhone, String facebookAccount) {
        this.workEmail = workEmail;
        this.privateEmail = privateEmail;
        this.privatePhone = privatePhone;
        this.facebookAccount = facebookAccount;
    }

    public ContactDetail() {
    }


    public String getMessenger() {
        return messenger;
    }

    public void setMessenger(String messenger) {
        this.messenger = messenger;
    }

    public boolean isHideLandlinePhone() {
        return hideLandlinePhone;
    }

    public void setHideLandlinePhone(boolean hideLandlinePhone) {
        this.hideLandlinePhone = hideLandlinePhone;
    }

    public boolean isHideWorkPhone() {
        return hideWorkPhone;
    }

    public void setHideWorkPhone(boolean hideWorkPhone) {
        this.hideWorkPhone = hideWorkPhone;
    }

    public boolean isHideMobilePhone() {
        return hideMobilePhone;
    }

    public void setHideMobilePhone(boolean hideMobilePhone) {
        this.hideMobilePhone = hideMobilePhone;
    }

    public boolean isHidePrivatePhone() {
        return hidePrivatePhone;
    }

    public void setHidePrivatePhone(boolean hidePrivatePhone) {
        this.hidePrivatePhone = hidePrivatePhone;
    }

    public String getWorkEmail() {
        return workEmail;
    }

    public void setWorkEmail(String workEmail) {
        this.workEmail = workEmail;
    }

    public String getPrivateEmail() {
        return privateEmail;
    }

    public void setPrivateEmail(String privateEmail) {
        this.privateEmail = privateEmail;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getLandLinePhone() {
        return landLinePhone;
    }

    public void setLandLinePhone(String landLinePhone) {
        this.landLinePhone = landLinePhone;
    }

    public String getPrivatePhone() {
        return privatePhone;
    }

    public void setPrivatePhone(String privatePhone) {
        this.privatePhone = privatePhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getFacebookAccount() {
        return facebookAccount;
    }

    public void setFacebookAccount(String facebookAccount) {
        this.facebookAccount = facebookAccount;
    }

    public String getTwitterAccount() {
        return twitterAccount;
    }

    public void setTwitterAccount(String twitterAccount) {
        this.twitterAccount = twitterAccount;
    }

    public String getLinkedInAccount() {
        return linkedInAccount;
    }

    public void setLinkedInAccount(String linkedInAccount) {
        this.linkedInAccount = linkedInAccount;
    }

    public boolean isProtectedIdentity() {
        return protectedIdentity;
    }

    public void setProtectedIdentity(boolean protectedIdentity) {
        this.protectedIdentity = protectedIdentity;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public boolean isHideEmergencyPhone() {
        return hideEmergencyPhone;
    }

    public void setHideEmergencyPhone(boolean hideEmergencyPhone) {
        this.hideEmergencyPhone = hideEmergencyPhone;
    }

    public void updateContactInfo(String workEmail, String workPhone, String landLinePhone) {
        this.workEmail = workEmail;
        this.workPhone = workPhone;
        this.landLinePhone = landLinePhone;
    }

    public Map<String,String> retreiveContactNumbers(){
        Map<String,String> contactNumbers = new HashMap<>();
        if(this.hidePrivatePhone == false)
            contactNumbers.put("privatePhone",this.privatePhone);
        if(this.hideMobilePhone == false)
            contactNumbers.put("mobilePhone",this.mobilePhone);
        if(this.hideLandlinePhone == false)
            contactNumbers.put("landLinePhone",this.landLinePhone);
        if(this.hideWorkPhone == false)
            contactNumbers.put("workPhone",this.workPhone);
        if(this.hideEmergencyPhone == false)
             contactNumbers.put("emergencyPhone",this.emergencyPhone);
        return contactNumbers;
    }


}
