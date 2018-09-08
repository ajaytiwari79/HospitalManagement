package com.kairos.enums.shift;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by vipul on 8/5/18.
 */
public enum ShiftStatus {
    PUBLISHED,UNPUBLISHED,FIXED,LOCKED,UNLOCKED,UNFIXED,PENDING,REQUESTED,VALIDATED,REJECTED,APPROVED;

    public static List<ShiftStatus> getListByValue(List<String> values) {
        if(Optional.ofNullable(values).isPresent()){
            return values.stream().map(ShiftStatus::valueOf)
                    .collect(Collectors.toList());
        }
        return null;

    }
}
