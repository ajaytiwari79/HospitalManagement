package com.kairos.dto.activity.pay_out;

import lombok.Getter;
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
public class ScheduleTimeByTimeTypeDTO {

    private int totalMin;
    private BigInteger timeTypeId;
    private String label;
}
