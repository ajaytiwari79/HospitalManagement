package com.kairos.wrapper.shift;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.tags.TagDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.employment.PlanningEmploymentDTO;
import com.kairos.enums.Gender;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffShiftDetailsDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private Long userId;
    private List<PlanningEmploymentDTO> employments;
    private List<TagDTO> tags;
    private List<ShiftWithActivityDTO> shifts;
    private List<EmploymentTypeDTO> employmentList;
    private String city;
    private String currentStatus;
    private String dateOfBirth;
    private Gender gender;
    private String profilePic;
    private String user_id;
    private String province;

    public String toString(){
        return this.firstName + this.getUserId();
    }

    @JsonIgnore
    public Set<Long> getEmploymentIds() {
        return employmentList.stream().map(employmentTypeDTO -> employmentTypeDTO.getId()).collect(Collectors.toSet());
    }
}
