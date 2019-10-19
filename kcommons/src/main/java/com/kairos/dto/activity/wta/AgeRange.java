package com.kairos.dto.activity.wta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by pavan on 24/4/18.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgeRange{
    private int from;
    private int to;
    private int leavesAllowed;
}
