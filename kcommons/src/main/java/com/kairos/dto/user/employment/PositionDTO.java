package com.kairos.dto.user.employment;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Created by yatharth on 19/4/18.
 */
@Getter
@Setter
public class PositionDTO {
    @NotNull
    private String endDate;
    private Long reasonCodeId;
    private Long accessGroupIdOnPositionEnd;
}
