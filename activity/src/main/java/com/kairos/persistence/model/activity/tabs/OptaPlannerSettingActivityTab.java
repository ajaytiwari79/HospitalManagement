package com.kairos.persistence.model.activity.tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.USE_DEFAULTS)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OptaPlannerSettingActivityTab implements Serializable {

    private int maxThisActivityPerShift;
    private int minLength;
    private boolean eligibleForMove;
}