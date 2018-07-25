package com.kairos.rule_validator;


public interface Specification<T> {
    /**
     * Check if {@code t} is satisfied by the rule_validator.
     *
     * @param t Object to test.
     * @return {@code true} if {@code t} satisfies the rule_validator.
     */
    boolean isSatisfied(T t);
    Specification<T> and(Specification<T> other);

}
