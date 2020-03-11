package com.kairos.dto.activity.shift;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isNull;

/**
 * @author pradeep
 * @date - 30/8/18
 */

@Getter
@Setter
@NoArgsConstructor
public class ActivityRuleViolation {

    private BigInteger activityId;
    private Set<String> errorMessages;
    private String name;
    private int counter;

    public ActivityRuleViolation(BigInteger activityId, String name, int counter,Set<String> errorMessages) {
        this.activityId = activityId;
        this.name = name;
        this.counter = counter;
        this.errorMessages = errorMessages;
    }


    public Set<String> getErrorMessages() {
        return errorMessages=isNull(errorMessages) ? new HashSet<>() : errorMessages;
    }

}
