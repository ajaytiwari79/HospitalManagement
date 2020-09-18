package com.kairos.dto.activity.activity.activity_tabs;

import java.io.Serializable;

/**
 * @author pradeep
 * @date - 21/8/18
 */

public enum CutOffIntervalUnit implements Serializable {
    DAYS, WEEKS, MONTHS, QUARTERS, YEARS, HALF_YEARLY;

    public enum CutOffBalances implements Serializable {
        EXPIRE, TRANSFER;
    }
}

