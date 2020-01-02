package com.kairos.persistence.model.staff.personal_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class SectorAndStaffExpertiseDTO {

    private Long id;
    private String name;
    private List<StaffExpertiseDTO> expertiseWithExperience;
    private boolean employmentExists;
}
