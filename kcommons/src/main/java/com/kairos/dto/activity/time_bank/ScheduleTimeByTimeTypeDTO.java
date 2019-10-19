package com.kairos.dto.activity.time_bank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */
@Getter
@Setter
@NoArgsConstructor
public class ScheduleTimeByTimeTypeDTO {

    private int totalMin;
    private BigInteger timeTypeId;
    private String name;
    private List<ScheduleTimeByTimeTypeDTO> children = new ArrayList();

    public ScheduleTimeByTimeTypeDTO(int totalMin) {
        this.totalMin = totalMin;
    }



}
