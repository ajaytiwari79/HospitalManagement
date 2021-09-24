package com.kairos.dto.activity.activity.activity_tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.glide_time.ActivityGlideTimeDetails;
import com.kairos.enums.LocationEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Set;

/**
 * Created by vipul on 13/4/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ActivityLocationSettingsDTO {
    private BigInteger activityId;
    private Set<ActivityGlideTimeDetails> glideTimeForCheckIn;
    private Set<ActivityGlideTimeDetails> glideTimeForCheckOut;
}
