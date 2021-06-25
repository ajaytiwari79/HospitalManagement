package com.kairos.enums.shift;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kairos.commons.utils.ObjectUtils.newHashSet;

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
        return newHashSet(FIX,LOCK,UNLOCK,UNFIX,PENDING,VALIDATE, DISAPPROVE,APPROVE);
    }
}
