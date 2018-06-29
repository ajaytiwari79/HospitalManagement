package com.kairos.specification;


public interface Specification<T> {
    /**
     * Check if {@code t} is satisfied by the specification.
     *
     * @param t Object to test.
     * @return {@code true} if {@code t} satisfies the specification.
     */
    boolean isSatisfied(T t);
    Specification<T> and(Specification<T> other);

}
