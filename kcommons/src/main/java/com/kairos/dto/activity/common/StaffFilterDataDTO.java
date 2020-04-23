package com.kairos.dto.activity.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

/**
 * Created By G.P.Ranjan on 15/1/20
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffFilterDataDTO {
    private List<BigInteger> activityIds;
    private List<Long> staffIds;
}
