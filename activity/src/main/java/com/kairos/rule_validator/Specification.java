package com.kairos.rule_validator;

import java.util.List;

/**
 * Created by vipul on 30/1/18.
 */
public interface Specification<T> {
    /**
     * Check if {@code t} is satisfied by the rule_validator.
     *
     * @param t Object to test.
     * @return {@code true} if {@code t} satisfies the rule_validator.
     */
    boolean isSatisfied(T t);

    void validateRules(T t);

    /**
     * Create a new rule_validator that is the AND operation of {@code this} rule_validator and another rule_validator.
     * @param specification Specification to AND.
     * @return A new rule_validator.
     */
    Specification<T> and(Specification<T> specification);

    /**
     * Create a new rule_validator that is the OR operation of {@code this} rule_validator and another rule_validator.
     *
     * @param activitySpecification Specification to OR.
     * @return A new rule_validator.
     */
    Specification<T> or(Specification<T> activitySpecification);

    List<String> isSatisfiedString(T t);
}
