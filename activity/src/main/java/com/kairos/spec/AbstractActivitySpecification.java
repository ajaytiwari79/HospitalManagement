package com.kairos.spec;

import com.kairos.service.exception.ExceptionService;
import java.util.List;

/**
 * Created by vipul on 30/1/18.
 */
public abstract class AbstractActivitySpecification<T> implements Specification<T> {
    /**
     * {@inheritDoc}
     */
    public abstract boolean isSatisfied(T t);

    public Specification<T> and(final Specification<T> specification) {
        return new AndActivitySpecification<T>(this, specification);
    }

    public Specification<T> or(final Specification<T> specification) {
        return new OrActivitySpecification<T>(this, specification);
    }
    public abstract List<String> isSatisfiedString(T t);
}
