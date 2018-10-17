package com.kairos.dto.user.staff.staff;

import com.kairos.enums.Gender;
import com.kairos.enums.StaffStatusEnum;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by oodles on 3/2/17.
 */
public class StaffDTO {

    private Long id;
    @NotBlank(message = "error.StaffDTO.firstName.notEmpty")
    private String firstName;
    @NotBlank(message = "error.StaffDTO.lastName.notEmpty")
    private String lastName;
    @NotBlank(message = "error.StaffDTO.cprNumber.notEmpty")
    private BigInteger cprNumber;
    private String familyName;

    private String privateEmail;
    private Integer privatePhone;
    private String workEmail;
    private Integer workPhone;

    private Date employedSince;
    private Long inactiveFrom;

    @NotNull(message = "error.StaffDTO.teamId.notnull")
    private Long teamId;

    private String primaryEmailAddress;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private List<Long> skills;

    private  Long unit;
    private Long anonymousStaffId;
    private StaffStatusEnum currentStatus;
    private Integer age;
    private Gender gender;

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
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



    public String getPrivateEmail() {
        return privateEmail;
    }

    public void setPrivateEmail(String privateEmail) {
        this.privateEmail = privateEmail;
    }



    public String getWorkEmail() {
        return workEmail;
    }

    public void setWorkEmail(String workEmail) {
        this.workEmail = workEmail;
    }


    public BigInteger getCprNumber() {
        return cprNumber;
    }

    public void setCprNumber(BigInteger cprNumber) {
        this.cprNumber = cprNumber;
    }

    public Integer getPrivatePhone() {
        return privatePhone;
    }

    public void setPrivatePhone(Integer privatePhone) {
        this.privatePhone = privatePhone;
    }

    public Integer getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(Integer workPhone) {
        this.workPhone = workPhone;
    }



    public Date getEmployedSince() {
        return employedSince;
    }

    public void setEmployedSince(Date employedSince) {
        this.employedSince = employedSince;
    }

    public Long getInactiveFrom() {
        return inactiveFrom;
    }

    public void setInactiveFrom(Long inactiveFrom) {
        this.inactiveFrom = inactiveFrom;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public List<Long> getSkills() {
        return skills;
    }

    public void setSkills(List<Long> skills) {
        this.skills = skills;
    }

    public Long getUnit() {
        return unit;
    }

    public void setUnit(Long unit) {
        this.unit = unit;
    }

    public Long getAnonymousStaffId() {
        return anonymousStaffId;
    }

    public void setAnonymousStaffId(Long anonymousStaffId) {
        this.anonymousStaffId = anonymousStaffId;
    }

    public StaffStatusEnum getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(StaffStatusEnum currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getPrimaryEmailAddress() {
        return primaryEmailAddress;
    }

    public void setPrimaryEmailAddress(String primaryEmailAddress) {
        this.primaryEmailAddress = primaryEmailAddress;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public StaffDTO() {
    }

    public StaffDTO(Long id, String firstName, String lastName, BigInteger cprNumber,String familyName, String privateEmail, Integer privatePhone,
                    String workEmail, Integer workPhone, Date employedSince,Long inactiveFrom,Long teamId, List<Long> skills,
                    Long unit, Long anonymousStaffId,StaffStatusEnum currentStatus) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cprNumber = cprNumber;
        this.familyName = familyName;
        this.privateEmail = privateEmail;
        this.privatePhone = privatePhone;
        this.workEmail = workEmail;
        this.workPhone = workPhone;
        this.employedSince = employedSince;
        this.inactiveFrom = inactiveFrom;
        this.teamId = teamId;
        this.skills = skills;
        this.unit = unit;
        this.anonymousStaffId = anonymousStaffId;
        this.currentStatus = currentStatus;
    }

    public StaffDTO(Long id, String firstName, String lastName, Gender gender, Integer age) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender=gender;
        this.age=age;
    }
}
