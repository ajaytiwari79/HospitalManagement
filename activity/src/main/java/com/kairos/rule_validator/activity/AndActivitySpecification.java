package com.kairos.rule_validator.activity;

import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.rule_validator.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 7/2/18.
 */
public class AndActivitySpecification<T> extends AbstractSpecification<T> {

    private Specification<T> specification1;
    private Specification<T> specification2;


    public AndActivitySpecification(Specification<T> specification1, Specification<T> specification2) {
        this.specification1 = specification1;
        this.specification2 = specification2;

    }


    @Override
    public boolean isSatisfied(T t) {
        return specification1.isSatisfied(t) && specification2.isSatisfied(t);
    }

    @Override
    public void validateRules(T t) {

    }

    @Override
    public List<String> isSatisfiedString(T t) {
        List<String> messages = new ArrayList<>();
        messages.addAll(specification1.isSatisfiedString(t));
        messages.addAll(specification2.isSatisfiedString(t));
        return messages;
    }
}

