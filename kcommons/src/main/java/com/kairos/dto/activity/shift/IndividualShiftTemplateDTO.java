package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class IndividualShiftTemplateDTO {
    private BigInteger id;
    private String name;
    private String remarks;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    private List<ShiftTemplateActivity> activities;
    private int durationMinutes;
}
