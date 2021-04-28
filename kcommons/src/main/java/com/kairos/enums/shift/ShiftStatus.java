package com.kairos.enums.shift;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by vipul on 8/5/18.
 */
public enum ShiftStatus implements Serializable {
    PUBLISH,UNPUBLISH,FIX,LOCK,UNLOCK,UNFIX,PENDING,REQUEST,VALIDATE, DISAPPROVE,APPROVE,MOVED,REJECT;

    public static List<ShiftStatus> getListByValue(List<String> values) {
        if(Optional.ofNullable(values).isPresent()){
            return values.stream().map(ShiftStatus::valueOf)
                    .collect(Collectors.toList());
        }
        return null;

    }
    public static Set<ShiftStatus> getAllStatusExceptRequestAndPending() {
        return Stream.of(ShiftStatus.values()).filter(current->(!(current.equals(REQUEST)|| current.equals(UNPUBLISH) || current.equals(MOVED) || current.equals(REJECT)||current.equals(PUBLISH)))).collect(Collectors.toSet());
    }
}
