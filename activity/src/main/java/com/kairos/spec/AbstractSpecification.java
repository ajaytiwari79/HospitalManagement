package com.kairos.spec;

import java.util.List;

/**
 * Created by vipul on 30/1/18.
 */
public abstract class AbstractSpecification<T> implements Specification<T> {
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract boolean isSatisfied(T t);

    @Override
    public Specification<T> and(final Specification<T> specification) {
        return new AndActivitySpecification<T>(this, specification);
    }

    @Override
    public Specification<T> or(final Specification<T> specification) {
        return new OrActivitySpecification<T>(this, specification);
    }
    @Override
    public abstract List<String> isSatisfiedString(T t);
}
