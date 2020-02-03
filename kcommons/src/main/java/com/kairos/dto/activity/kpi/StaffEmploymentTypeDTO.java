package com.kairos.dto.activity.kpi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffEmploymentTypeDTO {
    private List<Long> staffIds;
    private List<Long> unitIds;
    private List<Long> employmentTypeIds;
    private Long organizationId;
    private String startDate;
    private String endDate;
    private List<Long> tagIds;

    public StaffEmploymentTypeDTO(List<Long> employmentTypeIds, Long organizationId, String startDate, String endDate) {
        this.employmentTypeIds = employmentTypeIds;
        this.organizationId = organizationId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
