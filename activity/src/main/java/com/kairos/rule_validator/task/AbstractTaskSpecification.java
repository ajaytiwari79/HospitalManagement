package com.kairos.rule_validator.task;

import com.kairos.rule_validator.AndSpecification;
import com.kairos.rule_validator.OrSpecification;
import com.kairos.rule_validator.TaskSpecification;

/**
 * Created by prabjot on 24/11/17.
 */
public abstract class AbstractTaskSpecification<T> implements TaskSpecification<T> {

    /**
     * {@inheritDoc}
     */
    public abstract boolean isSatisfied(T t);

    /**
     * {@inheritDoc}
     */
    public TaskSpecification<T> and(final TaskSpecification<T> taskSpecification) {
        return new AndSpecification<T>(this, taskSpecification);
    }

    /**
     * {@inheritDoc}
     */
    public TaskSpecification<T> or(final TaskSpecification<T> specification) {
        return new OrSpecification<T>(this, specification);
    }

}
