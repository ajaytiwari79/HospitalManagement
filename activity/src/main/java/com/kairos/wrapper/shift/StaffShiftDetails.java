package com.kairos.wrapper.shift;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.user.staff.EmploymentDTO;
import com.kairos.enums.Gender;
import com.kairos.persistence.model.tag.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffShiftDetails {

    private Long id;
    private String firstName;
    private String lastName;
    private Long userId;
    private List<EmploymentDTO> employments;
    private List<Tag> tags;
    private List<ShiftWithActivityDTO> shifts;
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

}
