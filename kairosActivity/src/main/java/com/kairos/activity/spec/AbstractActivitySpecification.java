package com.kairos.activity.spec;

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
}
