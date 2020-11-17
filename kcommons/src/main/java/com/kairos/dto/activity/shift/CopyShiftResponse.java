package com.kairos.dto.activity.shift;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class CopyShiftResponse {
    private List<StaffWiseShiftResponse> successFul = new ArrayList<>();
    private List<StaffWiseShiftResponse> failure = new ArrayList<>();
    private Integer unCopiedShiftCount;
}
