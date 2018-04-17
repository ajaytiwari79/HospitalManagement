package com.kairos.shiftplanning.domain;

import com.kairos.shiftplanning.domain.activityConstraint.ActivityConstraints;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;

import java.sql.Time;
import java.util.List;

@XStreamAlias("TimeType")
public class TimeType {


    private String id;
    private String name;

    public TimeType() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public TimeType(String id, String name) {
        this.id = id;
        this.name = name;
    }


}
