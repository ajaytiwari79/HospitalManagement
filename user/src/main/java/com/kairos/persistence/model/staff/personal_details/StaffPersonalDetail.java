package com.kairos.persistence.model.staff.personal_details;

import com.kairos.dto.user.organization.AddressDTO;
import com.kairos.enums.Gender;
import com.kairos.enums.StaffStatusEnum;
import com.kairos.persistence.model.client.ContactDetail;
import com.kairos.persistence.model.staff.SectorAndStaffExpertiseQueryResult;
import com.kairos.persistence.model.staff.StaffExperienceInExpertiseDTO;
import com.kairos.persistence.model.staff.StaffTeamDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;
import java.util.Set;


/**
 * Created by prabjot on 10/1/17.
 */
@QueryResult
@Getter
@Setter
@NoArgsConstructor
public class StaffPersonalDetail {

    private Long id;
    @NotBlank(message = "error.Staff.firstname.notnull")
    private String firstName;
    @NotBlank(message = "error.Staff.lastname.notnull")
    private String lastName;
    private String signature;
    private long visitourId;
    private ContactDetail contactDetail;
    private String inactiveFrom;
    private StaffStatusEnum currentStatus;
    private long languageId;
    private List<Long> expertiseIds;
    private List<StaffExperienceInExpertiseDTO> expertiseWithExperience;
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
    private List<SectorAndStaffExpertiseQueryResult> sectorWiseExpertise;
    private AddressDTO primaryAddress;
    private AddressDTO secondaryAddress;
    private Set<Long> teamIdsOfStaff;
    @NotBlank(message = "error.Staff.userName.notnull")
    private String userName;
    private boolean userNameUpdated;
    private List<StaffTeamDTO> teamDetails;
    private List<StaffChildDetailDTO> staffChildDetailDTOS;

    public void setPrimaryAddress(AddressDTO primaryAddress) {
        this.primaryAddress = primaryAddress;
        this.primaryAddress.setPrimary(true);
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

    @AssertTrue(message = "Please provide a valid user name")
    public boolean isValid() {
        if(StringUtils.isBlank(userName) || StringUtils.containsWhitespace(userName.trim())){
            return false;
        }
        return true;
    }


}
