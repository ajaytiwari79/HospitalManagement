package com.kairos.dto.user.access_group;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PermissionDTO {
    private Long staffId;
    private Long roleId;
    private boolean created;
    private Long organizationId;
    private LocalDate startDate;
    private LocalDate endDate;
}
