package com.kairos.dto.user.staff.staff;

import com.kairos.enums.Gender;
import com.kairos.enums.StaffStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by oodles on 3/2/17.
 */
@Getter
@Setter
@NoArgsConstructor
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
    private List<Long> skills;
    private  Long unit;
    private Long anonymousStaffId;
    private StaffStatusEnum currentStatus;
    private Integer age;
    private Gender gender;
    private String errorMessage; //This field is used for reporting error message on staff upload via excel sheet


    public StaffDTO(Long id, String firstName, String lastName, BigInteger cprNumber, String familyName, String privateEmail, Integer privatePhone,
                    String workEmail, Integer workPhone, Date employedSince, Long inactiveFrom, Long teamId, List<Long> skills,
                    Long unit, Long anonymousStaffId, StaffStatusEnum currentStatus) {
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

    public StaffDTO(String firstName, String lastName, String privateEmail, String errorMessage) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.privateEmail = privateEmail;
        this.errorMessage = errorMessage;
    }
}
