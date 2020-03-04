package com.kairos.dto.activity.common;

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
public class UserInfo {
    private Long id;
    private String email;
    private String fullName;

    public UserInfo(Long id, String email, String fullName) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
    }
}
