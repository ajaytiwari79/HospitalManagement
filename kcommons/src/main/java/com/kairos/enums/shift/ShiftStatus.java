package com.kairos.enums.shift;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by vipul on 8/5/18.
 */
public enum ShiftStatus {
    PUBLISH,UNPUBLISH,FIX,LOCK,UNLOCK,UNFIX,PENDING,REQUEST,VALIDATE,REJECT,APPROVE;

    public static List<ShiftStatus> getListByValue(List<String> values) {
        if(Optional.ofNullable(values).isPresent()){
            return values.stream().map(ShiftStatus::valueOf)
                    .collect(Collectors.toList());
        }
        return null;

    }
    public static List<ShiftStatus> getAllStatusExceptRequestAndPending() {
        return Stream.of(ShiftStatus.values()).filter(current->(!(current.equals(REQUEST)|| current.equals(PENDING)))).collect(Collectors.toList());
    }
}
