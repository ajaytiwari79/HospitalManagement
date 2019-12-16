package com.kairos.persistence.model.staff.personal_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.enums.Gender;
import com.kairos.persistence.model.country.default_data.EmploymentTypeDTO;
import com.kairos.utils.CPRUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Set;

/**
 * Created by vipul on 8/9/17.
 */
@QueryResult
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffPersonalDetailQueryResult {
    private Long id;
    private String lastName;
    private Long employedSince;
    private String badgeNumber;
    private String userName;
    private Long externalId;
    private String firstName;
    private Long organizationId;
    private String cprNumber;
    private Long roasteringTime;
    private Long freeDay;
    private Long mostOverStaffingHours;
    private Long mostUnderStaffingHours;
    private Long accumulatedTimeBank;
    private Long accumulatedPoints;
    private String name;
    private String profilePic;
    protected Gender gender;
    private String city;
    private String province;
    private Boolean employment;
    private Integer age;
    private String privatePhone;
    private LocalDate dateOfBirth;
    private Boolean pregnant;
    // used for staff validation
    private Long accessGroupId;
    private String accessGroupName;
    private Long parentAccessGroupId;
    private String email;
    private Staff staff;
    private Set<AccessGroupRole> roles;
    private List<EmploymentTypeDTO> employmentTypes;
    private Long staffUserId;

    public Integer getAge() {
        this.age=this.cprNumber!=null?Period.between(CPRUtil.getDateOfBirthFromCPR(this.cprNumber), LocalDate.now()).getYears():null;
        return age;
    }

}
