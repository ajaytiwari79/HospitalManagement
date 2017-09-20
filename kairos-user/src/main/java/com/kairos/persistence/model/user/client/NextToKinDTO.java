package com.kairos.persistence.model.user.client;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.organization.AddressDTO;
import com.kairos.persistence.model.user.country.CitizenStatus;

import java.util.Map;

/**
 * Created by oodles on 24/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NextToKinDTO {
    private String nickName;
    private String firstName;
    private String lastName;
    private CitizenStatus civilianStatus;
    private AddressDTO homeAddress;
    private ContactDetail contactDetail;
    private String profilePic;
    private String cprNumber;
    private String  privateEmail;
    private boolean isVerifiedByGoogleMap;
    private long relationTypeId;

    public boolean isVerifiedByGoogleMap() {
        return isVerifiedByGoogleMap;
    }

    public void setVerifiedByGoogleMap(boolean verifiedByGoogleMap) {
        isVerifiedByGoogleMap = verifiedByGoogleMap;
    }

    public String getCprNumber() {
        return cprNumber;
    }

    public void setCprNumber(String cprNumber) {
        this.cprNumber = cprNumber;
    }

    public String getPrivateEmail() {
        return privateEmail;
    }

    public void setPrivateEmail(String privateEmail) {
        this.privateEmail = privateEmail;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

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


    public CitizenStatus getCivilianStatus() {
        return civilianStatus;
    }

    public void setCivilianStatus(CitizenStatus civilianStatus) {
        this.civilianStatus = civilianStatus;
    }

    public AddressDTO getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(AddressDTO homeAddress) {
        this.homeAddress = homeAddress;
    }

    public ContactDetail getContactDetail() {
        return contactDetail;
    }

    public void setContactDetail(ContactDetail contactDetail) {
        this.contactDetail = contactDetail;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public long getRelationTypeId() {
        return relationTypeId;
    }

    public void setRelationTypeId(long relationTypeId) {
        this.relationTypeId = relationTypeId;
    }
}
