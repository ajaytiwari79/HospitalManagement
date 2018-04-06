package com.kairos.activity.spec;

/**
 * Created by prabjot on 24/11/17.
 */
public interface TaskSpecification<T> {

    /**
     * Check if {@code t} is satisfied by the specification.
     *
     * @param t Object to test.
     * @return {@code true} if {@code t} satisfies the specification.
     */
    boolean isSatisfied(T t);

    /**
     * Create a new specification that is the AND operation of {@code this} specification and another specification.
     * @param taskSpecification Specification to AND.
     * @return A new specification.
     */
    TaskSpecification<T> and(TaskSpecification<T> taskSpecification);

    /**
     * Create a new specification that is the OR operation of {@code this} specification and another specification.
     * @param taskSpecification Specification to OR.
     * @return A new specification.
     */
    TaskSpecification<T> or(TaskSpecification<T> taskSpecification);

}
