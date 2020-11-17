package com.kairos.wrapper.action;

import com.kairos.enums.ActionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

import static com.kairos.constants.ActivityMessagesConstants.ERROR_NAME_NOTNULL;

/**
 * Created By G.P.Ranjan on 2/4/20
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActionDTO {
    private BigInteger id;
    @NotNull(message = ERROR_NAME_NOTNULL)
    private ActionType name;
    private String description;
    private Long unitId;
}
