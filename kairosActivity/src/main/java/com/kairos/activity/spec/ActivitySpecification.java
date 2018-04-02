package com.kairos.activity.spec;

/**
 * Created by vipul on 30/1/18.
 */
public interface ActivitySpecification<T> {
    /**
     * Check if {@code t} is satisfied by the specification.
     *
     * @param t Object to test.
     * @return {@code true} if {@code t} satisfies the specification.
     */
    boolean isSatisfied(T t);

    /**
     * Create a new specification that is the AND operation of {@code this} specification and another specification.
     * @param activitySpecification Specification to AND.
     * @return A new specification.
     */
    ActivitySpecification<T> and(ActivitySpecification<T> activitySpecification);
}
