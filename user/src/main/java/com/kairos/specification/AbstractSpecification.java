package com.kairos.specification;

public abstract class AbstractSpecification<T> implements Specification<T> {
    public abstract boolean isSatisfied(T t);
    @Override
    public Specification<T> and(Specification<T> other) {
        return new AndSpecification<>(this, other);
    }
}
