package com.kairos.persistence.model.user.client;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.persistence.model.enums.Gender;
import com.kairos.persistence.model.organization.AddressDTO;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by oodles on 24/1/17.
 */
@QueryResult
@JsonIgnoreProperties(ignoreUnknown = true)
public class NextToKinDTO {
    private String nickName;
    private String firstName;
    private String lastName;
    private AddressDTO homeAddress;
    private ContactDetail contactDetail;
    private String profilePic;
    private String cprNumber;
    private String  privateEmail;
    private boolean isVerifiedByGoogleMap;
    private Long relationTypeId;
    private Gender gender;
    private Integer age;


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

    private Long civilianStatusId;
    private Long id;

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

    public NextToKinDTO buildResponse(Client nextToKin, String serverUrl){
        ObjectMapper objectMapper = new ObjectMapper();
        this.id = nextToKin.getId();
        this.firstName = nextToKin.getFirstName();
        this.lastName = nextToKin.getLastName();
        this.civilianStatusId = nextToKin.getCivilianStatus().getId();
        this.homeAddress = objectMapper.convertValue(nextToKin.getHomeAddress(),AddressDTO.class);
        this.homeAddress.setZipCodeId(nextToKin.getHomeAddress().getZipCode().getId());
        this.gender = nextToKin.getGender();
        this.age = nextToKin.getAge();
        this.nickName = nextToKin.getNickName();
        this.profilePic = serverUrl + nextToKin.getProfilePic();
        this.contactDetail = nextToKin.getContactDetail();
        this.cprNumber = nextToKin.getCprNumber();
        return this;
    }


}
