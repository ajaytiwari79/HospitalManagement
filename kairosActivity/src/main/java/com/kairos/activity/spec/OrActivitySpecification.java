package com.kairos.activity.spec;

import java.util.Collections;
import java.util.List;

public class OrActivitySpecification<T> extends AbstractActivitySpecification<T> {

    private ActivitySpecification<T> activitySpecification1;
    private ActivitySpecification<T> activitySpecification2;


    public OrActivitySpecification(ActivitySpecification<T> activitySpecification1, ActivitySpecification<T> activitySpecification2) {
        this.activitySpecification1 = activitySpecification1;
        this.activitySpecification2 = activitySpecification2;

    }


    @Override
    public boolean isSatisfied(T t) {
        return activitySpecification1.isSatisfied(t) || activitySpecification2.isSatisfied(t);
    }

    @Override
    public List<String> isSatisfiedString(T t) {
        return Collections.emptyList();
    }

}
