package com.kairos.dto.user.staff.staff;

import com.kairos.dto.user.access_permission.AccessGroupRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffAccessRoleDTO {
    private Long id;
    private List<AccessGroupRole>  roles;
}
