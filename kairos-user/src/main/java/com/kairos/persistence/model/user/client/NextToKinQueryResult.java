package com.kairos.persistence.model.user.client;

import com.kairos.persistence.model.enums.Gender;
import com.kairos.persistence.model.user.country.CitizenStatus;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by prabjot on 18/9/17.
 */
@QueryResult
public class NextToKinQueryResult {

    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String nickName;
    private Integer age;
    private String profilePic;
    private String cprNumber;
    private ContactDetail contactDetail;
    private ContactAddress homeAddress;
    private CitizenStatus citizenStatus;
    private ZipCode zipCode;

    public String getCprNumber() {
        return cprNumber;
    }

    public void setCprNumber(String cprNumber) {
        this.cprNumber = cprNumber;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    private Municipality municipality;

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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public ContactDetail getContactDetail() {
        return contactDetail;
    }

    public void setContactDetail(ContactDetail contactDetail) {
        this.contactDetail = contactDetail;
    }

    public ContactAddress getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(ContactAddress homeAddress) {
        this.homeAddress = homeAddress;
    }

    public ZipCode getZipCode() {
        return zipCode;
    }

    public void setZipCode(ZipCode zipCode) {
        this.zipCode = zipCode;
    }

    public Municipality getMunicipality() {
        return municipality;
    }

    public void setMunicipality(Municipality municipality) {
        this.municipality = municipality;
    }

    public CitizenStatus getCitizenStatus() {
        return citizenStatus;
    }

    public void setCitizenStatus(CitizenStatus citizenStatus) {
        this.citizenStatus = citizenStatus;
    }

    public NextToKinQueryResult buildResponse(Client nextToKin,String serverUrl){
        this.id = nextToKin.getId();
        this.firstName = nextToKin.getFirstName();
        this.lastName = nextToKin.getLastName();
        this.citizenStatus = nextToKin.getCivilianStatus();
        this.homeAddress = nextToKin.getHomeAddress();
        this.gender = nextToKin.getGender();
        this.age = nextToKin.getAge();
        this.nickName = nextToKin.getNickName();
        this.profilePic = serverUrl + nextToKin.getProfilePic();
        this.contactDetail = nextToKin.getContactDetail();
        this.cprNumber = nextToKin.getCprNumber();
        return this;
    }
}
