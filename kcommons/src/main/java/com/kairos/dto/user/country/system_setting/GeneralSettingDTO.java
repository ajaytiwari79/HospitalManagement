package com.kairos.dto.user.country.system_setting;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

/**
 * Created By G.P.Ranjan on 25/11/19
 **/
@Getter
@Setter
public class GeneralSettingDTO {
    private Long id;
    private short idleTimeInMinutes;
    private short awayTimeInMinutes;

    @AssertTrue(message = "error.away.time.can.not.be.smaller.than.idle.time")
    public boolean isValid() {
        return awayTimeInMinutes > idleTimeInMinutes;
    }
}
