package com.kairos.dto.user.employment;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * Created by yatharth on 19/4/18.
 */
@Getter
@Setter
public class PositionDTO {
    @NotNull
    private String endDate;
    private BigInteger reasonCodeId;
    private Long accessGroupIdOnPositionEnd;
}
