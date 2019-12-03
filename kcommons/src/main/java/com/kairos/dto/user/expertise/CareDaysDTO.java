package com.kairos.dto.user.expertise;

import lombok.Getter;
import lombok.Setter;

/**
 * @author pradeep
 * @date - 16/11/18
 */
@Getter
@Setter
public class CareDaysDTO {
    private Long id;
    private Integer from;
    private Integer to;
    private Integer leavesAllowed;

}
