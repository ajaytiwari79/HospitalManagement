package com.kairos.dto.activity.common;

import com.kairos.dto.user.access_permission.AccessGroupRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author pradeep
 * @date - 9/1/19
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private Long id;
    private String email;
    private String fullName;
    private AccessGroupRole accessGroupRole;

}
