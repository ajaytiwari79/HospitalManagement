package com.kairos.activity.spec;

import com.kairos.activity.service.exception.ExceptionService;
import java.util.Collections;
import java.util.List;

public class OrActivitySpecification<T> extends AbstractActivitySpecification<T> {

    private Specification<T> specification1;
    private Specification<T> specification2;


    public OrActivitySpecification(Specification<T> activitySpecification1, Specification<T> activitySpecification2) {
        this.specification1 = activitySpecification1;
        this.specification2 = activitySpecification2;

    }


    @Override
    public boolean isSatisfied(T t) {
        return specification1.isSatisfied(t) || specification2.isSatisfied(t);
    }

    @Override
    public List<String> isSatisfiedString(T t) {
        return Collections.emptyList();
    }

}
