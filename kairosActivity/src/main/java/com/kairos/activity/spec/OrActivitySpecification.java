package com.kairos.activity.spec;

import com.kairos.activity.service.exception.ExceptionService;

public class OrActivitySpecification<T> extends AbstractActivitySpecification<T> {

    private Specification<T> specification1;
    private Specification<T> specification2;


    public OrActivitySpecification(Specification<T> activitySpecification1, Specification<T> activitySpecification2) {
        this.specification1 = activitySpecification1;
        this.specification2 = activitySpecification2;

    }


    @Override
    public boolean isSatisfied(T t, ExceptionService exceptionService) {
        return specification1.isSatisfied(t) || specification2.isSatisfied(t);
    }
}
