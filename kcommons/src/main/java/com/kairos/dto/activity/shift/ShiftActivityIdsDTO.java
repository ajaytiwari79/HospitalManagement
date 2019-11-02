package com.kairos.dto.activity.shift;
/*
 *Created By Pavan on 8/10/18
 *
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShiftActivityIdsDTO {
    private Set<BigInteger> activitiesToAdd=new HashSet();
    private Set<BigInteger> activitiesToEdit=new HashSet();
    private Set<BigInteger> activitiesToDelete=new HashSet();

}
