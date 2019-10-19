package com.kairos.dto.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.skill.Skill;
import com.kairos.enums.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by oodles on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class StaffDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private BigInteger cprNumber;
    private String familyName;

    private String privateEmail;
    private Integer privatePhone;
    private String workEmail;
    private Integer workPhone;

    private Date employedSince;
    private Boolean active;
    private Long inactiveFrom;

    private Long teamId;

    private List<Long> skills;

    private  Long unit;
    private Long anonymousStaffId;
    private ContactAddress contactAddress;
    private ContactDetail contactDetail;

    private Long visitourId;
    private String email;
    private String profilePic;
    private Set<Skill> skillSet;
    private Long unitEmploymentPositionId;
    private Gender gender;
    private boolean pregnant;
    private LocalDate dateOfBirth;
    private Set<Long> expertiseIds;
    private Long employmentTypeId;

    public StaffDTO(Long id, String firstName, String lastName, String profilePic) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePic = profilePic;
    }
    public String getFullName(){
        return this.firstName+" "+this.getLastName();
    }

}
