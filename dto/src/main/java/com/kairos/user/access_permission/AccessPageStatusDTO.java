package com.kairos.user.access_permission;

import javax.validation.constraints.NotNull;

/**
 * Created by prabjot on 10/10/17.
 */
public class AccessPageStatusDTO {

    @NotNull(message = "status can not be null")
    private Boolean active;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
