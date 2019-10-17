package com.kairos.dto.user.employment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentIdDTO {

    private Long oldEmploymentId;
    private Long newEmploymentId;
    private Long employmentLineId;
}
