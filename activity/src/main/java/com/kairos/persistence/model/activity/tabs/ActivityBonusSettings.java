package com.kairos.persistence.model.activity.tabs;

import com.kairos.annotations.KPermissionField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by vipul on 24/8/17.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityBonusSettings implements Serializable {

    private static final long serialVersionUID = -555758265267242594L;
    @KPermissionField
    private String bonusHoursType;
    @KPermissionField
    private boolean overRuleCtaWta;

}
