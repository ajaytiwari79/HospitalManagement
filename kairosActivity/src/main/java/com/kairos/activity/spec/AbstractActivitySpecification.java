package com.kairos.activity.spec;

import java.util.List;

/**
 * Created by vipul on 30/1/18.
 */
public abstract class AbstractActivitySpecification<T> implements ActivitySpecification<T> {
    /**
     * {@inheritDoc}
     */
    public abstract boolean isSatisfied(T t);

    public ActivitySpecification<T> and(final ActivitySpecification<T> activitySpecification) {
        return new AndActivitySpecification<T>(this, activitySpecification);
    }

    public ActivitySpecification<T> or(final ActivitySpecification<T> activitySpecification) {
        return new OrActivitySpecification<T>(this, activitySpecification);
    }
    public abstract List<String> isSatisfiedString(T t);
}
