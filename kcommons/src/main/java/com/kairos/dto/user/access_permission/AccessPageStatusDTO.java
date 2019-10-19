package com.kairos.dto.user.access_permission;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Created by prabjot on 10/10/17.
 */
@Getter
@Setter
public class AccessPageStatusDTO {

    @NotNull(message = "status can not be null")
    private Boolean active;
}
