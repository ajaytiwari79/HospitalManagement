package com.kairos.rule_validator.task;

/**
 * Created by prabjot on 24/11/17.
 */
public interface TaskSpecification<T> {

    /**
     * Check if {@code t} is satisfied by the rule_validator.
     *
     * @param t Object to test.
     * @return {@code true} if {@code t} satisfies the rule_validator.
     */
    boolean isSatisfied(T t);

    /**
     * Create a new rule_validator that is the AND operation of {@code this} rule_validator and another rule_validator.
     * @param taskSpecification Specification to AND.
     * @return A new rule_validator.
     */
    TaskSpecification<T> and(TaskSpecification<T> taskSpecification);

    /**
     * Create a new rule_validator that is the OR operation of {@code this} rule_validator and another rule_validator.
     * @param taskSpecification Specification to OR.
     * @return A new rule_validator.
     */
    TaskSpecification<T> or(TaskSpecification<T> taskSpecification);

}
