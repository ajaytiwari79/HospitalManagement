package com.kairos.persistence.model.staff.personal_details;

import com.kairos.commons.annotation.PermissionClass;
import com.kairos.dto.activity.tags.TagDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.organization.AddressDTO;
import com.kairos.dto.user.skill.SkillLevelDTO;
import com.kairos.dto.user.staff.staff.StaffChildDetailDTO;
import com.kairos.dto.user.team.TeamDTO;
import com.kairos.enums.Gender;
import com.kairos.enums.StaffStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;


/**
 * Created by prabjot on 10/1/17.
 */
@Getter
@Setter
@NoArgsConstructor
@PermissionClass(name = "Staff")
public class StaffPersonalDetail {

    private Long id;
    @NotBlank(message = "error.Staff.firstname.notnull")
    private String firstName;
    @NotBlank(message = "error.Staff.lastname.notnull")
    private String lastName;
    private String signature;
    private long visitourId;
    private ContactDetailDTO contactDetail;
    private String inactiveFrom;
    private StaffStatusEnum currentStatus;
    private Long languageId;
    private List<Long> expertiseIds;
    private List<StaffExpertiseDTO> expertiseWithExperience;
    private String cprNumber;
    private String familyName;

    // Visitour Speed Profile
    private Integer speedPercent;
    private Integer workPercent;
    private Integer overtime;
    private Float costDay;
    private Float costCall;
    private Float costKm;
    private Float costHour;
    private Float costHourOvertime;
    private Integer capacity;
    private String careOfName;
    private Gender gender;
    private boolean pregnant;
    private Long employmentTypeId;
    private List<SectorAndStaffExpertiseDTO> sectorWiseExpertise;
    private AddressDTO contactAddress;
    private AddressDTO secondaryContactAddress;
    private Set<Long> teamIdsOfStaff;
    //@NotBlank(message = "error.Staff.userName.notnull")
    private String userName;
    private boolean userNameUpdated;
    private List<TeamDTO> teams;
    private List<TagDTO> tags;
    @Valid
    private List<StaffChildDetailDTO> staffChildDetails;
    private String profilePic;
    private String cardNumber;
    private Integer age;
    private List<SkillLevelDTO> skills;
    private String errorMessage; //This field is used for reporting error message on staff upload via excel sheet
    private LocalDate dateOfBirth;
    private Long staffUserId;
    private Long externalId;
    private Set<AccessGroupRole> roles;

    public StaffPersonalDetail(Long id, List<SkillLevelDTO> skills) {
        this.id = id;
        this.skills = skills;
    }

    public void setContactAddress(AddressDTO contactAddress) {
        this.contactAddress = contactAddress;
        if(isNotNull(this.contactAddress)){
            this.contactAddress.setPrimary(true);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaffPersonalDetail that = (StaffPersonalDetail) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(contactDetail, that.contactDetail) &&
                currentStatus == that.currentStatus &&
                Objects.equals(expertiseIds, that.expertiseIds) &&
                Objects.equals(expertiseWithExperience, that.expertiseWithExperience) &&
                Objects.equals(cprNumber, that.cprNumber) &&
                Objects.equals(familyName, that.familyName) &&
                gender == that.gender &&
                Objects.equals(sectorWiseExpertise, that.sectorWiseExpertise);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, firstName, contactDetail, currentStatus, expertiseIds, expertiseWithExperience, cprNumber, familyName, gender, sectorWiseExpertise);
    }


    public boolean isValid() {
        if(StringUtils.isBlank(userName) || StringUtils.containsWhitespace(userName.trim())){
            return false;
        }
        return true;
    }


    public String getFullName() {
        return this.firstName+" "+this.lastName;
    }
}
