package com.kairos.rule_validator;

import com.kairos.rule_validator.activity.AndActivitySpecification;
import com.kairos.rule_validator.activity.OrActivitySpecification;

import java.util.List;

/**
 * Created by vipul on 30/1/18.
 */
public abstract class AbstractSpecification<T> implements Specification<T> {

    /*@Override
    public abstract boolean isSatisfied(T t);

    @Override
    public abstract void validateRules(T t);

    @Override
    public abstract List<String> isSatisfiedString(T t);*/

    @Override
    public Specification<T> and(final Specification<T> specification) {
        return new AndActivitySpecification<T>(this, specification);
    }

    @Override
    public Specification<T> or(final Specification<T> specification) {
        return new OrActivitySpecification<T>(this, specification);
    }
}
