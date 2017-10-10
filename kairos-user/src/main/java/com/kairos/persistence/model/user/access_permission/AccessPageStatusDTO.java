package com.kairos.persistence.model.user.access_permission;

import javax.validation.constraints.NotNull;

/**
 * Created by prabjot on 10/10/17.
 */
public class AccessPageStatusDTO {

    @NotNull(message = "status can not be null")
    private Boolean status;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
