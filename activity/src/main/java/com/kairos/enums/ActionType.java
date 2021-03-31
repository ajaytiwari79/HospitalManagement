package com.kairos.enums;

import java.io.Serializable;

public enum ActionType implements Serializable {
        DELETE_SHIFT,
        COPY_SHIFT,
        AVAILABILITY_SHIFT,
        UNAVAILABILITY_SHIFT,
        DELETE_EVENT,
        DELETE_ACTIVITY,
        COPY_ACTIVITY,
        FIX_ACTIVITY,
        UNFIX_ACTIVITY,
        I_AM_SICK,
        I_AM_FINE,
        PASTE_SHIFT
}
