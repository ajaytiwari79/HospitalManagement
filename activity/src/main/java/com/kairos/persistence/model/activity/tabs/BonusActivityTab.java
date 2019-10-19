package com.kairos.persistence.model.activity.tabs;

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
public class BonusActivityTab implements Serializable{
    private String bonusHoursType;
    private boolean overRuleCtaWta;

}
