package com.kairos.activity.spec;

import com.kairos.activity.service.exception.ExceptionService;

/**
 * Created by vipul on 30/1/18.
 */
public interface Specification<T> {
    /**
     * Check if {@code t} is satisfied by the specification.
     *
     * @param t Object to test.
     * @return {@code true} if {@code t} satisfies the specification.
     */
    boolean isSatisfied(T t, ExceptionService exceptionService);

    /**
     * Create a new specification that is the AND operation of {@code this} specification and another specification.
     * @param specification Specification to AND.
     * @return A new specification.
     */
    Specification<T> and(Specification<T> specification);
}
