package com.kairos.dto.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.country.skill.SkillDTO;
import com.kairos.enums.Gender;
import com.kairos.enums.StaffStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class StaffWithSkillDTO {
    private String middleName;

    private String mobileTelephone;

    private String autosignatureId;

    private String lastName;

    private String secondaryEmailAddress;

    private String organizationName;

    private String primaryEmailAddress;

    private String departmentName;

    private String version;

    private Long id;

    private String homeTelephone;

    private String primaryIdentifier;

    private CurrentAddress secondaryAddress;

    private String initials;

    private String unitName;

    private String fullName;

    private String firstName;

    private String workTelephone;
    private Gender gender;
    private boolean pregnant;
    private LocalDate dateOfBirth;

    private Long unitEmploymentPositionId;
    private CurrentAddress primaryAddress;
    private Set<SkillDTO> skillSet;

    public StaffWithSkillDTO(Long id, String firstName, Set<SkillDTO> skillSet) {
        this.id = id;
        this.firstName = firstName;
        this.skillSet = skillSet;
    }





    private StaffStatusEnum currentStatus;


    @Override
    public String toString()
    {
        return "ClassPojo [middleName = "+middleName+", mobileTelephone = "+mobileTelephone+", autosignatureId = "+autosignatureId+", lastName = "+lastName+", secondaryEmailAddress = "+secondaryEmailAddress+", organizationName = "+organizationName+", primaryEmailAddress = "+primaryEmailAddress+", departmentName = "+departmentName+", version = "+version+", id = "+id+", homeTelephone = "+homeTelephone+", primaryIdentifier = "+primaryIdentifier+", secondaryAddress = "+secondaryAddress+", initials = "+initials+", unitName = "+unitName+", currentStatus = "+currentStatus+", fullName = "+fullName+", firstName = "+firstName+", workTelephone = "+workTelephone+", primaryAddress = "+primaryAddress+"]";
    }
}