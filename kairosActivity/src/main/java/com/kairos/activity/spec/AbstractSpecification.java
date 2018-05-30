package com.kairos.activity.spec;

import com.kairos.activity.service.exception.ExceptionService;

/**
 * Created by vipul on 30/1/18.
 */
public abstract class AbstractSpecification<T> implements Specification<T> {
    /**
     * {@inheritDoc}
     */
    public abstract boolean isSatisfied(T t, ExceptionService exceptionService);

    public Specification<T> and(final Specification<T> specification) {
        return new AndActivitySpecification<T>(this, specification);
    }
}
