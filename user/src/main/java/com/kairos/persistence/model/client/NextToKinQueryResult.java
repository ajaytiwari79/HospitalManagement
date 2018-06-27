package com.kairos.persistence.model.client;

import com.kairos.persistence.model.enums.Gender;
import com.kairos.persistence.model.query_wrapper.AddressQueryResult;
import com.kairos.persistence.model.country.CitizenStatus;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Map;

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
    private Map homeAddress;
    private Long civilianStatusId;
    private ZipCode zipCode;
    private Long relationTypeId;

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

    public Map getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Map homeAddress) {
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

    public Long getCivilianStatusId() {
        return civilianStatusId;
    }

    public void setCivilianStatusId(Long civilianStatusId) {
        this.civilianStatusId = civilianStatusId;
    }

    public Long getRelationTypeId() {
        return relationTypeId;
    }

    public void setRelationTypeId(Long relationTypeId) {
        this.relationTypeId = relationTypeId;
    }

}
