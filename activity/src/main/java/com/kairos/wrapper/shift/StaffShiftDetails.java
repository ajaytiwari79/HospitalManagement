package com.kairos.wrapper.shift;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.staff.EmploymentDTO;
import com.kairos.persistence.model.tag.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffShiftDetails implements Serializable {

    private Long id;
    private String firstName;
    private String lastName;
    private List<EmploymentDTO> employments;
    private List<Tag> tags;
//    private List<ShiftDTO> shiftDTOList;

}
