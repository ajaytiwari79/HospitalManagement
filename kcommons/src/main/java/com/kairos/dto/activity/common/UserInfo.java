package com.kairos.dto.activity.common;

import com.kairos.dto.user.access_permission.AccessGroupRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author pradeep
 * @date - 9/1/19
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {
    private static final long serialVersionUID = -5063027528255969117L;
    private Long id;
    private String email;
    private String fullName;
    private AccessGroupRole accessGroupRole;

}
