package com.kairos.persistence.model.client;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.enums.Gender;
import com.kairos.persistence.model.auth.User;
import com.kairos.user.organization.AddressDTO;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotNull;

/**
 * Created by oodles on 24/1/17.
 */
@QueryResult
@JsonIgnoreProperties(ignoreUnknown = true)
public class NextToKinDTO {

    private String nickName;
    @NotNull(message = "First name can't be empty")
    private String firstName;
    @NotNull(message = "Last name can't be empty")
    private String lastName;
    private AddressDTO homeAddress;
    private ContactDetail contactDetail;
    private String profilePic;
    @NotNull(message = "CPR number can't be empty")
    private String cprNumber;
    private String privateEmail;
    private boolean isVerifiedByGoogleMap;
    private Long relationTypeId;
    private Gender gender;
    private Integer age;
    private boolean updateHouseholdAddress;
    @NotNull(message = "Civilian Status can't be empty")
    private Long civilianStatusId;
    private Long id;

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Long getCivilianStatusId() {
        return civilianStatusId;
    }

    public void setCivilianStatusId(Long civilianStatusId) {
        this.civilianStatusId = civilianStatusId;
    }


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

    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public Long getRelationTypeId() {
        return relationTypeId;
    }

    public void setRelationTypeId(Long relationTypeId) {
        this.relationTypeId = relationTypeId;
    }

    public boolean isUpdateHouseholdAddress() {
        return updateHouseholdAddress;
    }

    public void setUpdateHouseholdAddress(boolean updateHouseholdAddress) {
        this.updateHouseholdAddress = updateHouseholdAddress;
    }

    public NextToKinDTO buildResponse(User nextToKin, String serverUrl, long relationTypeId,
                                      NextToKinDTO nextToKinDTO) {
        ObjectMapper objectMapper = new ObjectMapper();
        this.id = nextToKin.getId();
        this.firstName = nextToKin.getFirstName();
        this.lastName = nextToKin.getLastName();
        this.civilianStatusId = nextToKinDTO.getCivilianStatusId();
        this.homeAddress = objectMapper.convertValue(nextToKinDTO.getHomeAddress(), AddressDTO.class);
        this.homeAddress.setMunicipalityId(nextToKinDTO.getHomeAddress().getMunicipalityId());
        this.homeAddress.setZipCodeId((long) nextToKinDTO.getHomeAddress().getZipCodeValue());
        this.gender = nextToKin.getGender();
        this.age = nextToKin.getAge();
        this.nickName = nextToKin.getNickName();
        this.profilePic = serverUrl + nextToKinDTO.getProfilePic();
        this.contactDetail = nextToKin.getContactDetail();
        this.cprNumber = nextToKin.getCprNumber();
        this.relationTypeId = relationTypeId;
        return this;
    }


}
