package com.kairos.rule_validator;

public class AndSpecification<T> extends AbstractSpecification<T> {
    private Specification<T> first;
    private Specification<T> second;

    public AndSpecification(Specification<T> first, Specification<T> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean isSatisfied(T t) {
        return first.isSatisfied(t) && second.isSatisfied(t);
    }
}
